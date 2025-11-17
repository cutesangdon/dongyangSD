package com.example.campmate.data.model

//1030cdh
data class AdminZoneGroup(
    val name: String, // 캠핑장 이름
    val sites: List<Campsite> // 이 관리자에 속한 캠핑존(Campsite) 리스트
)
