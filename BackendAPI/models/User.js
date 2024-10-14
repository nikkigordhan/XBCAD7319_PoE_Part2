const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const UserSchema = new mongoose.Schema({
  username: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  role: { type: String, enum: ['patient', 'staff']},
  // For patients only:
  name: { type: String },
  surname: { type: String },
  email: { type: String },
  phoneNumber: { type: String },
  medicalAid: { type: Boolean },
  medicalAidNumber: { type: String },
});

UserSchema.methods.matchPassword = async function (enteredPassword) {
  return await bcrypt.compare(enteredPassword, this.password);
};

const User = mongoose.model('User', UserSchema);

module.exports = User;
