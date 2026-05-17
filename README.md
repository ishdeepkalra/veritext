# 🛡️ VeriText: On-Device AI Scam Protection

**VeriText** is a privacy-first Android application designed to intercept and analyze incoming SMS messages in real-time to protect users from scams, phishing, and fraud. 

Built for the **Gemma 4 Good Hackathon**, VeriText leverages the power of Google's **Gemma 4** AI model running entirely on-device via **LiteRT**. This means your private messages never leave your phone—providing enterprise-grade AI threat detection without compromising user privacy or requiring an internet connection.

---

## ✨ Key Features

*   **🧠 Local AI Inference with Gemma 4:** Powered by Google's **Gemma 4** model running locally on your device's hardware via LiteRT. Enjoy state-of-the-art natural language processing without cloud dependencies.
*   **🔒 100% Privacy-Preserving:** Completely offline analysis. Your SMS data is never uploaded, shared, or stored on any external servers.
*   **⚡ "Always-On" Real-Time Scanning:** Employs a robust background foreground service (with Boot Receiver support) to instantly analyze new messages the moment they arrive, preventing the OS from killing the background protection process.
*   **🔔 Instant Intelligent Alerts:** Receive immediate notifications with a risk assessment and an AI-generated explanation if a malicious message is detected.
*   **📱 Modern UI:** Built natively in Kotlin using **Jetpack Compose** for a seamless, fast, and beautiful Android user experience.

---

## 🏗️ Architecture & Tech Stack

VeriText is built entirely natively for Android, emphasizing performance and security:
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose
*   **AI / Machine Learning:** LiteRT (formerly TensorFlow Lite)
*   **Core Model:** **Gemma 4** (Optimized for edge deployment)
*   **Background Processing:** Android Foreground Services & Broadcast Receivers

---

## 🚀 How It Works

1.  **Message Interception:** When an SMS is received, the `SmsReceiver` intercepts the payload.
2.  **Foreground Processing:** The text is securely passed to the `SmsAnalysisService` which operates consistently in the background.
3.  **Gemma 4 Analysis:** The `VeriTextModelController` tokenizes the message and runs it through the on-device **Gemma 4** model using LiteRT.
4.  **Risk Assessment:** The model outputs a JSON-formatted risk assessment (e.g., Safe, Suspicious, Scam) along with a rationale.
5.  **User Notification:** If a threat is detected, the app immediately alerts the user, displaying the reasoning behind the flag.

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
    *   Ensure you have the converted `gemma-4.tflite` (or `.bin`) LiteRT model.
    *   Place the model files inside the Android project's `app/src/main/assets/` folder.
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
