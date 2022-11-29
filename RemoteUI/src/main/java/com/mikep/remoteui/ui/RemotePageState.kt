package com.mikep.remoteui.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.kobting.remoteui.serialization.viewSerializersModule_v1
import com.github.kobting.remoteui.v1.responses.Page
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mikep.remoteui.remoteUIRepository
import com.mikep.remoteui.repository.RemoteUIRepository
import com.mikep.remoteui.repository.RemoteUIService
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import okhttp3.MediaType
import retrofit2.Retrofit

class RemotePageState(private val remoteUIRepository: RemoteUIRepository) {
    var currentPageData by mutableStateOf<Page?>(null)
        private set

    var initialPage: Page? = null
    var pageStack = ArrayDeque<Page>()

    suspend fun showPage(pageId: String): Page? {
        try {
            currentPageData = remoteUIRepository.getPage(pageId).page
            if(initialPage == null) initialPage = currentPageData
            else pageStack.addLast(currentPageData!!)
        } catch (ex: Exception) {
            println(ex)
        }
        return currentPageData
    }

    suspend fun popPage(pageId: String? = null): Boolean {
        if(pageStack.isEmpty()) {
            currentPageData = initialPage
            return true
        }

        if(pageId == null && pageStack.isNotEmpty()) {
            currentPageData = pageStack.removeLast()
            return true
        } else {
            val matchingPage = pageStack.findLast { it.pageName == pageId }
            if(matchingPage == null) return false
            while (pageStack.removeLast() != matchingPage);
            currentPageData = matchingPage
        }
        return false
    }
}


@Composable
fun rememberPageState(): RemotePageState = remember { RemotePageState(remoteUIRepository) }

