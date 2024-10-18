const express = require('express');
const { createForm } = require('../controller/form2Controller'); 
const router = express.Router();
const { protect } = require('../middleware/authMiddleware'); // Middleware for protecting routes

// POST route to create a new form submission 
router.post('/form2', protect, createForm); 

module.exports = router;
