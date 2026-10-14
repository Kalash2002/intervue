# web

The Dryrun website. Phase 0: one landing page with the promise, three prototype screenshots and the waitlist form (PLAN.md section 13, days 3 to 5).

## Run locally

```sh
pnpm install
pnpm dev        # page only, on :5173; /api is proxied to :8788
pnpm preview    # build, then serve page and functions with local KV on :8788
```

Run `pnpm preview` in a second terminal while `pnpm dev` is up to test the form with hot reload. Local KV lives in `.wrangler/` and is never committed.

Gates, from the repository root: `pnpm -r lint && pnpm -r typecheck && pnpm -r test`.

## Screenshots

Drop three PNGs into `public/screens/` named `screen-1.png`, `screen-2.png`, `screen-3.png`, in this order: the timed session, the assistant, the score and review. Keep each under about 300 kB (about 1200 px wide, compressed); they load near the top of the page.

## Environment

Copy `.env.example` to `.env.local`. In Cloudflare, set the same variables under the Pages project's settings for Production and Preview. All are optional: without `VITE_POSTHOG_KEY` analytics stay off, and without `VITE_PROTOTYPE_URL` the prototype download line is hidden.

PostHog events: `$pageview` (automatic), `waitlist_joined` with role, language and pay (no email), and `prototype_download_clicked`.

## Deploy to Cloudflare Pages (once)

1. `pnpm exec wrangler login`
2. `pnpm exec wrangler kv namespace create WAITLIST` and put the printed id into `wrangler.toml`.
3. Either connect the GitHub repository in the Cloudflare dashboard (Workers & Pages, Create, Pages, Connect to Git) with root directory `web`, build command `pnpm install --frozen-lockfile && pnpm build` and output directory `dist`, which gives a preview per PR and production on merge to `main`; or deploy from this machine with `pnpm pages:deploy`.
4. Add the environment variables above, then redeploy. Check the first build log uses pnpm and the root `pnpm-lock.yaml`; if it ran `npm install`, set the `PNPM_VERSION` environment variable in the Pages project.
5. In PostHog project settings, turn on "Discard client IP data".

## Read the waitlist

```sh
pnpm exec wrangler kv key list --binding WAITLIST --remote
pnpm exec wrangler kv key get --binding WAITLIST --remote "signup:someone@example.com"
```

Each value is JSON: email, role, companies, language, pay, country (from Cloudflare) and createdAt. Keys are `signup:<email>`, so a resubmission replaces the earlier entry.
