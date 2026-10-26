# Workflows

| File | Trigger | What it does |
|---|---|---|
| `ci.yml` | push / PR to `main`, manual | Maven build + tests + JaCoCo, ESLint/Stylelint/HTMLHint, CodeQL (Java & JS), OWASP Dependency-Check, Trivy fs scan, Docker build (not pushed) + Trivy image scan + health smoke test |
| `cd.yml` | push to `main`, tags `v*`, manual | Push multi-arch image to `ghcr.io/shubhamkhedkar/weather-forecast-app`, then deploy to `staging` and (tags only) `production` environments — **deploy steps are placeholders** |
| `release.yml` | tags `v*` | Builds the jar and publishes a GitHub Release with auto-generated notes + SHA256 checksums |

Dependabot config lives in `../dependabot.yml`.

> **Note:** GitHub Actions is currently **disabled** on this repository, so none of these run yet.
> Enable it under *Settings → Actions → General* when you're ready.

Quick tasks:
- Cut a release: `git tag v1.0.0 && git push origin v1.0.0`
- Run locally before pushing: `./mvnw verify && npm ci && npm run lint`

Full details: [`docs/CI-CD.md`](../../docs/CI-CD.md).
