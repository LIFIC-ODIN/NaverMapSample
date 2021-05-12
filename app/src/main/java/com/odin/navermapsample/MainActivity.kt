package com.odin.navermapsample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var naver: NaverMap
    private lateinit var mLocationSource: FusedLocationSource

    private val permissionReqCode = 100
    private val permissionList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setup()
        setupMap()
        setupListener()
    }

    private fun setup() {
        mLocationSource = FusedLocationSource(this, permissionReqCode)
    }

    private fun setupMap() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naver_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naver_map, it).commit()
            }

        mapFragment.getMapAsync(mapReadyCallback)
    }

    private fun setupListener() {
        btn_search1.setOnClickListener {
            updateCamera(37.5670135, 126.9783740,"짜장면시키신분?","40,000원")
        }

        btn_search2.setOnClickListener {
            updateCamera(37.5666102, 126.9783881,"미용실이에요","12,444원")
        }

        btn_search3.setOnClickListener {
            mLocationSource.lastLocation?.let { location ->
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                naver.moveCamera(cameraUpdate)
            }
        }
    }

    private fun updateCamera(lat: Double, lon: Double,title: String, price:String) {
        setInfoWindow(lat, lon,title,price, infoWindowClick)
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(lat, lon))
        naver.moveCamera(cameraUpdate)
    }

    private fun setMarker(lat: Double, lon: Double) {
        Marker().run {
            isIconPerspectiveEnabled = true
            icon = OverlayImage.fromResource(R.drawable.ic_launcher_background)
            alpha = 0.9f
            position = LatLng(lat, lon)
            zIndex = 10
            map = naver
        }
    }

    private fun setInfoWindow(lat: Double, lon: Double, title: String, price:String, click: Overlay.OnClickListener) {
        InfoWindow().run {
            adapter = object : InfoWindow.DefaultViewAdapter(this@MainActivity) {
                override fun getContentView(window: InfoWindow): View {
                    val view = View.inflate(this@MainActivity, R.layout.item_overay, null)
                    (view.findViewById<View>(R.id.tv_title) as TextView).text = title
                    (view.findViewById<View>(R.id.tv_price) as TextView).text = price
                    return view
                }
            }
            tag = "$title $price"
            zIndex = 10 // 우선순위
            alpha = 0.9f // 투명도
            position = LatLng(lat, lon)
            open(naver)
            onClickListener = click
        }
    }

    private val mapReadyCallback = OnMapReadyCallback {
        naver = it
        naver.locationSource = mLocationSource
        ActivityCompat.requestPermissions(this, permissionList, permissionReqCode)
    }

    private val infoWindowClick = Overlay.OnClickListener { overlay ->
        Toast.makeText(this@MainActivity, overlay.tag.toString(),Toast.LENGTH_SHORT).show()
        false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionReqCode) {
            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                naver.locationTrackingMode = LocationTrackingMode.Follow
            }
        }
    }
}