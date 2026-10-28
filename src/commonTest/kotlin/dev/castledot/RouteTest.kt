package dev.castledot

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * **Test cases**
 * - [x] each known path maps to its route, with or without trailing slash
 * - [x] unknown path is not found
 * - [x] route path parses back to the same route
 */

class RouteTest {
    @Test
    fun `GIVEN known paths WHEN parsed THEN routes match`() {
        assertEquals(Route.Home, parseRoute("/"))
        assertEquals(Route.Feed, parseRoute("/feed/"))
        assertEquals(Route.About, parseRoute("/about"))
        assertEquals(Route.Article("hello"), parseRoute("/articles/hello/"))
    }

    @Test
    fun `GIVEN unknown path WHEN parsed THEN not found`() {
        assertEquals(Route.NotFound, parseRoute("/articles/"))
        assertEquals(Route.NotFound, parseRoute("/nope/x/y"))
    }

    @Test
    fun `GIVEN a route WHEN printed and parsed THEN same route`() {
        val route = Route.Article("pixel-fonts")

        assertEquals(route, parseRoute(route.path()))
    }
}
