const express = require('express');
const http = require('http');
const fs = require('fs');
const cors = require('cors');
const connectDB = require('./config/db');
const authRoutes = require('./routes/authRoutes');
const appointmentRoutes = require('./routes/appointmentRoutes');
const medicalHistoryRoutes = require('./routes/medicalHistoryRoutes');
const { sendReminders } = require('./services/schedular'); 
const dotenv = require('dotenv');

dotenv.config();
const app = express();
app.use(cors());
app.use(express.json());

// Database Connection
connectDB();

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/appointments', appointmentRoutes);
app.use('/api/medicalHistory', medicalHistoryRoutes);

// Start the reminder service
sendReminders(); // Start the reminder service


app.use(cors());


// Create the HTTP server
const port = 3000;
http.createServer(app).listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});




