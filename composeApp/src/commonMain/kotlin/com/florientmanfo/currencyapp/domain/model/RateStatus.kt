package com.florientmanfo.currencyapp.domain.model

import androidx.compose.ui.graphics.Color
import com.florientmanfo.currencyapp.ui.theme.freshColor
import com.florientmanfo.currencyapp.ui.theme.staleColor

enum class RateStatus(
    val title: String,
    val color: Color
) {
    Idle(
        title = "Rates",
        color = Color.White
    ),
    Fresh(
        title = "Fresh rates",
        color = freshColor
    ),
    Stale(
        title = "Rates are not fresh",
        color = staleColor
    )
}