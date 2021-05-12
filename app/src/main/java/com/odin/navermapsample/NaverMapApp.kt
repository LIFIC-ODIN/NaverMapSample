package com.odin.navermapsample

import android.app.Application
import com.naver.maps.map.NaverMapSdk

class NaverMapApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(getString(R.string.CLIENT_ID))
    }
}