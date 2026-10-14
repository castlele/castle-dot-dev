"use client";

import { useState } from "react";
import { ArticleList } from "@/components/ArticleList";
import { allTags, type FeedItem, type FeedQuery, filterFeed } from "@/lib/feed";
import styles from "./Feed.module.css";

export function Feed({ items }: { items: FeedItem[] }) {
  const [query, setQuery] = useState<FeedQuery>({
    search: "",
    tags: [],
    order: "newest",
  });
  const found = filterFeed(items, query);

  const toggleTag = (tag: string) =>
    setQuery((q) => ({
      ...q,
      tags: q.tags.includes(tag)
        ? q.tags.filter((t) => t !== tag)
        : [...q.tags, tag],
    }));

  return (
    <>
      <h1>feed</h1>
      <div className={styles.controls}>
        <input
          type="search"
          aria-label="Search articles"
          placeholder="search…"
          className={styles.field}
          value={query.search}
          onChange={(e) => setQuery({ ...query, search: e.target.value })}
        />
        <select
          aria-label="Sort by date"
          className={styles.field}
          value={query.order}
          onChange={(e) =>
            setQuery({ ...query, order: e.target.value as FeedQuery["order"] })
          }
        >
          <option value="newest">newest first</option>
          <option value="oldest">oldest first</option>
        </select>
      </div>
      <div className={styles.tags}>
        {allTags(items).map((tag) => (
          <button
            key={tag}
            type="button"
            className={styles.tag}
            aria-pressed={query.tags.includes(tag)}
            onClick={() => toggleTag(tag)}
          >
            #{tag}
          </button>
        ))}
      </div>
      {found.length > 0 ? (
        <ArticleList articles={found} />
      ) : (
        <p className={styles.empty}>nothing found</p>
      )}
    </>
  );
}
