<div align="center">
  <img src="assets/recurio-logo.webp" width="150" alt="Recurio logo">
  <h1>Recurio</h1>
  <p>Your payments, in one place — by GiuClab</p>

[![Android](https://img.shields.io/badge/Android-8%2B-16875C)](#features)
[![Version](https://img.shields.io/badge/version-1.0.0-385AD0)](https://github.com/GiuClab/Recurio/releases)
[![License](https://img.shields.io/badge/license-GPL--3.0--or--later-385AD0)](LICENSE)
[![Android checks](https://github.com/GiuClab/Recurio/actions/workflows/android.yml/badge.svg)](https://github.com/GiuClab/Recurio/actions/workflows/android.yml)

[Downloads](https://github.com/GiuClab/Recurio/releases) · [Feedback & suggestions](https://github.com/GiuClab/Recurio/issues/new/choose) · [Support on Ko-fi](https://ko-fi.com/giuclab/tip)

[Italiano](docs/README.it.md) · [Deutsch](docs/README.de.md) · [Français](docs/README.fr.md)
</div>

Recurio is a free, open source Android payment planner with local reminders. The source is prepared for the first stable release, **1.0.0**. Production signing, device verification and publication must be completed before a stable APK is available; see [release preparation](distribution/STABLE_RELEASE.md). F-Droid publication is planned; no official listing exists yet. Current UI screenshots will be added after device verification; no mockup is presented as an app screenshot.

## Features
- 🗓 Add, edit, pause and delete payments with one-time, weekly, monthly, quarterly or yearly recurrence.
- 💶 Calendar-month totals and next-12-month forecasts, separated by currency. No automatic exchange conversion or bank charge verification.
- 🔗 Share a browser link to Recurio, paste it or select a service from the searchable 145-entry catalog. Editable name suggestions recognize domains and known product paths.
- 🔔 Optional local reminders with amount, currency and a bundled/cached service logo (or payment initial), 0–365 days of advance notice and chosen time. Singular/plural reminder text in all four languages. Android may delay notifications to save battery.
- 🔐 Password-encrypted manual export and restore. Restore previews the record count and replaces existing payments only after confirmation.
- 🌍 Automatic supported phone language on first install; otherwise an English chooser for English, Italian, German or French. Six-step guide, replayable in Settings.
- 💬 GitHub feedback/suggestions, Ko-fi support, and Share/Copy download-page link. Sharing the app link does not share payments or personal data.

## Privacy
No Recurio account, backend, advertising, analytics, bank access or browser-cookie access. Live payment records stay in Android's private app storage, without extra database encryption. Automatic Android cloud/device backup is excluded. Uninstalling or clearing app data deletes records not exported manually.

Website icons are enabled by default for new installs, with an explanation and an off switch before network requests. Existing explicit preferences are kept. Requests go directly to public HTTPS sites: sites/network infrastructure can see your IP and public icon/page requests, but saved subscription paths, prices, renewal dates and browser cookies are never sent. After backup restore, a popup asks whether to retrieve icons before new site requests start. GitHub and Copilot icons are bundled under MIT; other service logos are not bundled without verified redistribution terms. See [catalog and icon review](docs/CATALOG.it.md).

Backup files use AES-256-GCM and PBKDF2-HMAC-SHA256. Minimum password: 5 characters; forgotten passwords cannot be recovered. Keep the file and password separately. The system file picker can save to a cloud provider if you choose one; that provider may sync the encrypted file. Backup excludes icons, Android grants and app preferences. See [backup format](docs/BACKUP.md).

External GitHub and Ko-fi actions open the browser and follow those services' own privacy policies. No feedback is submitted automatically. F-Droid is marked as coming soon, with no invented app link.

## Install and signing
Stable release naming: `Recurio-1.0.0.apk`, GitHub tag `v1.0.0`, release title `Recurio 1.0.0`, without the pre-release flag. The stable build workflow requires production signing secrets and verifies the expected certificate before providing an APK and checksum. See [signing, alpha migration and publication](distribution/STABLE_RELEASE.md).

Alpha09 used a development certificate. A differently signed stable build requires exporting an encrypted backup before uninstalling the alpha, installing the stable app and restoring the backup. Private signing keys must remain outside this repository. Historical alpha releases and their verification reports remain available; they do not verify version 1.0.0.

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
