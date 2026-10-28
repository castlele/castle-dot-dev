package dev.castledot

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Header() {
    val p = LocalPalette.current

    FlowRow(
        Modifier.fillMaxWidth().padding(bottom = Space.s3),
        horizontalArrangement = Arrangement.spacedBy(Space.s3),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        RouteLink(
            Site.name, Route.Home,
            size = FontSize.l, color = p.fg, weight = FontWeight.Bold,
            underline = false,
        )

        Spacer(Modifier.weight(1F))

        Row(horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
            RouteLink("feed", Route.Feed)
            RouteLink("about", Route.About)
        }
    }
    Rule()
    Spacer(Modifier.height(Space.s4))
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Who(extra: @Composable () -> Unit = {}) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Space.s4),
        verticalArrangement = Arrangement.spacedBy(Space.s4),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(about.avatar, about.name)
        Column {
            Text(about.name, size = FontSize.xl, weight = FontWeight.Bold)
            Text(about.role, color = LocalPalette.current.muted)
            extra()
        }
    }
}

@Composable
fun HomeScreen() {
    Who {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
            Text(about.greeting)
            RouteLink("more ->", Route.About)
        }
    }
    Spacer(Modifier.height(Space.s5))
    Text("latest", size = FontSize.l, weight = FontWeight.Bold)
    Spacer(Modifier.height(Space.s3))
    ArticleList(articles.take(5))
    Spacer(Modifier.height(Space.s4))
    RouteLink("all articles ->", Route.Feed)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen() {
    Who()
    Spacer(Modifier.height(Space.s4))
    Prose(about.body)
    Spacer(Modifier.height(Space.s4))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        about.cv?.let { ExternalLink("cv (pdf)", it) }
        about.links.forEach { ExternalLink(it.label, it.url) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedScreen() {
    val p = LocalPalette.current
    val search = rememberTextFieldState()
    var query by remember { mutableStateOf(FeedQuery()) }
    val found = filterFeed(articles, query.copy(search = search.text.toString()))

    Text("feed", size = FontSize.xl, weight = FontWeight.Bold)
    Spacer(Modifier.height(Space.s3))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Space.s2),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        BasicTextField(
            search,
            modifier = Modifier.border(BorderWidth, p.border)
                .padding(horizontal = Space.s2, vertical = Space.s1),
            textStyle = textStyle(FontSize.s, p.fg, LocalFont.current),
            cursorBrush = SolidColor(p.accent),
            decorator = { inner ->
                if (search.text.isEmpty()) {
                    Text("search...", size = FontSize.s, color = p.muted)
                }
                inner()
            },
        )
        Chip(
            if (query.order == Order.Newest) "newest first" else "oldest first",
            selected = false,
        ) {
            query = query.copy(
                order = if (query.order == Order.Newest) Order.Oldest else Order.Newest,
            )
        }
    }
    Spacer(Modifier.height(Space.s3))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Space.s2),
        verticalArrangement = Arrangement.spacedBy(Space.s2),
    ) {
        for (tag in allTags(articles)) {
            val on = tag in query.tags
            Chip("#$tag", selected = on) {
                query = query.copy(tags = if (on) query.tags - tag else query.tags + tag)
            }
        }
    }
    Spacer(Modifier.height(Space.s4))
    if (found.isEmpty()) {
        Text("nothing found", color = p.muted)
    } else {
        ArticleList(found)
    }
}

@Composable
private fun Chip(label: String, selected: Boolean, onClick: () -> Unit) {
    val p = LocalPalette.current
    Text(
        label,
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand)
            .background(if (selected) p.accent else p.bg)
            .border(BorderWidth, p.accent)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = Space.s2),
        size = FontSize.s,
        color = if (selected) p.bg else p.accent,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArticleScreen(slug: String) {
    val article = articles.find { it.slug == slug } ?: return NotFoundScreen()
    val p = LocalPalette.current
    val (newer, older) = neighbours(articles, slug).let { it.newer to it.older }
    val related = similar(articles, slug)

    Text(article.title, size = FontSize.xl, weight = FontWeight.Bold)
    Spacer(Modifier.height(Space.s2))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(Space.s3)) {
        Text(article.date, size = FontSize.s, color = p.muted)
        article.tags.forEach { Text("#$it", size = FontSize.s, color = p.accent) }
    }
    Spacer(Modifier.height(Space.s4))
    Prose(article.body)
    Spacer(Modifier.height(Space.s5))
    Rule()
    Spacer(Modifier.height(Space.s3))
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
        if (older != null) RouteLink("<- ${older.title}", Route.Article(older.slug))
        if (newer != null) RouteLink("${newer.title} ->", Route.Article(newer.slug))
    }
    if (related.isNotEmpty()) {
        Spacer(Modifier.height(Space.s5))
        Text("similar", size = FontSize.l)
        Spacer(Modifier.height(Space.s3))
        ArticleList(related)
    }
}

@Composable
fun NotFoundScreen() {
    Text("404", size = FontSize.xl, weight = FontWeight.Bold)
    Text("page not found", color = LocalPalette.current.muted)
    Spacer(Modifier.height(Space.s3))
    RouteLink("home", Route.Home)
}
