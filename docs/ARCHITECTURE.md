# ZoVnilka architecture

## Current foundation
- Native Android client using Kotlin and Jetpack Compose.
- Fastify REST API with rate limiting and security headers.
- PostgreSQL tables for users, conversations, members and messages.
- Password hashing with bcrypt and short-lived JWT access tokens.

## Next milestones
1. Add authenticated conversation creation, member management and message history.
2. Add Socket.IO authentication and server-authoritative realtime message delivery.
3. Add refresh-token rotation, logout, device sessions, email verification and password reset.
4. Add object storage with signed upload/download URLs.
5. Add Firebase Cloud Messaging push notifications.
6. Add WebRTC signaling and TURN infrastructure for reliable audio/video calls.

## Production checklist
Before public launch: authorization tests for every conversation operation, input validation, abuse controls, rate limits, database backups, privacy policy, data deletion/export flow, secure media storage, HTTPS, secret rotation and security review.
