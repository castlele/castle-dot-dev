import Link from "next/link";
import type { Article } from "@/lib/articles";
import styles from "./ArticleList.module.css";

type Props = {
  articles: Pick<Article, "slug" | "title" | "date" | "description">[];
};

export function ArticleList({ articles }: Props) {
  return (
    <ul className={styles.list}>
      {articles.map((a) => (
        <li key={a.slug} className={styles.item}>
          <time className={styles.date} dateTime={a.date}>
            {a.date}
          </time>
          <Link href={`/articles/${a.slug}/`}>{a.title}</Link>
          {a.description && (
            <p className={styles.description}>{a.description}</p>
          )}
        </li>
      ))}
    </ul>
  );
}
