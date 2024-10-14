const express = require('express');
const { bookAppointment, rescheduleAppointment, cancelAppointment, getPatientNotifications, getStaffNotifications } = require('../controller/appointmentController');
const router = express.Router();
const { protect } = require('../middleware/authMiddleware'); // Assuming you have a middleware to protect routes

// Route to book an appointment
router.post('/', protect, bookAppointment); 
// Route to reschedule an appointment
router.put('/:appointmentId', protect, rescheduleAppointment);
// Route to cancel an appointment
router.delete('/:appointmentId', protect, cancelAppointment);
// Route for patient notifications
router.get('/notifications/patient', protect, getPatientNotifications); 
// Route for staff notifications
router.get('/notifications/staff', protect, getStaffNotifications); 


module.exports = router;
