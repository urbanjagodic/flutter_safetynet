package com.urbanj.flutter_safetynet.data

import com.squareup.moshi.Json
import java.sql.Timestamp

data class RecaptchaResponse(
        @field:Json(name = "success") var success : Boolean?,
        @field:Json(name = "challenge_ts") var challenge_ts : String?,
        @field:Json(name = "apk_package_name") var apk_package_name : String?,
        @field:Json(name = "error-codes") var error_codes : List<String>?
)
