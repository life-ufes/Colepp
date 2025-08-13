package com.example.colepp.common.utils

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonHandler {
    fun <T> getEncodedJsonParamAsUri(
        rawParam: T
    ): String {
        val jsonParam = Gson().toJson(rawParam)
        return Uri.encode(jsonParam)
    }

    inline fun <reified T> SavedStateHandle.getDecodedJsonParam(
        paramName: String
    ): T {
        val encodedJson = get<String>(paramName).orEmpty()
        val itemType = object : TypeToken<T>() {}.type
        return Gson().fromJson(encodedJson, itemType)
    }

    inline fun <reified T> decodeJsonParamFromUri(encodedUri: String): T {
        val decodedJson = Uri.decode(encodedUri)
        return Gson().fromJson(decodedJson, T::class.java)
    }
}