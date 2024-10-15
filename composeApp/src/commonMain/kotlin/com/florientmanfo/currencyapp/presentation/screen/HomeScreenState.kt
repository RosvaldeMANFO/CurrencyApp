package com.florientmanfo.currencyapp.presentation.screen

import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.RateStatus
import com.florientmanfo.currencyapp.domain.model.RequestState

data class HomeScreenState(
    val rateStatus: RateStatus = RateStatus.Idle,
    val sourceCurrency: RequestState<Currency> = RequestState.Idle,
    val targetCurrency: RequestState<Currency> = RequestState.Idle,
    val amount: Double = 0.0
)