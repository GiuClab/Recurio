# Recurio v1.0

Download **Recurio-v1.0.apk** for Android 8 or later.

- The app now displays version **1.0** consistently on the main screen, Settings and About. Version labels come from the build configuration (versionCode 10).
- The About dialog has a compact logo/version header, tighter text spacing, grouped links with accessible touch targets, and a smaller footer. The preview label has been removed in all four languages.
- Payment reminders retain their amount, currency and service image. Other screens and payment, backup and reminder behavior are unchanged.

Unit tests, Android lint and APK build passed on GitHub Actions run 43503461603, from source 05138ac0f6073d1d9e9c67431bbdfdaef76ccaea. The downloaded APK manifest was checked: versionName 1.0, versionCode 10. The final appearance still needs verification on a phone.

The file uses the GitHub Actions debug signing certificate, not a production signing identity. A new build may use a different certificate. If Android rejects installation over the earlier APK, export any saved records before uninstalling it, then install this file and restore the backup. If no records have been entered, no payment backup is needed.

SHA256SUMS.txt contains the APK checksum. The v1.0 download has been replaced with this updated build, and the tag points to the matching source and publication metadata.

No F-Droid listing is available yet.

Feedback: https://github.com/GiuClab/Recurio/issues/new/choose
Support: https://ko-fi.com/giuclab/tip
