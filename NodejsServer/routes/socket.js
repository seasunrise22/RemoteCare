const jwt = require('jsonwebtoken');
// const axios = require('axios');
const db = require('../models');

module.exports = (io) => {
    io.on('connection', (socket) => {
        console.log('A user connected');

        socket.on('loginSuccess', async (token) => {
            try {
                console.log('Login success event received');
                const decodedToken = jwt.verify(token, process.env.JWT_SECRET);
                const userId = decodedToken.userId;
                console.log("User ID: " + userId);

                // IsConnected 모델 생성 및 저장
                const user = await db.User.find({ where: { userId } });
                if (user) {
                    await user.createIsConnected({ isConnected: true });
                    console.log('IsConnected 모델 생성 및 저장 성공');
                } else {
                    console.log('해당 유저를 찾을 수 없습니다.');
                }

                // // HTTP POST 요청 보내기
                // axios.post('http://localhost:8081/api/saveConnectState', { userId })
                //     .then(response => {
                //         console.log('HTTP POST 요청 성공: ', response.data);
                //     })
                //     .catch(error => {
                //         console.log('HTTP POST 요청 실패: ', error);
                //     });
            } catch (error) {
                console.log('토큰 검증 실패: ', error);
            }
        });

        socket.on('disconnect', () => {
            console.log('A user disconnected');
        });
    });
}