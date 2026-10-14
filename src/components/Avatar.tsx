import Image from "next/image";
import styles from "./Avatar.module.css";

export function Avatar({ src, name }: { src: string; name: string }) {
  return (
    <Image
      className={styles.avatar}
      src={src}
      alt={`${name}'s avatar`}
      width={128}
      height={128}
      priority
    />
  );
}
