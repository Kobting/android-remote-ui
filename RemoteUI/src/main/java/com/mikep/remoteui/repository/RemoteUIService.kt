package com.mikep.remoteui.repository

import com.github.kobting.remoteui.v1.responses.PageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RemoteUIService {

    @GET("v1/page")
    suspend fun getPage(@Query("id") id: String): PageResponse

}