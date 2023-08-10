const jwt = require('jsonwebtoken');
const db = require('../models');

module.exports = (io) => {
    io.on('connection', (socket) => {

        socket.on('login', async (token) => {
            try {
                const decodedToken = jwt.verify(token, process.env.JWT_SECRET); // 인증실패하면 error throw 됨
                const userName = decodedToken.userName;
                console.log(userName + ' is connected');

                // IsConnected 모델 생성 및 저장
                const user = await db.User.findOne({ where: { userName } });
                if (user) {
                    await user.update({ socketId: socket.id });
                    const userState = await user.getUserState();
                    if (userState) {
                        await userState.update({ isConnected: true });
                    } else {
                        await user.createUserState({ isConnected: true });
                    }
                } else {
                    console.log('해당 유저를 찾을 수 없습니다.');
                }
            } catch (error) {
                console.log('토큰 검증 실패: ', error);
            }
        });

        socket.on('disconnect', async () => {
            const user = await db.User.findOne({ where: { socketId: socket.id } });
            let userName;
            if (user) {
                userName = user.userName;
                const userState = await user.getUserState();
                if (userState) {
                    await userState.update({ isConnected: false });
                }
            }

            console.log(userName + ' is disconnected');
        });
    });
}