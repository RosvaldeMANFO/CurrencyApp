package com.florientmanfo.currencyapp.presentation.screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.florientmanfo.currencyapp.data.local.PreferencesImpl
import com.florientmanfo.currencyapp.domain.PreferencesRepository
import com.florientmanfo.currencyapp.domain.model.CurrencyCode
import com.florientmanfo.currencyapp.domain.model.CurrencyType
import com.florientmanfo.currencyapp.domain.model.RateStatus
import com.florientmanfo.currencyapp.domain.model.RequestState
import com.florientmanfo.currencyapp.domain.usecase.FetchNewRatesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
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
            getRateStatus()
            readUsedCurrencies()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }

            is HomeUiEvent.ChangeAmount -> {
                _screenState.value = _screenState.value.copy(
                    amount = event.value
                )
            }

            HomeUiEvent.SwitchCurrencies -> {
                switchCurrencies()
            }

            is HomeUiEvent.SelectCurrency -> {
                when (event.type) {
                    CurrencyType.None -> return
                    CurrencyType.Source -> {
                        saveCurrency(
                            key = PreferencesImpl.SOURCE_CURRENCY_KEY,
                            currencyCode = event.code
                        )
                    }
                    CurrencyType.Target -> {
                        saveCurrency(
                            key = PreferencesImpl.TARGET_CURRENCY_KEY,
                            currencyCode = event.code
                        )
                    }
                }
            }
        }
    }

    private fun saveCurrency(key: String, currencyCode: CurrencyCode) {
        screenModelScope.launch(Dispatchers.IO) {
            preferences.saveUsedCurrenciesCode(key, currencyCode)
        }
    }

    private fun switchCurrencies() {
        _screenState.value = _screenState.value.copy(
            targetCurrency = _screenState.value.sourceCurrency,
            sourceCurrency = _screenState.value.targetCurrency
        )
    }

    private suspend fun fetchNewRates() {
        fetchNewRatesUseCase().also { currencies ->
            _screenState.value = _screenState.value.copy(allCurrencies = currencies)
        }
    }

    private suspend fun getRateStatus() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            ).collectLatest { value ->
                _screenState.value = _screenState.value
                    .copy(
                        rateStatus = if (value) RateStatus.Fresh else RateStatus.Stale
                    )
            }
        }
    }

    private suspend fun readUsedCurrencies() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readUsedCurrenciesCode().collectLatest { currencies ->
                val source = _screenState.value.allCurrencies
                    .find { it.code == currencies[PreferencesImpl.SOURCE_CURRENCY_KEY]?.name }
                val target = _screenState.value.allCurrencies
                    .find { it.code == currencies[PreferencesImpl.TARGET_CURRENCY_KEY]?.name }

                if (source != null && target != null) {
                    _screenState.value = _screenState.value.copy(
                        sourceCurrency = RequestState.Success(data = source),
                        targetCurrency = RequestState.Success(data = target)
                    )
                }
            }
        }
    }
}

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
    data class ChangeAmount(val value: Double) : HomeUiEvent()
    data object SwitchCurrencies : HomeUiEvent()
    data class SelectCurrency(val type: CurrencyType, val code: CurrencyCode) : HomeUiEvent()
}