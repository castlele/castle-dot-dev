# Deploy

Push to `master` → `.github/workflows/ci.yml` checks the code, builds the Docker
image (Caddy + static `out/`), pushes it to GHCR and restarts it on the VPS over
SSH.

The VPS already runs a front Caddy (`firefly_caddy`, Docker) on ports 80/443.
The site container has no public ports. It joins the front Caddy's Docker
network, and the front Caddy proxies `castledot.dev` to it.

```
browser → Cloudflare → firefly_caddy :443 → castledot_site :80 → /srv
```

## One-time setup

1. Cloudflare DNS for `castledot.dev`: `A @ 72.56.110.238` and
   `CNAME www castledot.dev`, both proxied (orange cloud). SSL/TLS mode: **Full
   (strict)**.
2. Cloudflare → SSL/TLS → Origin Server → create a certificate for
   `castledot.dev, *.castledot.dev`. Save it next to the firefly compose file as
   `castledot-certs/origin.pem` and `castledot-certs/origin.key`.
3. Firefly compose, service `firefly_caddy`: mount the certs.

   ```yaml
   volumes:
     - ./castledot-certs:/certs/castledot:ro
   ```

4. Firefly Caddyfile: add the site block.

   ```caddyfile
   castledot.dev, www.castledot.dev {
   	tls /certs/castledot/origin.pem /certs/castledot/origin.key
   	reverse_proxy castledot_site:80
   }
   ```

5. Find the front Caddy's network:
   `docker inspect firefly_caddy -f '{{range $k, $v := .NetworkSettings.Networks}}{{$k}}{{end}}'`.
   Create `~/castle-dot-dev/.env` with `PROXY_NETWORK=<that name>` and copy
   `compose.yml` into `~/castle-dot-dev/`.
6. GitHub repo → Settings → Secrets and variables → Actions:
   - Variable `SITE_URL` = `https://castledot.dev`.
   - Secrets `VPS_HOST` = `72.56.110.238`, `VPS_USER`, `VPS_SSH_KEY` (private
     key of a user that can run `docker`).
7. Push to `master`. After the first image lands in GHCR, make the package
   public (GitHub → Packages → castle-dot-dev → Settings), or run
   `docker login ghcr.io` on the VPS with a `read:packages` token.
8. Restart the front Caddy so it picks up the new block and mount:
   `docker compose up -d firefly_caddy` in the firefly folder.

## Local check

```bash
docker build -t site .
docker run --rm -p 8088:80 site
# open http://localhost:8088
```
