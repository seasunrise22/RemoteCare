const { userJoin, userLogin } = require('../controllers/auth')

const express = require('express');
const router = express.Router();

router.post('/join', userJoin);
router.post('/login', userLogin);

module.exports = router;