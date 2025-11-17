// com.example.campmate.data.repository/CampsiteRepository.kt

package com.example.campmate.data

import com.example.campmate.data.model.Campsite
import com.example.campmate.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CampsiteRepository @Inject constructor(
    // Hilt를 사용하여 ApiService 인스턴스를 주입받습니다.
    private val apiService: ApiService,
) {

    /**
     * 캠핑장 검색 API를 호출합니다.
     * ViewModel에서 넘어온 검색어와 지역 필터를 백엔드에 전달합니다.
     * * @param keyword 검색어 (캠핑장 이름 등)
     * @param region 지역 필터 (예: "경기도"). null이면 지역 필터링 없음.
     * @return 검색 결과로 Campsite 객체의 리스트를 반환합니다.
     */
    suspend fun searchCampsites(keyword: String, region: String?): List<Campsite> {
        return try {
            // ApiService에 정의된 검색 함수 (GET /api/zones/search) 호출
            val response = apiService.searchCampsites(keyword, region)

            // 응답이 성공(2xx)이고 body가 null이 아닐 경우 데이터 반환
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                // HTTP 상태 코드가 4xx 또는 5xx 일 경우 (API 호출은 성공, 서버 처리 실패)
                // 필요하다면 여기서 로깅 (e.g., Log.e("Repo", "Search failed: ${response.code()}"))
                emptyList()
            }
        } catch (e: Exception) {
            // 네트워크 연결 실패, JSON 파싱 오류 등 예외 발생
            e.printStackTrace()
            emptyList()
        }
    }

    // 이외에도 getAllCampsites, getCampsiteDetail 등 다른 API 호출 함수들이 여기에 포함됩니다.

    // 예시: 상세 정보 가져오기
    suspend fun getCampsiteDetail(campsiteId: Long): Campsite? {
        // 이 부분은 예시이므로 필요에 따라 구현합니다.
        // val response = apiService.getCampsiteDetail(campsiteId)
        // return if (response.isSuccessful) response.body() else null
        return null // 실제 구현 시 수정 필요
    }
}