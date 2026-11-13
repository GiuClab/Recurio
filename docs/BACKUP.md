# Recurio backup format v1

Portable password-based authenticated encryption using platform JCA implementations. No key is tied to Android Keystore, a server or the source phone. This is an open, versioned Recurio container, not ZIP or JSON.

## Container
All byte offsets below are zero-based. Concatenation order:

1. Bytes 0–9: ASCII `RECURIOBK1` (10 bytes).
2. Bytes 10–25: 16-byte salt from SecureRandom.
3. Bytes 26–37: 12-byte IV from SecureRandom.
4. Bytes 38 onward: AES-256-GCM ciphertext with appended 128-bit authentication tag.

Key derivation: PBKDF2WithHmacSHA256, 600,000 iterations, 256-bit key, exact user password (no trimming/normalization). Header bytes 0–37 are authenticated as GCM additional authenticated data. A fresh salt and IV are generated for every export. Changing KDF parameters requires a new format version. Password length: 5–1,024 characters. Password is never written to preferences or backup metadata; editable UI strings cannot be guaranteed erased from managed memory. Key-derivation character buffers and plaintext byte buffers are cleared where practical.

## Plaintext payload
Java DataOutputStream format, big-endian numbers and modified UTF-8 writeUTF strings:

- int32 payload version = 1
- int32 record count (0–5,000)
- each record: UTF id, UTF name, int64 amount in hundredths, UTF ISO currency code, int64 anchor LocalDate epoch day, UTF Cycle enum name, UTF original link, boolean reminder, int32 daysBefore, int32 hour, int32 minute, boolean active.

No trailing plaintext bytes are accepted. Boolean bytes must be 0 or 1. Duplicate IDs, invalid amounts/currencies, out-of-range fields and non-web/credential-bearing links are rejected. Supported anchor years: 1900–2200. Payload size capped at 4 MiB; encrypted input capped at 4 MiB + 128 bytes before decryption. A zero-record backup is valid but still requires explicit replacement confirmation.

## Restore and lifecycle
File access uses Android Storage Access Framework, without broad storage permission. Encryption/decryption and file I/O run off the UI thread. Export uses an encrypted-only temporary cache file while the system picker is open. Successful/cancelled transfers remove that file; process death can leave encrypted cache residue that contains no stored password. No plaintext export file is created.

Restore authenticates, decodes and validates fully before user confirmation. It replaces rather than merges. Storage uses Android AtomicFile so a failed write can recover the previous record file. Old alarms are cancelled after a successful write and new reminders are scheduled. App preferences, icon cache, OS notification grants and other account/device data are not exported.

Local live data remains in Android's private application storage without added database encryption. Only exported backup files receive this password-based encryption. Manual backups chosen in a cloud provider can be synchronized by that provider. No account, sharing or synchronization feature is part of Recurio.

References: https://developer.android.com/privacy-and-security/cryptography and https://developer.android.com/training/data-storage/shared/documents-files . No independent security audit or physical-device file-provider interoperability test is claimed for this preview.

From alpha08 the UI and reader accept passwords of at least 5 characters. Older backups with longer passwords remain readable. Backups created with 5–9-character passwords require alpha08 or later to restore through the app. The container version and cryptographic parameters are unchanged. Incorrect-password/authentication errors are presented separately from export password confirmation and file/format errors.
