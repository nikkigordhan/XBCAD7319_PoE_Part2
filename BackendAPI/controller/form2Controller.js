const Form2 = require('../models/Form2'); 

// Controller to handle form submissions
const form2Controller = {
  // POST: Create a new form submission
  createForm: async (req, res) => {
    try {
      const { name, areasConcernedForNeedling, username } = req.body;

      // Check if all required fields are provided
      if (!name || !areasConcernedForNeedling || !username) {
        return res.status(400).json({ message: 'Please fill in all fields' });
      }

      // Create a new Form2 entry
      const newForm = new Form2({
        name,
        areasConcernedForNeedling,
        username,
      });

      // Save the form data to the database
      await newForm.save();

      return res.status(201).json({ message: 'Form submitted successfully', form: newForm });
    } catch (error) {
      console.error(error);
      return res.status(500).json({ message: 'Server error' });
    }
  },
};

module.exports = form2Controller;
