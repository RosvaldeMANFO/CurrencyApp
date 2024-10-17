package com.florientmanfo.currencyapp.data.local

import com.florientmanfo.currencyapp.domain.PreferencesRepository
import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.CurrencyCode
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class PreferencesImpl(
    private val settings: Settings
) : PreferencesRepository {
    companion object {
        const val TIMESTAMP_KEY = "lastUpdated"
        const val SOURCE_CURRENCY_KEY = "sourceCurrency"
        const val TARGET_CURRENCY_KEY = "targetCurrency"

        val DEFAULT_SOURCE_CURRENCY = CurrencyCode.EUR.name
        val DEFAULT_TARGET_CURRENCY = CurrencyCode.USD.name

    }

    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()

    override suspend fun saveLastUpdated(lastUpdated: String) {
        flowSettings.putLong(
            key = TIMESTAMP_KEY,
            value = Instant.parse(lastUpdated).toEpochMilliseconds()
        )
    }

    override suspend fun isDataFresh(currentTimestamp: Long): Flow<Boolean> {
       return  flowSettings.getLongFlow(
            key = TIMESTAMP_KEY,
            defaultValue = 0L
        ).map { value ->
           if (value != 0L) {
               val currentInstant = Instant.fromEpochMilliseconds(currentTimestamp)
               val savedInstant = Instant.fromEpochMilliseconds(value)

               val currentDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
               val savedDateTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())

               val dayDifference = currentDateTime.date.dayOfYear - savedDateTime.date.dayOfYear

               dayDifference <= 1
           } else false
       }
    }

    override suspend fun saveUsedCurrenciesCode(key: String, currencyCode: CurrencyCode) {
        flowSettings.putString(
            key = key,
            value = currencyCode.name
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun readUsedCurrenciesCode(): Flow<Map<String, CurrencyCode>> {
        return flowSettings.getStringFlow(
            key = SOURCE_CURRENCY_KEY,
            defaultValue = DEFAULT_SOURCE_CURRENCY
        ).flatMapLatest { source ->
            val sourceCurrency = CurrencyCode.valueOf(source)
            flowSettings.getStringFlow(
                key = TARGET_CURRENCY_KEY,
                defaultValue = DEFAULT_TARGET_CURRENCY
            ).map { target ->
                val targetCurrency = CurrencyCode.valueOf(target)
                mapOf(
                    SOURCE_CURRENCY_KEY to sourceCurrency,
                    TARGET_CURRENCY_KEY to targetCurrency
                )
            }
        }
    }
}