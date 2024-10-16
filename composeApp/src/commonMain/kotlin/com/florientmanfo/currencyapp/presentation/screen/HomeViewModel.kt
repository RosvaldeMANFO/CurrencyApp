package com.florientmanfo.currencyapp.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.florientmanfo.currencyapp.domain.PreferencesRepository
import com.florientmanfo.currencyapp.domain.model.RateStatus
import com.florientmanfo.currencyapp.domain.usecase.FetchNewRatesUseCase
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val fetchNewRatesUseCase: FetchNewRatesUseCase
) : ScreenModel {
    private val _screenState: MutableState<HomeScreenState> = mutableStateOf(HomeScreenState())
    val screenState: State<HomeScreenState> = _screenState

    init {
        screenModelScope.launch {
            fetchNewRates()
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
        fetchNewRatesUseCase().also { currencies ->
            _screenState.value = _screenState.value.copy(allCurrencies = currencies)
            getRateStatus()
        }
    }

    private suspend fun getRateStatus() {
        preferences.isDataFresh(
            currentTimestamp = Clock.System.now().toEpochMilliseconds()
        ).collect { value ->
            _screenState.value = _screenState.value
                .copy(
                    rateStatus = if (value) RateStatus.Fresh else RateStatus.Stale
                )
        }
    }
}

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
}