const mongoose = require('mongoose');

// Define the schema for Form2
const Form2Schema = new mongoose.Schema({
  name: { type: String, required: true }, 
  areasConcernedForNeedling: { type: String, required: true }, 
  username: { type: String, required: true }, 
});

// Create the model for Form2 using the schema
const Form2 = mongoose.model('Form2', Form2Schema);


module.exports = Form2;

