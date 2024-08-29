package com.viktorger.zulip_client.app.core.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestWithHeader = chain.request()
            .newBuilder()
            .addHeader("Authorization", BASIC_AUTH_KEY)
            .build()
        return chain.proceed(requestWithHeader)
    }

    companion object {
        const val BASIC_AUTH_KEY = "Basic dmlrdG9yZ2VyMjg4QGdtYWlsLmNvbTpjRDZpWFdlaVFqSGdVSkw3dHdPTUVEUVNwUG9QdmoxMQ=="
    }
}