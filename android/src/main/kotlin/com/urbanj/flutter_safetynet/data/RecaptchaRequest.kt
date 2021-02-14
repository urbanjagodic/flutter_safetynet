package com.urbanj.flutter_safetynet.data

import com.squareup.moshi.Json

data class RecaptchaRequest(@field:Json(name = "token") var token : String?)
