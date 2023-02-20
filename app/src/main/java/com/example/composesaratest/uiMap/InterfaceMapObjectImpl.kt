package com.example.composesaratest.uiMap

import android.content.Context
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.mapview.MapView

interface InterfaceMapObjectImpl {
    fun showMyLocation()
    fun showDrivingRoute()
    fun drivingCancel()
    fun createPlaceMark(pointOrder: MapObjectImpl.PointOrder)
    fun cameraMapMove(pointList: List<MapObjectImpl.PointOrder>, zoom: Float)
    fun getMapView() : MapView
}