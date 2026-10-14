# Deploy

Push to `master` → `.github/workflows/ci.yml` checks the code, builds the Docker
image (Caddy + static `out/`), pushes it to GHCR and restarts it on the VPS over
SSH.

## One-time setup

1. Buy the domain in Cloudflare. Add an `A` record to the VPS IP, proxied
   (orange cloud). SSL/TLS mode: **Full (strict)**.
2. Cloudflare → SSL/TLS → Origin Server → create a certificate. Save it on the
   VPS as `~/castle-dot-dev/certs/origin.pem` and `origin.key`.
3. On the VPS: install Docker. Put `compose.yml` into `~/castle-dot-dev/` and
   create `~/castle-dot-dev/.env` with `SITE_DOMAIN=<your domain>`.
4. GitHub repo → Settings:
   - Variables: `SITE_URL` = `https://<your domain>`.
   - Secrets: `VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY` (private key of a deploy
     user that can run `docker`).
5. After the first deploy: make the GHCR package public, or run
   `docker login ghcr.io` on the VPS with a read-only token.
6. Firewall: allow only 22, 80, 443. Optionally allow 80/443 only from
   [Cloudflare IPs](https://www.cloudflare.com/ips/).

## Local check

```bash
docker build -t site .
docker run --rm -p 8088:80 -e SITE_DOMAIN=:80 site
# open http://localhost:8088
```
