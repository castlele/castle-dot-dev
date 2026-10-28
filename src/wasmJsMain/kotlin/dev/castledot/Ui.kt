package dev.castledot

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import dev.castledot.resources.Res
import org.jetbrains.compose.resources.MissingResourceException
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.decodeToSvgPainter

val LocalNavigate = compositionLocalOf<(Route) -> Unit> { {} }

@Composable
fun Link(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = FontSize.m,
    color: Color = LocalPalette.current.accent,
    weight: FontWeight? = null,
    underline: Boolean = true,
    onClick: () -> Unit,
) {
    val style = textStyle(size, color, LocalFont.current, weight).let {
        if (underline) it.copy(textDecoration = TextDecoration.Underline) else it
    }
    BasicText(
        text,
        modifier.pointerHoverIcon(PointerIcon.Hand)
            .clickable(role = Role.Button, onClick = onClick),
        style,
    )
}

@Composable
fun RouteLink(
    text: String,
    route: Route,
    modifier: Modifier = Modifier,
    size: TextUnit = FontSize.m,
    color: Color = LocalPalette.current.accent,
    weight: FontWeight? = null,
    underline: Boolean = true,
) {
    val go = LocalNavigate.current
    Link(text, modifier, size, color, weight, underline) { go(route) }
}

@Composable
fun ExternalLink(text: String, url: String) {
    val uri = LocalUriHandler.current
    Link(text) { uri.openUri(url) }
}

// Files under composeResources/files/, addressed by their site path.
@Composable
fun rememberAsset(path: String): Painter? {
    val density = LocalDensity.current
    val painter by produceState<Painter?>(null, path) {
        value = try {
            val bytes = Res.readBytes("files$path")
            if (path.endsWith(".svg")) {
                bytes.decodeToSvgPainter(density)
            } else {
                BitmapPainter(bytes.decodeToImageBitmap())
            }
        } catch (_: MissingResourceException) {
            null
        }
    }
    return painter
}

@Composable
fun Avatar(path: String, name: String) {
    val painter = rememberAsset(path) ?: return
    Image(
        painter,
        contentDescription = "$name's avatar",
        modifier = Modifier.size(128.dp)
            .border(BorderWidth, LocalPalette.current.border),
    )
}

@Composable
fun ArticleList(articles: List<Article>) {
    Column(verticalArrangement = Arrangement.spacedBy(Space.s4)) {
        for (a in articles) {
            Column(verticalArrangement = Arrangement.spacedBy(Space.s1)) {
                Text(a.date, size = FontSize.s, color = LocalPalette.current.muted)
                RouteLink(a.title, Route.Article(a.slug))
                if (a.description.isNotEmpty()) {
                    Text(
                        a.description,
                        size = FontSize.s,
                        color = LocalPalette.current.muted,
                    )
                }
            }
        }
    }
}
