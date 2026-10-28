# AGENTS.md

This file gives guidance to AI coding agents in this repository.

## Project

Personal site: short info about the author and their articles. Static
Compose Multiplatform web app (Kotlin/Wasm). No backend, no DB. Content is
Markdown in git. To publish = to push to `master`.

Read only the spec you need:

- Requirements and URLs — `docs/dev.md`
- Deploy and server setup — `docs/deploy.md`

## Commands

**Run freely** — run these to check your change before you report it done:

```bash
./gradlew jvmTest                       # all tests (logic, no browser)
./gradlew wasmJsBrowserDistribution     # production build into build/dist/
```

**Ask first:**

```bash
./gradlew wasmJsBrowserDevelopmentRun   # long-running dev server
docker build -t site .                  # full image build
```

Add `-PincludeDrafts` to a Gradle command to include `draft: true` articles.

## Content

- Article: `content/articles/<slug>.md`. The file name is the URL slug.
  Frontmatter: `title`, `date` (YYYY-MM-DD), `tags` (list), `description`,
  optional `draft: true` (hidden from production builds).
- Article images: `src/commonMain/composeResources/files/articles/<slug>/`,
  linked as `/articles/<slug>/<file>`.
- About page: `content/about.md`. Avatar: `src/commonMain/composeResources/files/`
  (`avatar: /avatar.svg`). CV: `src/wasmJsMain/resources/`, linked as `/cv.pdf`.
- The font has no arrow glyphs. Write `->` and `<-`, not `→` and `←`.
- Safe to publish: no data that can lead to family or friends.

## Code rules

- Kotlin official style, 80 columns. Write code that already fits.
- Colors, spacing and font sizes come only from `Theme.kt`.
- `src/commonMain`: pure logic and content, no UI. `src/wasmJsMain`: Compose UI
  and browser glue. Tests for logic live in `src/commonTest`, run on the JVM.
- Test names: `` `GIVEN … WHEN … THEN …` ``.
- Commit messages: `type(scope): message`, for example `feat(feed):`,
  `fix(articles):`, `chore(deploy):`. One subject line only: no body, no
  trailers (no `Co-Authored-By`).
- Branches: work directly on `master`. Do not create branches or worktrees
  unless the user asks for one.
- Do not commit or push. Leave changes uncommitted; the user commits them.
