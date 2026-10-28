package dev.castledot

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * **Test cases**
 * - [x] about page reads scalars, comments and the links block
 * - [x] text without frontmatter is all body
 */

class DocumentTest {
    @Test
    fun `GIVEN about file WHEN parsed THEN links and optional cv are read`() {
        val raw = """
            |---
            |name: castle
            |role: Engineer
            |greeting: Hi!
            |avatar: /avatar.svg
            |# cv: /cv.pdf
            |links:
            |  - label: github
            |    url: https://github.com/castlele
            |  - label: email
            |    url: mailto:hello@example.com
            |---
            |
            |Body text.
        """.trimMargin()

        val about = parseAbout(raw)

        assertEquals(null, about.cv)
        assertEquals(
            listOf(
                Link("github", "https://github.com/castlele"),
                Link("email", "mailto:hello@example.com"),
            ),
            about.links,
        )
        assertEquals("Body text.", about.body)
    }

    @Test
    fun `GIVEN no frontmatter WHEN parsed THEN everything is body`() {
        val doc = parseDocument("Just text\n---\nmore")

        assertEquals(emptyMap(), doc.fields)
        assertEquals("Just text\n---\nmore", doc.body)
    }
}
