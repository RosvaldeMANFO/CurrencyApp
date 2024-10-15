package com.florientmanfo.currencyapp.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.RateStatus
import com.florientmanfo.currencyapp.domain.model.RequestState
import com.florientmanfo.currencyapp.ui.theme.staleColor
import com.florientmanfo.currencyapp.util.displayCurrentDateTime
import currencyapp.composeapp.generated.resources.Res
import currencyapp.composeapp.generated.resources.exchange_illustration
import currencyapp.composeapp.generated.resources.refresh_ic
import org.jetbrains.compose.resources.painterResource


@Composable
fun HomeHeader(
    status: RateStatus,
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    amount: Double,
    onAmountChange: (Double) -> Unit,
    onRateRefresh: () -> Unit,
    onSwitchClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(all = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ){
        RateStatus(
            status,
            onRateRefresh
        )
        CurrencyInputs(
            source = source,
            target = target,
            onSwitchClick = onSwitchClick
        )
        AmountInput(
            amount = amount,
            onAmountChange = onAmountChange
        )
    }
}

@Composable
fun RateStatus(
    status: RateStatus,
    onRateRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(Res.drawable.exchange_illustration),
                contentDescription = "Exchange Rate Illustration"
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = displayCurrentDateTime(),
                    color = Color.White
                )
                Text(
                    text = status.title,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize,
                    color = status.color
                )
            }
        }
        if(status == RateStatus.Stale){
            IconButton(onClick = onRateRefresh){
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.refresh_ic),
                    contentDescription = "Refresh Icon",
                    tint = staleColor
                )
            }
        }
    }
}