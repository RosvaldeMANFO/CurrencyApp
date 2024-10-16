package com.florientmanfo.currencyapp.di

import com.florientmanfo.currencyapp.data.local.MongoImpl
import com.florientmanfo.currencyapp.data.local.PreferencesImpl
import com.florientmanfo.currencyapp.data.remote.api.CurrencyApiServiceImpl
import com.florientmanfo.currencyapp.domain.CurrencyApiService
import com.florientmanfo.currencyapp.domain.MongoRepository
import com.florientmanfo.currencyapp.domain.PreferencesRepository
import com.florientmanfo.currencyapp.domain.usecase.FetchNewRatesUseCase
import com.florientmanfo.currencyapp.presentation.screen.HomeViewModel
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<MongoRepository> { MongoImpl() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
    single { FetchNewRatesUseCase(
        preferences = get(),
        mongoDb = get(),
        api = get())
    }
    factory {
        HomeViewModel(
            preferences = get(),
            fetchNewRatesUseCase = get()
        )
    }
}

fun initializeKoin(){
    startKoin {
        modules(appModule)
    }
}