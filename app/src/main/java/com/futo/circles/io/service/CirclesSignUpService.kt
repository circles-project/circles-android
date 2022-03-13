package com.futo.circles.io.service

import com.futo.circles.io.request.ValidateSignUpTokenRequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CirclesSignUpService {

    @POST("_matrix/client/r0/register")
    suspend fun validateSignUpToken(
        @Body authParams: ValidateSignUpTokenRequestBody
    ): ResponseBody

}