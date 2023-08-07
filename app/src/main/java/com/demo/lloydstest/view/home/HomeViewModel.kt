package com.demo.lloydstest.view.home

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.lloydstest.R
import com.demo.lloydstest.genericadapters.SpinnerGenericAdapter
import com.demo.lloydstest.models.Country
import com.demo.lloydstest.models.ValidateResponse
import com.demo.lloydstest.networkcalls.ApiEnums
import com.demo.lloydstest.networkcalls.ApiProcessor
import com.demo.lloydstest.networkcalls.Repository
import com.demo.lloydstest.networkcalls.RetrofitApi
import com.demo.lloydstest.utils.Constants
import com.demo.lloydstest.utils.getCurrentActivity
import com.demo.lloydstest.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val showLoader = MutableLiveData(false)
    val countries = MutableLiveData<List<Country>>(null)
    val countryAdapter by lazy { SpinnerGenericAdapter<Country>(R.layout.item_spinner_country) }
    val code  = ObservableField("")
    val phoneNumber  = ObservableField("")
    val countryDetailsMLD  = MutableLiveData<ValidateResponse>(null)
    val isMessageValidShow  =  ObservableField(false)
    val validMessage  =  ObservableField("")


    /**
     * Phone number validation message
     * **/
    fun initPhoneValidation(isValid:Boolean):String {
        return if(isValid)
            "Phone number is valid"
        else
            "Phone number is not valid"
    }

    /**
     * API call of get country codes
     * **/
    fun hitCountryCodesData() =
        viewModelScope.launch {
            kotlin.runCatching {
                repository.makeCall(
                    apiKey = ApiEnums.COUNTRY_CODE,
                    saveInCache = true,
                    loader = true,
                    requestProcessor = object : ApiProcessor<Response<Map<String, Country>>> {
                        override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<Map<String, Country>> {
                            showLoader.postValue(true)
                            return retrofitApi.countries(
                                header = Constants.API_KEY,
                            )
                        }

                        override fun onError(message: String, responseCode: Int) {
                            super.onError(message, responseCode)
                            showLoader.postValue(false)
                        }

                        override fun onResponse(res: Response<Map<String, Country>>) {
                            showLoader.postValue(false)
                            val data = ArrayList<Country>()
                            res.body()?.let { it ->
                                it.map { item ->
                                    data.add(
                                        Country(
                                            item.key,
                                            item.value.countryName,
                                            item.value.diallingCode
                                        )
                                    )
                                }
                            }
                            countries.value = data
                            countryAdapter.addItems(data)
                            code.set(data[0].diallingCode?.let { it.substring(1, it.length) })
                        }
                    }
                )
            }.onFailure {
                it.printStackTrace()
                getCurrentActivity().showToast(getCurrentActivity().getString(R.string.some_error_occurred))
            }
    }

    /**
     * API call of phone number validation
     * **/
    private fun hitValidatePhoneNumber(phoneNumber:String) = viewModelScope.launch {
        repository.makeCall(
            apiKey = ApiEnums.COUNTRY_VALIDATION,
            saveInCache = false,
            loader = true,
            requestProcessor = object : ApiProcessor<Response<ValidateResponse>> {
                override suspend fun sendRequest(retrofitApi: RetrofitApi): Response<ValidateResponse> {
                    showLoader.postValue(true)
                    return retrofitApi.validateNumber(
                        header = Constants.API_KEY,
                        number = phoneNumber
                    )
                }

                override fun onError(message: String, responseCode: Int) {
                    super.onError(message, responseCode)
                    showLoader.postValue(false)
                }

                override fun onResponse(res: Response<ValidateResponse>) {
                    showLoader.postValue(false)
                    res.body()?.let { it ->
                        countryDetailsMLD.value = it
                        isMessageValidShow.set(true)
                        validMessage.set(it.valid.let { it1-> if(it1)
                            getCurrentActivity().getString(R.string.phone_number_valid)
                        else
                            getCurrentActivity().getString(R.string.phone_number_not_valid)
                        })
                        isMessageValidShow.notifyChange()
                    }
                }
            }
        )
    }

    /**
     * Onclick of valid button
     */
    fun onClickValid() {
        Log.d("Demo", "Phone: ${code.get()} ${phoneNumber.get()}")
        if(code.get().isNullOrBlank())
            getCurrentActivity().showToast("Please select country code")
        else if(phoneNumber.get().isNullOrBlank()){
            getCurrentActivity().showToast("Please enter phone number")
        }else {
            hitValidatePhoneNumber("${code.get()}${phoneNumber.get()}")
        }
    }


}