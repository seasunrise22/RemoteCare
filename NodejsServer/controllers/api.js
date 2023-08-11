const db = require('../models');

exports.checkReceiverLogin = async (req, res) => {
    const { userName } = req.query;
    const user = await db.User.findOne({ where: { userName } });
    if (user) {
        const userState = await user.getUserState();
        if (userState) {
            if (userState.isConnected) {
                res.json({
                    message: userName + ' 는(은) 현재 연결된 상태입니다.',
                    readyToCall: true,
                });
            } else {
                res.json({
                    message: userName + ' 는(은) 현재 연결되지 않은 상태입니다.',
                    readyToCall: false,
                });
            }
        }
    } else {
        res.json({
            message: userName + ' 는(은) 없는 회원입니다.',
            readyToCall: false,
        });
    }
}