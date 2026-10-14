import { describe, expect, test } from "vitest";
import {
  type Article,
  neighbours,
  parseArticle,
  similar,
  sortByDate,
} from "./articles";

/**
 * **Test cases**
 * - [x] valid frontmatter parses into an article
 * - [x] missing field fails with the file path
 * - [x] draft flag defaults to false
 * - [x] sort puts the newest first
 * - [x] neighbours of the middle, first and unknown article
 * - [x] similar ranks by shared tags, skips self and zero matches
 */

const article = (slug: string, date: string, tags: string[]): Article => ({
  slug,
  title: slug,
  date,
  tags,
  description: "",
  draft: false,
  body: "",
});

describe("parseArticle", () => {
  test("GIVEN valid frontmatter WHEN parsed THEN fields are read", () => {
    const raw = [
      "---",
      "title: Hello",
      "date: 2026-01-02",
      "tags: [kotlin, web]",
      "description: First post",
      "---",
      "Body",
    ].join("\n");

    expect(parseArticle("hello", raw)).toEqual({
      slug: "hello",
      title: "Hello",
      date: "2026-01-02",
      tags: ["kotlin", "web"],
      description: "First post",
      draft: false,
      body: "Body",
    });
  });

  test("GIVEN no date WHEN parsed THEN error names file and field", () => {
    const raw = "---\ntitle: Hi\ntags: []\ndescription: x\n---\n";

    expect(() => parseArticle("hi", raw)).toThrow(
      'content/articles/hi.md: bad or missing "date"',
    );
  });

  test("GIVEN draft true WHEN parsed THEN article is a draft", () => {
    const raw =
      "---\ntitle: Hi\ndate: 2026-01-01\ntags: []\ndescription: x\n" +
      "draft: true\n---\n";

    expect(parseArticle("hi", raw).draft).toBe(true);
  });
});

describe("ordering and links", () => {
  const list = sortByDate([
    article("old", "2024-01-01", ["a"]),
    article("new", "2026-01-01", ["a", "b"]),
    article("mid", "2025-01-01", ["b"]),
  ]);

  test("GIVEN mixed dates WHEN sorted THEN newest is first", () => {
    expect(list.map((a) => a.slug)).toEqual(["new", "mid", "old"]);
  });

  test("GIVEN middle article WHEN neighbours THEN both sides set", () => {
    const { newer, older } = neighbours(list, "mid");

    expect(newer?.slug).toBe("new");
    expect(older?.slug).toBe("old");
  });

  test("GIVEN newest article WHEN neighbours THEN no newer", () => {
    expect(neighbours(list, "new").newer).toBeUndefined();
  });

  test("GIVEN unknown slug WHEN neighbours THEN none", () => {
    expect(neighbours(list, "nope")).toEqual({
      newer: undefined,
      older: undefined,
    });
  });

  test("GIVEN shared tags WHEN similar THEN ranked by overlap", () => {
    const more = [...list, article("both", "2023-01-01", ["a", "b"])];

    expect(similar(more, "new").map((a) => a.slug)).toEqual([
      "both",
      "mid",
      "old",
    ]);
  });

  test("GIVEN no shared tags WHEN similar THEN empty", () => {
    const more = [...list, article("lone", "2026-02-01", ["z"])];

    expect(similar(more, "lone")).toEqual([]);
  });
});
