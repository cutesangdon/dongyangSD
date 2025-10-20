package com.example.campmate.ui.checklist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campmate.data.model.ChecklistItem
import com.example.campmate.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _items = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val items: StateFlow<List<ChecklistItem>> = _items.asStateFlow()

    private val _presets = MutableStateFlow<Map<String, List<String>>>(emptyMap())
    val presets: StateFlow<Map<String, List<String>>> = _presets.asStateFlow()

    private val _isLoadingPresets = MutableStateFlow(false)
    val isLoadingPresets: StateFlow<Boolean> = _isLoadingPresets.asStateFlow()

    init {
        fetchPresets()
        // TODO: 로그인된 사용자의 체크리스트를 서버에서 불러오는 로직(/api/checklist/getChecklist/{customerId}) 추가 필요
    }

    private fun fetchPresets() {
        viewModelScope.launch {
            _isLoadingPresets.value = true
            Log.d("ChecklistViewModel", "프리셋 데이터 로딩 시작...")
            try {
                val response = apiService.getChecklistPresets()
                if (response.isSuccessful && response.body() != null) {
                    val presetList = response.body()!!
                    Log.d("ChecklistViewModel", "프리셋 데이터 수신 성공: ${presetList.size}개 아이템")

                    if (presetList.isEmpty()) {
                        Log.w("ChecklistViewModel", "경고: 서버에서 받은 프리셋 데이터가 비어있습니다. DB를 확인하세요.")
                    }

                    // ✅✅✅ [핵심 수정] 서버가 보내준 List를 UI에서 사용하기 좋은 Map 형태로 가공합니다. ✅✅✅
                    _presets.value = presetList
                        .groupBy(
                            keySelector = { it.category }, // "basic", "cooking" 등으로 그룹화
                            valueTransform = { it.itemName } // 각 그룹에는 아이템 이름만 포함
                        )

                } else {
                    Log.e("ChecklistViewModel", "프리셋 로딩 실패: 서버 응답 코드 ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ChecklistViewModel", "프리셋 로딩 중 네트워크 오류 발생", e)
            } finally {
                _isLoadingPresets.value = false
                Log.d("ChecklistViewModel", "프리셋 데이터 로딩 종료.")
            }
        }
    }

    fun addPresetItem(itemName: String) {
        if (_items.value.any { it.text.equals(itemName, ignoreCase = true) }) {
            return
        }

        viewModelScope.launch {
            try {
                // TODO: 실제 로그인된 사용자 ID로 교체해야 합니다. 현재는 1L로 하드코딩.
                val customerId = 1L
                val response = apiService.addChecklistItem(customerId, itemName)

                if (response.isSuccessful && response.body() != null) {
                    val newItemResponse = response.body()!!
                    val newChecklistItem = ChecklistItem(
                        id = newItemResponse.id.toInt(),
                        text = newItemResponse.itemName,
                        isChecked = newItemResponse.isChecked
                    )
                    _items.update { currentList -> currentList + newChecklistItem }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addItem(text: String) {
        // TODO: 서버에 아이템을 추가하는 API(/api/checklist/getAddItem/{customerId}) 호출 로직으로 변경 필요
        if (text.isNotBlank()) {
            val newItem = ChecklistItem(id = (_items.value.maxOfOrNull { it.id } ?: 0) + 1, text = text)
            _items.update { currentList -> currentList + newItem }
        }
    }

    fun toggleChecked(itemId: Int) {
        // TODO: 서버에 체크 상태를 업데이트하는 API(/api/checklist/{itemId}) 호출 로직으로 변경 필요
        _items.update { currentList ->
            currentList.map { item ->
                if (item.id == itemId) item.copy(isChecked = !item.isChecked) else item
            }
        }
    }

    fun removeCheckedItems() {
        // TODO: 서버에서 체크된 아이템을 삭제하는 API(/api/checklist/{itemId}) 호출 로직으로 변경 필요
        _items.update { currentList ->
            currentList.filter { !it.isChecked }
        }
    }
}

