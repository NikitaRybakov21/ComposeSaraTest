package com.example.composesaratest.uiMap

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.composesaratest.R
import com.yandex.mapkit.mapview.MapView

@Composable
fun MapLayout(mapViewImpl: InterfaceMapObjectImpl, viewModelMapView: ViewModelMapView) {

    val zoom by remember { mutableStateOf(14f) }

    val isMapStart = viewModelMapView.isStartMap.collectAsState()
    val textDetails = viewModelMapView.textDetails.collectAsState()
    val showDetails = viewModelMapView.showDetails.collectAsState()

    val listPointOrder by viewModelMapView.orderPoints.collectAsState()

    viewModelMapView.getPageOrderList()

    SetStateMap(listPointOrder,mapViewImpl,zoom)

    Column {
        Text(text = "Карта заказов", color = Color(167, 167, 167, 255), fontSize = 35.sp, modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, top = 4.dp))

        CardMap(mapViewImpl, listPointOrder.size, isMapStart)
        CardDetails(textDetails, showDetails, mapViewImpl)
    }
}

@Composable
fun SetStateMap(listPointOrder: List<MapObjectImpl.PointOrder>, mapViewImpl: InterfaceMapObjectImpl, zoom: Float) {
    listPointOrder.forEach {
            pointOrder -> mapViewImpl.createPlaceMark(pointOrder)
    }

    if(listPointOrder.isNotEmpty()) {
        mapViewImpl.cameraMapMove(listPointOrder, zoom)
    }
}

@Composable
fun CardDetails(textDetails: State<String>, showDetails: State<Boolean>, mapViewImpl: InterfaceMapObjectImpl) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp),

        backgroundColor = Color.White,
        elevation = 5.dp,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column {
            AnimatedVisibility(visible = showDetails.value) {
                Details(textDetails,mapViewImpl)
            }

            AnimatedVisibility(visible = !showDetails.value) {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "...", color = Color(110, 110, 110), fontSize = 30.sp, modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun Details(textDetails: State<String>, mapViewImpl: InterfaceMapObjectImpl) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(modifier = Modifier.padding(start = 12.dp)) {
            Image(
                painter = painterResource(id = R.drawable.pin_icon),
                contentDescription = "",
                modifier = Modifier
                    .size(width = 25.dp, height = 25.dp)
                    .padding(all = 2.dp)
            )
            Text(text = textDetails.value, color = Color(110,110,110), fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Row(modifier = Modifier.padding(start = 12.dp)) {
            Image(
                painter = painterResource(id = R.drawable.position),
                contentDescription = "",
                modifier = Modifier
                    .size(width = 25.dp, height = 25.dp)

                    .padding(all = 2.dp)
            )
            Text(text = "ваша локация", color = Color(110,110,110), fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Row {
            Button(
                onClick = {
                    mapViewImpl.showDrivingRoute()
                },
                modifier = Modifier.wrapContentWidth().padding(top = 10.dp, start = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(238, 255, 250, 255))
            ) {
                Text(text = "построить маршрут", color = Color(110,110,110), fontSize = 15.sp)
            }

            Button(
                onClick = {
                    mapViewImpl.drivingCancel()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 8.dp),
                   shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(Color.White)
            ) {
                Text(text = "отменить", color = Color(110,110,110), fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun CardMap(mapViewImpl: InterfaceMapObjectImpl, size: Int, isMapStart: State<Boolean>) {

    Card(modifier = Modifier
        .clickable { }
        .fillMaxWidth()
        .height(500.dp)
        .padding(horizontal = 8.dp),

        backgroundColor = Color.White,
        elevation = 5.dp,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),

            backgroundColor = Color.White,
            elevation = 5.dp,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            MapView(mapViewImpl.getMapView())
            OptionMap(mapViewImpl,size)

            if(!isMapStart.value) {
                LoadingMapAnimated()
            }
        }
    }
}

@Composable
fun OptionMap(mapViewImpl: InterfaceMapObjectImpl, size: Int) {
    val color by remember { mutableStateOf(Color.White) }

    Column {
        TextHeader("Активные заказы: $size")

        Card(
            modifier = Modifier
                .clickable {
                    mapViewImpl.showMyLocation()
                }
                .padding(start = 1.dp, top = 8.dp)
                .size(width = 40.dp, height = 40.dp),

            backgroundColor = color,
            shape = RoundedCornerShape(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_share_location_24),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.5f)
                    .padding(all = 2.dp)
            )
        }
    }
}

@Composable
fun LoadingMapAnimated() {
    val transition = rememberInfiniteTransition()
    val parameter = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000, easing = LinearEasing)),
    )
    LoadingMap(parameter.value)
}

@Composable
fun LoadingMap(value: Float) {

    Card(modifier = Modifier
        .fillMaxSize(),

        backgroundColor = Color.White,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Map loading...", color = Color(110,110,110), fontSize = 35.sp)

            Image(
                painter = painterResource(id = R.drawable.progressbar_map_loadig),
                contentDescription = "",
                modifier = Modifier
                    .padding(top = 10.dp)
                    .rotate(value)
                    .size(width = 80.dp, height = 80.dp)
            )
        }
    }
}

@Composable
fun MapView(mapView: MapView) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { mapView },
    )
}

@Composable
fun TextHeader(name: String) {
    Column(horizontalAlignment = Alignment.Start ) {
        Card(
            modifier = Modifier.wrapContentSize(),

            backgroundColor = Color.White,
            elevation = 5.dp,
            shape = RoundedCornerShape(36.dp)
        ) {
            Text(
                text = name,
                color = Color(110, 110, 110),
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, top = 4.dp, end = 16.dp)
            )
        }
    }
}