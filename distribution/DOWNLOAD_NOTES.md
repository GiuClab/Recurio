# Recurio v1.0

Download **Recurio-v1.0.apk** for Android 8 or later.

Payment reminders now display the amount, currency and service image. Bundled or cached logos are used when available; otherwise the payment initial is shown. Receiving a reminder does not start a website request. Other app screens and behavior are unchanged.

Unit tests, Android lint and APK build passed on GitHub Actions. Notification appearance still needs verification on a phone.

**Package details:** v1.0 is the download/release label. The APK contents, internal version label (`0.1.0-alpha09`, code 9) and in-app text have not been changed beyond the notification update. This APK uses the GitHub Actions debug signing certificate, not a production signing identity. Renaming it does not change that.

If Android rejects installation over the earlier APK because the signatures differ, export any payment records before uninstalling the old app, then install this file and restore the backup. If no records have been entered, no payment backup is needed.

The APK is the exact file built by workflow run 43498312426 from source a84c65403c52ec38ac101d27d9c1b26cd090a08e, renamed without altering its bytes. SHA256SUMS.txt provides the checksum.

No F-Droid listing is available yet.

Feedback: https://github.com/GiuClab/Recurio/issues/new/choose
Support: https://ko-fi.com/giuclab/tip
