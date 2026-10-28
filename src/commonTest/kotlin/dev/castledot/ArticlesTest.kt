package dev.castledot

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * **Test cases**
 * - [x] valid frontmatter parses into an article
 * - [x] missing field fails with the file path
 * - [x] draft flag defaults to false
 * - [x] sort puts the newest first
 * - [x] neighbours of the middle, first and unknown article
 * - [x] similar ranks by shared tags, skips self and zero matches
 * - [x] every file in content/ parses
 */

private fun article(slug: String, date: String, tags: List<String>) =
    Article(slug, slug, date, tags, "", false, "")

class ArticlesTest {
    @Test
    fun `GIVEN valid frontmatter WHEN parsed THEN fields are read`() {
        val raw = listOf(
            "---",
            "title: Hello",
            "date: 2026-01-02",
            "tags: [kotlin, web]",
            "description: First post",
            "---",
            "Body",
        ).joinToString("\n")

        assertEquals(
            Article(
                "hello", "Hello", "2026-01-02", listOf("kotlin", "web"),
                "First post", false, "Body",
            ),
            parseArticle("hello", raw),
        )
    }

    @Test
    fun `GIVEN no date WHEN parsed THEN error names file and field`() {
        val raw = "---\ntitle: Hi\ntags: []\ndescription: x\n---\n"

        val error = assertFailsWith<IllegalArgumentException> {
            parseArticle("hi", raw)
        }

        assertEquals(
            "content/articles/hi.md: bad or missing \"date\"",
            error.message,
        )
    }

    @Test
    fun `GIVEN draft true WHEN parsed THEN article is a draft`() {
        val raw = "---\ntitle: Hi\ndate: 2026-01-01\ntags: []\n" +
            "description: x\ndraft: true\n---\n"

        assertTrue(parseArticle("hi", raw).draft)
    }

    @Test
    fun `GIVEN no draft key WHEN parsed THEN article is not a draft`() {
        val raw = "---\ntitle: Hi\ndate: 2026-01-01\ntags: []\n" +
            "description: x\n---\n"

        assertFalse(parseArticle("hi", raw).draft)
    }

    private val list = sortByDate(
        listOf(
            article("old", "2024-01-01", listOf("a")),
            article("new", "2026-01-01", listOf("a", "b")),
            article("mid", "2025-01-01", listOf("b")),
        ),
    )

    @Test
    fun `GIVEN mixed dates WHEN sorted THEN newest is first`() {
        assertEquals(listOf("new", "mid", "old"), list.map { it.slug })
    }

    @Test
    fun `GIVEN middle article WHEN neighbours THEN both sides set`() {
        val n = neighbours(list, "mid")

        assertEquals("new", n.newer?.slug)
        assertEquals("old", n.older?.slug)
    }

    @Test
    fun `GIVEN newest article WHEN neighbours THEN no newer`() {
        assertNull(neighbours(list, "new").newer)
    }

    @Test
    fun `GIVEN unknown slug WHEN neighbours THEN none`() {
        val n = neighbours(list, "nope")

        assertNull(n.newer)
        assertNull(n.older)
    }

    @Test
    fun `GIVEN shared tags WHEN similar THEN ranked by overlap`() {
        val more = list + article("both", "2023-01-01", listOf("a", "b"))

        assertEquals(
            listOf("both", "mid", "old"),
            similar(more, "new").map { it.slug },
        )
    }

    @Test
    fun `GIVEN no shared tags WHEN similar THEN empty`() {
        val more = list + article("lone", "2026-02-01", listOf("z"))

        assertEquals(emptyList(), similar(more, "lone"))
    }

    @Test
    fun `GIVEN the content folder WHEN loaded THEN every file parses`() {
        assertTrue(articles.isNotEmpty())
        assertTrue(about.name.isNotEmpty())
    }
}
