package com.florientmanfo.currencyapp.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.florientmanfo.currencyapp.domain.CurrencyApiService
import com.florientmanfo.currencyapp.domain.PreferencesRepository
import com.florientmanfo.currencyapp.domain.model.RateStatus
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private val _screenState: MutableState<HomeScreenState> = mutableStateOf(HomeScreenState())
    val screenState: State<HomeScreenState> = _screenState

    init {
        screenModelScope.launch {
            fetchNewRates()
            getRateStatus()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }
        }
    }

    private suspend fun fetchNewRates() {
        try {
            api.getLatestExchangeRate()
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private suspend fun getRateStatus() {
        val rateStatus = if (preferences.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        ) RateStatus.Fresh
        else RateStatus.Stale
        _screenState.value = _screenState.value
            .copy(rateStatus = rateStatus)
    }
}

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}