# Writing articles

One article = one Markdown file. Push it to `master` and it is live.

## File

`content/articles/<slug>.md`. The file name is the URL:
`content/articles/pixel-fonts.md` → `/articles/pixel-fonts/`.

Slug: lowercase words joined with `-`. Changing a slug breaks old links.

## Frontmatter

```yaml
---
title: Why a pixel font
date: 2026-10-10
tags: [design, web]
description: Notes on picking a pixel font that you can still read.
draft: true # optional
---
```

- `title` — required, not empty. The page shows it as the `h1`.
- `date` — `YYYY-MM-DD`, without quotes. A quoted date is a string, and the
  build rejects it. Feed order and prev/next links come from this date.
- `tags` — a list, may be empty. Lowercase. Reuse existing tags: the feed
  filters by them and "similar" articles are picked by shared tags.
- `description` — one sentence. Shown in article lists and link previews.
- `draft: true` — visible in `npm run dev`, left out of the production build.

A bad or missing field stops the build with the file name and the field.

## Body

GitHub-flavored Markdown: headings, lists, links, quotes, tables, task lists,
strikethrough.

- Start sections at `##`. The title is already the `h1`.
- Code blocks name the language for highlighting: ` ```ts `.
- Raw HTML and inline SVG render as written. Color them with tokens in `style`
  (`style="fill: var(--color-accent)"`) so they follow dark mode. `<script>`
  does not run. Markdown inside an HTML block needs a blank line after the
  opening tag.

## Links to articles

Use the root path with a trailing slash:

```md
See [why a pixel font](/articles/pixel-fonts/).
```

Nothing checks these links. Renaming a slug or linking to a draft gives a 404.

## Images

Put files in `public/articles/<slug>/` and link them from the site root:

```md
![A pixel castle](/articles/hello-world/pixel-castle.svg)
```

Always write the alt text.

## Privacy

Safe to publish: nothing that can lead to family or friends — names, photos,
places, dates.

## Check

```bash
npm run dev     # preview, drafts included
npm run build   # fails on bad frontmatter
```
