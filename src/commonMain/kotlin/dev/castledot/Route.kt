package dev.castledot

sealed interface Route {
    data object Home : Route
    data object Feed : Route
    data object About : Route
    data class Article(val slug: String) : Route
    data object NotFound : Route
}

fun Route.path(): String = when (this) {
    Route.Home -> "/"
    Route.Feed -> "/feed/"
    Route.About -> "/about/"
    is Route.Article -> "/articles/$slug/"
    Route.NotFound -> "/404/"
}

// The trailing slash is optional.
fun parseRoute(path: String): Route {
    val parts = path.split("/").filter { it.isNotEmpty() }

    return when {
        parts.isEmpty() -> Route.Home
        parts == listOf("feed") -> Route.Feed
        parts == listOf("about") -> Route.About
        parts.size == 2 && parts[0] == "articles" -> Route.Article(parts[1])
        else -> Route.NotFound
    }
}
