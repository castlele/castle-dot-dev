package dev.castledot

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        var route by remember { mutableStateOf(parseRoute(window.location.pathname)) }

        DisposableEffect(Unit) {
            val onBack: (org.w3c.dom.events.Event) -> Unit = {
                route = parseRoute(window.location.pathname)
            }

            window.addEventListener("popstate", onBack)

            onDispose {
                window.removeEventListener("popstate", onBack)
            }
        }

        LaunchedEffect(route) {
            document.title = title(route)
        }

        App(route) { target ->
            window.history.pushState(null, "", target.path())
            route = target
        }
    }
}
