package com.florientmanfo.currencyapp.data.local

import com.florientmanfo.currencyapp.data.local.entity.CurrencyEntity
import com.florientmanfo.currencyapp.domain.MongoRepository
import com.florientmanfo.currencyapp.domain.model.Currency
import com.florientmanfo.currencyapp.domain.model.RequestState
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoImpl : MongoRepository {

    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(CurrencyEntity::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    override suspend fun insertCurrencyData(currency: Currency) {
        realm?.writeBlocking{
            copyToRealm(
                CurrencyEntity().apply {
                    code = currency.code
                    value = currency.value
                }
            )
        }
    }

    override fun readCurrencyData(): Flow<RequestState<List<Currency>>> {
        return realm?.query<CurrencyEntity>()
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(
                    data = result.list.map {
                        Currency(it.code, it.value)
                    }
                )
            }
            ?: flow { RequestState.Error(message = "Realm not configured.") }
    }

    override suspend fun cleanUp() {
        realm?.write {
            val currencyCollection = this.query<CurrencyEntity>()
            delete(currencyCollection)
        }
    }
}