package com.florientmanfo.currencyapp.di

import com.florientmanfo.currencyapp.data.local.PreferencesImpl
import com.florientmanfo.currencyapp.data.remote.api.CurrencyApiServiceImpl
import com.florientmanfo.currencyapp.domain.CurrencyApiService
import com.florientmanfo.currencyapp.domain.PreferencesRepository
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
}

fun initializeKoin(){
    startKoin {
        modules(appModule)
    }
}