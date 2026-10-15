package com.aistudy.aistudy.repo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aistudy.aistudy.data.db.AppDatabase
import com.aistudy.aistudy.data.repository.AuthRepositoryImpl
import com.aistudy.aistudy.data.security.SessionStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test cho [AuthRepositoryImpl] — FR1 + N1 + N3.
 *
 * Room chay in-memory (khong dung file dia); [SessionStore] thay bang Fake
 * SessionStore in-memory de test khong phu thuoc EncryptedSharedPreferences.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AuthRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var fakeSessionStore: FakeSessionStore
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        fakeSessionStore = FakeSessionStore()
        repository = AuthRepositoryImpl(
            database = database,
            userDao = database.userDao(),
            sessionStore = fakeSessionStore,
            io = UnconfinedTestDispatcher()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    // --------------------------------------------------------------- register

    @Test
    fun register_taoUserVaProfile_moiKhiThanhCong() = runTest {
        val result = repository.register("Alice@Example.com", "Passw0rd!", "Alice")

        assertTrue("register phai thanh cong", result.isSuccess)
        val user = result.getOrThrow()
        assertTrue("user phai co id > 0", user.id > 0L)
        assertEquals("email phai duoc chuan hoa", "alice@example.com", user.email)
        assertEquals("displayName phai dung", "Alice", user.displayName)

        // Profile mac dinh phai ton tai
        val profile = database.profileDao().findByUser(user.id)
        assertNotNull("profile phai duoc tao", profile)
        assertEquals("userId phai khop", user.id, profile!!.userId)
        assertTrue("onboardedAt phai la null (chua onboarding)", profile.onboardedAt == null)

        // Session duoc luu
        assertEquals("session phai luu userId", user.id, fakeSessionStore.currentUserId())
    }

    @Test
    fun register_emailTrung_thaiBai() = runTest {
        repository.register("Bob@Example.com", "Passw0rd!", "Bob")

        val result = repository.register("bob@example.com", "AnotherPwd1", "Bob2")

        assertTrue("register trung email phai that bai", result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(
            "phai la IllegalStateException, thay vi: ${exception?.javaClass}",
            exception is IllegalStateException
        )
        assertTrue(
            "message phai chua 'da duoc dung', thay vi: ${exception?.message}",
            exception?.message?.contains("da duoc dung") == true
        )
    }

    @Test
    fun register_emailKhongHopLe_thaiBai() = runTest {
        val result = repository.register("not-an-email", "Passw0rd!", "Test")

        assertTrue("email khong hop le phai that bai", result.isFailure)
    }

    @Test
    fun register_matKhauNgan_thaiBai() = runTest {
        val result = repository.register("Test@Example.com", "short", "Test")

        assertTrue("mat khau < 8 ky tu phai that bai", result.isFailure)
    }

    @Test
    fun register_matKhauKhongCoChuHoacSo_thaiBai() = runTest {
        val resultLetterOnly = repository.register("T1@Example.com", "onlyletters", "T")
        val resultDigitOnly = repository.register("T2@Example.com", "12345678", "T")

        assertTrue("mat khau chi chu cai phai that bai", resultLetterOnly.isFailure)
        assertTrue("mat khau chi so phai that bai", resultDigitOnly.isFailure)
    }

    @Test
    fun register_displayNameTrong_thaiBai() = runTest {
        val result = repository.register("Test@Example.com", "Passw0rd!", "   ")

        assertTrue("displayName trong phai that bai", result.isFailure)
    }

    // ------------------------------------------------------------------ login

    @Test
    fun login_dungEmailMatKhau_thanhCong() = runTest {
        val registered = repository.register("User@Example.com", "Passw0rd!", "User").getOrThrow()
        repository.logout()

        val result = repository.login("user@example.com", "Passw0rd!")

        assertTrue("login thanh cong", result.isSuccess)
        assertEquals("phai tra ve dung user", registered.id, result.getOrThrow().id)
        assertEquals("session phai duoc luu", registered.id, fakeSessionStore.currentUserId())
    }

    @Test
    fun login_saiMatKhau_thaiBai() = runTest {
        repository.register("User@Example.com", "Passw0rd!", "User")

        val result = repository.login("user@example.com", "WrongPwd99")

        assertTrue("sai mat khau phai that bai", result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(
            "phai la IllegalArgumentException, thay vi: ${exception?.javaClass}",
            exception is IllegalArgumentException
        )
    }

    @Test
    fun login_emailKhongTonTai_thaiBai() = runTest {
        val result = repository.login("nobody@example.com", "Passw0rd!")

        assertTrue("email khong ton tai phai that bai", result.isFailure)
        assertTrue(
            "phai la IllegalArgumentException, thay vi: ${result.exceptionOrNull()?.javaClass}",
            result.exceptionOrNull() is IllegalArgumentException
        )
    }

    // ---------------------------------------------------------------- lockout

    @Test
    fun login_sai5Lan_lienTiep_biKhoaTamThoi() = runTest {
        repository.register("Lock@Example.com", "Passw0rd!", "Lock")

        // 5 lan sai
        repeat(5) { i ->
            val result = repository.login("lock@example.com", "WrongPwd$")
            assertTrue("lan ${i + 1} phai that bai", result.isFailure)
        }

        // Lan 6: dung mat khau nhung van bi khoa
        val resultAfterLock = repository.login("lock@example.com", "Passw0rd!")
        assertTrue("sau 5 lan sai phai van that bai", resultAfterLock.isFailure)
        val exception = resultAfterLock.exceptionOrNull()
        assertTrue(
            "phai la IllegalStateException khi bi khoa, thay vi: ${exception?.javaClass}",
            exception is IllegalStateException
        )
        assertTrue(
            "message phai bao tam khoa, thay vi: ${exception?.message}",
            exception?.message?.contains("khoa") == true
        )
    }

    @Test
    fun login_dungSauKhiBiKhoa_vanBiKhoa() = runTest {
        repository.register("Lock2@Example.com", "Passw0rd!", "Lock2")

        repeat(5) { repository.login("lock2@example.com", "WrongPwd") }

        // Dung van bi khoa, khong duoc phep login
        val result = repository.login("lock2@example.com", "Passw0rd!")
        assertTrue("van bi khoa sau khi dat gioi han", result.isFailure)
    }

    // ----------------------------------------------------------------- logout

    @Test
    fun logout_xoaSession() = runTest {
        val user = repository.register("Logout@Example.com", "Passw0rd!", "Logout").getOrThrow()
        assertEquals(user.id, fakeSessionStore.currentUserId())

        repository.logout()

        assertEquals("sau logout session phai null", null, fakeSessionStore.currentUserId())
    }
}

/**
 * Fake [SessionStore] in-memory — khong dung EncryptedSharedPreferences.
 * Dung trong instrumented test de tranh phu thuoc AndroidKeyStore.
 */
private class FakeSessionStore : SessionStore {

    private val _userId = MutableStateFlow<Long?>(null)

    override val userId: kotlinx.coroutines.flow.Flow<Long?> get() = _userId

    override fun currentUserId(): Long? = _userId.value

    override fun save(userId: Long) {
        _userId.value = userId
    }

    override fun clear() {
        _userId.value = null
    }
}
