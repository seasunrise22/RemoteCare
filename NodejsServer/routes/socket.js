const jwt = require('jsonwebtoken');
const db = require('../models');

module.exports = (io) => {
    io.on('connection', (socket) => {
        console.log('A user connected');

        socket.on('loginSuccess', async (token) => {
            try {
                console.log('Login success event received');
                const decodedToken = jwt.verify(token, process.env.JWT_SECRET);
                const userName = decodedToken.userName;
                console.log("userName: " + userName);

                // IsConnected 모델 생성 및 저장
                const user = await db.User.findOne({ where: { userName } });
                if (user) {
                    const userState = await user.getUserState();
                    if (userState) {
                        await userState.update({ isConnected: true });
                        console.log('IsConnected 모델 업데이트 성공');
                    } else {
                        await user.createUserState({ isConnected: true });
                        console.log('IsConnected 모델 생성 및 저장 성공');
                    }
                } else {
                    console.log('해당 유저를 찾을 수 없습니다.');
                }
            } catch (error) {
                console.log('토큰 검증 실패: ', error);
            }
        });

        socket.on('disconnect', () => {
            console.log('A user disconnected');
        });
    });
}