package dev.castledot

enum class Order { Newest, Oldest }

data class FeedQuery(
    val search: String = "",
    val tags: Set<String> = emptySet(),
    val order: Order = Order.Newest,
)

fun allTags(articles: List<Article>): List<String> =
    articles.flatMap { it.tags }.distinct().sorted()

// Search is case-insensitive; every selected tag must be on the article.
fun filterFeed(articles: List<Article>, query: FeedQuery): List<Article> {
    val search = query.search.trim().lowercase()
    val found = articles.filter { a ->
        a.tags.containsAll(query.tags) &&
            (listOf(a.title, a.description, a.body) + a.tags).any {
                search in it.lowercase()
            }
    }
    return when (query.order) {
        Order.Newest -> found.sortedByDescending { it.date }
        Order.Oldest -> found.sortedBy { it.date }
    }
}
