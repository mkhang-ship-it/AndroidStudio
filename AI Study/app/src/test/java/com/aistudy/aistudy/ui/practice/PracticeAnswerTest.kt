package com.aistudy.aistudy.ui.practice

import com.aistudy.aistudy.data.repository.GamificationRepository
import com.aistudy.aistudy.data.repository.GamificationResult
import com.aistudy.aistudy.data.repository.ImprovementKind
import com.aistudy.aistudy.data.repository.QuestionRepository
import com.aistudy.aistudy.data.repository.QuizRepository
import com.aistudy.aistudy.domain.model.entity.QuestionAnswer
import com.aistudy.aistudy.domain.model.entity.QuizItem
import com.aistudy.aistudy.domain.model.entity.User
import com.aistudy.aistudy.domain.model.entity.UserBadge
import com.aistudy.aistudy.domain.model.entity.XpEvent
import com.aistudy.aistudy.domain.model.enums.QuestionType
import com.aistudy.aistudy.domain.model.enums.Subject
import com.aistudy.aistudy.domain.model.enums.XpReason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Regression cho loi "chon dung option nhung van bao sai" o Practice/Quiz.
 *
 * `correctAnswer` cua MULTIPLE_CHOICE co the la chu cai (A-D / 1-4) tu
 * FakeAiClient hoac text option tu Gemini. UI chi luu text option khi nguoi
 * dung chon, nen phai quy doi answer key ve text truoc khi so sanh.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PracticeAnswerTest {

    private lateinit var mainDispatcher: TestDispatcher

    @Before
    fun setUp() {
        mainDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun mcItem(correctAnswer: String = "A"): QuizItem = QuizItem(
        id = 1L,
        userId = 1L,
        subjectCode = Subject.MATHEMATICS,
        questionType = QuestionType.MULTIPLE_CHOICE,
        prompt = "2 + 2 = ?",
        optionA = "4",
        optionB = "5",
        optionC = "6",
        optionD = "7",
        correctAnswer = correctAnswer,
        createdAt = 0L
    )

    @Test
    fun `answer key chu cai A danh dau option A la dung`() {
        val item = mcItem(correctAnswer = "A")

        assertTrue(item.isCorrectAnswer(item.optionA!!))
        assertFalse(item.isCorrectAnswer(item.optionB!!))
        assertFalse(item.isCorrectAnswer(item.optionC!!))
        assertFalse(item.isCorrectAnswer(item.optionD!!))
    }

    @Test
    fun `answer key chu cai duoc quy doi thanh text option de hien thi`() {
        assertEquals("4", mcItem(correctAnswer = "A").correctAnswerText())
        assertEquals("5", mcItem(correctAnswer = "B").correctAnswerText())
        assertEquals("6", mcItem(correctAnswer = "C").correctAnswerText())
        assertEquals("7", mcItem(correctAnswer = "D").correctAnswerText())
    }

    @Test
    fun `answer key so 1 quy doi thanh option A`() {
        assertEquals("4", mcItem(correctAnswer = "1").correctAnswerText())
        assertTrue(mcItem(correctAnswer = "1").isCorrectAnswer("4"))
    }

    @Test
    fun `answer key dang text option van duoc danh dau dung`() {
        val item = mcItem(correctAnswer = "5")

        assertTrue(item.isCorrectAnswer("5"))
        assertFalse(item.isCorrectAnswer("4"))
    }

    @Test
    fun `cau tu luan so sanh bo qua hoa thuong va khoang trang`() {
        val item = QuizItem(
            userId = 1L,
            subjectCode = Subject.SCIENCE,
            questionType = QuestionType.SHORT_ANSWER,
            prompt = "Thu do cua Phap?",
            correctAnswer = "Paris",
            createdAt = 0L
        )

        assertTrue(item.isCorrectAnswer("  paris "))
        assertFalse(item.isCorrectAnswer("London"))
    }

    @Test
    fun `chon dung option A duoc tinh dung trong view model`() = runTest(mainDispatcher) {
        val item = mcItem(correctAnswer = "A")
        val quiz = FakeQuizRepository(listOf(item))
        val vm = PracticeViewModel(quiz, FakeQuestionRepository(), FakeGamificationRepository())

        vm.load(userId = 1L)
        advanceUntilIdle()
        assertEquals(item.id, vm.state.value.currentItem?.id)

        vm.setUserAnswer(item.optionA!!)
        vm.submitAnswer()
        advanceUntilIdle()

        assertTrue("phai chuyen sang trang thai da cham", vm.state.value.submitted)
        assertEquals("chon dung phai tang correctCount", 1, vm.state.value.correctCount)
        assertEquals(listOf(item.id to true), quiz.answers)
    }

    @Test
    fun `chon sai option B van tinh sai trong view model`() = runTest(mainDispatcher) {
        val item = mcItem(correctAnswer = "A")
        val quiz = FakeQuizRepository(listOf(item))
        val vm = PracticeViewModel(quiz, FakeQuestionRepository(), FakeGamificationRepository())

        vm.load(userId = 1L)
        advanceUntilIdle()

        vm.setUserAnswer(item.optionB!!)
        vm.submitAnswer()
        advanceUntilIdle()

        assertTrue("phai chuyen sang trang thai da cham", vm.state.value.submitted)
        assertEquals("chon sai khong duoc tang correctCount", 0, vm.state.value.correctCount)
        assertEquals(listOf(item.id to false), quiz.answers)
    }
}

private class FakeQuizRepository(items: List<QuizItem>) : QuizRepository {

    private val itemsFlow = MutableStateFlow(items)
    val answers = mutableListOf<Pair<Long, Boolean>>()

    override fun observeAll(userId: Long): Flow<List<QuizItem>> = itemsFlow

    override fun observeUnanswered(userId: Long): Flow<List<QuizItem>> = itemsFlow

    override fun observeBySubject(userId: Long, subject: Subject): Flow<List<QuizItem>> = itemsFlow

    override suspend fun generateFromQuestion(
        userId: Long,
        sourceQuestionId: Long?,
        count: Int
    ): Result<List<Long>> = Result.success(emptyList())

    override suspend fun answer(id: Long, isCorrect: Boolean): Result<Unit> {
        answers += id to isCorrect
        return Result.success(Unit)
    }

    override suspend fun accuracy(userId: Long): Double = 0.0

    override suspend fun answeredCount(userId: Long): Int = 0

    override suspend fun totalCount(userId: Long): Int = 0
}

private class FakeQuestionRepository : QuestionRepository {

    override fun observeHistory(userId: Long): Flow<List<QuestionAnswer>> = flowOf(emptyList())

    override fun observeBookmarked(userId: Long): Flow<List<QuestionAnswer>> = flowOf(emptyList())

    override suspend fun getQuestion(id: Long): QuestionAnswer? = null

    override suspend fun search(userId: Long, keyword: String): List<QuestionAnswer> = emptyList()

    override suspend fun bySubject(userId: Long, subject: Subject): List<QuestionAnswer> = emptyList()

    override suspend fun ask(userId: Long, text: String): Result<QuestionAnswer> =
        Result.failure(UnsupportedOperationException("khong dung trong test nay"))

    override suspend fun askWithImage(
        userId: Long,
        text: String,
        imagePath: String?,
        imageIv: String?
    ): Result<QuestionAnswer> = Result.failure(UnsupportedOperationException("khong dung trong test nay"))

    override suspend fun improve(id: Long, kind: ImprovementKind): Result<String> =
        Result.failure(UnsupportedOperationException("khong dung trong test nay"))

    override suspend fun setBookmarked(id: Long, value: Boolean) = Unit

    override suspend fun addReviewTime(id: Long, seconds: Int) = Unit

    override suspend fun deleteQuestions(userId: Long, ids: List<Long>) = Unit
}

private class FakeGamificationRepository : GamificationRepository {

    override fun observeUser(userId: Long): Flow<User?> = flowOf(null)

    override fun observeBadges(userId: Long): Flow<List<UserBadge>> = flowOf(emptyList())

    override fun observeXpEvents(userId: Long): Flow<List<XpEvent>> = flowOf(emptyList())

    override suspend fun recordActivity(
        userId: Long,
        reason: XpReason,
        nowEpochDay: Long
    ): GamificationResult = GamificationResult()

    override suspend fun xpSince(userId: Long, sinceMillis: Long): Int = 0

    override suspend fun recalculate(userId: Long) = Unit
}
