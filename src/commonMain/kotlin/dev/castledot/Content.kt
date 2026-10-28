package dev.castledot

val about: About = parseAbout(GeneratedContent.about)

// Drafts are hidden unless Gradle runs with -PincludeDrafts.
val articles: List<Article> = sortByDate(
    GeneratedContent.articles
        .map { (slug, raw) -> parseArticle(slug, raw) }
        .filter { GeneratedContent.includeDrafts || !it.draft },
)
