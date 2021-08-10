package com.wizl.lookalike.logick.analytics

import android.os.Bundle
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.wizl.lookalike.App
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import kotlin.collections.HashMap

class AnalyticsService {

    companion object {

        private const val AMPLITUDE_API_KEY = "986d62d91da577d82a351eab6f103d97"
        private const val YANDEX_ANALYTICS_API_KEY = "0bbd0d40-40a3-47d1-8720-e644747741a0"

        fun initAnalytics(app: App) {

            // Creating an extended library configuration.
            val config: YandexMetricaConfig =
                YandexMetricaConfig.newConfigBuilder(YANDEX_ANALYTICS_API_KEY).build()
            // Initializing the AppMetrica SDK.
            YandexMetrica.activate(app.applicationContext, config)
            // Automatic tracking of user activity.
            YandexMetrica.enableActivityAutoTracking(app)

            // Initialize
//            Amplitude.getInstance().initialize(app.applicationContext, AMPLITUDE_API_KEY)
            // Enable COPPA (Turning off sensitive data tracking)
//            Amplitude.getInstance().enableCoppaControl()

            FacebookSdk.sdkInitialize(app.applicationContext)
            FacebookSdk.setAutoLogAppEventsEnabled(true)
            FacebookSdk.setAutoInitEnabled(true)
            FacebookSdk.setAdvertiserIDCollectionEnabled(true)
            FacebookSdk.fullyInitialize()

        }

        fun start(session: Long) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["num"] = session
            send("start", eventAttributes)
        }

        private fun send(event: String) {
//            Amplitude.getInstance().logEvent(event)
            YandexMetrica.reportEvent(event)
            AppEventsLogger
                .newLogger(App.instance.applicationContext)
                .logEvent(event)
        }

        private fun send(event: String, eventAttr: Map<String, Any>) {
//            Amplitude.getInstance().logEvent(event, JSONObject(eventAttr))
            YandexMetrica.reportEvent(event, eventAttr)

            AppEventsLogger
                .newLogger(App.instance.applicationContext)
                .logEvent(event, mapToBundle(eventAttr))
        }

        private fun mapToBundle(data: Map<String, Any>): Bundle {
            val bundle = Bundle()
            for ((key, value) in data) {
                when (value) {
                    is String -> bundle.putString(
                        key,
                        value as String?
                    )
                    is Double -> {
                        bundle.putDouble(key, (value as Double?)!!)
                    }
                    is Int -> {
                        bundle.putInt(key, (value as Int?)!!)
                    }
                    is Float -> {
                        bundle.putFloat(key, (value as Float?)!!)
                    }
                }
            }
            return bundle
        }

        fun galleryTap() {
            send("[gallery] gallery_tap")
        }

        fun galleryStartGenderSelect(){
            send("[gallery] gallery_start_gender_select")
        }

        fun galleryStartIntent() {
            send("[gallery] gallery_start_intent")
        }

        fun galleryResultOk() {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["result"] = "ok"
            send("[gallery] gallery_result", eventAttributes)
        }

        fun galleryResultCanceled() {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["result"] = "canceled"
            send("[gallery] gallery_result", eventAttributes)
        }

        fun galleryResultError() {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["result"] = "error"
            send("[gallery] gallery_result", eventAttributes)
        }

        fun galleryPhotoPicked() {
            send("[gallery] photo_picked")
        }

        fun galleryViewed() {
            send("[gallery] viewed")
        }

        fun loadViewed() {
            send("[load] viewed")
        }

        fun loadGalleryTap() {
            send("[load] gallery_tap")
        }

        fun loadTryAgainTap() {
            send("[load] try_again_tap")
        }

        fun loadError(message: String?) {
            if (message != null) {
                val eventAttributes = HashMap<String, Any>()
                eventAttributes["mess"] = message
                send("[load] request_error", eventAttributes)
            } else {
                send("[load] request_error")
            }
        }

        fun serverError(s: String) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["mess"] = s
            send("[load] request_failed", eventAttributes)
        }

        fun loadSavedPrevie() {
            send("[load] save_previe")
        }

        fun loadResultOk(score: Float) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["score"] = score
            send("[load] server_result", eventAttributes)
        }

        fun loadsavedResult() {
            send("[load] server result")
        }

        fun resultViewed(score: Float) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["score"] = score
            send("[result] server_viewed", eventAttributes)
        }

        fun resultGalleryTap(score: Float) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["score"] = score
            send("[result] gallery_tap", eventAttributes)
        }

        fun resultSaveTap(score: Float) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["score"] = score
            send("[result] save_tap", eventAttributes)

        }

        fun resultShareTap(score: Float) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["score"] = score
            send("[result] share_tap", eventAttributes)

        }

        fun resultSaveOk() {
            send("[result] save_ok")
        }

        fun paywallViewed() {
            send("[paywall] viewed")
        }

        fun paywallClouseTap() {
            send("[paywall] clouse_tap")
        }

        fun paywallSubsOk(status: Int) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["status"] = status
            send("[paywall] subs_ok")
        }

        fun paywallSubsError() {
            send("[paywall] subs_error")
        }

        fun paywallSubsTap() {
            send("[paywall] subs_tap")
        }

        fun paywallSubsAcknowledgePurchase(responseCode: Int) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["responseCode"] = responseCode
            send("[paywall] subs_AcknowledgePurchase", eventAttributes)
        }

        fun appSubsAcknowledgePurchase(responseCode: Int) {
            val eventAttributes = HashMap<String, Any>()
            eventAttributes["responseCode"] = responseCode
            send("[app] subs_AcknowledgePurchase")
        }

    }

}

