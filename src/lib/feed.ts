import type { Article } from "./articles";

export type FeedItem = Pick<
  Article,
  "slug" | "title" | "date" | "tags" | "description"
> & { text: string };

export type FeedQuery = {
  search: string;
  tags: string[];
  order: "newest" | "oldest";
};

export function allTags(items: FeedItem[]): string[] {
  return [...new Set(items.flatMap((i) => i.tags))].sort();
}

// Search is case-insensitive; every selected tag must be on the article.
export function filterFeed(items: FeedItem[], query: FeedQuery): FeedItem[] {
  const search = query.search.trim().toLowerCase();
  const found = items.filter(
    (i) =>
      query.tags.every((t) => i.tags.includes(t)) &&
      [i.title, i.description, i.text, ...i.tags].some((s) =>
        s.toLowerCase().includes(search),
      ),
  );
  const sign = query.order === "newest" ? -1 : 1;
  return found.sort((a, b) => sign * a.date.localeCompare(b.date));
}
