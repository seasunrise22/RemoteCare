const passport = require('passport');
const User = require('../models/user');
const local = require('./localStrategy');

module.exports = () => {
    passport.serializeUser((user, done) => {
        done(null, user.userName);
    });

    passport.deserializeUser((userName, done) => {
        User.findOne({ where: { userName } })
            .then(user => done(null, user))
            .catch(err => done(err));
    });

    local();
}