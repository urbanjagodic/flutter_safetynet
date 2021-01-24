import 'dart:async';

import 'package:flutter/services.dart';
import 'data/harmful_app.dart';
import 'data/plugin_values.dart';
import 'data/safetynet_exception.dart';
import 'data/method_action.dart';
import 'util/util.dart';

class FlutterSafetynet {

  /// Name of the [MethodChannel].
  static const MethodChannel _channel = const MethodChannel(PluginValues.CHANNEL_NAME);

  /// Checks whether verify apps feature is enabled.
  ///
  /// Returns `true` if verify apps feature is enabled, `false` otherwise.
  /// Throws a [SafetynetException] if unknown API error occurs.
  static Future<bool> isVerifyAppsEnabled() async {
    try {
      return await _channel.invokeMethod(Util.parseActionName(MethodAction.IS_VERIFY_APPS_ENABLED));
    } on PlatformException catch(ex) {
      throw SafetynetException(ex.code, ex.message);
    }
  }

  /// Invokes as system-dialog which requests the user to enable verify apps feature.
  /// Dialog is only invoked if verify apps feature is disabled.
  ///
  /// Throws a [SafetynetException] if unknown API error occurs.
  static Future<void> enableVerifyApps() async {
    try {
      await _channel.invokeMethod(Util.parseActionName(MethodAction.ENABLE_VERIFY_APPS));
    } on PlatformException catch(ex) {
      throw SafetynetException(ex.code, ex.message);
    }
  }

  /// Checks whether user has any potentially harmful apps on his device.
  ///
  /// Returns a [Future] with a [List<HarmfulApp>].
  /// Throws a [SafetynetException] if unknown API error occurs.
  ///
  /// **Verify apps feature must be enabled** in order to use this method,
  /// otherwise it produces a [SafetynetException] error.
  static Future<List<HarmfulApp>> listHarmfulApps() async {
    List<HarmfulApp> harmFulApps = <HarmfulApp>[];
    try {
      dynamic result = await _channel.invokeMethod(Util.parseActionName(MethodAction.LIST_HARMFUL_APPS));
      result.forEach((element) {
        harmFulApps.add(HarmfulApp(
            packageName: element[PluginValues.HARMFUL_APP_PACKAGE_NAME],
            apkSha256: element[PluginValues.HARMFUL_APP_APK_SHA],
            category: element[PluginValues.HARMFUL_APP_CATEGORY]));
      });
    } on PlatformException catch(ex) {
      throw SafetynetException(ex.code, ex.message);
    }
    return harmFulApps;
  }
}
