package com.demo.lloydstest.networkcalls

import android.util.Log
import com.demo.lloydstest.R
import com.demo.lloydstest.pref.PreferenceFile
import com.demo.lloydstest.pref.token
import com.demo.lloydstest.utils.Alerts
import com.demo.lloydstest.utils.getCurrentActivity
import com.demo.lloydstest.utils.isNetworkAvailable
import com.demo.lloydstest.utils.showNegativeAlerter
import com.demo.lloydstest.utils.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(
    private val retrofitApi: RetrofitApi,
    private val cacheUtil: CacheUtil<String, Response<Any>>,
    private val preferenceFile: PreferenceFile
) {
    fun <T> makeCall(
        apiKey: ApiEnums,
        loader: Boolean,
        saveInCache: Boolean,
        requestProcessor: ApiProcessor<Response<T>>,
        getFromCache: Boolean = false,
        showToastOnErrors: Boolean = true
    ) {
        if (getFromCache)
            cacheUtil[apiKey.name + preferenceFile.retrieveKey(token)]?.let {
                requestProcessor.onResponse(it as Response<T>)
            } ?: getResponseFromCall(loader, requestProcessor,showToastOnErrors)
        else
            getResponseFromCall(loader,  requestProcessor, showToastOnErrors)
    }

    private fun <T> getResponseFromCall(
        loader: Boolean,
        requestProcessor: ApiProcessor<Response<T>>,
        showToastOnErrors: Boolean = true
    ) {
        try {
            val activity = getCurrentActivity()
            activity.let {
                if (!activity.isNetworkAvailable()) {
                    activity.showNegativeAlerter(activity.getString(R.string.your_device_offline))
                    requestProcessor.onError("no_internet", 1)
                    return
                }
                if (loader) {
                    Alerts.showProgress()
                }
            }

            val dataResponse: Flow<Response<Any>> = flow {
                val response = requestProcessor.sendRequest(retrofitApi) as Response<Any>
                emit(response)
            }.flowOn(Dispatchers.IO)

            CoroutineScope(Dispatchers.Main).launch {
                dataResponse.catch { exception ->
                    exception.printStackTrace()
                    Alerts.hideProgress()
                    requestProcessor.onError("something_went_wrong", 2)
                    activity.let {
                        activity.showToast(
                            activity.resources?.getString(R.string.some_error_occurred)?:""
                        )
                    }
                }.collect { response ->
                    Alerts.hideProgress()
                    when {
                            response.code() in 100..199 -> {
                            /**Informational*/
                            activity.let {
                                requestProcessor.onError(
                                    activity.resources?.getString(R.string.some_error_occurred) ?: "",
                                    response.code()
                                )
                                activity.showToast(
                                    activity.resources?.getString(R.string.some_error_occurred) ?: ""
                                )
                            }
                        }
                        response.isSuccessful -> {
                            /**Success*/
                            requestProcessor.onResponse(response as Response<T>)
                        }
                        response.code() in 300..399 -> {
                            /**Redirection*/
                            activity?.let {
                                requestProcessor.onError(activity.resources?.getString(R.string.some_error_occurred) ?: "", response.code())
                                activity.showToast(activity.resources?.getString(R.string.some_error_occurred) ?: "")
                            }
                        }
                        response.code() == 401 -> {
                            /**UnAuthorized*/
                            activity.let {
                                requestProcessor.onError(activity.resources?.getString(R.string.some_error_occurred) ?: "", response.code())
                                activity.showToast(activity.resources?.getString(R.string.authentication_error) ?: "")
                            }
                        }
                        response.code() == 404 -> {
                            /**Page Not Found*/
                            activity.let {
                                requestProcessor.onError(activity.resources?.getString(R.string.some_error_occurred) ?: "", response.code())
                                activity.showToast(
                                    activity.resources?.getString(R.string.some_error_occurred) ?: ""
                                )
                            }
                        }
                        response.code() in 500..599 -> {
                            /**ServerErrors*/
                            activity.let {
                                requestProcessor.onError(activity.resources?.getString(R.string.some_error_occurred) ?: "", response.code())
                                activity.showToast(activity.resources?.getString(R.string.some_error_occurred) ?: "")
                            }
                        }
                        else -> {
                            /**ClientErrors*/
                            val res = response.errorBody()!!.string()
                            val jsonObject = JSONObject(res)
                            when {
                                jsonObject.has("message") -> {
                                    Log.e(
                                        "Repository",
                                        "makeCall: ${jsonObject.getString("message")}"
                                    )
                                    requestProcessor.onError(
                                        jsonObject.getString("message"),
                                        response.code()
                                    )
                                    if(showToastOnErrors)
                                        if (!jsonObject.getString("message").equals("Data not found", true))
                                        activity.let {
                                            activity.showToast(jsonObject.getString("message"))
                                        }
                                }
                                jsonObject.has("error") -> {
                                    val message = jsonObject.getJSONObject("error").getString("text") ?: ""
                                    Log.e("Repository", "makeCall: $message")
                                    requestProcessor.onError(
                                        message,
                                        response.code()
                                    )
                                    if (!message.equals("Data not found", true))
                                        activity.let {
                                            activity.showToast(message)
                                        }
                                }
                                else -> {
                                    activity.let {
                                        requestProcessor.onError(activity.resources?.getString(R.string.some_error_occurred) ?: "", response.code())
                                        activity.showToast(activity.resources?.getString(R.string.some_error_occurred) ?: "")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
