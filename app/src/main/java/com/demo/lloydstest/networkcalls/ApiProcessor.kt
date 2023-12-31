package com.demo.lloydstest.networkcalls

interface ApiProcessor<T> {

    suspend fun sendRequest(retrofitApi: RetrofitApi): T

    fun onResponse(res: T)

    fun onError(message: String, responseCode:Int) {}

}