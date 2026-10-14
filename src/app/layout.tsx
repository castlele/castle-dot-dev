import type { Metadata } from "next";
import { Pixelify_Sans } from "next/font/google";
import Link from "next/link";
import { site } from "@/lib/site";
import "@/styles/tokens.css";
import styles from "./layout.module.css";

const pixel = Pixelify_Sans({
  subsets: ["latin"],
  variable: "--font-pixel",
});

export const metadata: Metadata = {
  metadataBase: new URL(site.url),
  title: { default: site.name, template: `%s · ${site.name}` },
};

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en" className={pixel.variable}>
      <body>
        <div className={styles.page}>
          <header className={styles.header}>
            <Link href="/" className={styles.logo}>
              {site.name}
            </Link>
            <nav className={styles.nav}>
              <Link href="/feed/">feed</Link>
              <Link href="/about/">about</Link>
            </nav>
          </header>
          <main>{children}</main>
        </div>
      </body>
    </html>
  );
}
