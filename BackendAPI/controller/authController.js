const User = require('../models/User');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const register = async (req, res) => {
    const { username, password, role, name, surname, email, phoneNumber, medicalAid, medicalAidNumber } = req.body;
  
    try {
      console.log("Register request received:", req.body);
  
      // Check if user already exists
      const userExists = await User.findOne({ username });
      if (userExists) {
        console.log("User already exists with username:", username);
        return res.status(400).json({ message: 'Username already taken' });
      }
  
      // Hash password
      console.log("Hashing password for:", username);
      const hashedPassword = await bcrypt.hash(password, 10);
  
      // Create new user
      const user = new User({
        username,
        password: hashedPassword,
        role,
        name,
        surname,
        email,
        phoneNumber,
        medicalAid,
        medicalAidNumber,
      });
  
      // Save user to database
      console.log("Saving new user to the database...");
      await user.save();
  
      console.log("User registered successfully:", username);
      res.status(201).json({ message: 'User registered successfully' });
    } catch (error) {
      console.error("Error during registration:", error);
      res.status(500).json({ message: 'Server error' });
    }
  };

  const login = async (req, res) => {
    const { username, password } = req.body;
  
    try {
      console.log("Login request received for username:", username);
  
      // Find user by username
      const user = await User.findOne({ username });
      if (!user) {
        console.log("No user found with username:", username);
        return res.status(400).json({ message: 'Invalid credentials' });
      }
  
      // Check if password matches
      console.log("Checking password for user:", username);
      const isMatch = await user.matchPassword(password);
      if (!isMatch) {
        console.log("Invalid password for username:", username);
        return res.status(400).json({ message: 'Invalid credentials' });
      }
  
      // Generate token
      console.log("Generating token for user:", username);
      const token = jwt.sign({ id: user._id, role: user.role }, process.env.JWT_SECRET, {
        expiresIn: '1h',
      });
  
      console.log("Login successful for user:", username);
  
      // Differentiate response based on user role
      if (user.role === 'staff') {
        return res.json({
          token,
          userId: user._id,
          role: user.role,
          message: 'Welcome to the staff portal!',
        });
      } else if (user.role === 'patient') {
        return res.json({
          token,
          userId: user._id,
          role: user.role,
          message: 'Welcome to the patient portal!',
        });
      } else {
        return res.status(400).json({ message: 'Unknown user role' });
      }
    } catch (error) {
      console.error("Error during login:", error);
      res.status(500).json({ message: 'Server error' });
    }
  };
  
  
  const forgetPassword = async (req, res) => {
    const { username, newPassword } = req.body;
  
    try {
      console.log("Forget password request for username:", username);
  
      // Find user by username
      const user = await User.findOne({ username });
      if (!user) {
        console.log("No user found with username:", username);
        return res.status(404).json({ message: 'User not found' });
      }
  
      // Hash new password
      console.log("Hashing new password for user:", username);
      const hashedPassword = await bcrypt.hash(newPassword, 10);
      user.password = hashedPassword;
  
      // Save updated user
      console.log("Saving updated password for user:", username);
      await user.save();
  
      console.log("Password updated successfully for user:", username);
      res.json({ message: 'Password updated successfully' });
    } catch (error) {
      console.error("Error during forget password:", error);
      res.status(500).json({ message: 'Server error' });
    }
  };


  // Export the functions
   module.exports = {
    register,
    login,
    forgetPassword,
  };