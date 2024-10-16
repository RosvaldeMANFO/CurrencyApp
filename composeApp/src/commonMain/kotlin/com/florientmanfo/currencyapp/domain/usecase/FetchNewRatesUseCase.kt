package com.florientmanfo.currencyapp.domain.usecase

import com.florientmanfo.currencyapp.domain.CurrencyApiService
import com.florientmanfo.currencyapp.domain.MongoRepository
import com.florientmanfo.currencyapp.domain.PreferencesRepository
import com.florientmanfo.currencyapp.domain.model.Currency
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock

class FetchNewRatesUseCase(
    private val preferences: PreferencesRepository,
    private val mongoDb: MongoRepository,
    private val api: CurrencyApiService,
) {

    suspend operator fun invoke(): List<Currency> {
        return try {
            val currencies = mutableListOf<Currency>()
            val localCache = mongoDb.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    val isFresh = preferences.isDataFresh(
                        Clock.System.now()
                            .toEpochMilliseconds()
                    ).first()
                    if (!isFresh) {
                        fetchData()
                    }
                    currencies.addAll(localCache.getSuccessData().map {
                        Currency(it.code, it.value)
                    })
                } else {
                    fetchData()
                }
            } else if (localCache.isError()) {
                throw Exception("FetchNewRatesUseCase: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
            }
            currencies
        } catch (e: Exception) {
            println(e.message)
            listOf()
        }
    }

    private suspend fun fetchData() {
        val fetchedData = api.getLatestExchangeRate()
        if (fetchedData.isSuccess()) {
            mongoDb.cleanUp()
            fetchedData.getSuccessData().forEach {
                mongoDb.insertCurrencyData(it)
            }
        } else if (fetchedData.isError()) {
            throw Exception("FetchNewRatesUseCase: FETCHING FAILED ${fetchedData.getErrorMessage()}")
        }
    }

}