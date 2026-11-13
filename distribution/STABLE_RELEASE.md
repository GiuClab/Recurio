# Recurio 1.0.0 release preparation

The source is prepared for version 1.0.0 (versionCode 10). This document does not assert that a stable APK has been built, tested on a device or published.

The stable workflow builds `Recurio-1.0.0.apk` and `SHA256SUMS.txt`, after unit tests, debug/release lint, signature verification and comparison against the expected certificate. It does not publish a GitHub release automatically.

## Signing setup

Use a production signing key stored outside Git. Keep an offline backup of the keystore, alias and passwords. Do not paste private keys or passwords into issues, pull requests or chat.

Configure repository Actions secrets: `RECURIO_KEYSTORE_BASE64`, `RECURIO_STORE_PASSWORD`, `RECURIO_KEY_ALIAS`, `RECURIO_KEY_PASSWORD`. Set the Actions variable `RECURIO_CERT_SHA256` to the production certificate SHA-256 fingerprint, obtained independently from the keystore. The workflow refuses missing signing inputs or an unexpected certificate.

Alpha09 used a development certificate. A new production certificate is not a compatible in-place update of alpha09. Before uninstalling alpha09, export an encrypted backup and keep the password. Install the stable build and restore the backup. If compatibility with the existing installation is required, first locate and assess the existing signing key; do not silently replace it.

## Final verification and publication

Run **Build stable release** on the reviewed source commit. Download the stable artifact. On an Android device, verify a real reminder with amount/currency and service logo, a payment without a website (initial), icon downloads disabled, lock-screen privacy, tap-to-open and encrypted backup/restore. The service image uses a bundled logo or cached favicon; if it is missing, the notification uses the payment initial. No network request starts when a reminder is received.

After these checks, publish a regular GitHub release at tag `v1.0.0`, targeting that exact source commit, titled `Recurio 1.0.0`. Attach the verified `Recurio-1.0.0.apk` and `SHA256SUMS.txt`; do not select pre-release. Use `distribution/RELEASE_NOTES.md`. Keep old alpha releases as historical versions. F-Droid remains unavailable until actually listed.
