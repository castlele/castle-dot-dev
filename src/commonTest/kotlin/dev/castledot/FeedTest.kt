package dev.castledot

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * **Test cases**
 * - [x] empty query returns all, newest first
 * - [x] search matches title, description, text and tags, any case
 * - [x] every selected tag must match
 * - [x] oldest order
 * - [x] tags are unique and sorted
 */

private fun item(slug: String, date: String, tags: List<String>, text: String = "") =
    Article(slug, slug, date, tags, "", false, text)

private val items = listOf(
    item("kotlin-tips", "2025-01-01", listOf("kotlin")),
    item("web-notes", "2026-01-01", listOf("web", "css"), "About Grid layout"),
    item("pixel-art", "2024-01-01", listOf("design", "css")),
)

private fun slugs(list: List<Article>) = list.map { it.slug }

class FeedTest {
    @Test
    fun `GIVEN empty query WHEN filtered THEN all, newest first`() {
        assertEquals(
            listOf("web-notes", "kotlin-tips", "pixel-art"),
            slugs(filterFeed(items, FeedQuery())),
        )
    }

    @Test
    fun `GIVEN text in body WHEN searched in caps THEN found`() {
        assertEquals(
            listOf("web-notes"),
            slugs(filterFeed(items, FeedQuery(search = " GRID "))),
        )
    }

    @Test
    fun `GIVEN tag name WHEN searched THEN found`() {
        assertEquals(
            listOf("pixel-art"),
            slugs(filterFeed(items, FeedQuery(search = "design"))),
        )
    }

    @Test
    fun `GIVEN two tags WHEN filtered THEN only articles with both`() {
        val result = filterFeed(items, FeedQuery(tags = setOf("css", "web")))

        assertEquals(listOf("web-notes"), slugs(result))
    }

    @Test
    fun `GIVEN oldest order WHEN filtered THEN oldest first`() {
        val result = filterFeed(
            items,
            FeedQuery(tags = setOf("css"), order = Order.Oldest),
        )

        assertEquals(listOf("pixel-art", "web-notes"), slugs(result))
    }

    @Test
    fun `GIVEN repeated tags WHEN collected THEN unique and sorted`() {
        assertEquals(listOf("css", "design", "kotlin", "web"), allTags(items))
    }
}
