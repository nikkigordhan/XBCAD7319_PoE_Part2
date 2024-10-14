const Appointment = require('../models/Appointment');

// Book an appointment
const bookAppointment = async (req, res) => {
  const { date, time, description } = req.body;
  const patientId = req.user.id; // Assuming req.user is populated by your auth middleware

  try {
    const appointment = new Appointment({
      patient: patientId,
      date,
      time,
      description
    });

    await appointment.save();
    res.status(201).json({
      message: 'Appointment booked successfully',
      appointment,
    });
  } catch (error) {
    console.error("Error booking appointment:", error);
    res.status(500).json({ message: 'Server error' });
  }
};

// Reschedule an appointment
const rescheduleAppointment = async (req, res) => {
    // Trim the appointmentId to remove any unwanted characters like \n or spaces
    const appointmentId = req.params.appointmentId.trim();
    const { date, time, description } = req.body;

    try {
        // Find the appointment by ID
        const appointment = await Appointment.findById(appointmentId);

        // Check if the appointment exists
        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Check if the logged-in user is the owner of the appointment
        if (appointment.patient.toString() !== req.user._id.toString()) {
            return res.status(403).json({ message: 'You do not have permission to reschedule this appointment' });
        }

        // Update the appointment details
        appointment.date = date || appointment.date;
        appointment.time = time || appointment.time;
        appointment.description = description || appointment.description;

        // Save the updated appointment
        const updatedAppointment = await appointment.save();

        res.status(200).json({
            message: 'Appointment rescheduled successfully',
            appointment: updatedAppointment
        });
    } catch (error) {
        console.error('Error rescheduling appointment:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Cancel an appointment
const cancelAppointment = async (req, res) => {
    const appointmentId = req.params.appointmentId.trim();

    try {
        // Find the appointment by ID
        const appointment = await Appointment.findById(appointmentId);

        // Check if the appointment exists
        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Verify if the logged-in user is the owner of the appointment
        if (appointment.patient.toString() !== req.user._id.toString()) {
            return res.status(403).json({ message: 'You do not have permission to cancel this appointment' });
        }

        // Delete the appointment using deleteOne or findByIdAndDelete
        await Appointment.findByIdAndDelete(appointmentId);

        res.status(200).json({ message: 'Appointment canceled successfully' });
    } catch (error) {
        console.error('Error canceling appointment:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get Notifications for Patients
const getPatientNotifications = async (req, res) => {
    try {
        const now = new Date();
        const oneDayLater = new Date(now);
        oneDayLater.setDate(now.getDate() + 1);

        // Fetch appointments for the logged-in patient
        const appointments = await Appointment.find({
            patient: req.user._id, // Only fetch appointments for the logged-in patient
            date: { $gte: now, $lt: oneDayLater } // Get appointments within the next 24 hours
        }).populate('patient', 'name email');

        // Prepare notifications based on fetched appointments
        const notifications = appointments.map(appointment => ({
            appointmentId: appointment._id,
            message: `Reminder: You have an appointment tomorrow at ${appointment.date}.`,
            date: appointment.date,
            time: appointment.time,
            description: appointment.description
        }));

        if (!notifications.length) {
            return res.status(404).json({ message: 'No notifications found for this patient' });
        }

        res.status(200).json(notifications);
    } catch (error) {
        console.error('Error fetching patient notifications:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get Notifications for Staff Members
const getStaffNotifications = async (req, res) => {
    try {
        // Fetch all appointments that are pending confirmation
        const notifications = await Appointment.find({
            confirmed: false // Assuming there's a confirmed field to check status
        }).populate('patient', 'name email'); // Populate patient details for notifications

        if (!notifications.length) {
            return res.status(404).json({ message: 'No notifications found for staff' });
        }

        // Prepare notifications for staff
        const staffNotifications = notifications.map(appointment => ({
            appointmentId: appointment._id,
            patientId: appointment.patient,
            message: `Appointment for ${appointment.patient.name} on ${appointment.date} needs confirmation.`,
            date: appointment.date,
            time: appointment.time,
            description: appointment.description
        }));

        res.status(200).json(staffNotifications);
    } catch (error) {
        console.error('Error fetching staff notifications:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


module.exports = {
    bookAppointment,
    rescheduleAppointment,
    cancelAppointment,
    getPatientNotifications,
    getStaffNotifications,
};