package com.florientmanfo.currencyapp.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.CurrencyCode
import com.florientmanfo.currencyapp.domain.model.RequestState
import currencyapp.composeapp.generated.resources.Res
import currencyapp.composeapp.generated.resources.switch_ic
import org.jetbrains.compose.resources.painterResource


@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        CurrencyView(
            placeholder = "From",
            currency = source,
            onClick = {}
        )
        IconButton(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onSwitchClick
        ){
            Icon(
                painter = painterResource(Res.drawable.switch_ic),
                contentDescription = "Switch Icon",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        CurrencyView(
            placeholder = "To",
            currency = target,
            onClick = {}
        )
    }
}
@Composable
fun RowScope.CurrencyView(
    placeholder: String,
    currency: RequestState<Currency>,
    onClick: () -> Unit
){
    Column(
        modifier = Modifier.weight(1F),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            modifier = Modifier.padding(start= 12.dp),
            text = placeholder,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5F))
                .clip(RoundedCornerShape(size = 8.dp))
                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05F))
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if(currency.isSuccess()){
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(
                        CurrencyCode.valueOf(
                            currency.getSuccessData().code
                        ).flag
                    ),
                    tint = Color.Unspecified,
                    contentDescription = "Country Flag"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = CurrencyCode.valueOf(
                        currency.getSuccessData().code
                    ).name,
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}