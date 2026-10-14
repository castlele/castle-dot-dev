import type { Metadata } from "next";
import { Avatar } from "@/components/Avatar";
import { getAbout } from "@/lib/about";
import { renderMarkdown } from "@/lib/articles";
import styles from "./page.module.css";

export const metadata: Metadata = { title: "about" };

export default async function AboutPage() {
  const about = await getAbout();
  const html = await renderMarkdown(about.body);

  return (
    <>
      <section className={styles.head}>
        <Avatar src={about.avatar} name={about.name} />
        <div>
          <h1 className={styles.name}>{about.name}</h1>
          <p className={styles.role}>{about.role}</p>
        </div>
      </section>
      {/* Built from our own Markdown; remark-rehype drops raw HTML. */}
      <div dangerouslySetInnerHTML={{ __html: html }} />
      <ul className={styles.links}>
        {about.cv && (
          <li>
            <a href={about.cv}>cv (pdf)</a>
          </li>
        )}
        {about.links.map((l) => (
          <li key={l.url}>
            <a href={l.url} rel="me noopener">
              {l.label}
            </a>
          </li>
        ))}
      </ul>
    </>
  );
}
