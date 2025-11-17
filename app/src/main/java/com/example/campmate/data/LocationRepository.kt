package com.example.campmate.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationClient: FusedLocationProviderClient
) {
    // 현재 위치를 한 번만 가져오는 간단한 suspend 함수 (권한은 이미 체크되었다고 가정)
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        // (실제로는 복잡한 콜백을 코루틴으로 변환하는 로직이 필요하지만,
        //  간단한 예시를 위해 마지막 위치를 가져오는 방식을 사용합니다.
        //  더 정확한 방식은 getCurrentLocation(Priority, CancellationToken) 사용을 권장합니다.)
        return locationClient.lastLocation.await() // .await()는 kotlinx-coroutines-play-services 필요
        // 또는 콜백 기반의 getCurrentLocation 사용
    }
}