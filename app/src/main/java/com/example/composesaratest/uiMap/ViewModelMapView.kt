package com.example.composesaratest.uiMap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModelMapView : ViewModel() , InterfaceViewModelMapView {

    private val listPointOrder = mutableListOf(
        MapObjectImpl.PointOrder(Point(55.751574, 37.573856), "ул. Лена д10 кв33"),
        MapObjectImpl.PointOrder(Point(55.771574, 37.563856), "ул. Оочееееень длииииное нааазваааниеее д10 кв33"),
        MapObjectImpl.PointOrder(Point(55.761574, 37.573856), "ул. Ленина д10 кв33"),
        MapObjectImpl.PointOrder(Point(55.651574, 37.773856), "ул. Петина д10 кв33"),
        MapObjectImpl.PointOrder(Point(55.651574, 37.473856), "ул. Калинина д10 кв33"),
        MapObjectImpl.PointOrder(Point(55.851574, 37.573856), "ул. Гагарина д10 кв33")
    )

    private val _orderPoints = MutableStateFlow(listOf<MapObjectImpl.PointOrder>())
    val orderPoints: StateFlow<List<MapObjectImpl.PointOrder>> get() = _orderPoints

    var textDetails = MutableStateFlow("...")
    var showDetails = MutableStateFlow(false)
    var isStartMap = MutableStateFlow(false)

    override fun setTextDetails(text : String) {
        textDetails.value = text
        showDetails.value = true
    }

    override fun hideDetails() {
        showDetails.value = false
    }

    fun getPageOrderList() {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) { Thread.sleep(6500) }
                isStartMap.value = true

                listPointOrder
            }.onSuccess {
                _orderPoints.emit(it)
            }.onFailure { }
        }
    }

}