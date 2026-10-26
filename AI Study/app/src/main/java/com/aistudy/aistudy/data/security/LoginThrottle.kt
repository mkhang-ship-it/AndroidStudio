package com.aistudy.aistudy.data.security

import android.content.Context
import java.util.concurrent.ConcurrentHashMap

/**
 * Bo dem dang nhap sai — giu lockout 5 phut ke ca khi app bi kill/restart.
 *
 * Truoc day bo dem nam trong `ConcurrentHashMap` cua repository nen chi can
 * tat app la reset (BUG-01). Ban [SharedPrefsLoginThrottle] ghi xuong
 * SharedPreferences; key la email da chuan hoa, khong chua mat khau.
 */
interface LoginThrottle {

    /** So lan sai lien tiep hien tai (0 neu sach). */
    fun attempts(key: String): Int

    /** Thoi diem (epoch millis) khoa het han; 0L neu khong bi khoa. */
    fun lockedUntil(key: String): Long

    /** Ghi nhan mot lan sai; tu khoa khi cham [maxAttempts]. */
    fun recordFailure(
        key: String,
        nowMillis: Long,
        maxAttempts: Int,
        lockDurationMillis: Long
    )

    /** Xoa trang thai sau khi dang nhap thanh cong hoac lock da het han. */
    fun clear(key: String)
}

/** Ban in-memory dung cho unit test va lam fallback. */
class InMemoryLoginThrottle : LoginThrottle {

    private val state = ConcurrentHashMap<String, Pair<Int, Long>>()

    override fun attempts(key: String): Int = state[key]?.first ?: 0

    override fun lockedUntil(key: String): Long = state[key]?.second ?: 0L

    override fun recordFailure(
        key: String,
        nowMillis: Long,
        maxAttempts: Int,
        lockDurationMillis: Long
    ) {
        val next = attempts(key) + 1
        val lockedUntil = if (next >= maxAttempts) nowMillis + lockDurationMillis else 0L
        state[key] = next to lockedUntil
    }

    override fun clear(key: String) {
        state.remove(key)
    }
}

/** Ban that dung trong app — ben vung qua restart. */
class SharedPrefsLoginThrottle(
    context: Context,
    prefsName: String = DEFAULT_PREFS_NAME
) : LoginThrottle {

    private val prefs = context.applicationContext
        .getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override fun attempts(key: String): Int = prefs.getInt(attemptsKey(key), 0)

    override fun lockedUntil(key: String): Long = prefs.getLong(lockKey(key), 0L)

    override fun recordFailure(
        key: String,
        nowMillis: Long,
        maxAttempts: Int,
        lockDurationMillis: Long
    ) {
        val next = attempts(key) + 1
        val lockedUntil = if (next >= maxAttempts) nowMillis + lockDurationMillis else 0L
        // commit() thay vi apply(): chi phi khong dang ke (chi khi sai mat khau)
        // nhung dam bao tien trinh khac / lan khoi dong sau doc duoc ngay.
        prefs.edit()
            .putInt(attemptsKey(key), next)
            .putLong(lockKey(key), lockedUntil)
            .commit()
    }

    override fun clear(key: String) {
        prefs.edit()
            .remove(attemptsKey(key))
            .remove(lockKey(key))
            .commit()
    }

    private fun attemptsKey(key: String) = "attempts_$key"

    private fun lockKey(key: String) = "locked_$key"

    companion object {
        const val DEFAULT_PREFS_NAME = "login_throttle"
    }
}
