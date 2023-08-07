package com.demo.lloydstest.networkcalls

import com.demo.lloydstest.models.Country
import com.demo.lloydstest.models.ValidateResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.*

interface RetrofitApi {

    @GET(COUNTRIES)
    suspend fun countries(
        @Header("apikey") header: String
    ): Response<Map<String, Country>>

    @GET(VALIDATE)
    suspend fun validateNumber(
        @Header("apikey") header: String,
        @Query("number") number:String
    ): Response<ValidateResponse>

}