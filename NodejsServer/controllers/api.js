const IsConnected = require('../models/isConnected');

exports.sendToReceiver = (req, res) => {
    console.log(req.body.userId);
}

exports.saveConnectState = async (req, res) => {
    try {
        await IsConnected.create({
            isConnected: true,
        });
        res.json('로그인 상태 저장 성공');
    } catch (error) {
        console.error(error);
    }
}