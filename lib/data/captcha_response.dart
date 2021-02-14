class CaptchaResponse {
  CaptchaResponse({
    this.success,
    this.challengeTs,
    this.apkPackageName,
    this.errorCodes
  });

  final bool success;
  final DateTime challengeTs;
  final String apkPackageName;
  final List<String> errorCodes;

  static CaptchaResponse fromJson(Map<String, dynamic> json) {
    String challenge_ts = json['challenge_ts'] as String;
    List<dynamic> errors = json['error_codes'] as List<dynamic>;
    return CaptchaResponse(
      success: json['success'] as bool,
      challengeTs: challenge_ts != null ? DateTime.tryParse(challenge_ts) : null,
      apkPackageName: json['apk_package_name'] as String,
      errorCodes: errors != null ? errors.cast<String>().toList() : null,
    );
  }

  @override
  String toString() {
    return "SUCCESS: $success, CHALLENGE_TS: $challengeTs, APK: $apkPackageName, ERRORS: ${errorCodes.toString()}";
  }
}