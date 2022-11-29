package com.mikep.remoteui.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun RemoteUITheme(

) {
   MaterialTheme {
      RemotePage(initialPageName = "settings/something/testpage", debug = false)
   }
}