package dev.castledot

data class Article(
    val slug: String,
    val title: String,
    val date: String,
    val tags: List<String>,
    val description: String,
    val draft: Boolean,
    val body: String,
)

private val DATE = Regex("""\d{4}-\d{2}-\d{2}""")

fun parseArticle(slug: String, raw: String): Article {
    val doc = parseDocument(raw)
    fun fail(field: String): Nothing = throw IllegalArgumentException(
        "content/articles/$slug.md: bad or missing \"$field\"",
    )
    val f = doc.fields
    return Article(
        slug = slug,
        title = (f["title"] as? String)?.takeIf { it.isNotEmpty() }
            ?: fail("title"),
        date = (f["date"] as? String)?.takeIf { DATE.matches(it) }
            ?: fail("date"),
        tags = (f["tags"] as? List<*>)?.map { it as? String ?: fail("tags") }
            ?: fail("tags"),
        description = f["description"] as? String ?: fail("description"),
        draft = f["draft"] == "true",
        body = doc.body,
    )
}

// Newest first.
fun sortByDate(articles: List<Article>): List<Article> =
    articles.sortedByDescending { it.date }

class Neighbours(val newer: Article?, val older: Article?)

fun neighbours(articles: List<Article>, slug: String): Neighbours {
    val i = articles.indexOfFirst { it.slug == slug }
    return Neighbours(
        newer = articles.getOrNull(if (i > 0) i - 1 else -1),
        older = articles.getOrNull(if (i >= 0) i + 1 else -1),
    )
}

// Articles with the most shared tags; ties go to the newest.
fun similar(articles: List<Article>, slug: String, count: Int = 3): List<Article> {
    val tags = articles.find { it.slug == slug }?.tags.orEmpty().toSet()
    return articles
        .filter { it.slug != slug }
        .map { it to it.tags.count { t -> t in tags } }
        .filter { (_, score) -> score > 0 }
        .sortedWith(
            compareByDescending<Pair<Article, Int>> { it.second }
                .thenByDescending { it.first.date },
        )
        .take(count)
        .map { it.first }
}
