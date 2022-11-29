package com.mikep.remoteui.ui

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.github.kobting.remoteui.v1.definitions.*
import com.github.kobting.remoteui.v1.responses.Page
import com.mikep.remoteui.json
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.serialization.encodeToString
import androidx.navigation.compose.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

@Composable
fun RemotePage(
    initialPageName: String = "home",
    remotePageState: RemotePageState = rememberPageState(),
    debug: Boolean = false
) {
    LaunchedEffect(remotePageState) {
        remotePageState.showPage(initialPageName)
    }

    val composableScope = androidx.compose.runtime.rememberCoroutineScope()
    BackHandler {
        composableScope.launch { remotePageState.popPage() }
    }
    Page(page = remotePageState.currentPageData, remotePageState = remotePageState, composableScope = composableScope, debugEnabled = debug)
}

@Composable
fun Page(page: Page?, remotePageState: RemotePageState, composableScope: CoroutineScope, debugEnabled: Boolean) {
    if (page == null) return
    val debug = remember { mutableStateOf(debugEnabled) }
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            if (page.appBar != null) {
                TopAppBar(
                    title = { Text(text = page.appBar!!.title) },
                    backgroundColor = parseColor(page.appBar!!.properties.backgroundColor),
                    actions = {
                        IconButton(onClick = { debug.value = !debug.value }) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = "Debug Info"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (page.bottomNavigationBar != null) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                BottomNavigation(backgroundColor = parseColor(page.bottomNavigationBar!!.properties.backgroundColor)) {
                    page.bottomNavigationBar!!.navigationItems.forEachIndexed { index, bottomNavigationItem ->
                        BottomNavigationItem(
                            selected = currentDestination?.hierarchy?.any { it.route == page.pageName } == true,
                            onClick = {
                                composableScope.launch {
                                    remotePageState.showPage(bottomNavigationItem.actionOnClick.onClickData)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.DefaultFromString(
                                        bottomNavigationItem.icon.name
                                    ), ""
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(scaffoldPadding)
        ) {
            if (!debug.value) {
                ViewListTree(views = page.views)
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(scaffoldPadding)
                ) {
                    Text(text = json.encodeToString(page), color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ViewListTree(modifier: Modifier = Modifier, views: List<View>?) {

    views?.forEach { view ->
        when (view) {
            is ImageView -> RemoteImage(view)
            is TextView -> RemoteText(view)
            is ColumnView -> RemoteColumn(view)
            is RowView -> RemoteRow(view)
            is Card -> RemoteCard(view)
            is CheckBox -> TODO()
            is Chip -> TODO()
            is Divider -> RemoteDivider(view)
            is IconView -> RemoteIcon(view)
            else -> TODO("Handle unknown view type")
        }
    }
}

@Composable
fun RemoteIcon(view: IconView) {
    Icon(
        imageVector = Icons.DefaultFromString(
            view.icon.name
        ),
        contentDescription = "",
        tint = parseColor(view.icon.color),
        modifier = Modifier.viewProperties(view.properties, LocalContext.current)
    )
}

@Composable
fun RemoteImage(view: ImageView) {
    Image(
        painter = rememberImagePainter(view.imageSource),
        contentScale = ContentScale.Crop,
        contentDescription = view.properties.accessibility,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}

@Composable
fun RemoteText(view: TextView) {
    Text(
        text = view.text,
        color = parseColor(view.textColor),
        textAlign = view.textAlignment.toTextAlign(),
        fontSize = view.textSize.sp,
        modifier = Modifier.viewProperties(view.properties, LocalContext.current)
    )
}

@Composable
fun RemoteColumn(view: ColumnView) {
    Column(
        modifier = Modifier.viewProperties(view.properties, LocalContext.current)
    ) {
        ViewListTree(views = view.views)
    }
}

@Composable
fun RemoteRow(view: RowView) {
    Row(modifier = Modifier.viewProperties(view.properties, LocalContext.current), verticalAlignment = Alignment.CenterVertically) {
        ViewListTree(views = view.views)
    }
}

@Composable
fun RemoteCard(view: Card) {
    Card(
        modifier = Modifier.viewProperties(view.properties, LocalContext.current),
        backgroundColor = parseColor(view.properties.backgroundColor)
    ) {
        Column {
            Row {
                if(view.icon != null) {
                    Icon(
                        imageVector = Icons.DefaultFromString(view.icon?.name),
                        contentDescription = "TODO"
                    )
                }
                Column {
                    Text(text = view.title)
                    Text(text = view.subhead)
                }
            }
            ViewListTree(views = listOf(view.content))
        }
    }
}

@Composable
fun RemoteDivider(view: Divider) {
    Divider(color = Color.Black, thickness = 1.dp, modifier = Modifier.viewProperties(view.properties, LocalContext.current))
}

@Preview
@Composable
fun RemoteCardPreview() {
    val testCard = Card(
        content = ImageView(imageSource = "https://images.freeimages.com/images/large-previews/540/dandelion-37-1388001.jpg"),
        title = "Title",
        subhead = "Subhead",
        icon = com.github.kobting.remoteui.v1.properties.Icon("home", "#3355EE")
    )
    RemoteCard(view = testCard)
}