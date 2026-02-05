# 📋 Azmew Java Connector: Implementation Requirements & Progress Tracker

This document is the **Master Roadmap** for the Java Spring Boot migration. It tracks every micro-step to ensure 1:1 logic parity with the Node.js PoC.

---

## 🚦 CURRENT PROGRESS: Step 1 & 2

### 🏗️ Phase 1: Infrastructure & Automation (DONE ✅)
- [x] **1.1 Monorepo Setup**: Created `azmew-be` and `azmew-fe` folders.
- [x] **1.2 Automation**: Created `./run.sh` for one-click startup and port management.
- [x] **1.3 Security**: Standardized `.gitignore` across the whole monorepo.
- [x] **1.4 Backend Parity**: Added `dotenv-java` and CORS configuration to Java.

---

### 💾 Phase 2: Data Architecture (Mirroring `db.json`)
- [x] **Step 2.1: Tenant Entity** -> ✅ DONE
- [x] **Step 2.2: SocialPage Entity (THE CORE)** -> ✅ DONE
- [x] **Step 2.3: SocialMessage Entity** -> ✅ DONE

---

### 🔗 Phase 3: Meta Integration (Facebook/Instagram)
- [x] **Step 3.1: Auth Flow** -> ✅ DONE (Facebook Complete)
- [x] **Step 3.2: Webhook Handler** -> ✅ DONE (Verification + Ingestion)
- [x] **Step 3.4: Message Ingestion** -> ✅ DONE (MetaMessageParser implemented)
- [ ] **Step 3.3: Historic Sync** -> ⏳ Pending

---

### 🎵 Phase 4: TikTok Integration
- [ ] **Step 4.1: TikTok OAuth**
- [ ] **Step 4.2: Webhook Handler**

---

### ⚛️ Phase 5: Managed Frontend (React)
- [x] **Step 5.1: Account Dashboard** -> ✅ DONE
- [x] **Step 5.2: Unified Inbox UI** -> ✅ DONE (Premium Evolution Complete)
    - [x] Framer Motion animations
    - [x] Glassmorphism 2.0 design
    - [x] Real-time message polling
    - [x] Platform-specific styling

---

## 📝 Rules for Progress
1.  **Strict Mirroring**: No new features. If Node.js did it, Java must do it 1:1.
2.  **Sequential Execution**: Complete Step X before moving to Step X+1.
3.  **Hybrid Development**: Update Backend and Frontend together for continuous testing.
4.  **Verification**: After each step, run `./run.sh` to ensure build stability.

---
**Last Updated**: 2026-02-05
