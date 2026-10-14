import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "export",
  trailingSlash: true,
  // Static export has no image server.
  images: { unoptimized: true },
};

export default nextConfig;
