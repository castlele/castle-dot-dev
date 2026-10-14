# Initial dev info

<!-- TODO: Mark this page as complete whenever the first version will be hosted online -->

## Description

Static web site showcasing info about me as a professional and my thoughts in
the form of articles.

## Technical Requirements

0. Main page with major navigation and short info about me:
   - greeting view with image and short info
   - latest articles with navigation to the full list
   - simple header navigation to: feed, about me
1. Feed with all articles:
   - searching by name and content, tag
   - filtering by: tags
   - sorting by: date
2. Article page:
   - title
   - content: text, links, images, code
   - tags
3. About me
   - avatar
   - info
   - CV link
   - contacts
4. Performance has to be super fluent
5. Fonts, images and everything has to be super personal to showcase who I am
6. It has to be an easy task to post an article or change info about me:
   - ADMIN can add new data
   - USER can only read data

## Restrictions

1. Safe to publish
2. No super personal info (no possibility to find my family or friends)
3. host itself has to be independent of my personal tools and data

## Architecture Design

Static site. No backend and no DB: the build reads Markdown files and writes
HTML. ADMIN = anyone who can push to `master`. USER = site visitor.

```mermaid
flowchart LR
    MD["content/*.md"] --> Build["next build (output: export)"]
    Build --> Out["out/ (HTML, CSS, JS)"]
    Out --> Image["Docker image: Caddy + out/"]
    Image --> VPS["VPS behind Cloudflare"]
```

- Stack: Next.js + React + TypeScript, CSS Modules, Pixelify Sans font.
- Search, tag filter and date sort run in the browser over an index that the
  build puts into the feed page.
- Deploy: push to `master` → GitHub Actions → image in GHCR → VPS pulls it.

## Front-end

URLs:

- `/` — greeting, latest articles
- `/feed/` — all articles: search, tag filter, date sort
- `/articles/{slug}/` — article, next/prev, similar by tags
- `/about/` — avatar, info, CV, contacts
