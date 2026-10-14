import type { Metadata } from "next";
import Link from "next/link";
import { notFound } from "next/navigation";
import { ArticleList } from "@/components/ArticleList";
import {
  getArticles,
  neighbours,
  renderMarkdown,
  similar,
} from "@/lib/articles";
import styles from "./page.module.css";

type Props = { params: Promise<{ slug: string }> };

export async function generateStaticParams() {
  return (await getArticles()).map(({ slug }) => ({ slug }));
}

export async function generateMetadata({ params }: Props): Promise<Metadata> {
  const { slug } = await params;
  const article = (await getArticles()).find((a) => a.slug === slug);
  if (!article) return {};
  return {
    title: article.title,
    description: article.description,
    openGraph: {
      type: "article",
      title: article.title,
      description: article.description,
      publishedTime: article.date,
      tags: article.tags,
      url: `/articles/${slug}/`,
    },
  };
}

export default async function ArticlePage({ params }: Props) {
  const { slug } = await params;
  const articles = await getArticles();
  const article = articles.find((a) => a.slug === slug);
  if (!article) notFound();

  const html = await renderMarkdown(article.body);
  const { newer, older } = neighbours(articles, slug);
  const related = similar(articles, slug);

  return (
    <article>
      <h1 className={styles.title}>{article.title}</h1>
      <p className={styles.meta}>
        <time dateTime={article.date}>{article.date}</time>
        {article.tags.map((t) => (
          <span key={t} className={styles.tag}>
            #{t}
          </span>
        ))}
      </p>
      <div
        className={styles.content}
        // Built from our own Markdown; remark-rehype drops raw HTML.
        dangerouslySetInnerHTML={{ __html: html }}
      />
      <nav className={styles.pager}>
        {older ? (
          <Link href={`/articles/${older.slug}/`}>← {older.title}</Link>
        ) : (
          <span />
        )}
        {newer && (
          <Link href={`/articles/${newer.slug}/`}>{newer.title} →</Link>
        )}
      </nav>
      {related.length > 0 && (
        <section>
          <h2 className={styles.subtitle}>similar</h2>
          <ArticleList articles={related} />
        </section>
      )}
    </article>
  );
}
