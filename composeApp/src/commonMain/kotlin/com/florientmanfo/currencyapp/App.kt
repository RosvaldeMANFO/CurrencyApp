package com.florientmanfo.currencyapp

import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import com.florientmanfo.currencyapp.di.initializeKoin
import com.florientmanfo.currencyapp.presentation.screen.HomeScreen
import com.florientmanfo.currencyapp.ui.theme.CurrencyAppThem
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    initializeKoin()

    CurrencyAppThem {
       Navigator(HomeScreen())
    }
}