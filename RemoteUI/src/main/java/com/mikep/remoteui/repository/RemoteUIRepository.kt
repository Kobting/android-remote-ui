package com.mikep.remoteui.repository

import com.github.kobting.remoteui.v1.responses.PageResponse

class RemoteUIRepository(private val service: RemoteUIService) {

    suspend fun getPage(pageId: String): PageResponse {
        return service.getPage(pageId)
    }

}