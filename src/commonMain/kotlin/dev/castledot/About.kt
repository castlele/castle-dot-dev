package dev.castledot

data class Link(val label: String, val url: String)

data class About(
    val name: String,
    val role: String,
    val greeting: String,
    val avatar: String,
    val cv: String?,
    val links: List<Link>,
    val body: String,
)

fun parseAbout(raw: String): About {
    val doc = parseDocument(raw)
    fun text(field: String) = doc.fields[field] as? String
        ?: throw IllegalArgumentException(
            "content/about.md: bad or missing \"$field\"",
        )
    return About(
        name = text("name"),
        role = text("role"),
        greeting = text("greeting"),
        avatar = text("avatar"),
        cv = doc.fields["cv"] as? String,
        links = (doc.fields["links"] as? List<*>).orEmpty()
            .filterIsInstance<Map<*, *>>()
            .map { Link(it["label"] as String, it["url"] as String) },
        body = doc.body,
    )
}
