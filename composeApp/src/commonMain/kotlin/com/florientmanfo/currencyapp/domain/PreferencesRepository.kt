package com.florientmanfo.currencyapp.domain

import com.florientmanfo.currencyapp.domain.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun saveLastUpdated(lastUpdated: String)
    suspend fun isDataFresh(currentTimestamp: Long): Flow<Boolean>
    suspend fun saveUsedCurrenciesCode(key: String, currencyCode: CurrencyCode)
    fun readUsedCurrenciesCode(): Flow<Map<String, CurrencyCode>>
}