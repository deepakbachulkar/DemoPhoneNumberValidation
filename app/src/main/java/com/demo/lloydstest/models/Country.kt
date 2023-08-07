package com.demo.lloydstest.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    @SerializedName("code") val code:String? = "",
    @SerializedName("country_name") val countryName:String? = "",
    @SerializedName("dialling_code") val diallingCode: String? = ""
): Parcelable