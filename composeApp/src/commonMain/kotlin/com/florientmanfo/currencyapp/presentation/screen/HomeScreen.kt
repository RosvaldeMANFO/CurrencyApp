package com.florientmanfo.currencyapp.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.florientmanfo.currencyapp.presentation.component.HomeHeader

class HomeScreen: Screen {

    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val screenState by viewModel.screenState

       Column(
           modifier = Modifier
               .fillMaxSize()
       ) {
           HomeHeader(
               status = screenState.rateStatus,
               source = screenState.sourceCurrency,
               target = screenState.targetCurrency,
               amount = screenState.amount,
               onRateRefresh = {
                   viewModel.sendEvent(
                       HomeUiEvent.RefreshRates
                   )
               },
               onSwitchClick = {

               },
               onAmountChange = {

               }
           )
       }
    }
}