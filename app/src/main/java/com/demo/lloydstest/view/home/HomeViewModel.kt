package com.demo.lloydstest.view.home

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableParcelable
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
import com.demo.lloydstest.utils.hideKeyboard
import com.demo.lloydstest.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val showLoader = MutableLiveData(false)
    val countries = MutableLiveData<List<Country>>(null)
    val isCountryList  =  ObservableField(false)
    val countryAdapter by lazy { SpinnerGenericAdapter<Country>(R.layout.item_spinner_country) }
    val code  = ObservableField("")
    val phoneNumber  = ObservableField("")
    val countryDetails  = ObservableParcelable<ValidateResponse>(null)
    val isMessageValidShow  =  ObservableField(false)


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
                            if(data.isNotEmpty()) {
                                countries.value = data
                                countryAdapter.addItems(data)
                                code.set(data[0].diallingCode?.let { it.substring(1, it.length) })
                                isCountryList.set(true)
                            }
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
                    res.body()?.let {
                        countryDetails.set(it)
                        isMessageValidShow.set(true)
                    }
                }
            }
        )
    }

    fun onClick(view: View){
        when(view.id){
            R.id.valid ->{
                getCurrentActivity().hideKeyboard()
                onClickValid()
            }
            R.id.syncCodes -> {
                hitCountryCodesData()
            }
        }
    }
    /**
     * Onclick of valid button
     */
    private fun onClickValid() {
        Log.d("Demo", "Phone: ${code.get()} ${phoneNumber.get()}")
        if(code.get().isNullOrBlank())
            getCurrentActivity().showToast(getCurrentActivity().getString(R.string.please_select_country_code))
        else if(phoneNumber.get().isNullOrBlank()){
            getCurrentActivity().showToast(getCurrentActivity().getString(R.string.please_enter_phone_number))
        }else {
            hitValidatePhoneNumber("${code.get()}${phoneNumber.get()}")
        }
    }
}