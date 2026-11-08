<div align="center">

<a href="https://github.com/rakibul200514/ChorPolish">
  <img src="https://readme-typing-svg.demolab.com?font=Inter&weight=700&size=30&duration=2800&pause=900&color=7C3AED&center=true&vCenter=true&width=720&lines=Chor+Polish+Backend;Real-time+multiplayer+game+engine;Secure.+Scalable.+Production-ready." alt="Chor Polish Backend" />
</a>

<p><strong>⚡ A production-ready multiplayer backend for the Raja Mantri Chor Sipahi game.</strong></p>
<p>Built with Node.js, Socket.IO and MongoDB for fast, reliable real-time gameplay.</p>

<p>
  <a href="https://github.com/rakibul200514/ChorPolish"><img src="https://img.shields.io/badge/project-Chor%20Polish-7c3aed?style=for-the-badge&logo=github&logoColor=white" alt="Project" /></a>
  <img src="https://img.shields.io/badge/Node.js-20+-339933?style=for-the-badge&logo=nodedotjs&logoColor=white" alt="Node.js" />
  <img src="https://img.shields.io/badge/Socket.IO-real--time-010101?style=for-the-badge&logo=socketdotio&logoColor=white" alt="Socket.IO" />
  <img src="https://img.shields.io/badge/MongoDB-database-47A248?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB" />
</p>

</div>

## ✨ What powers the game

| Capability | Experience |
| --- | --- |
| **Real-time synchronization** | Duplex communication through Socket.IO |
| **Production security** | Helmet, CORS and Express rate limiting |
| **Reliable observability** | Morgan request logging for fast diagnostics |
| **Flexible deployment** | Render, Railway, Docker or any VPS |
| **High availability** | PM2 process management and restart support |

## 🚀 Deploy in minutes

### Recommended: Render

1. Push the project to GitHub.
2. Create a new **Web Service** on [Render](https://render.com/) and connect the repository.
3. Render will detect `render.yaml` and configure the service automatically.
4. Add `MONGODB_URI` in the service environment variables.

### VPS / Ubuntu

```bash
# Install dependencies and PM2
npm install --production
npm install -g pm2

# Configure environment variables
cp .env.example .env

# Start and monitor the backend
pm2 start app.js --name "chor-polish"
pm2 status
pm2 logs chor-polish
```

### Docker

```bash
docker compose up -d --build
```

> **Tip:** Keep secrets in environment variables. Never commit `.env` files or production credentials.

## 📱 Android app integration

The app automatically switches between development and production endpoints:

- **Debug build:** `http://10.0.2.2:3000` (Android emulator)
- **Release build:** Production URL configured in `utils/NetworkConfig.kt`

## 🧩 API at a glance

| Method | Endpoint | Purpose |
| :---: | --- | --- |
| `GET` | `/health` | Server health check |
| `GET` | `/admin` | Real-time admin dashboard |
| `POST` | `/api/rooms/create` | Create a game room |
| `POST` | `/api/rooms/join` | Join an existing room |

## 🛠️ Local development

```bash
npm install
npm run dev
```

Before starting, create `.env` from `.env.example` and set a valid `MONGODB_URI`.

<div align="center">

### Built for fast, fair and fun multiplayer rounds 🎮

<a href="https://github.com/rakibul200514/ChorPolish">View the project</a>

</div>
