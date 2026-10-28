# Deploy

Push to `master` → `.github/workflows/ci.yml` checks the code, builds the Docker
image (Caddy + the Wasm build), pushes it to GHCR and restarts it on the VPS over
SSH.

The VPS also runs firefly, and its Caddy owns ports 80/443. The site does not
touch firefly: its own Caddy listens on host port 8443 with a Cloudflare Origin
Certificate. A Cloudflare Origin Rule sends `castledot.dev` traffic to 8443.

```
browser → Cloudflare :443 → VPS :8443 → castledot_site (Caddy) → /srv
```

## One-time setup

1. Cloudflare, zone **castledot.dev** → DNS → Records:
   - `A`, name `@`, `72.56.110.238`, Proxied.
   - `CNAME`, name `www`, target `castledot.dev`, Proxied.
2. Zone castledot.dev → SSL/TLS → Overview: mode **Full (strict)**.
3. Zone castledot.dev → Rules → Origin Rules → Create rule:
   - When: Hostname is in `castledot.dev`, `www.castledot.dev`.
   - Then: Destination Port → Rewrite to `8443`.
4. Zone castledot.dev → SSL/TLS → Origin Server → Create Certificate for
   `castledot.dev, *.castledot.dev`. On the VPS save it as
   `~/castle-dot-dev/certs/origin.pem` and `~/castle-dot-dev/certs/origin.key`.
5. On the VPS: copy `compose.yml` into `~/castle-dot-dev/`. Open port 8443 in
   the firewall (`ufw allow 8443/tcp`, and in the hosting panel if it has a
   firewall).
6. GitHub repo → Settings → Secrets and variables → Actions:
   - Secrets `VPS_HOST` = `72.56.110.238`, `VPS_USER`, `VPS_SSH_KEY` (private
     key of a user that can run `docker`).
7. Push to `master`. After the first image lands in GHCR, make the package
   public (GitHub → Packages → castle-dot-dev → Settings), or run
   `docker login ghcr.io` on the VPS with a `read:packages` token. Then re-run
   the failed deploy job.

## Local check

```bash
docker build -t site .
docker run --rm -p 8088:80 site
# open http://localhost:8088
```
