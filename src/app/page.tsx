import Link from "next/link";
import { ArticleList } from "@/components/ArticleList";
import { Avatar } from "@/components/Avatar";
import { getAbout } from "@/lib/about";
import { getArticles } from "@/lib/articles";
import styles from "./page.module.css";

export default async function Home() {
  const about = await getAbout();
  const latest = (await getArticles()).slice(0, 5);

  return (
    <>
      <section className={styles.greeting}>
        <Avatar src={about.avatar} name={about.name} />
        <div>
          <h1 className={styles.name}>{about.name}</h1>
          <p className={styles.role}>{about.role}</p>
          <p>
            {about.greeting} <Link href="/about/">more →</Link>
          </p>
        </div>
      </section>
      <section>
        <h2>latest</h2>
        <ArticleList articles={latest} />
        <p className={styles.all}>
          <Link href="/feed/">all articles →</Link>
        </p>
      </section>
    </>
  );
}
