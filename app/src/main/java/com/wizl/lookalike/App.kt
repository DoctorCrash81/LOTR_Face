package com.wizl.lookalike

import android.app.Application
import android.text.TextUtils
import com.android.billingclient.api.*
import com.wizl.lookalike.logick.UserPersisten
import com.wizl.lookalike.logick.analytics.AnalyticsService
import com.wizl.lookalike.logick.net.Network
import com.wizl.lookalike.ui.PaywallActivity

class App : Application() {

    private val mNetwork = Network.instance
    val language: String
        get() {
            return if (
                applicationContext.resources.configuration.locale.language == "ru" ||
                applicationContext.resources.configuration.locale.language == "ua" ||
                applicationContext.resources.configuration.locale.language == "by" ||
                applicationContext.resources.configuration.locale.language == "kz"
            ) "ru"
            else "en"
        }

    object Holder {
        lateinit var INSTANCE: App
    }

    companion object {
        val instance: App by lazy { Holder.INSTANCE }
    }

    init {
        Holder.INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()
        mNetwork.initApi()

        AnalyticsService.initAnalytics(this)

        checkSubs()

    }

    private fun checkSubs() {
        // Проверяем подписку (на случай если ее отменили)
        val billingClient =
            BillingClient.newBuilder(baseContext).enablePendingPurchases().setListener(
                PurchasesUpdatedListener() { result, purchases ->
                    when (result.responseCode) {
                        BillingClient.BillingResponseCode.OK -> {
                            //сюда мы попадем когда будет осуществлена покупка
                        }
                    }
                }).build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}

            override fun onBillingSetupFinished(result: BillingResult) {
                when (result.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        val purchasesList: List<Purchase?>? =
                            billingClient.queryPurchases(BillingClient.SkuType.SUBS).purchasesList
                        //если товар уже куплен, предоставить его пользователю
                        if (purchasesList != null) {
                            for (element in purchasesList) {
                                if (element != null) {
                                    UserPersisten.isPrem =
                                        TextUtils.equals(PaywallActivity.SKU_ID_2, element.sku)
                                    if (UserPersisten.isPrem) {
                                        // Проверяем подтверждение
                                        if (!element.isAcknowledged) {
                                            val acknowledgePurchaseParams =
                                                AcknowledgePurchaseParams.newBuilder()
                                                    .setPurchaseToken(element.purchaseToken)
                                                    .build()
                                            billingClient.acknowledgePurchase(
                                                acknowledgePurchaseParams
                                            ) {
                                                AnalyticsService.appSubsAcknowledgePurchase(it.responseCode)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }

}