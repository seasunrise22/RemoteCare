const express = require('express');
const morgan = require('morgan');
const passport = require('passport');
const dotenv = require('dotenv');

const app = express();
const apiRouter = require('./routes/api');
const authRouter = require('./routes/auth');
const { sequelize } = require('./models');
const passportConfig = require('./passport');
dotenv.config();

passportConfig();
app.use(morgan('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use('/api', apiRouter);
app.use('/auth', authRouter);

app.use(passport.initialize());
app.use(passport.session());

sequelize.sync({ force:false })
.then(() => {
    console.log("데이터베이스 연결 성공");
})
.catch((err) => {
    console.error(err);
});

app.listen(8081, () => {
    console.log('Server listening on port 8081');
});
