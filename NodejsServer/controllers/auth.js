const bcrypt = require('bcrypt');
const User = require('../models/user');

exports.userJoin = async (req, res) => {
    const { userId, userPassword, userPosition } = req.body;
    try {
        const hash = await bcrypt.hash(userPassword, 12);
        await User.create({
            userId,
            userPassword,
            userPosition
        });
        return res.json('회원가입 성공!');
    } catch (error) {
        console.error(error);
    }
}