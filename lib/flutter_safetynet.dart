
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterSafetynet {
  static const MethodChannel _channel =
      const MethodChannel('flutter_safetynet');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
