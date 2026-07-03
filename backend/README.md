# Chor Polish Backend 🚀

Multiplayer backend for Raja Mantri Chor Sipahi game using Node.js, Socket.IO, and MongoDB. Fully production-ready and deployable to any cloud or VPS.

## 📦 Features
- **Real-time Synchronization**: Socket.IO for duplex communication.
- **Production Security**: Helmet, CORS, and Express Rate Limiting.
- **Robust Logging**: Morgan for request logging.
- **Easy Deployment**: One-click Render/Railway support and Docker Compose.
- **Process Management**: PM2 configuration for high availability.

## 🚀 One-Click Deployment (Recommended)

### Render.com
1. Push your project to GitHub.
2. Connect your repo to Render as a **Blueprint**.
3. It will automatically detect `render.yaml` and set everything up.
4. Add `MONGODB_URI` in the dashboard variables.

## 🛠️ Manual Deployment (VPS / Ubuntu)
1. Install Node.js, MongoDB, and PM2: `npm install pm2 -g`
2. Clone repo and `cd backend`
3. `npm install --production`
4. Create `.env` from `.env.example` and fill in values.
5. Start with PM2: `pm2 start app.js --name "chor-polish"`
6. Monitor: `pm2 monit` or `pm2 logs`

## 🐳 Docker Deployment
1. Build & Run: `docker-compose up -d`

## 🔗 Android App Integration
The app automatically switches between Development and Production URLs:
- **Debug Build**: Connects to `http://10.0.2.2:3000` (Localhost).
- **Release Build**: Connects to your production URL configured in `utils/NetworkConfig.kt`.

## 🛰️ API Summary
- `GET /health`: Server health check.
- `GET /admin`: Real-time dashboard.
- `POST /api/rooms/create`: Create match.
- `POST /api/rooms/join`: Join match.
