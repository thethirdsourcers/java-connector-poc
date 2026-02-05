# 🎼 Azmew Social Connector: Java Mirror Architecture

This document serves as the high-level technical specification for mirroring the Node.js PoC into a modular Java Spring Boot application.

## 📁 Project Structure Overview
- **Path**: `c:\Users\acer\Desktop\poc\java-connector-poc`
- **Source**: `c:\Users\acer\Desktop\poc\proof-of-concept`

## 🧠 Core Component Mapping

| Node.js Functionality | Java Class / Component | Pattern |
| :--- | :--- | :--- |
| **Express Server** | `AzmewJavaConnectorPocApplication` | Spring Boot Main |
| **Auth Routes (`auth.js`)** | `AuthController` | `@RestController` |
| **Token Sync (`syncService.js`)** | `TokenExchangeService` | `@Service` |
| **Webhook Logic (`webhook.js`)** | `WebhookController` | `@RestController` |
| **Message Routes (`messages.js`)** | `MessageController` | `@RestController` |
| **`db.json` Storage** | `Tenant`, `SocialPage`, `Message` | JPA Entities |

---

## 🏗️ Data Model (Mirroring Node Schema)

### 1. `Tenant`
Represents the business client.
- `UUID id`
- `String name`
- `String apiKey`

### 2. `SocialPage` (The "Connected Account")
Mirror of the `pages` array in Node's storage.
- `UUID id`
- `String pageId` (Platform ID)
- `String accessToken`
- `String platform` (FACEBOOK, INSTAGRAM, TIKTOK)
- `UUID tenantId` (Foreign Key)

### 3. `SocialMessage`
Mirror of the `messages` store.
- `UUID id`
- `String externalId` (Platform Message ID)
- `String senderId`
- `String text`
- `LocalDateTime timestamp`
- `UUID pageId` (Foreign Key)

---

## ⚡ Integration Flow

### 1. OAuth Handshake
1. `AuthController` generates the Redirect URL for the chosen platform.
2. User authenticates.
3. Platform redirects back to Java `callback` endpoint.
4. Java `WebClient` exchanges the code for a **Long-Lived token**.
5. Data is saved to PostgreSQL.

### 2. Webhook Consumption
1. Platform sends POST request to `/api/webhook/{platform}`.
2. `WebhookController` validates the challenge (for FB).
3. Payload is mapped to a `SocialMessage` entity.
4. Data is persisted and ready for the Frontend.

---

## 📅 Task Sequence
1. **Phase A**: Entities & Repositories (The Foundation)
2. **Phase B**: WebClient Configurations (The APIs)
3. **Phase C**: OAuth Controllers (The Connectors)
4. **Phase D**: Webhook Handlers (The Ingestion)

---
**Status**: Architecture Approved
**Reference Node.js Logic**: `c:\Users\acer\Desktop\poc\proof-of-concept\azmew-be`
