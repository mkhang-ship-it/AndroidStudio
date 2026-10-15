package com.aistudy.aistudy.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.aistudy.aistudy.data.db.AppDatabase
import com.aistudy.aistudy.data.db.dao.QuestionAnswerDao
import com.aistudy.aistudy.data.db.dao.UserDao
import com.aistudy.aistudy.domain.model.entity.QuestionAnswer
import com.aistudy.aistudy.domain.model.entity.User
import com.aistudy.aistudy.domain.model.enums.Subject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Kiểm thử [QuestionAnswerDao] trên Room in-memory, chạy trên emulator.
 *
 * Bảng này có FK trỏ tới `users`, nên mỗi test phải chèn một user trước khi chèn
 * câu hỏi — nếu thiếu, SQLite sẽ chặn FOREIGN KEY.
 */
@RunWith(AndroidJUnit4::class)
class QuestionAnswerDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: QuestionAnswerDao
    private lateinit var userDao: UserDao
    private var userId: Long = 0L

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.questionAnswerDao()
        userDao = db.userDao()
        userId = runBlocking {
            userDao.insert(
                User(
                    email = "learner@example.com",
                    passwordHash = "hash",
                    passwordSalt = "salt",
                    displayName = "Learner",
                    createdAt = 1L
                )
            )
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun answer(
        subject: Subject = Subject.MATHEMATICS,
        questionText: String = "Câu hỏi",
        hash: String,
        createdAt: Long,
        reviewTimeSeconds: Int = 0,
        isBookmarked: Boolean = false
    ) = QuestionAnswer(
        userId = userId,
        subjectCode = subject,
        questionText = questionText,
        questionHash = hash,
        answerText = "Trả lời cho: $questionText",
        isBookmarked = isBookmarked,
        reviewTimeSeconds = reviewTimeSeconds,
        createdAt = createdAt
    )

    @Test
    fun insert_thenObserveByUser_returnsNewestFirst() {
        runBlocking {
            val older = answer(hash = "h-old", questionText = "Câu cũ", createdAt = 100L)
            val newer = answer(hash = "h-new", questionText = "Câu mới", createdAt = 200L)
            dao.insert(older)
            dao.insert(newer)

            val observed = dao.observeByUser(userId).first()

            assertEquals(2, observed.size)
            // ORDER BY createdAt DESC → câu mới nhất lên đầu.
            assertEquals("Câu mới", observed[0].questionText)
            assertEquals("Câu cũ", observed[1].questionText)
        }
    }

    @Test
    fun search_matchesKeywordInsideQuestionText() {
        runBlocking {
            dao.insert(answer(hash = "h1", questionText = "Đạo hàm của x^2 là gì?", createdAt = 1L))
            dao.insert(answer(hash = "h2", questionText = "Tích phân của 2x là gì?", createdAt = 2L))
            dao.insert(answer(hash = "h3", questionText = "Thủ đô của Pháp?", createdAt = 3L))

            val hits = dao.search(userId, "Đạo hàm")

            assertEquals(1, hits.size)
            assertEquals("Đạo hàm của x^2 là gì?", hits[0].questionText)
        }
    }

    @Test
    fun bySubject_returnsOnlyMatchingSubject() {
        runBlocking {
            dao.insert(answer(subject = Subject.MATHEMATICS, hash = "m1", questionText = "Toán 1", createdAt = 1L))
            dao.insert(answer(subject = Subject.SCIENCE, hash = "s1", questionText = "Lý 1", createdAt = 2L))
            dao.insert(answer(subject = Subject.MATHEMATICS, hash = "m2", questionText = "Toán 2", createdAt = 3L))

            val maths = dao.bySubject(userId, Subject.MATHEMATICS)

            assertEquals(2, maths.size)
            assertTrue(maths.all { it.subjectCode == Subject.MATHEMATICS })
        }
    }

    @Test
    fun countBySubject_groupsAndCountsPerSubject() {
        runBlocking {
            dao.insert(answer(subject = Subject.MATHEMATICS, hash = "m1", createdAt = 1L))
            dao.insert(answer(subject = Subject.MATHEMATICS, hash = "m2", createdAt = 2L))
            dao.insert(answer(subject = Subject.SCIENCE, hash = "s1", createdAt = 3L))
            dao.insert(answer(subject = Subject.HISTORY, hash = "h1", createdAt = 4L))

            val bySubject: Map<Subject, Int> =
                dao.countBySubject(userId).associate { it.subjectCode to it.total }

            assertEquals(2, bySubject[Subject.MATHEMATICS])
            assertEquals(1, bySubject[Subject.SCIENCE])
            assertEquals(1, bySubject[Subject.HISTORY])
            assertEquals(3, dao.countByUser(userId))
            assertEquals(3, dao.distinctSubjectCount(userId))
        }
    }

    @Test
    fun totalReviewSeconds_sumsAcrossRows() {
        runBlocking {
            dao.insert(answer(hash = "r1", createdAt = 1L, reviewTimeSeconds = 30))
            dao.insert(answer(hash = "r2", createdAt = 2L, reviewTimeSeconds = 45))
            dao.insert(answer(hash = "r3", createdAt = 3L, reviewTimeSeconds = 0))

            assertEquals(75L, dao.totalReviewSeconds(userId))
        }
    }

    @Test
    fun totalReviewSeconds_noRows_returnsZero() {
        runBlocking {
            assertEquals(0L, dao.totalReviewSeconds(userId))
        }
    }

    @Test
    fun addReviewTime_increasesRowAndTotal() {
        runBlocking {
            val id = dao.insert(answer(hash = "r1", createdAt = 1L, reviewTimeSeconds = 10))

            dao.addReviewTime(id, 5)

            assertEquals(15, dao.findById(id)!!.reviewTimeSeconds)
            assertEquals(15L, dao.totalReviewSeconds(userId))
        }
    }

    @Test
    fun setBookmarked_togglesFlagAndBookmarkedFlow() {
        runBlocking {
            val id = dao.insert(answer(hash = "b1", createdAt = 1L))

            assertFalse(dao.findById(id)!!.isBookmarked)
            assertTrue(dao.observeBookmarked(userId).first().isEmpty())

            dao.setBookmarked(id, true)
            assertTrue(dao.findById(id)!!.isBookmarked)
            assertEquals(listOf(id), dao.observeBookmarked(userId).first().map { it.id })

            dao.setBookmarked(id, false)
            assertFalse(dao.findById(id)!!.isBookmarked)
            assertTrue(dao.observeBookmarked(userId).first().isEmpty())
        }
    }

    @Test
    fun findByHash_returnsMostRecentWithThatHash() {
        runBlocking {
            assertNull(dao.findByHash("missing"))
            dao.insert(answer(hash = "same", questionText = "Bản cũ", createdAt = 10L))
            val latest = dao.insert(answer(hash = "same", questionText = "Bản mới", createdAt = 20L))

            assertEquals(latest, dao.findByHash("same")!!.id)
        }
    }
}
