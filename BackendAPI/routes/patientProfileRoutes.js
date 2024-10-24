const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/authMiddleware');
const { savePatientProfile, getPatientProfile } = require('../controller/patientProfileController');

// Route to save patient profile
router.post('/', protect, savePatientProfile); 
// Route to get patient profile
router.get('/', protect, getPatientProfile);

module.exports = router;