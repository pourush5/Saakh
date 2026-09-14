# Saakh (साख) — Chapter B: Bihar

> **Offline-first, cryptographically secure digital ledger designed to protect India's informal workforce from wage theft.**

[![License](https://img.shields.io/badge/License-Apache_2.0-orange.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Google Play](https://img.shields.io/badge/Google_Play-Download-brightgreen.svg)](https://play.google.com/store/apps/details?id=com.pourush.saakh)
[![Initiative](https://img.shields.io/badge/Initiative-A2Z_Bharat-blue.svg)](https://github.com/pourush5/A2Z_Bharat)

**Saakh** (Hindi for credibility/trust) is a **100% offline, cryptographically secure digital ledger** designed to protect India's informal workforce from wage theft.

This project is the **'B' (Bihar)** chapter of the **A2Z Bharat Initiative**, aimed at building software solutions for regional challenges in India.

🔗 [**Download the Latest App Here (Play Store)**](https://play.google.com/store/apps/details?id=com.pourush.saakh)

---

## 📖 The Problem

For migrant laborers, standard technological solutions for logging **hours and daily wage** fail due to two massive hurdles:

1. **Zero Internet Connectivity:** Work sites often lack reliable networks.
2. **Device Fragmentation:** Relying on Bluetooth or cloud-syncing across budget devices is highly unreliable.

Without a verified ledger, labourers rely on a contractor's physical notebook, leading to frequent wage disputes and exploitation.

## 💡 The Solution: Optical Airgap Protocol

Saakh bypasses the internet entirely. Data travels through light using a **2-way QR code handshake**.

```text
[ Laborer Device ]                                        [ Contractor Device ]
        |                                                           |
        |--- 1. Logs hours & daily wage and generates Unsigned ---->|
        |       Payload (REQ)                                       |
        |                                                           |
        |                                               [ Scans REQ via Camera ]
        |                                               [ Signs via Android Hardware Keystore ]
        |                                               [ ECDSA ]
        |                                                           |
        |<-- 2. Scans Contractor's Signed Response (RES) -----------|
        |
[ Validates Signature against TOFU Store ]
[ Updates Local Ledger: Verified ✅ ]
```

1. **The Request (`REQ`):** The laborer logs their **hours and daily wage**, generating an unsigned QR code payload.
2. **The Signature:** The contractor scans it. The app passes the data through the Android Hardware Keystore, signs it with a device-specific Private Key using **ECDSA**, and generates a Response (`RES`) QR code.
3. **The Handshake:** The laborer scans the response. The app validates the signature mathematically, and their local database updates the ledger to **"Verified ✅"**.

The laborer walks away with undeniable, locally stored cryptographic proof of their work.

---

## 🏗️ Technical Architecture

- **100% Offline Cryptography:** Hardware-backed Keystore system utilizing **ECDSA** for digital signatures.
- **Trust On First Use (TOFU):** Implemented a localized PKI system. The laborer's device securely binds and remembers the contractor's public key for frictionless future scans.
- **Decoupled Architecture:** Strict adherence to SOLID principles, isolating the UI from data layers.
- **State Management:** Jetpack DataStore handles asynchronous, type-safe role state (`Laborer` vs `Contractor`).
- **Local Persistence:** Room (SQLite) manages the ledger and the TOFU Trust Store, operating safely on background Coroutines.
- **Bilingual UI:** Built entirely in Jetpack Compose with a side-by-side Hindi/English interface to ensure absolute transparency.

---

## 📲 Installation & Downloads

- **Google Play Store:** [Download on Google Play](https://play.google.com/store/apps/details?id=com.pourush.saakh)

---

## 🔨 Building From Source

### Prerequisites

- Android Studio (Ladybug or newer)
- JDK 17+
- Android SDK (API Level 34+)

### Build Steps

```bash
git clone https://github.com/pourush5/Saakh.git
cd Saakh
./gradlew assembleDebug
```

---

## 🇮🇳 Initiative & Maintainer

Saakh is the **Chapter B** project of the [A2Z Bharat](https://github.com/pourush5/A2Z_Bharat) open-source initiative, aimed at building software solutions for regional challenges in India.

- **Founding Developer:** Pourush Pandey
- **Connect:** [pourushpandey.vercel.app](https://pourushpandey.vercel.app)
- **Play Store Portfolio:** [Google Play Developer Profile](https://play.google.com/store/apps/dev?id=6511351521879785639)
- **A2Z Bharat:** [A2Z Bharat Links](https://linktr.ee/a2zbharat)

---

## 📄 License

This project is open-source software licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

```text
SPDX-License-Identifier: Apache-2.0
```
