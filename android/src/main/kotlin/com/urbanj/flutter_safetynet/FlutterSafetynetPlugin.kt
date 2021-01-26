package com.urbanj.flutter_safetynet

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import androidx.annotation.NonNull
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.HarmfulAppsData
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetClient

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class FlutterSafetynetPlugin: FlutterPlugin, MethodCallHandler {

    private lateinit var channel : MethodChannel
    private lateinit var context: Context
    private lateinit var safetyNetClient: SafetyNetClient

    private lateinit var SAFEBROWSING_API_KEY : String
    private lateinit var RECAPTCHA_SITE_API_KEY : String


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_safetynet")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        safetyNetClient = SafetyNet.getClient(context)
        SAFEBROWSING_API_KEY = getMetaData(context, "safebrowsing_api_key")!!
        RECAPTCHA_SITE_API_KEY = getMetaData(context, "recaptcha_api_key")!!
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            PluginActions.IS_VERIFY_APPS_ENABLED.name -> isVerifyAppsEnabled(result)
            PluginActions.ENABLE_VERIFY_APPS.name -> enableVerifyApps(result)
            PluginActions.LIST_HARMFUL_APPS.name -> listHarmfulApps(result)
            PluginActions.INIT_SAFE_BROWSING.name -> initSafeBrowsing(result)
            PluginActions.SHUT_DOWN_SAFE_BROWSING.name -> shutdownSafeBrowsing(result)
            PluginActions.VERIFY_URL.name -> {
                var url: String = call.argument<String>("url")!!
                var threats: List<Int> = call.argument<List<Int>>("threats")!!
                verifyUrl(result, url, threats)
            }
            PluginActions.VERIFY_RECAPTCHA.name -> verifyRecaptcha(result)
            else -> {
              result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    private fun isVerifyAppsEnabled(result: Result) {
        safetyNetClient.isVerifyAppsEnabled
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        result.success(task.result!!.isVerifyAppsEnabled)
                    } else {
                        result.error("verify_apps_error",
                                "Unknown error occurred when checking if verify apps is enabled", null)
                    }
                }
    }

    private fun enableVerifyApps(result: Result) {
        safetyNetClient
                .enableVerifyApps()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        result.error("enable_verify_apps_error", "General error occurred", null)
                    }
                }
    }

    private fun listHarmfulApps(result: Result) {
        safetyNetClient
                .listHarmfulApps()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val appList : List<HarmfulAppsData> = task.result!!.harmfulAppsList

                        var harmfulApps = mutableListOf<Map<String, Any>>();
                        if (appList.isNotEmpty()) {
                            for (harmfulApp in appList) {
                                var appMap = hashMapOf(
                                        "PACKAGE_NAME" to harmfulApp.apkPackageName,
                                        "APK_SHA" to harmfulApp.apkSha256,
                                        "CATEGORY" to harmfulApp.apkCategory)
                                harmfulApps.add(appMap);
                            }
                        }
                        result.success(harmfulApps)
                    } else {
                        result.error("list_harmful_apps_error",
                                "Call isVerifyAppsEnabled() method to ensure that the user has consented.", null)
                    }
                }
    }

    private fun initSafeBrowsing(result: Result) {
        safetyNetClient
                .initSafeBrowsing()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        result.error("init_safe_browsing_error", "Couldn't init safe browsing.", null)
                    }
                }
    }

    private fun shutdownSafeBrowsing(result: Result) {
        safetyNetClient
                .shutdownSafeBrowsing()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        result.error("shut_down_safe_browsing_error", "Couldn't shut down safe browsing.", null)
                    }
                }
    }

    private fun verifyUrl(result: Result, url: String, threats: List<Int>) {
        safetyNetClient
                .lookupUri(url, SAFEBROWSING_API_KEY, *threats.toIntArray())
                .addOnSuccessListener { task ->
                    result.success(task.detectedThreats!!.isEmpty())
                }
                .addOnFailureListener { ex ->
                    if (ex is ApiException) {
                        val status = CommonStatusCodes.getStatusCodeString(ex.statusCode)
                        result.error("verify_url_error", "$status : ${ex.message}", null)
                    } else {
                        result.error("verify_url_error", "Unknown error while verifying url.", null)
                    }
                }
    }

    private fun verifyRecaptcha(result : Result) {
        safetyNetClient
                .verifyWithRecaptcha(RECAPTCHA_SITE_API_KEY)
                .addOnSuccessListener { response ->
                    val userResponseToken = response.tokenResult!!
                    if (userResponseToken.isNotEmpty()) {
                        result.success(response.tokenResult!!)
                        // Validate the user response token using the
                        // reCAPTCHA siteverify API.
                        verifyRecaptchaTokenResponse(userResponseToken)
                    } else {
                        result.error("verify_recaptcha_error", "Response recaptcha user token was null", null)
                    }
                }
                .addOnFailureListener{ ex ->
                    if (ex is ApiException) {
                        val status = CommonStatusCodes.getStatusCodeString(ex.statusCode)
                        result.error("verify_recaptcha_error", "$status : ${ex.message}", null)
                    } else {
                        result.error("verify_recaptcha_error", "Uknown verify recaptcha error", null)
                    }
                }
    }

    private fun verifyRecaptchaTokenResponse(token : String) {

    }


    private fun getMetaData(context: Context, name: String?): String? {
        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            return appInfo.metaData.getString(name)
        } catch (e: NameNotFoundException) {
        }
        return null
    }
}

private enum class PluginActions {
    IS_VERIFY_APPS_ENABLED,
    ENABLE_VERIFY_APPS,
    LIST_HARMFUL_APPS,
    INIT_SAFE_BROWSING,
    SHUT_DOWN_SAFE_BROWSING,
    VERIFY_URL,
    VERIFY_RECAPTCHA
}
