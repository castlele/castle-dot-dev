---
title: Hello, world
date: 2026-10-01
tags: [meta, web]
description: Why this site exists and how it is built.
---

This is the first article. The site is a static export of
[Next.js](https://nextjs.org). Every article is a Markdown file in git.

![A pixel castle](/articles/hello-world/pixel-castle.svg)

## How an article becomes a page

The build reads the file, renders it to HTML and writes `index.html`:

```ts
const html = await renderMarkdown(article.body);
```

> Less code, fewer bugs.
