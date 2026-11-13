# Verification — 2026-11-13

Recurio 0.1.0-alpha09, versionCode 9; Android 8+ (API 26), target 35.

- assembleDebug: successful.
- testDebugUnitTest: 55 tests; 0 failures, 0 errors.
- lintDebug: successful; 0 errors, 10 warnings.
- APK signature verified; same certificate as alpha08 for compatible testing updates.
- Added checks cover the sixth community slide, actual project/download/feedback URLs, absent F-Droid listing and all four localized community/share strings.
- New WebP launcher and in-app logo derivatives are from the user-approved image; adaptive launcher uses GiuClab-blue background and safe foreground padding. F-Droid source/dual-license notice is included, used under GPL-3.0-or-later. App third-party notices include Octicons and F-Droid attribution.
- Previous backup, presentation, first-run, catalog, grammar, sorting, currency and recurrence checks remain successful.
- No physical-device/emulator runtime UI, Android share-sheet or launcher-mask test was completed in this environment.
- Local checks are separate from GitHub Actions results. This preview is not production-signed; no private key is committed.

APK SHA-256: c20f65d93fd3c3994b84915b029700dbd3c0d505c35bd417a11b778fb239c4c8

After packaging bitmap resources in drawable-nodpi, assembleDebug/lintDebug succeeded again; Kotlin source is unchanged from the 55-test run.
