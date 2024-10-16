package com.florientmanfo.currencyapp.domain

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun saveLastUpdated(lastUpdated: String)
    suspend fun isDataFresh(currentTimestamp: Long): Flow<Boolean>
}