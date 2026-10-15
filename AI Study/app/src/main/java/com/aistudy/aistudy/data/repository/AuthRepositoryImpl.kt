package com.aistudy.aistudy.data.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import com.aistudy.aistudy.data.db.AppDatabase
import com.aistudy.aistudy.data.db.dao.UserDao
import com.aistudy.aistudy.data.security.PasswordHasher
import com.aistudy.aistudy.data.security.SessionStore
import com.aistudy.aistudy.domain.model.entity.User
import com.aistudy.aistudy.domain.rules.ProfileDefaults
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

/**
 * FR1 + N1 + N3.
 *
 * Session duoc giu bang `EncryptedSharedPreferences` ([SessionStore]) chu khong
 * phai trong `AppDatabase`, de logout khong xoa duoc nham va mat phai tai
 * mot bang Room moi dong.
 */
class AuthRepositoryImpl(
    private val database: AppDatabase,
    private val userDao: UserDao,
    private val sessionStore: SessionStore,
    private val io: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    private val _sessionUserId = MutableStateFlow(sessionStore.currentUserId())

    override val sessionUserId: StateFlow<Long?> = _sessionUserId.asStateFlow()

    override val currentUser: Flow<User?> = _sessionUserId
        .flatMapLatest { id -> if (id == null) flowOf(null) else userDao.observeById(id) }

    /**
     * Lockout tam thoi theo email — KHONG luu xuong Room (tranh migration schema).
     * Key: email da chuan hoa; value: (so lan sai, lockedUntilMillis).
     * `lockedUntil == 0L` nghia la chua bi khoa.
     *
     * Chi la lop bao ve trong tien trinh; xem muc "rui ro con lai" — khong ben
     * vung qua restart va khong dong bo giua nhieu tien trinh.
     */
    private val failedAttempts = ConcurrentHashMap<String, Pair<Int, Long>>()

    override suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<User> = runIo {
        val normalisedEmail = email.trim().lowercase()
        require(normalisedEmail.matches(EMAIL_PATTERN)) { "Email khong hop le" }
        require(password.length >= MIN_PASSWORD_LENGTH) {
            "Mat khau phai co it nhat $MIN_PASSWORD_LENGTH ky tu"
        }
        require(password.any { it.isLetter() } && password.any { it.isDigit() }) {
            "Mat khau phai co ca chu va so"
        }
        require(displayName.isNotBlank()) { "Ten hien thi khong duoc de trong" }

        if (userDao.findByEmail(normalisedEmail) != null) {
            throw IllegalStateException("Email nay da duoc dung")
        }

        val hashed = PasswordHasher.hash(password)

        // Ghi `users` + `profiles` + `lastLoginAt` trong MOT transaction: neu bat ky
        // buoc nao that bai thi khong de lai user "mo coi" khong co profile.
        val id = try {
            database.withTransaction {
                val newId = userDao.insert(
                    User(
                        email = normalisedEmail,
                        passwordHash = hashed.hash,
                        passwordSalt = hashed.salt,
                        displayName = displayName.trim(),
                        createdAt = System.currentTimeMillis()
                    )
                )

                // Profile mac dinh phai ton tai ngay luc dang ky: `onboardedAt == null`
                // chinh la tin hieu duy nhat AuthGate dung de chuyen sang onboarding.
                val profile = ProfileDefaults.forNewUser(newId)
                database.profileDao().insert(profile)

                userDao.touchLogin(newId, System.currentTimeMillis())
                newId
            }
        } catch (e: SQLiteConstraintException) {
            // Chan race: findByEmail o tren co the loai, hoac UNIQUE index tren email
            // bao ve khi hai luong dang ky cung email chay song song.
            throw IllegalStateException("Email nay da duoc dung")
        }

        // Session duoc ghi ngoai transaction DB: EncryptedSharedPreferences khong
        // phai nguon du lieu Room nen khong thuoc pham vi rollback.
        persistSession(id)
        userDao.findById(id) ?: throw IllegalStateException("Khong doc lai duoc user vua tao")
    }

    override suspend fun login(email: String, password: String): Result<User> = runIo {
        val normalisedEmail = email.trim().lowercase()
        val now = System.currentTimeMillis()

        // Lockout don gian theo email (khong schema). Neu dang trong thoi gian khoa
        // thi tu choi som, truoc ca khi tra cuu DB / verify password.
        val entry = failedAttempts[normalisedEmail]
        val lockedUntil = entry?.second ?: 0L
        if (now < lockedUntil) {
            throw IllegalStateException(LOCKED_MESSAGE)
        }
        // Lock da het han (khac 0L nghia la tung bi khoa) -> dat lai bo dem.
        val attempts = if (lockedUntil != 0L) 0 else entry?.first ?: 0

        val user = userDao.findByEmail(normalisedEmail)
            ?: throw IllegalArgumentException("Email hoac mat khau khong dung")

        if (!PasswordHasher.verify(password, user.passwordHash, user.passwordSalt)) {
            val next = attempts + 1
            failedAttempts[normalisedEmail] =
                if (next >= MAX_FAILED_ATTEMPTS) next to (now + LOCK_MS)
                else next to 0L
            throw IllegalArgumentException("Email hoac mat khau khong dung")
        }

        failedAttempts.remove(normalisedEmail)
        userDao.touchLogin(user.id, System.currentTimeMillis())
        persistSession(user.id)
        user
    }

    override suspend fun logout() {
        withContext(io) {
            sessionStore.clear()
            _sessionUserId.value = null
        }
    }

    override suspend fun deleteAccount(): Result<Unit> = runIo {
        val userId = sessionStore.currentUserId()
            ?: throw IllegalStateException("Chua dang nhap")

        // Thu tu bat buoc: xoa du lieu sinh hoat truoc, roi moi xoa user.
        // `profiles` bi CASCADE theo `users` nen khong can xoa tay.
        // Boc trong MOT transaction de khong bao gio xoa du lieu sinh hoat
        // nhung that bai o buoc xoa user (roi lai tai khoan "nua voi").
        database.withTransaction {
            database.clearUserData(userId)
            userDao.deleteById(userId)
        }

        sessionStore.clear()
        _sessionUserId.value = null
    }

    private fun persistSession(userId: Long) {
        sessionStore.save(userId)
        _sessionUserId.value = userId
    }

    private suspend inline fun <T> runIo(crossinline block: suspend () -> T): Result<T> =
        withContext(io) {
            try {
                Result.success(block())
            } catch (e: CancellationException) {
                // CancellationException la co che huy coroutine, khong phai loi
                // nghiep vu — phai rethrow de cooperative cancellation hoat dong.
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8

        /** So lan sai mat khau lien tiep truoc khi khoa tam thoi. */
        const val MAX_FAILED_ATTEMPTS = 5

        /** Thoi gian khoa tam thoi: 5 phut. */
        const val LOCK_MS = 5 * 60 * 1000L

        const val LOCKED_MESSAGE = "Tai khoan tam khoa 5 phut"

        /** Du kien `Patterns.EMAIL_ADDRESS` cua Android, tach ra de repository khong can android. */
        val EMAIL_PATTERN = Regex("^[^@\\s]+@[^@\\s.]+(\\.[^@\\s.]+)+$")
    }
}
