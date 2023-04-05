package org.futo.circles.io

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import retrofit2.http.Url

interface BugreportApiService {

    @Multipart
    @POST
    suspend fun sendBugReport(
        @Url url: String,
        @PartMap map: Map<String, @JvmSuppressWildcards RequestBody>
    ): ResponseBody
}