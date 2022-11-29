package com.mikep.remoteui.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.kobting.remoteui.v1.definitions.View
import com.github.kobting.remoteui.v1.properties.Alignment
import com.github.kobting.remoteui.v1.properties.OnClickType
import com.github.kobting.remoteui.v1.properties.Size
import com.github.kobting.remoteui.v1.properties.ViewProperties

fun Modifier.viewProperties(properties: ViewProperties, context: Context) =
    this.then(
        padding(
            start = properties.paddingLeft.dp,
            top = properties.paddingTop.dp,
            end = properties.paddingRight.dp,
            bottom = properties.paddingBottom.dp
        )
            .then(
                when (properties.size.width) {
                    Size.MAX -> fillMaxWidth()
                    Size.WRAP -> wrapContentWidth()
                    else -> {
                        if (properties.size.width.isNotBlank()) {
                            width(properties.size.width.toFloat().dp)
                        } else {
                            this
                        }
                    }
                }
            )
            .then(
                when (properties.size.height) {
                    Size.MAX -> fillMaxHeight()
                    Size.WRAP -> wrapContentHeight()
                    else -> {
                        if (properties.size.height.isNotBlank()) {
                            height(properties.size.height.toFloat().dp)
                        } else {
                            this
                        }
                    }
                }
            )
            .then(
                if (properties.backgroundColor != null) {
                    background(parseColor(properties.backgroundColor))
                } else {
                    this
                }
            )
            .then(
                if (properties.onClick.type == OnClickType.NONE) {
                    this
                } else {
                    clickable(onClickLabel = properties.accessibility) {
                        when (properties.onClick.type) {
                            OnClickType.NONE -> {

                            }
                            OnClickType.PAGE -> {
                                Toast.makeText(context, "Page Click: ${properties.onClick.onClickData}", Toast.LENGTH_LONG).show()
                            }
                            OnClickType.LINK -> {
                                val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                                intent.data = Uri.parse(properties.onClick.onClickData)
                                context.startActivity(intent)
                                Toast.makeText(context, "Link Click: ${properties.onClick.onClickData}", Toast.LENGTH_LONG).show()
                            }
                            OnClickType.NAVIGATION -> {
                                Toast.makeText(context, "Navigation Click: ${properties.onClick.onClickData}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
    )

fun parseColor(color: String?): Color {
    return Color(android.graphics.Color.parseColor(color ?: "#FFFFFF"))
}

fun Alignment.toTextAlign(): TextAlign {
    return when(this) {
        Alignment.LEFT -> TextAlign.Left
        Alignment.RIGHT -> TextAlign.Right
        Alignment.CENTER -> TextAlign.Center
        else -> TextAlign.Left
    }
}