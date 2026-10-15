import fs from "node:fs/promises";
import path from "node:path";
import matter from "gray-matter";

export type About = {
  name: string;
  role: string;
  greeting: string;
  avatar: string;
  cv?: string;
  links: { label: string; url: string }[];
  body: string;
};

export async function getAbout(): Promise<About> {
  const file = path.join(process.cwd(), "content/about.md");
  const { data, content } = matter(await fs.readFile(file, "utf8"));

  for (const field of ["name", "role", "greeting", "avatar"]) {
    if (typeof data[field] !== "string") {
      throw new Error(`content/about.md: bad or missing "${field}"`);
    }
  }

  return { ...data, links: data.links ?? [], body: content } as About;
}
