**Saakh** (Hindi for credibility/trust) is a 100% offline, cryptographically secure digital ledger designed to protect India's informal workforce from wage theft. 

This project is the **'B' (Bihar)** chapter of the **A2Z Bharat Initiative**, aimed at building software solutions for regional challenges in India.

🔗 **[Download the Latest App Here (Play Store) ](https://play.google.com/store/apps/details?id=com.pourush.saakh)**

---

## 📖 The Problem

For migrant laborers, standard technological solutions for logging work fail due to two massive hurdles:
1. **Zero Internet Connectivity:** Work sites often lack reliable networks.
2. **Device Fragmentation:** Relying on Bluetooth or cloud-syncing across budget devices is highly unreliable.

Without a verified ledger, labourers rely on a contractor's physical notebook, leading to frequent wage disputes and exploitation.

## 💡 The Solution: Optical Airgap Protocol

Saakh bypasses the internet entirely. Data travels through light using a **2-way QR code handshake**. 


1. **The Request (`REQ`):** The laborer logs their hours, generating an unsigned QR code payload.
2. **The Signature:** The contractor scans it. The app passes the data through the Android Hardware Keystore, signs it with a device-specific Private Key (ECC), and generates a Response (`RES`) QR code.
3. **The Handshake:** The laborer scans the response. The app validates the signature mathematically, and their local database updates the ledger to **"Verified ✅"**.

The laborer walks away with undeniable, locally stored cryptographic proof of their work.

---

## 🏗️ Technical Architecture

* **100% Offline Cryptography:** Hardware-backed Keystore system utilizing Elliptic Curve Cryptography (ECC) for digital signatures.
* **Trust On First Use (TOFU):** Implemented a localized PKI system. The laborer's device securely binds and remembers the contractor's public key for frictionless future scans.
* **Decoupled Architecture:** Strict adherence to SOLID principles, isolating the UI from data layers.
* **State Management:** Jetpack DataStore handles asynchronous, type-safe role state (`Laborer` vs `Contractor`).
* **Local Persistence:** Room (SQLite) manages the ledger and the TOFU Trust Store, operating safely on background Coroutines.
* **Bilingual UI:** Built entirely in Jetpack Compose with a side-by-side Hindi/English interface to ensure absolute transparency.

  This is part of the A2ZBharat intiative : [A2Z Bharat Links](https://linktr.ee/a2zbharat)
