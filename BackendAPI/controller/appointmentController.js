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
        description,
        status: 'pending' // Status is set to pending when an appointment is booked
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
    const appointmentId = req.params.appointmentId.trim();
    const { date, time, description } = req.body;

    try {
        const appointment = await Appointment.findById(appointmentId);

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Check if the logged-in user is allowed to reschedule this appointment
        if (appointment.patient.toString() !== req.user._id.toString()) {
            return res.status(403).json({ message: 'You do not have permission to reschedule this appointment' });
        }

        // Update the appointment details and mark it as rescheduled
        appointment.date = date || appointment.date;
        appointment.time = time || appointment.time;
        appointment.description = description || appointment.description;
        appointment.status = 'rescheduled'; // Change status to rescheduled

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
        const appointment = await Appointment.findById(appointmentId);

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Verify if the logged-in user has permission to cancel this appointment
        if (appointment.patient.toString() !== req.user._id.toString()) {
            return res.status(403).json({ message: 'You do not have permission to cancel this appointment' });
        }

        // Mark the appointment as canceled instead of deleting it
        appointment.status = 'canceled';

        await appointment.save();

        res.status(200).json({ message: 'Appointment canceled successfully' });
    } catch (error) {
        console.error('Error canceling appointment:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Approve an appointment
const approveAppointment = async (req, res) => {
    const appointmentId = req.params.appointmentId.trim();

    try {
        const appointment = await Appointment.findById(appointmentId).populate('patient', 'name email'); // Populate patient details

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Verify if the logged-in user is a staff member
        if (!req.user.isStaff) {
            return res.status(403).json({ message: 'You do not have permission to approve this appointment' });
        }

        // Ensure the appointment is still pending
        if (appointment.status !== 'pending') {
            return res.status(400).json({ message: 'Appointment cannot be approved as it is not pending' });
        }

        // Update the appointment status to 'approved'
        appointment.status = 'approved';

        await appointment.save();

        res.status(200).json({
            message: 'Appointment approved successfully',
            appointment,
        });
    } catch (error) {
        console.error('Error approving appointment:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


const getPatientNotifications = async (req, res) => {
    try {
        const now = new Date();
        const oneDayLater = new Date(now);
        oneDayLater.setDate(now.getDate() + 1);

        // Fetch all appointments for the logged-in patient within the next 24 hours
        const upcomingAppointments = await Appointment.find({
            patient: req.user._id, // Only fetch appointments for the logged-in patient
            date: { $gte: now, $lt: oneDayLater }, // Get appointments within the next 24 hours
            status: 'confirmed' // Only notify for confirmed appointments happening tomorrow
        }).populate('patient', 'name email');

        // Fetch all appointments with status changes (rescheduled, canceled, confirmed)
        const statusChangedAppointments = await Appointment.find({
            patient: req.user._id,
            status: { $in: ['rescheduled', 'canceled', 'confirmed'] } // Notify about status changes
        }).populate('patient', 'name email');

        // Prepare notifications for upcoming appointments
        const upcomingNotifications = upcomingAppointments.map(appointment => ({
            appointmentId: appointment._id,
            message: `Reminder: You have a confirmed appointment tomorrow at ${appointment.time}.`,
            date: appointment.date,
            time: appointment.time,
            description: appointment.description,
            status: appointment.status
        }));

        // Prepare notifications for status changes (rescheduled, canceled, confirmed)
        const statusChangeNotifications = statusChangedAppointments.map(appointment => {
            let message;
            if (appointment.status === 'rescheduled') {
                message = `Your appointment on ${appointment.date} has been rescheduled to ${appointment.date} at ${appointment.time}.`;
            } else if (appointment.status === 'canceled') {
                message = `Your appointment on ${appointment.date} has been canceled.`;
            } else if (appointment.status === 'confirmed') {
                message = `Your appointment on ${appointment.date} has been confirmed.`;
            }

            return {
                appointmentId: appointment._id,
                message,
                date: appointment.date,
                time: appointment.time,
                description: appointment.description,
                status: appointment.status
            };
        });

        // Combine both notifications
        const notifications = [...upcomingNotifications, ...statusChangeNotifications];

        if (!notifications.length) {
            return res.status(404).json({ message: 'No notifications found for this patient' });
        }

        res.status(200).json(notifications);
    } catch (error) {
        console.error('Error fetching patient notifications:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

const getStaffNotifications = async (req, res) => {
    try {
        // Fetch all appointments that are either pending, rescheduled, or canceled
        const appointments = await Appointment.find({
            status: { $in: ['pending', 'rescheduled', 'canceled'] } // Include all relevant statuses
        }).populate('patient', 'name email'); // Populate patient details for notifications

        if (!appointments.length) {
            return res.status(404).json({ message: 'No notifications found for staff' });
        }

        // Prepare notifications for staff based on appointment status
        const staffNotifications = appointments.map(appointment => {
            let message;
            if (appointment.status === 'pending') {
                message = `New appointment for ${appointment.patient.name} on ${appointment.date} at ${appointment.time} is pending confirmation.`;
            } else if (appointment.status === 'rescheduled') {
                message = `Appointment for ${appointment.patient.name} has been rescheduled to ${appointment.date} at ${appointment.time}.`;
            } else if (appointment.status === 'canceled') {
                message = `Appointment for ${appointment.patient.name} on ${appointment.date} has been canceled.`;
            }

            return {
                appointmentId: appointment._id,
                patientId: appointment.patient._id,
                message,
                date: appointment.date,
                time: appointment.time,
                description: appointment.description,
                status: appointment.status
            };
        });

        res.status(200).json(staffNotifications);
    } catch (error) {
        console.error('Error fetching staff notifications:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// View all confirmed appointments for the logged-in patient
const getConfirmedAppointmentsForPatient = async (req, res) => {
    try {
        // Find all appointments for the logged-in patient with status "confirmed"
        const appointments = await Appointment.find({
            patient: req.user._id, // Logged-in patient's ID
            status: 'confirmed'   // Only confirmed appointments
        }).populate('patient', 'name email'); 

        if (!appointments.length) {
            return res.status(404).json({ message: 'No confirmed appointments found for this patient' });
        }

        res.status(200).json(appointments);
    } catch (error) {
        console.error('Error fetching confirmed appointments for patient:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// View all appointments for all patients regardless of status
const getAllAppointments = async (req, res) => {
    try {
        // Find all appointments regardless of their status
        const appointments = await Appointment.find({})
            .populate('patient', 'name email'); 

        if (!appointments.length) {
            return res.status(404).json({ message: 'No appointments found' });
        }

        res.status(200).json(appointments);
    } catch (error) {
        console.error('Error fetching all appointments:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Add Appointment Notes
const addAppointmentNotes = async (req, res) => {
    const { appointmentId } = req.params; // Appointment ID from the route parameter
    const { notes } = req.body; // Notes from the request body

    try {
        const appointment = await Appointment.findById(appointmentId).populate('patient', 'name email'); 

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Verify if the logged-in user is a staff member
        if (!req.user.isStaff) {
            return res.status(403).json({ message: 'You do not have permission to add notes to this appointment' });
        }

        // Add or update the notes for the appointment
        appointment.notes = notes;

        await appointment.save();

        res.status(200).json({
            message: 'Appointment notes added successfully',
            appointment,
        });
    } catch (error) {
        console.error('Error adding appointment notes:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

// Get Appointment Notes
const getAppointmentNotes = async (req, res) => {
    const { appointmentId } = req.params;

    try {
        const appointment = await Appointment.findById(appointmentId).populate('patient', 'name email');

        if (!appointment) {
            return res.status(404).json({ message: 'Appointment not found' });
        }

        // Verify if the logged-in user is a staff member
        if (!req.user.isStaff) {
            return res.status(403).json({ message: 'You do not have permission to view notes for this appointment' });
        }

        res.status(200).json({
            message: 'Appointment notes retrieved successfully',
            notes: appointment.notes,
        });
    } catch (error) {
        console.error('Error fetching appointment notes:', error);
        res.status(500).json({ message: 'Server error' });
    }
};
module.exports = {
    bookAppointment,
    rescheduleAppointment,
    cancelAppointment,
    approveAppointment,
    getPatientNotifications,
    getStaffNotifications,
    getConfirmedAppointmentsForPatient, 
    getAllAppointments,
    addAppointmentNotes,
    getAppointmentNotes,
};