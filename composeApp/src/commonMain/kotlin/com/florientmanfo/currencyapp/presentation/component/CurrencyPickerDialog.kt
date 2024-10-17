package com.florientmanfo.currencyapp.presentation.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.CurrencyCode
import com.florientmanfo.currencyapp.domain.model.CurrencyType

@Composable
fun CurrencyPickerDialog(
    modifier: Modifier = Modifier,
    currencies: List<Currency>,
    onConfirmClick: (CurrencyCode) -> Unit,
    onDismiss: () -> Unit
) {
    val allCurrencies = remember {
        mutableStateListOf<Currency>().apply { addAll(currencies) }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCurrencyCode by remember {
        mutableStateOf(CurrencyCode.EUR)
    }

    AlertDialog(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        title = {
            Text(
                text = "Select a currency",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val size = LocalViewConfiguration.current.minimumTouchTargetSize
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(size = 99.dp)),
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query.uppercase()

                        if (query.isNotEmpty()) {
                            val filteredCurrencies = allCurrencies.filter {
                                it.code.contains(query.uppercase())
                            }
                            allCurrencies.clear()
                            allCurrencies.addAll(filteredCurrencies)
                        } else {
                            allCurrencies.clear()
                            allCurrencies.addAll(currencies)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search here",
                            color = Color.Unspecified.copy(alpha = 0.30F),
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1F),
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1F),
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1F),
                        errorContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1F),
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                AnimatedContent(
                    targetState = allCurrencies
                ) { availableCurrencies ->
                    if (availableCurrencies.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((size.height.value / 0.2).dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)

                        ) {
                            items(
                                items = availableCurrencies,
                                key = { it.code }
                            ) { currency ->
                                CurrencyCodePickerView(
                                    code = CurrencyCode.valueOf(currency.code),
                                    isSelected = selectedCurrencyCode.name == currency.code,
                                    onSelect = { selectedCurrencyCode = it }
                                )
                            }
                        }
                    } else {
                        ErrorScreen(modifier = Modifier.height((size.height.value / 0.75).dp))
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmClick(selectedCurrencyCode)
            }) {
                Text(
                    text = "Confirm",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}