import type { Metadata } from "next";
import { getArticles } from "@/lib/articles";
import { Feed } from "./Feed";

export const metadata: Metadata = { title: "feed" };

export default async function FeedPage() {
  // shortcut: the whole index ships in the page HTML; move to a JSON file
  // or Pagefind when the feed grows past a few hundred articles.
  const items = (await getArticles()).map((a) => ({
    slug: a.slug,
    title: a.title,
    date: a.date,
    tags: a.tags,
    description: a.description,
    text: a.body,
  }));
  return <Feed items={items} />;
}
