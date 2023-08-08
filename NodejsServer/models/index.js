const Sequelize = require('sequelize');
const User = require('./user');
const UserState = require('./userState');
const env = process.env.NODE_ENV || 'development';
const config = require('../config/config')[env];

const db = {};
const sequelize = new Sequelize(
    config.database, config.username, config.password, config,
);

db.sequelize = sequelize;
db.User = User;
db.UserState = UserState;

User.initiate(sequelize);
UserState.initiate(sequelize);

User.associate(db);
UserState.associate(db);

module.exports = db;