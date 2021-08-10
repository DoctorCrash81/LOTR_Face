package com.wizl.beautyscanner.ui

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchasesResult
import com.facebook.appevents.AppEventsLogger
import com.wizl.beautyscanner.App
import com.wizl.beautyscanner.R
import com.wizl.beautyscanner.logick.UserPersisten
import com.wizl.beautyscanner.logick.analytics.AnalyticsService
import com.wizl.beautyscanner.logick.helpers.DisplayHelper
import kotlinx.android.synthetic.main.activity_paywall.*
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.set


class PaywallActivity : AppCompatActivity() {

    companion object {
        const val SKU_ID_1 = "greetify_week"
        const val SKU_ID_2 = "greetify_week_3d"
        const val IMAGE_MODE = "image_mode"
    }

    private lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paywall)

        // Меняем картинку
        val bSecondImage = intent.getBooleanExtra(IMAGE_MODE,false)

        //var bm = BitmapFactory.decodeResource(paywall_img1)
        var res = R.mipmap.paywall_img1
        if (bSecondImage) res = R.mipmap.paywall_img2

        _imgPaywall1.setImageResource(res)

        AnalyticsService.paywallViewed()

        _contTitle.layoutParams.height = DisplayHelper.widthPixels * 4 / 5

        _info.visibility = View.GONE
        _bt.visibility = View.GONE
        _btClouse.setOnClickListener {
            AnalyticsService.paywallClouseTap()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(MyPurchasesUpdatedListener())
            .build()

        billingClient.startConnection(MyBillingClientStateListener())
    }

    private fun launchBilling(billingClient: BillingClient, skuDetails: SkuDetails) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails)
            .build()
        billingClient.launchBillingFlow(this, billingFlowParams)
    }

    private fun querySkuDetails(billingClient: BillingClient) {
        val skuDetailsParamsBuilder = SkuDetailsParams.newBuilder()
        val skuList: MutableList<String> = ArrayList()
        skuList.add(SKU_ID_2)
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(
            skuDetailsParamsBuilder.build()
        ) { result, skuDetailsList ->
            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    if (skuDetailsList != null) {
                        val skuDetailsMap: HashMap<String, SkuDetails> = HashMap()
                        for (skuDetails in skuDetailsList) {
                            skuDetailsMap[skuDetails.sku] = skuDetails
                        }
                        updateSubs(skuDetailsMap)
                        _bt.setOnClickListener {
                            AnalyticsService.paywallSubsTap()
                            val skuDetails = skuDetailsMap[SKU_ID_2]
                            if (skuDetails != null) {
                                launchBilling(billingClient, skuDetails)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateSubs(skuDetailsMap: HashMap<String, SkuDetails>) {

        val skuDetails = skuDetailsMap[SKU_ID_2]
        if (skuDetails != null) {

            /*
            {
                "skuDetailsToken":"AEuhp4J7dQOFcprr-DK5DpdZ4sELes9u7NwoWRBGrZWheSYp2Drjw1cGqArUyqWFbAMA",
                "productId":"greetify_week_3d",
                "type":"subs",
                "price":"599,00 ₽",
                "price_amount_micros":599000000,
                "price_currency_code":"RUB",
                "subscriptionPeriod":"P1W",
                "introductoryPriceAmountMicros":59000000,
                "introductoryPricePeriod":"P3D",
                "introductoryPrice":"59,00 ₽",
                "introductoryPriceCycles":1,
                "title":"Weekly subscription (Greetify: Beauty Score)",
                "description":"Access to all features within 1 week"
                }
             */

            skuDetails.description
            skuDetails.originalJson



            _txt1.text = "${getString(R.string._3_days_free)} ${skuDetails.introductoryPrice}"
            _txt2.text =
                "${getString(R.string.then)} ${skuDetails.price} ${getString(R.string._week)}"

            _progress.visibility = View.GONE
            _info.visibility = View.VISIBLE
            _bt.visibility = View.VISIBLE

        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun queryPurchases(billingClient: BillingClient): List<Purchase?>? {
        val purchasesResult: PurchasesResult =
            billingClient.queryPurchases(BillingClient.SkuType.SUBS)
        return purchasesResult.purchasesList
    }

    private inner class MyPurchasesUpdatedListener : PurchasesUpdatedListener {
        override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    //сюда мы попадем когда будет осуществлена покупка

                    val purchase = purchases!![0]
                    AnalyticsService.paywallSubsOk(purchase.purchaseState)
                    AppEventsLogger
                        .newLogger(App.instance.applicationContext)
                        .logPurchase(BigDecimal(1), Currency.getInstance("RUB"))
                    UserPersisten.isPrem = true
                    setResult(Activity.RESULT_OK)

                    if (purchase.purchaseState === Purchase.PurchaseState.PURCHASED) {
                        if (!purchase.isAcknowledged) {
                            val acknowledgePurchaseParams =
                                AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.purchaseToken)
                                    .build()
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams) {
                                AnalyticsService.paywallSubsAcknowledgePurchase(it.responseCode)
                                finish()
                            }
                        } else {
                            finish()
                        }
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    private inner class MyBillingClientStateListener : BillingClientStateListener {
        override fun onBillingServiceDisconnected() {
            //сюда мы попадем если что-то пойдет не так
            AnalyticsService.paywallSubsError()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        override fun onBillingSetupFinished(result: BillingResult) {
            when (result.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    //здесь мы можем запросить информацию о товарах и покупках
                    querySkuDetails(billingClient) //запрос о товарах
                    val purchasesList: List<Purchase?>? =
                        queryPurchases(billingClient) //запрос о покупках
                    //если товар уже куплен, предоставить его пользователю
                    if (purchasesList != null) {
                        for (element in purchasesList) {
                            if (element != null) {
                                if (TextUtils.equals(SKU_ID_2, element.sku)) {
                                    UserPersisten.isPrem = true
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                } else {
                                    UserPersisten.isPrem = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}