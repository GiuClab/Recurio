<div align="center">
  <img src="assets/recurio-logo.webp" width="150" alt="Recurio logo">
  <h1>Recurio</h1>
  <p>Your payments, in one place — by GiuClab</p>

[![Android](https://img.shields.io/badge/Android-8%2B-16875C)](#features)
[![Download](https://img.shields.io/badge/download-v1.0-385AD0)](https://github.com/GiuClab/Recurio/releases/tag/v1.0)
[![License](https://img.shields.io/badge/license-GPL--3.0--or--later-385AD0)](LICENSE)
[![Android checks](https://github.com/GiuClab/Recurio/actions/workflows/android.yml/badge.svg)](https://github.com/GiuClab/Recurio/actions/workflows/android.yml)

[Download Recurio-v1.0.apk](https://github.com/GiuClab/Recurio/releases/download/v1.0/Recurio-v1.0.apk) · [Feedback & suggestions](https://github.com/GiuClab/Recurio/issues/new/choose) · [Support on Ko-fi](https://ko-fi.com/giuclab/tip)

[Italiano](docs/README.it.md) · [Deutsch](docs/README.de.md) · [Français](docs/README.fr.md)
</div>

Recurio is a free, open source Android payment planner with local reminders. The current download is **Recurio-v1.0.apk**, with service images and amounts in payment notifications. The release label is v1.0; the internal app version and in-app text remain unchanged. This APK is still debug-signed; see installation details below. F-Droid publication is planned; no official listing exists yet. Current UI screenshots will be added after device verification; no mockup is presented as an app screenshot.

## Features
- 🗓 Add, edit, pause and delete payments with one-time, weekly, monthly, quarterly or yearly recurrence.
- 💶 Calendar-month totals and next-12-month forecasts, separated by currency. No automatic exchange conversion or bank charge verification.
- 🔗 Share a browser link to Recurio, paste it or select a service from the searchable 145-entry catalog. Editable name suggestions recognize domains and known product paths.
- 🔔 Optional local reminders with amount, currency and a bundled/cached service logo (or payment initial), 0–365 days of advance notice and chosen time. Receiving a reminder does not start a website request. Singular/plural reminder text in all four languages. Android may delay notifications to save battery.
- 🔐 Password-encrypted manual export and restore. Restore previews the record count and replaces existing payments only after confirmation.
- 🌍 Automatic supported phone language on first install; otherwise an English chooser for English, Italian, German or French. Six-step guide, replayable in Settings.
- 💬 GitHub feedback/suggestions, Ko-fi support, and Share/Copy download-page link. Sharing the app link does not share payments or personal data.

## Privacy
No Recurio account, backend, advertising, analytics, bank access or browser-cookie access. Live payment records stay in Android's private app storage, without extra database encryption. Automatic Android cloud/device backup is excluded. Uninstalling or clearing app data deletes records not exported manually.

Website icons are enabled by default for new installs, with an explanation and an off switch before network requests. Existing explicit preferences are kept. Requests go directly to public HTTPS sites: sites/network infrastructure can see your IP and public icon/page requests, but saved subscription paths, prices, renewal dates and browser cookies are never sent. After backup restore, a popup asks whether to retrieve icons before new site requests start. GitHub and Copilot icons are bundled under MIT; other service logos are not bundled without verified redistribution terms. See [catalog and icon review](docs/CATALOG.it.md).

Backup files use AES-256-GCM and PBKDF2-HMAC-SHA256. Minimum password: 5 characters; forgotten passwords cannot be recovered. Keep the file and password separately. The system file picker can save to a cloud provider if you choose one; that provider may sync the encrypted file. Backup excludes icons, Android grants and app preferences. See [backup format](docs/BACKUP.md).

External GitHub and Ko-fi actions open the browser and follow those services' own privacy policies. No feedback is submitted automatically. F-Droid is marked as coming soon, with no invented app link.

## Install and signing
Download [Recurio-v1.0.apk](https://github.com/GiuClab/Recurio/releases/download/v1.0/Recurio-v1.0.apk) from [GitHub Releases](https://github.com/GiuClab/Recurio/releases/tag/v1.0) and open it on Android 8 or later. The release includes `SHA256SUMS.txt`.

Only payment notifications changed in the app. The filename and GitHub release use v1.0; the existing internal version (`0.1.0-alpha09`, code 9), screens and in-app text are preserved. This is the exact APK built and checked by [GitHub Actions run 43498312426](https://github.com/GiuClab/Recurio/actions/runs/43498312426), renamed without modifying its bytes. It is debug-signed, not production-signed.

Its certificate differs from the earlier alpha APK. If Android rejects installation over that version, export any saved records before uninstalling, install the new APK and restore the backup. If no payment records have been entered, no payment backup is needed. Keep signing keys outside Git; a production signing identity is still needed for long-term distribution. See [download notes](distribution/DOWNLOAD_NOTES.md).

## Build and verification
Use a FLOSS JDK 17, Android SDK platform 35 and Build Tools 35.0.0:

```sh
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`. Gradle wrapper checksum is pinned. Dependencies come from Google Maven, Maven Central and the Gradle Plugin Portal. No Play Services, Firebase or proprietary tracking SDKs.

Local verification evidence is in [docs/verification](docs/verification/BUILD.md); GitHub Actions status is separate. Physical-device UI, notification/battery behavior and file-provider interoperability require device testing. No complete coverage or independent security audit is claimed.

## Catalog, artwork and contribution
Edit `catalog/services.json` and run `python3 tools/generate_catalog.py`. Unknown-domain labels are editable heuristics, not guaranteed brand identification. Do not commit private subscription links, backups, credentials or signing keys. Contributions should include relevant checks, translations and redistributable assets.

The approved Recurio logo follows the GiuClab family style; provenance is in [assets/NOTICE.md](assets/NOTICE.md). Third-party marks remain with their owners; no affiliation is implied. Octicons MIT and the F-Droid logo GPL-3.0-or-later notices/source are included under `third_party/` and in the app. The F-Droid icon indicates planned distribution only.

## License and support
Copyright © 2026 GiuClab. Code and project artwork: GPL-3.0-or-later, except separately identified third-party assets. See [LICENSE](LICENSE).

[Support Recurio on Ko-fi](https://ko-fi.com/giuclab/tip)

Made with ❤️ by GiuClab
