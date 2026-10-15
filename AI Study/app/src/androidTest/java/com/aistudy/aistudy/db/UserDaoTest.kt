package com.aistudy.aistudy.db

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aistudy.aistudy.data.db.AppDatabase
import com.aistudy.aistudy.data.db.dao.UserDao
import com.aistudy.aistudy.domain.model.entity.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Kiểm thử [UserDao] trên Room in-memory, chạy trên emulator (`connectedAndroidTest`).
 *
 * Dùng AndroidJUnit4 + inMemoryDatabaseBuilder thay vì Robolectric để bám sát SQLite
 * thật: chỉ ở đây ràng buộc UNIQUE của index `email` mới thực sự được thi hành.
 */
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: UserDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.userDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun newUser(
        email: String,
        displayName: String = "Người dùng",
        createdAt: Long = 1_000L,
        lastLoginAt: Long? = null
    ) = User(
        email = email,
        passwordHash = "hash-${email}",
        passwordSalt = "salt-${email}",
        displayName = displayName,
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )

    @Test
    fun insert_thenFindByEmail_returnsInsertedUser() {
        runBlocking {
            val id = dao.insert(newUser(email = "alice@example.com", displayName = "Alice"))

            val found = dao.findByEmail("alice@example.com")

            assertNotNull(found)
            assertEquals(id, found!!.id)
            assertEquals("Alice", found.displayName)
        }
    }

    @Test
    fun findByEmail_unknownEmail_returnsNull() {
        runBlocking {
            assertNull(dao.findByEmail("nobody@example.com"))
        }
    }

    @Test
    fun insert_duplicateEmail_throwsConstraintException() {
        runBlocking {
            dao.insert(newUser(email = "dup@example.com"))
            // Index UNIQUE trên `users.email` phải chặn bản ghi thứ hai.
            assertThrows(SQLiteConstraintException::class.java) {
                runBlocking { dao.insert(newUser(email = "dup@example.com")) }
            }
        }
    }

    @Test
    fun touchLogin_updatesLastLoginAt() {
        runBlocking {
            val id = dao.insert(newUser(email = "bob@example.com", lastLoginAt = null))
            val now = 1_700_000_000_000L

            dao.touchLogin(id, now)

            assertEquals(now, dao.findById(id)!!.lastLoginAt)
        }
    }

    @Test
    fun touchLogin_secondCall_overwritesPreviousValue() {
        runBlocking {
            val id = dao.insert(newUser(email = "carol@example.com", lastLoginAt = 111L))

            dao.touchLogin(id, 222L)

            assertEquals(222L, dao.findById(id)!!.lastLoginAt)
        }
    }
}
