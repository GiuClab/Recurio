# SPDX-License-Identifier: GPL-3.0-or-later
"""Optional test-fixture generator; requires Python cryptography. Not used by the app/build.
The public password, fixed salt and nonce below are TEST DATA ONLY.
"""
from pathlib import Path
import datetime
import hashlib
import struct
from cryptography.hazmat.primitives.ciphers.aead import AESGCM

def text(value):
    encoded = value.encode('ascii')
    return struct.pack('>H', len(encoded)) + encoded

payload = (
    struct.pack('>ii', 1, 1) + text('fixture-id') + text('Fixture Netflix')
    + struct.pack('>q', 1599) + text('EUR')
    + struct.pack('>q', (datetime.date(2026, 11, 27) - datetime.date(1970, 1, 1)).days)
    + text('MONTHLY') + text('https://netflix.com/account')
    + struct.pack('>?iii?', True, 3, 18, 36, True)
)
salt = bytes(range(16))
iv = bytes(range(16, 28))
header = b'RECURIOBK1' + salt + iv
key = hashlib.pbkdf2_hmac('sha256', b'public-fixture-password', salt, 600000, 32)
path = Path(__file__).resolve().parents[1] / 'app/src/test/resources/portable-backup.recurio'
path.parent.mkdir(parents=True, exist_ok=True)
path.write_bytes(header + AESGCM(key).encrypt(iv, payload, header))
