package com.florientmanfo.currencyapp.data.remote.api

import com.florientmanfo.currencyapp.domain.CurrencyApiService
import com.florientmanfo.currencyapp.domain.model.ApiResponse
import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.RequestState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CurrencyApiService: CurrencyApiService {
    companion object{
        const val ENDPOINT = "cur_live_aIcTUtSqldqYspa0uBEBhJsHlDrhswDXwZniYRrJ"
        const val API_KEY = "https://api.currencyapi.com/v3/latest"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation){
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(DefaultRequest){
            headers {
                append("apikey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRate(): RequestState<List<Currency>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            if(response.status.value == 200){
                val apiResponse = Json.decodeFromString<ApiResponse>(response.body())
                RequestState.Success(data = apiResponse.data.values.toList())
            } else {
                throw Exception("HTTP error code: ${response.status}")
            }
        } catch (e: Exception){
            RequestState.Error(message = e.message.toString())
        }
    }
}