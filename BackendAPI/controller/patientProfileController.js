const User = require('../models/User'); // Adjust the path as necessary

const savePatientProfile = async (req, res) => {
    const { email, phoneNumber, medicalAid, medicalAidNumber } = req.body;

    try {
        // Find the user profile by user ID
        let userProfile = await User.findById(req.user._id);

        if (userProfile) {
            // Only update the specified fields
            userProfile.email = email || userProfile.email;
            userProfile.phoneNumber = phoneNumber || userProfile.phoneNumber;
            userProfile.medicalAid = medicalAid || userProfile.medicalAid;
            userProfile.medicalAidNumber = medicalAidNumber || userProfile.medicalAidNumber;

            await userProfile.save();
            res.status(200).json({
                message: 'Patient profile updated successfully',
                profile: {
                    email: userProfile.email,
                    phoneNumber: userProfile.phoneNumber,
                    medicalAid: userProfile.medicalAid,
                    medicalAidNumber: userProfile.medicalAidNumber,
                }
            });
        } else {
            // If user profile doesn't exist, return an error
            return res.status(404).json({ message: 'User not found' });
        }
    } catch (error) {
        console.error('Error saving patient profile:', error);
        res.status(500).json({ message: 'Server error' });
    }
};


// Get Patient Profile
const getPatientProfile = async (req, res) => {
    try {
        // Fetch the user's profile using the logged-in user's ID
        const user = await User.findById(req.user._id).select('-password'); // Exclude the password field

        if (!user) {
            return res.status(404).json({ message: 'User not found' });
        }

        res.status(200).json(user);
    } catch (error) {
        console.error('Error fetching patient profile:', error);
        res.status(500).json({ message: 'Server error' });
    }
};

module.exports = {
    savePatientProfile,
    getPatientProfile,
};