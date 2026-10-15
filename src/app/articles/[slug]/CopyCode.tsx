"use client";

import { useEffect } from "react";

// Handles the copy buttons that renderMarkdown puts into code blocks.
export function CopyCode() {
  useEffect(() => {
    const onClick = async (e: MouseEvent) => {
      const button = (e.target as Element).closest("button[data-copy]");
      const code = button?.parentElement?.querySelector("code");
      if (!button || !code) return;
      try {
        await navigator.clipboard.writeText(code.textContent ?? "");
        button.textContent = "copied";
      } catch {
        button.textContent = "failed";
      }
      setTimeout(() => (button.textContent = "copy"), 1500);
    };
    document.addEventListener("click", onClick);
    return () => document.removeEventListener("click", onClick);
  }, []);

  return null;
}
