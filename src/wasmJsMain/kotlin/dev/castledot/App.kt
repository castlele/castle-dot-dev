package dev.castledot

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import dev.castledot.resources.Res
import dev.castledot.resources.pixelify_sans
import org.jetbrains.compose.resources.Font

fun title(route: Route): String = when (route) {
    Route.Home -> Site.name
    Route.Feed -> "feed · ${Site.name}"
    Route.About -> "about · ${Site.name}"
    is Route.Article ->
        articles.find { it.slug == route.slug }?.let { "${it.title} · ${Site.name}" }
            ?: "404 · ${Site.name}"
    Route.NotFound -> "404 · ${Site.name}"
}

@Composable
fun App(route: Route, go: (Route) -> Unit) {
    val palette = if (isSystemInDarkTheme()) Dark else Light

    CompositionLocalProvider(
        LocalPalette provides palette,
        LocalFont provides FontFamily(Font(Res.font.pixelify_sans)),
        LocalNavigate provides go,
    ) {
        key(route) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(palette.bg)
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    Modifier
                        .widthIn(max = ContentWidth)
                        .fillMaxWidth()
                        .padding(
                            top = Space.s4,
                            start = Space.s3,
                            end = Space.s3,
                            bottom = Space.s5,
                        ),
                ) {
                    Header()

                    when (route) {
                        Route.Home -> HomeScreen()
                        Route.Feed -> FeedScreen()
                        Route.About -> AboutScreen()
                        is Route.Article -> ArticleScreen(route.slug)
                        Route.NotFound -> NotFoundScreen()
                    }
                }
            }
        }
    }
}
