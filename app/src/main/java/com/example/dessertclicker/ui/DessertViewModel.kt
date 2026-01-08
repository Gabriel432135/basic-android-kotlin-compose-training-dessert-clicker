package com.example.dessertclicker.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dessertclicker.data.Datasource
import com.example.dessertclicker.model.Dessert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DessertViewModel : ViewModel() {
    //DessertUiState
    private val _uiState = MutableStateFlow(DessertUiState())
    private val desserts = Datasource.dessertList
    val uiState: StateFlow<DessertUiState> = _uiState.asStateFlow()

    init{
        resetGame()
    }

    fun resetGame(){
        val dessertToShow = determineDessertToShow(desserts, _uiState.value.dessertsSold)
        _uiState.value = DessertUiState(
            revenue = 0,
            dessertsSold = 0,
            currentDessertImageId = dessertToShow.imageId,
            currentDessertPrice = dessertToShow.price
        )
    }
    fun updateRevenue(){
        _uiState.update { currentState ->
            val revenue  = currentState.revenue + currentState.currentDessertPrice
            val dessertsSold = currentState.dessertsSold + 1
            val dessertToShow = determineDessertToShow(desserts, dessertsSold)
            currentState.copy(
                revenue = revenue,
                dessertsSold = dessertsSold,
                //mostrar a próxima sobremesa
                currentDessertIndex = currentState.currentDessertIndex + 1,
                currentDessertPrice = dessertToShow.price,
                currentDessertImageId = dessertToShow.imageId
            )
        }
    }

    fun determineDessertToShow(
        desserts: List<Dessert>,
        dessertsSold: Int
    ): Dessert {
        var dessertToShow = desserts.first()
        for (dessert in desserts) {
            if (dessertsSold >= dessert.startProductionAmount) {
                dessertToShow = dessert
            } else {
                // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
                // you'll start producing more expensive desserts as determined by startProductionAmount
                // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
                // than the amount sold.
                break
            }
        }

        return dessertToShow
    }

}