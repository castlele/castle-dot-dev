# AGENTS.md

This file gives guidance to AI coding agents in this repository.

## Project

Personal site: short info about the author and their articles. Static Next.js
export (`output: "export"`), React + TypeScript. No backend, no DB. Content is
Markdown in git. To publish = to push to `master`.

Read only the spec you need:

- Requirements and URLs — `docs/dev.md`
- Deploy and server setup — `docs/deploy.md`
- Writing articles: frontmatter, images, drafts — `docs/articles.md`

## Commands

**Run freely** — run these to check your change before you report it done:

```bash
npm test                       # Vitest, all tests
npx vitest run src/lib/feed    # one test file
npm run lint                   # ESLint
npm run build                  # static export into out/
```

**Ask first:**

```bash
npm run format                 # rewrites every file in the repo
npm run dev                    # long-running dev server
docker build -t site .         # full image build
```

A Claude Code hook formats the files you edit at the end of each turn
(`.claude/hooks/format.sh`). Do not run Prettier yourself.

## Content

- Articles: `content/articles/<slug>.md`. Rules in `docs/articles.md`.
- About page: `content/about.md`. Avatar and CV: `public/`.
- Safe to publish: no data that can lead to family or friends.

## Code rules

- Prettier, **80 columns**. Write code that already fits.
- Styles: CSS Modules next to the component. Colors, spacing and sizes come only
  from the tokens in `src/styles/tokens.css`.
- Pages are Server Components. Use `'use client'` only for interactivity (the
  feed search).
- Pure logic lives in `src/lib/` and has Vitest tests in `*.test.ts` next to it.
  Test names: `` `GIVEN … WHEN … THEN …` ``.
- Commit messages: `type(scope): message`, for example `feat(feed):`,
  `fix(articles):`, `chore(deploy):`. One subject line only: no body, no
  trailers (no `Co-Authored-By`).
- Branches: work directly on `master`. Do not create branches or worktrees
  unless the user asks for one.
- Do not commit or push. Leave changes uncommitted; the user commits them.

<!-- BEGIN:nextjs-agent-rules -->

## This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may
all differ from your training data. Read the relevant guide in
`node_modules/next/dist/docs/` (resolved from this file's directory; in
monorepos the `next` package may not be visible from the repo root) before
writing any code. Heed deprecation notices.

This block is written and re-added by `next dev` — verify at
`node_modules/next/dist/server/lib/generate-agent-files.js`. Removing it from a
diff only re-creates the uncommitted change; committing it with your work keeps
the tree clean.

<!-- END:nextjs-agent-rules -->
