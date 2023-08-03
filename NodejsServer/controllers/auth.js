const bcrypt = require('bcrypt');
const passport = require('passport');
const jwt = require('jsonwebtoken');
const User = require('../models/user');

exports.userJoin = async (req, res) => {
    const { userId, userPassword, userPosition } = req.body;
    try {
        const hash = await bcrypt.hash(userPassword, 12);
        await User.create({
            userId,
            userPassword: hash,
            userPosition
        });
        return res.json('회원가입 성공!');
    } catch (error) {
        console.error(error);
    }
}

exports.userLogin = async (req, res, next) => {
    passport.authenticate('local', (authError, user, info) => {
        if(authError) {
            console.error(authError);
        }
        if(!user) {
            return res.json(info.message);
        }
        return req.login(user, (loginError) => {
            if(loginError) {
                console.error(loginError);
            }
            const token = jwt.sign({
                userId: user.userId
            }, process.env.JWT_SECRET);
            
            return res.json({
                success: true,
                message: '로그인에 성공하였습니다.',
                token
            });
        });
    })(req, res, next);
}