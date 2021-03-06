package com.droidtitan.volley.util.volley

import android.content.res.Resources
import com.android.volley.*
import com.droidtitan.volley.R
import com.droidtitan.volley.util.Api
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.Charset

/**
 * @param res Android resources for fetching a string.
 * @return A user readable message describing the Http failure.
 */
public fun VolleyError.toString(res: Resources): String = when {
    isApiError() -> getApiErrorMessage(res)
    isNetworkError() -> res.getString(R.string.no_data_connection)
    this is TimeoutError -> res.getString(R.string.generic_server_down)
    else -> res.getString(R.string.generic_error)
}


private fun VolleyError.isNetworkError() = this is NetworkError || this is NoConnectionError

private fun VolleyError.isApiError() = this is ServerError || this is AuthFailureError

/** @return A user readable message that interprets the cause of the Api failure. */
private fun VolleyError.getApiErrorMessage(res: Resources): String {

    if (networkResponse != null) {
        when (networkResponse.statusCode) {
            404 -> return res.getString(R.string.no_results_found)
            422, 401 -> {
                try {
                    val json = String(networkResponse.data, Charset.defaultCharset())
                    val type = object : TypeToken<Map<String, String>>() {}.type

                    val map = Gson().fromJson<Map<String, String>>(json, type)
                    return map[Api.ERROR_KEY] ?: res.getString(R.string.generic_error)
                } catch (e: Exception) {
                    return message ?: res.getString(R.string.generic_error)
                }
            }
            else -> return res.getString(R.string.generic_server_down)
        }
    }
    return res.getString(R.string.generic_error)
}


inline fun <reified T : Any> RequestQueue.add(listener: Listener<T>?,
                                              url: String,
                                              noinline configure: ((Request<*>) -> Any)? = null,
                                              method: Int = Request.Method.GET) {

    val volleyListener = Response.Listener<T> { r -> listener?.onCompleted(null, r) }
    val errorListener = Response.ErrorListener { e -> listener?.onCompleted(e, null) }
    val request = GsonRequest(method, url, T::class.java, volleyListener, errorListener)

    configure?.invoke(request)
    add(request)
}

fun Request<*>.dontCache(): Request<*> = setShouldCache(false)

fun Request<*>.withTag(tag: Any): Request<*> = setTag(tag)