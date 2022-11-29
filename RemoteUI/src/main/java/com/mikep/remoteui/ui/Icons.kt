package com.mikep.remoteui.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.vector.ImageVector

fun Icons.DefaultFromString(iconName: String?): ImageVector {
    return when(iconName?.lowercase()) {
        "home" -> Default.Home
        "location" -> Default.LocationOn
        "more" -> Default.Menu
        else -> Default.Info
    }
}