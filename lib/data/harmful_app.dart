import 'dart:typed_data';

class HarmfulApp {
  HarmfulApp({ this.packageName, this.apkSha256, this.category});

  final String packageName;
  final Uint8List apkSha256;
  final int category;
}