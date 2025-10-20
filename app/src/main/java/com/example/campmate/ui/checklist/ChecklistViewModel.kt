package com.example.campmate.ui.checklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.UserSession
import com.example.campmate.data.model.ChecklistItem
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userSession: UserSession
) : ViewModel() {

    private val _items = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val items: StateFlow<List<ChecklistItem>> = _items.asStateFlow()

    private val _presets = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val presets: StateFlow<Map<String, List<String>>> = _presets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    private var customerId: Long? = null

    init {
        customerId = userSession.getUserId()

        // ✅ [추가] ViewModel 생성 시 customerId를 로그로 출력하여 확인
        if (customerId == null) {
            Log.w("ChecklistViewModel", "사용자 ID를 찾을 수 없습니다. 로그인이 필요합니다.")
        } else {
            Log.d("ChecklistViewModel", "ViewModel 초기화 완료. 사용자 ID: $customerId")
        }

        fetchPresets()
        loadChecklist()
    }

    private fun loadChecklist() {
        // ✅ [수정] id가 null일 경우, 사용자에게 오류 메시지를 보냅니다.
        val id = customerId
        if (id == null) {
            viewModelScope.launch { _errorEvent.emit("사용자 정보를 불러올 수 없습니다. 다시 로그인해주세요.") }
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getChecklist(id)
                if (response.isSuccessful && response.body() != null) {
                    _items.value = response.body()!!.map { dto ->
                        ChecklistItem(dto.id.toInt(), dto.itemName, dto.isChecked)
                    }
                }
            } catch (e: Exception) {
                _errorEvent.emit("체크리스트를 불러오는 데 실패했습니다.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ... (나머지 코드는 이전 답변과 동일합니다)
    private fun fetchPresets() {
        viewModelScope.launch {
            try {
                val response = apiService.getChecklistPresets()
                if (response.isSuccessful && response.body() != null) {
                    _presets.value = response.body()!!
                        .groupBy(keySelector = { it.category }, valueTransform = { it.itemName })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addPresetItem(itemName: String) {
        if (_items.value.any { it.text.equals(itemName, ignoreCase = true) }) return
        addItemToServer(itemName)
    }

    fun addItem(text: String) {
        if (text.isNotBlank()) {
            addItemToServer(text)
        }
    }

    private fun addItemToServer(itemName: String) {
        viewModelScope.launch {
            val id = customerId
            if (id == null) {
                _errorEvent.emit("로그인이 필요한 기능입니다.")
                return@launch
            }

            try {
                val response = apiService.addChecklistItem(id, itemName)
                if (response.isSuccessful && response.body() != null) {
                    val newItem = response.body()!!
                    _items.update { list ->
                        list + ChecklistItem(newItem.id.toInt(), newItem.itemName, newItem.isChecked)
                    }
                } else {
                    _errorEvent.emit("아이템 추가에 실패했습니다. (서버 오류)")
                }
            } catch (e: Exception) {
                _errorEvent.emit("아이템 추가에 실패했습니다. (네트워크 오류)")
                Log.e("ChecklistViewModel", "아이템 추가 실패", e)
            }
        }
    }

    fun toggleChecked(itemId: Int) {
        val itemToUpdate = _items.value.find { it.id == itemId } ?: return
        val newCheckedState = !itemToUpdate.isChecked
        _items.update { list ->
            list.map { if (it.id == itemId) it.copy(isChecked = newCheckedState) else it }
        }
        viewModelScope.launch {
            try {
                val response = apiService.updateChecklistItem(itemId.toLong(), newCheckedState)
                if (!response.isSuccessful) {
                    _items.update { list ->
                        list.map { if (it.id == itemId) it.copy(isChecked = !newCheckedState) else it }
                    }
                }
            } catch (e: Exception) {
                _items.update { list ->
                    list.map { if (it.id == itemId) it.copy(isChecked = !newCheckedState) else it }
                }
                Log.e("ChecklistViewModel", "체크 상태 업데이트 중 네트워크 오류", e)
            }
        }
    }

    fun removeCheckedItems() {
        val itemsToRemove = _items.value.filter { it.isChecked }
        if (itemsToRemove.isEmpty()) return
        val originalList = _items.value
        _items.update { currentList ->
            currentList.filter { !it.isChecked }
        }
        viewModelScope.launch {
            try {
                val deleteJobs = itemsToRemove.map { item ->
                    launch { apiService.deleteChecklistItem(item.id.toLong()) }
                }
                deleteJobs.joinAll()
            } catch (e: Exception) {
                _items.value = originalList
                Log.e("ChecklistViewModel", "체크된 아이템 삭제 실패", e)
            }
        }
    }
}

