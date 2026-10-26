package com.aistudy.aistudy.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudy.aistudy.data.repository.QuestionRepository
import com.aistudy.aistudy.domain.model.entity.QuestionAnswer
import com.aistudy.aistudy.domain.model.enums.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** FR6 - Lich su */

data class HistoryUiState(
    val isLoading: Boolean = true,
    val allItems: List<QuestionAnswer> = emptyList(),
    val items: List<QuestionAnswer> = emptyList(),
    val searchKeyword: String = "",
    val filterBookmarked: Boolean = false,
    val selectedSubjects: Set<Subject> = emptySet(),
    val selectionMode: Boolean = false,
    val selectedIds: Set<Long> = emptySet(),
    /** Các môn đang có trong lịch sử (động, không giới hạn môn). */
    val subjects: List<Subject> = emptyList(),
    /** id câu đang được hỏi lại. */
    val reaskingId: Long? = null,
    /** Kết quả hỏi lại gần nhất (AI trả lời mới). */
    val reaskResult: QuestionAnswer? = null,
    val reaskError: String? = null,
    val errorMessage: String? = null,
    val hasError: Boolean = false
)

class HistoryViewModel(
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState(isLoading = true))
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    private val searchKeyword = MutableStateFlow("")
    private val filterBookmarked = MutableStateFlow(false)
    private val selectedSubjects = MutableStateFlow<Set<Subject>>(emptySet())
    private var currentUserId: Long? = null
    private var loadJob: Job? = null

    fun load(userId: Long) {
        // Man hinh co the goi lai sau moi recomposition - chi giu mot collector
        // duy nhat cho cung mot user, tranh loop load/ghi state lien tuc.
        if (userId == currentUserId && loadJob?.isActive == true) return
        currentUserId = userId
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = HistoryUiState(isLoading = true)
            try {
                questionRepository.observeHistory(userId).collect { raw ->
                    val list = dedupeQuestions(raw)
                    val result = applyFiltersToList(
                        list,
                        searchKeyword.value.trim(),
                        filterBookmarked.value,
                        selectedSubjects.value
                    )
                    val subs = result.map { it.subjectCode }.distinct()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        allItems = list,
                        items = result,
                        subjects = subs,
                        searchKeyword = searchKeyword.value,
                        filterBookmarked = filterBookmarked.value,
                        selectedSubjects = selectedSubjects.value,
                        errorMessage = null,
                        hasError = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    hasError = true,
                    errorMessage = e.message
                )
            }
        }
    }

    fun setSearch(keyword: String) {
        searchKeyword.value = keyword
        refreshFilters()
    }

    fun setFilterBookmarked(value: Boolean) {
        filterBookmarked.value = value
        refreshFilters()
    }

    fun toggleSubject(subject: Subject) {
        selectedSubjects.value = selectedSubjects.value.toMutableSet().apply {
            if (!add(subject)) remove(subject)
        }
        refreshFilters()
    }

    fun clearSubjects() {
        selectedSubjects.value = emptySet()
        refreshFilters()
    }

    fun toggleBookmark(id: Long, value: Boolean) {
        viewModelScope.launch {
            runCatching { questionRepository.setBookmarked(id, value) }
        }
    }

    fun setSelectionMode(enabled: Boolean) {
        _state.update {
            it.copy(selectionMode = enabled, selectedIds = if (enabled) it.selectedIds else emptySet())
        }
    }

    fun toggleSelected(id: Long) {
        _state.update {
            val ids = it.selectedIds.toMutableSet()
            if (!ids.add(id)) ids.remove(id)
            it.copy(selectedIds = ids)
        }
    }

    fun selectAll() {
        val items = _state.value.items
        val allSelected = items.isNotEmpty() && items.map { it.id }.toSet() == _state.value.selectedIds
        val ids = if (allSelected) emptySet() else items.map { it.id }.toSet()
        _state.update { it.copy(selectedIds = ids) }
    }

    fun deleteSelected() {
        val ids = _state.value.selectedIds
        if (ids.isEmpty()) return
        viewModelScope.launch {
            runCatching { questionRepository.deleteQuestions(ids.toList()) }
            _state.update { it.copy(selectionMode = false, selectedIds = emptySet()) }
        }
    }

    fun deleteOne(id: Long) {
        viewModelScope.launch {
            runCatching { questionRepository.deleteQuestions(listOf(id)) }
        }
    }

    /** FR6 - "Hỏi lại": gửi lại câu hỏi gốc, AI trả lời mới, lưu thêm một bản ghi. */
    fun askAgain(item: QuestionAnswer) {
        val userId = currentUserId ?: return
        if (item.questionText.isBlank()) return
        _state.update { it.copy(reaskingId = item.id, reaskError = null, reaskResult = null) }
        viewModelScope.launch {
            val result = questionRepository.ask(userId, item.questionText)
            _state.update {
                it.copy(
                    reaskingId = null,
                    reaskError = result.exceptionOrNull()?.message,
                    reaskResult = result.getOrNull()
                )
            }
        }
    }

    fun dismissReask() {
        _state.update { it.copy(reaskResult = null, reaskError = null) }
    }

    private fun refreshFilters() {
        val current = _state.value
        val result = applyFiltersToList(
            current.allItems,
            searchKeyword.value.trim(),
            filterBookmarked.value,
            selectedSubjects.value
        )
        _state.value = current.copy(
            items = result,
            searchKeyword = searchKeyword.value,
            filterBookmarked = filterBookmarked.value,
            selectedSubjects = selectedSubjects.value,
            subjects = result.map { it.subjectCode }.distinct()
        )
    }

    private fun applyFiltersToList(
        list: List<QuestionAnswer>,
        keyword: String,
        bookmarkedOnly: Boolean,
        subjects: Set<Subject>
    ): List<QuestionAnswer> {
        var result = list
        if (bookmarkedOnly) {
            result = result.filter { it.isBookmarked }
        }
        if (subjects.isNotEmpty()) {
            result = result.filter { it.subjectCode in subjects }
        }
        if (keyword.isNotEmpty()) {
            result = result.filter {
                it.questionText.contains(keyword, ignoreCase = true) ||
                    it.answerText.contains(keyword, ignoreCase = true)
            }
        }
        return result
    }

    /**
     * Cau hoi da hoi roi chi hien mot lan: gop theo `questionHash` (hash cua van
     * ban da chuan hoa) va giu ban moi nhat. Cau hoi chi gui anh, khong co van
     * ban, dung chung mot hash rong nen duoc tach rieng theo id de khong gop nham.
     */
    private fun dedupeQuestions(list: List<QuestionAnswer>): List<QuestionAnswer> =
        list.groupBy { item ->
            if (item.questionText.isBlank()) "id:${item.id}" else item.questionHash
        }.map { (_, group) ->
            group.maxWithOrNull(
                compareBy<QuestionAnswer> { it.isBookmarked }.thenBy { it.createdAt }
            ) ?: group.first()
        }.sortedByDescending { it.createdAt }
}