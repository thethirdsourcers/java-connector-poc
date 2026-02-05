# 🎯 Phase 3.4 & UI Evolution Plan

## 1. Backend: Meta Message Ingestion (Step 3.4)
The `WebhookController` currently logs the payload. We will now implement the logic to:
- **Parse the Payload**: Extract `sender_id`, `recipient_id`, `text`, and `mid` (message ID).
- **Match the Account**: Retrieve the `SocialPage` using the `recipient_id`.
- **Deduplicate**: Check `SocialMessageRepository` for the `externalMessageId` to prevent double-entry.
- **Save**: Persist to the `social_messages` table.

## 2. Frontend: Premium UI-UX Evolution
We will transform the existing "Functional" UI into a "Premium" experience:
- **Animation**: Add `framer-motion` for slide-in animations when switching accounts.
- **Visual Feedback**: Hover affects on message bubbles and a "Glow" effect for the selected account.
- **Empty State Art**: Generate custom SVG assets for the empty inbox.
- **Typography**: Switch to a more modern font stack with varying weights.

## 3. Security (Phase 2 Integration)
- Implement X-Hub-Signature validation to ensure only Meta can send messages to your webhook.

---
**Next Action**: Implement `MetaMessageParser` service.
