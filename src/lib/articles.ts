import fs from "node:fs/promises";
import path from "node:path";
import matter from "gray-matter";
import rehypeShiki from "@shikijs/rehype";
import rehypeStringify from "rehype-stringify";
import remarkGfm from "remark-gfm";
import remarkParse from "remark-parse";
import remarkRehype from "remark-rehype";
import type { ShikiTransformer } from "shiki";
import { unified } from "unified";

export type Article = {
  slug: string;
  title: string;
  date: string;
  tags: string[];
  description: string;
  draft: boolean;
  body: string;
};

const dir = path.join(process.cwd(), "content/articles");

export function parseArticle(slug: string, raw: string): Article {
  const { data, content } = matter(raw);
  const fail = (field: string) => {
    throw new Error(`content/articles/${slug}.md: bad or missing "${field}"`);
  };
  const date =
    data.date instanceof Date ? data.date.toISOString().slice(0, 10) : "";
  if (typeof data.title !== "string" || !data.title) fail("title");
  if (!date) fail("date");
  if (!Array.isArray(data.tags) || data.tags.some((t) => typeof t !== "string"))
    fail("tags");
  if (typeof data.description !== "string") fail("description");
  return {
    slug,
    title: data.title,
    date,
    tags: data.tags,
    description: data.description,
    draft: data.draft === true,
    body: content,
  };
}

// Newest first.
export function sortByDate(articles: Article[]): Article[] {
  return [...articles].sort((a, b) => b.date.localeCompare(a.date));
}

export async function getArticles(): Promise<Article[]> {
  const files = (await fs.readdir(dir)).filter((f) => f.endsWith(".md"));
  const articles = await Promise.all(
    files.map(async (f) =>
      parseArticle(
        f.slice(0, -3),
        await fs.readFile(path.join(dir, f), "utf8"),
      ),
    ),
  );
  const isProd = process.env.NODE_ENV === "production";
  return sortByDate(articles.filter((a) => !isProd || !a.draft));
}

export function neighbours(articles: Article[], slug: string) {
  const i = articles.findIndex((a) => a.slug === slug);
  return {
    newer: i > 0 ? articles[i - 1] : undefined,
    older: i >= 0 ? articles[i + 1] : undefined,
  };
}

// Articles with the most shared tags; ties go to the newest.
export function similar(articles: Article[], slug: string, count = 3) {
  const tags = new Set(articles.find((a) => a.slug === slug)?.tags);
  return articles
    .filter((a) => a.slug !== slug)
    .map((a) => ({ a, score: a.tags.filter((t) => tags.has(t)).length }))
    .filter(({ score }) => score > 0)
    .sort((x, y) => y.score - x.score || y.a.date.localeCompare(x.a.date))
    .slice(0, count)
    .map(({ a }) => a);
}

// A copy button in every code block; CopyCode handles the click.
const copyButton: ShikiTransformer = {
  name: "copy-button",
  pre(pre) {
    pre.children.push({
      type: "element",
      tagName: "button",
      properties: { type: "button", dataCopy: "", ariaLive: "polite" },
      children: [{ type: "text", value: "copy" }],
    });
  },
};

export async function renderMarkdown(markdown: string): Promise<string> {
  const file = await unified()
    .use(remarkParse)
    .use(remarkGfm)
    // Raw HTML passes through as-is: only repo owners write articles.
    .use(remarkRehype, { allowDangerousHtml: true })
    .use(rehypeShiki, {
      themes: { light: "github-light", dark: "github-dark" },
      transformers: [copyButton],
    })
    .use(rehypeStringify, { allowDangerousHtml: true })
    .process(markdown);
  return String(file);
}
