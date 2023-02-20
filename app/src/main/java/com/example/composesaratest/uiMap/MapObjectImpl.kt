package com.example.composesaratest.uiMap

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.composesaratest.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.*
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import com.yandex.runtime.ui_view.ViewProvider

class MapObjectImpl(
    private val mapView: MapView,
    private val mainActivity: MainActivity,
    viewModelMapView: ViewModelMapView
) : InterfaceMapObjectImpl{

    private var mapObjects = mapView.map.mapObjects.addCollection()
    private var mapObjectSave : PolylineMapObject? = null

    private val drivingRouter: DrivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
    private var drivingSession: DrivingSession? = null

    private val drivingListener = object : DrivingRouteListener {

        override fun onDrivingRoutes(routes: List<DrivingRoute>) {
            for (route in routes) {
                mapObjectSave = mapObjects.addPolyline(route.geometry)
            }
        }

        override fun onDrivingRoutesError(error: Error) {
            var errorMessage: String? = "unknown error"
            if (error is RemoteError) {
                errorMessage = "remote error"
            } else if (error is NetworkError) {
                errorMessage = "network_error"
            }
            Toast.makeText(mainActivity, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitRequest(startPoint : Point, endPoint : Point) {
        drivingCancel()

        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()

        drivingOptions.routesCount = 1

        val requestPoints = java.util.ArrayList<RequestPoint>()
        requestPoints.add(
            RequestPoint(startPoint, RequestPointType.WAYPOINT, null)
        )
        requestPoints.add(
            RequestPoint(endPoint, RequestPointType.WAYPOINT, null)
        )

        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, drivingListener)
    }

    override fun drivingCancel() {
        mapObjectSave?.let {
            mapObjects.remove(it)
            mapObjectSave = null
        }
        drivingSession?.cancel()
    }

    private fun drivingRoute(myPoint: Point) {

        val screenCenter =  Point((myPoint.latitude + selectGeometryPoint.latitude) / 2, (myPoint.longitude + selectGeometryPoint.longitude) / 2)

        mapView.map.move(
            CameraPosition(screenCenter, 10f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
        submitRequest(myPoint, selectGeometryPoint)
    }

    data class PointOrder(val point: Point, val order: String)

    override fun getMapView() = mapView

    private val permissionLauncher = mainActivity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { requestLocation() }
    private fun requestLocationPermissions() = permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

    private var isDriving : Boolean = false

    override fun showMyLocation() {
        isDriving = false
        requestLocation()
    }

    override fun showDrivingRoute() {
        isDriving = true
        requestLocation()
    }

    private fun requestLocation() {
        if(ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions()
        } else {
            val locationManager = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000f,  LocalListener(mainActivity))

            testLocation()
        }
    }

    private fun testLocation() {
        val point = Point(55.6435,37.7274)

        createMyPlaceMark(point)

        Toast.makeText(mainActivity,"update ${point.latitude.toFloat()}  /  ${point.longitude.toFloat()}", Toast.LENGTH_SHORT).show()

        if(isDriving) {
            drivingRoute(point)
        }
    }

    inner class LocalListener(private val context: Context) : LocationListener {

        override fun onLocationChanged(location: Location) {

      /*    val point = Point(location.latitude,location.longitude)

            createMyPlaceMark(point)

            Toast.makeText(context,"update ${location.latitude.toFloat()}  /  ${location.longitude.toFloat()}", Toast.LENGTH_SHORT).show()

            if(isDriving) {
                drivingRoute(point)
            }                                        */
        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(context,"ERROR", Toast.LENGTH_SHORT).show()
        }
        override fun onProviderEnabled(provider: String) {}
    }

    private var saveMark : PlacemarkMapObject = mapView.map.mapObjects.addPlacemark(Point(0.0,0.0))

    private fun createMyPlaceMark(pointLocation: Point) {

        mapView.map.mapObjects.remove(saveMark)

        val mark: PlacemarkMapObject = mapView.map.mapObjects.addPlacemark(pointLocation)
        mark.setIcon(ImageProvider.fromResource(mapView.context, R.drawable.your_loc))
        mark.isDraggable = true
        mark.userData = "yourLocation"

        saveMark = mark

        cameraMapMove(pointLocation)
    }

    private fun cameraMapMove(pointLocation: Point) {
        mapView.map.move(
            CameraPosition(pointLocation, 14f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
    }

    override fun createPlaceMark(pointOrder: PointOrder) {
        val placeMark = PlaceMarkView(mapView.context, text = pointOrder.order)

        mapView.map.mapObjects.addPlacemark(pointOrder.point, ViewProvider(placeMark))

        val mark: PlacemarkMapObject = mapView.map.mapObjects.addPlacemark(pointOrder.point)
        mark.setIcon(ImageProvider.fromResource(mapView.context, R.drawable.map_marker_icon))
        mark.isDraggable = true

        mark.addTapListener(mapObjectTapListener)
        mark.userData = pointOrder.order

        listPlaceMark.add(mark)
    }

    override fun cameraMapMove(pointList: List<PointOrder>, zoom: Float) {
        mapView.map.move(
            CameraPosition(pointList[0].point, zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
    }

    private val listPlaceMark = ArrayList<PlacemarkMapObject>()

    private var selectGeometryPoint : Point = Point(0.0,0.0)

    private val mapObjectTapListener = MapObjectTapListener { mapObject, _ ->
        if (mapObject is PlacemarkMapObject) {

            listPlaceMark.forEach { placeMarkMapObject ->
                placeMarkMapObject.setIcon(
                    ImageProvider.fromResource(mainActivity,
                    R.drawable.map_marker_icon
                ))
            }

            mapObject.setIcon(ImageProvider.fromResource(mainActivity, R.drawable.map_marker_icon_select))

            selectGeometryPoint = mapObject.geometry

            val userData = mapObject.userData
            viewModelMapView.setTextDetails(userData as String)
        }
        true
    }
}



