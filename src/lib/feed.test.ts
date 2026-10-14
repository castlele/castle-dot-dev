import { describe, expect, test } from "vitest";
import { allTags, type FeedItem, type FeedQuery, filterFeed } from "./feed";

/**
 * **Test cases**
 * - [x] empty query returns all, newest first
 * - [x] search matches title, description, text and tags, any case
 * - [x] every selected tag must match
 * - [x] oldest order
 * - [x] tags are unique and sorted
 */

const item = (
  slug: string,
  date: string,
  tags: string[],
  text = "",
): FeedItem => ({ slug, title: slug, date, tags, description: "", text });

const items = [
  item("kotlin-tips", "2025-01-01", ["kotlin"]),
  item("web-notes", "2026-01-01", ["web", "css"], "About Grid layout"),
  item("pixel-art", "2024-01-01", ["design", "css"]),
];

const query = (q: Partial<FeedQuery>): FeedQuery => ({
  search: "",
  tags: [],
  order: "newest",
  ...q,
});

const slugs = (list: FeedItem[]) => list.map((i) => i.slug);

describe("filterFeed", () => {
  test("GIVEN empty query WHEN filtered THEN all, newest first", () => {
    expect(slugs(filterFeed(items, query({})))).toEqual([
      "web-notes",
      "kotlin-tips",
      "pixel-art",
    ]);
  });

  test("GIVEN text in body WHEN searched in caps THEN found", () => {
    expect(slugs(filterFeed(items, query({ search: " GRID " })))).toEqual([
      "web-notes",
    ]);
  });

  test("GIVEN tag name WHEN searched THEN found", () => {
    expect(slugs(filterFeed(items, query({ search: "design" })))).toEqual([
      "pixel-art",
    ]);
  });

  test("GIVEN two tags WHEN filtered THEN only articles with both", () => {
    const result = filterFeed(items, query({ tags: ["css", "web"] }));

    expect(slugs(result)).toEqual(["web-notes"]);
  });

  test("GIVEN oldest order WHEN filtered THEN oldest first", () => {
    const result = filterFeed(items, query({ order: "oldest", tags: ["css"] }));

    expect(slugs(result)).toEqual(["pixel-art", "web-notes"]);
  });
});

describe("allTags", () => {
  test("GIVEN repeated tags WHEN collected THEN unique and sorted", () => {
    expect(allTags(items)).toEqual(["css", "design", "kotlin", "web"]);
  });
});
