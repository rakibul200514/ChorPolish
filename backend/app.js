const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const mongoose = require('mongoose');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const rateLimit = require('express-rate-limit');
require('dotenv').config();

const app = express();

// Security Middlewares
app.use(helmet());
app.use(cors({
    origin: process.env.SOCKET_CORS_ORIGIN || "*",
    methods: ["GET", "POST"]
}));
app.use(express.json());

// Logging
app.use(morgan(process.env.NODE_ENV === 'production' ? 'combined' : 'dev'));
app.use((req, res, next) => {
    console.log(`${new Date().toISOString()} - ${req.method} ${req.url}`);
    next();
});

// Rate Limiting
const limiter = rateLimit({
    windowMs: 15 * 60 * 1000, // 15 minutes
    max: 100 // limit each IP to 100 requests per windowMs
});
app.use('/api/', limiter);

const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: process.env.SOCKET_CORS_ORIGIN || "*",
        methods: ["GET", "POST"]
    }
});

// MongoDB Connection
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/chorpolish';
mongoose.connect(MONGODB_URI)
    .then(() => console.log('Connected to MongoDB'))
    .catch(err => console.error('MongoDB connection error:', err));

// Socket.IO Handlers
const registerSocketHandlers = require('./socketHandlers');
io.on('connection', (socket) => {
    console.log(`User connected: ${socket.id}`);
    registerSocketHandlers(io, socket);
});

// Routes
const roomRoutes = require('./routes/rooms');
app.use('/api/rooms', roomRoutes);

// Health Check
app.get('/health', (req, res) => {
    res.status(200).json({ status: 'ok', timestamp: new Date() });
});

// Admin Dashboard
app.get('/admin', (req, res) => {
    // In a real production app, this should be protected by JWT/Auth
    res.send(`
        <html>
            <head>
                <title>Chor Polish Admin</title>
                <style>
                    body { font-family: sans-serif; padding: 40px; background: #f4f4f9; }
                    .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    h1 { color: #333; }
                    #rooms { margin-top: 20px; }
                    li { padding: 10px; border-bottom: 1px solid #eee; list-style: none; }
                </style>
            </head>
            <body>
                <div class="card">
                    <h1>Chor Polish Live Dashboard</h1>
                    <p>Monitoring active rooms in real-time.</p>
                    <div id="rooms">Loading active rooms...</div>
                </div>
                <script>
                    function fetchRooms() {
                        fetch('/api/rooms/active')
                            .then(r => r.json())
                            .then(data => {
                                document.getElementById('rooms').innerHTML = data.length > 0 ?
                                    data.map(r => '<li><strong>Room ' + r.roomCode + '</strong>: ' + r.players.length + ' players (' + r.phase + ')</li>').join('') :
                                    '<p>No active rooms.</p>';
                            })
                            .catch(err => {
                                document.getElementById('rooms').innerHTML = '<p style="color:red">Error loading rooms.</p>';
                            });
                    }
                    fetchRooms();
                    setInterval(fetchRooms, 5000);
                </script>
            </body>
        </html>
    `);
});

const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Server running in ${process.env.NODE_ENV || 'development'} mode on port ${PORT}`);
});

process.on('unhandledRejection', (reason, promise) => {
    console.error('Unhandled Rejection at:', promise, 'reason:', reason);
});

process.on('uncaughtException', (err) => {
    console.error('Uncaught Exception:', err);
});
