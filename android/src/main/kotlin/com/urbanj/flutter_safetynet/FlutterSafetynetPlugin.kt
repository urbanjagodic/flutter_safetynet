package com.urbanj.flutter_safetynet

import android.content.Context
import androidx.annotation.NonNull
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


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_safetynet")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        safetyNetClient = SafetyNet.getClient(context)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            PluginActions.IS_VERIFY_APPS_ENABLED.name -> {
              isVerifyAppsEnabled(result)
            }
            PluginActions.ENABLE_VERIFY_APPS.name -> {
              enableVerifyApps(result)
            }
            PluginActions.LIST_HARMFUL_APPS.name -> {
                listHarmfulApps(result)
            }
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
}

private enum class PluginActions {
    IS_VERIFY_APPS_ENABLED,
    ENABLE_VERIFY_APPS,
    LIST_HARMFUL_APPS
}
