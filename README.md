# ZoVnilka

Native Android messenger starter.

## Stack
- Android: Kotlin + Jetpack Compose
- API: Node.js + Fastify + TypeScript
- Realtime transport: Socket.IO (next milestone)
- Database: PostgreSQL
- Hosting target: Render

## Repository layout
- `android/` — native Android application
- `server/` — REST API and database layer
- `docs/` — architecture and deployment notes

## Run the API locally
Requirements: Node.js 20+ and PostgreSQL 15+.

```bash
cd server
cp .env.example .env
npm install
npm run dev
```

Set a strong random `JWT_SECRET` (at least 32 characters) and a valid `DATABASE_URL` in `.env`.

## Android
Open the `android/` directory in Android Studio. The initial client is native Compose, not a WebView.
The default emulator API URL is `http://10.0.2.2:3000/`. Change `BASE_URL` in `android/app/build.gradle.kts` for a physical device or deployed HTTPS API.

## Render
Create a PostgreSQL database and a Web Service with root directory `server`.
- Build command: `npm install && npm run build`
- Start command: `npm start`
- Environment: `NODE_ENV=production`, `DATABASE_URL`, `JWT_SECRET`, `CORS_ORIGIN`

Do not store user uploads on Render's ephemeral filesystem. Use object storage and signed URLs when media uploads are implemented.

## Status
This is the initial development scaffold, not a production-ready messenger. Message delivery, membership-protected conversations, push notifications, media uploads, refresh-token rotation and WebRTC calls are planned milestones.
