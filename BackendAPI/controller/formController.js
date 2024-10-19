const Form2 = require('../models/Form2'); // Adjust the path if necessary

// Create a new form entry
const createForm2 = async (req, res) => {
    const { name, areasConcernedForNeedling, date, signature } = req.body;

    // Validate input
    if (!name || !areasConcernedForNeedling || !date || !signature) {
        return res.status(400).json({ error: 'All fields are required.' });
    }

    // Create a new Form2 instance
    const formEntry = new Form2({
        name,
        areasConcernedForNeedling,
        date,
        signature,
    });

    try {
        await formEntry.save(); // Save to the database
        return res.status(201).json({ message: 'Form submitted successfully', formEntry });
    } catch (error) {
        console.error('Error saving form data:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};

// Export the controller functions
module.exports = {
  createForm2,
};
