package com.urbanj.flutter_safetynet.data

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RecaptchaService {

    @POST("verify_captcha")
    @Headers("Content-Type: application/json")
    fun verifyRecaptcha(@Body recaptchaRequest: RecaptchaRequest) : Call<RecaptchaResponse>
}