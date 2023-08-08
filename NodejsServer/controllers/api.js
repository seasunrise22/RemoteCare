const UserState = require('../models/userState');

exports.sendToReceiver = (req, res) => {
    console.log(req.body.userId);
}

exports.saveConnectState = async (req, res) => {
    try {
        await UserState.create({
            isConnected: true,
        });
        res.json('로그인 상태 저장 성공');
    } catch (error) {
        console.error(error);
    }
}