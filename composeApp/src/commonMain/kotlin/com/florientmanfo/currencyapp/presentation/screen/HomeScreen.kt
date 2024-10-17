package com.florientmanfo.currencyapp.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.florientmanfo.currencyapp.domain.model.CurrencyType
import com.florientmanfo.currencyapp.presentation.component.CurrencyPickerDialog
import com.florientmanfo.currencyapp.presentation.component.HomeBody
import com.florientmanfo.currencyapp.presentation.component.HomeHeader

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val screenState by viewModel.screenState

        var dialogOpened by remember { mutableStateOf(false) }

        var selectedCurrencyType: CurrencyType by remember {
            mutableStateOf(CurrencyType.None)
        }

        if (dialogOpened && selectedCurrencyType != CurrencyType.None) {
            CurrencyPickerDialog(
                currencies = screenState.allCurrencies,
                onConfirmClick = { code ->
                    viewModel.sendEvent(
                        HomeUiEvent.SelectCurrency(
                            type = selectedCurrencyType,
                            code = code
                        )
                    )
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                },
                onDismiss = {
                    dialogOpened = false
                }
            )
        }

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
                    viewModel.sendEvent(
                        HomeUiEvent.SwitchCurrencies
                    )
                },
                onAmountChange = { amount ->
                    viewModel.sendEvent(
                        HomeUiEvent.ChangeAmount(amount)
                    )
                },
                onCurrencyTypeSelected = { currencyType ->
                    selectedCurrencyType = currencyType
                    dialogOpened = true
                }
            )
            HomeBody(
                source = screenState.sourceCurrency,
                target = screenState.targetCurrency,
                amount = screenState.amount
            )
        }
    }
}