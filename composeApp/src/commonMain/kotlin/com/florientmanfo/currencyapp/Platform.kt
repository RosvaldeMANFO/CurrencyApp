package com.florientmanfo.currencyapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform