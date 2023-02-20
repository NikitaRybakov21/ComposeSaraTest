package com.example.composesaratest.uiMap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.composesaratest.ui.theme.ComposeSaraTestTheme
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.mapview.MapView

class MainActivity : ComponentActivity() {

    private val mapView: MapView by lazy {
        MapView(this)
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        DirectionsFactory.initialize(this)

        val viewModelMapView = ViewModelMapView()
        val mapImpl = MapObjectImpl(mapView, this, viewModelMapView)

        setContent {
            ComposeSaraTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapLayout(mapImpl,viewModelMapView)
                }
            }
        }
    }
}

