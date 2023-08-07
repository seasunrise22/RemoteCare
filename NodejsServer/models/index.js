const Sequelize = require('sequelize');
const User = require('./user');
const IsConnected = require('./isConnected');
const env = process.env.NODE_ENV || 'development';
const config = require('../config/config')[env];

const db = {};
const sequelize = new Sequelize(
    config.database, config.username, config.password, config,
);

db.sequelize = sequelize;
db.User = User;
db.IsConnected = IsConnected;

User.initiate(sequelize);
IsConnected.initiate(sequelize);

User.associate(db);
IsConnected.associate(db);

module.exports = db;