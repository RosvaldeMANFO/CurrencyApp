package com.florientmanfo.currencyapp.domain

import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.RequestState

interface CurrencyApiService {
    suspend fun getLatestExchangeRate(): RequestState<List<Currency>>
}