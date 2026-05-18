# 🛡️ VeriText: On-Device AI Scam Protection

### 📱 **What is VeriText?**
**VeriText** is a native, local-first Android application that provides real-time, enterprise-grade protection against SMS phishing (smishing) and scams. By leveraging the power of **Gemma 4**, VeriText intercepts incoming text messages and instantly analyzes them for malicious intent—all without a single byte of your private data ever leaving your device.

### 🚨 **The Problem**
SMS-based scams and phishing attacks are growing exponentially, targeting vulnerable demographics with alarming success. While AI is highly capable of detecting these threats, existing solutions require uploading your private, highly sensitive text messages to a cloud server for analysis. This creates a massive privacy paradox: **to protect your security, you must surrender your privacy.**

### 🛡️ **The Solution & How We Built It**
We built VeriText to solve this paradox by bringing the AI directly to the edge. Designed specifically for the **Gemma 4 Good Hackathon**, VeriText represents a breakthrough in local-first mobile security.

---

## ✨ Key Features

*   **🧠 Local AI Inference with Gemma 4:** Powered by Google's **Gemma 4** model running locally on your device's hardware via LiteRT. Enjoy state-of-the-art natural language processing without cloud dependencies.
*   **🔒 100% Privacy-Preserving:** Completely offline analysis. Your SMS data is never uploaded, shared, or stored on any external servers.
*   **⚡ "Always-On" Real-Time Scanning:** Employs a robust foreground service (with Boot Receiver support) to instantly analyze new messages the moment they arrive, preventing the OS from killing the protection process.
*   **🔔 Instant Intelligent Alerts:** Receive immediate notifications with a risk assessment and an AI-generated explanation if a malicious message is detected.
*   **📱 Modern UI:** Built natively in Kotlin using **Jetpack Compose** for a seamless, fast, and beautiful Android user experience.

---

## 🏗️ Architecture & Tech Stack

VeriText is built entirely natively for Android, emphasizing performance and security:
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **AI / Machine Learning:** LiteRT (formerly TensorFlow Lite)
*   **Core Model:** `gemma-4-E4B-it` (Optimized for edge deployment)
*   **Background Processing:** Android Foreground Services & Broadcast Receivers

#### **Architecture Overview**
```text
Incoming SMS Payload (Sender + Body)
        │
        ▼
  SmsReceiver (Android OS Broadcast)
        │
        ├──► If App UI is closed: Triggers SmsAnalysisService (Foreground)
        └──► If App UI is open: Routes payload directly to Jetpack Compose UI
        │
        ▼
  Gemma 4 E4B-it (via Google LiteRT, 100% offline)
        │
        ├──► Tokenizes SMS text for local hardware inference
        ├──► Analyzes payload for urgency or psychological manipulation tactics
        ├──► Scans for deceptive phishing links or malicious instructions
        └──► Formats output into structured, parseable JSON
        │
        ▼
  Safety Verdict + Threat Reasoning + Actionable Advice
  (Delivered instantly via High-Priority OS Notification)
```

## 🚀 How It Works

1.  **Message Interception:** When an SMS is received, the `SmsReceiver` intercepts the payload.
2.  **Foreground Processing:** The text is securely passed to the `SmsAnalysisService` which operates consistently in the background.
3.  **Gemma 4 Analysis:** The `VeriTextModelController` tokenizes the message and runs it through the on-device **Gemma 4** model using LiteRT.
4.  **Risk Assessment:** The model outputs a JSON-formatted risk assessment (e.g., SAFE, SUSPICIOUS, SCAM) along with a rationale.
5.  **User Notification:** If a threat is detected, the app immediately alerts the user, displaying the reasoning behind the flag.

---

## 🌍 The Value and Impact

VeriText sits at the intersection of **Safety & Trust** and **Digital Inclusivity**. By entirely eliminating the need for cloud compute, VeriText democratizes high-end AI security. It works flawlessly in remote areas with zero internet connectivity and guarantees absolute data sovereignty for the user. 

As a true local-first mobile application designed for the **Cactus Prize** category, VeriText showcases the incredible edge capabilities of LiteRT and Gemma 4, proving that powerful, privacy-preserving AI can fit right in your pocket.

---

## 🛠️ Local Development & Setup

### Prerequisites
*   Android Studio (Jellyfish or newer recommended)
*   Android SDK 34+
*   A physical Android device (Recommended for LiteRT GPU/CPU hardware acceleration testing)

### Installation
1.  **Clone the repository:**
    ```bash
    git clone https://github.com/ishdeepkalra/veritext.git
    cd veritext
    ```
2.  **Add the Gemma 4 LiteRT Model:**
    *   Download the pre-converted `gemma-4-E4B-it.litertlm` LiteRT model from Hugging Face: [Download Model](https://huggingface.co/litert-community/gemma-4-E4B-it-litert-lm/blob/main/gemma-4-E4B-it.litertlm).
    *   Because of its size (3.6GB), the model is not bundled in the APK.
    *   Push the model file to your device's local storage at: `/sdcard/Android/data/com.veritext.app/files/gemma-4-E4B-it.litertlm`
    *   You can use ADB: `adb push gemma-4-E4B-it.litertlm /sdcard/Android/data/com.veritext.app/files/`
3.  **Build and Run:**
    *   Open the project in Android Studio.
    *   Sync Gradle files.
    *   Run the app on a physical device or emulator. (Note: physical devices provide significantly faster on-device AI inference).
4.  **Permissions:** 
    *   Upon first launch, ensure you grant the requested **SMS reading** and **Notification** permissions so the background service can function properly.

---

## 🤝 Contributing

Contributions to improve on-device AI safety and inference optimizations are welcome! 
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---
*Built with ❤️ for the Gemma 4 Good Hackathon.*
