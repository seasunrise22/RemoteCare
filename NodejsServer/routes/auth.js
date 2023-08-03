const { userJoin } = require('../controllers/auth')

const express = require('express');
const router = express.Router();

router.post('/join', userJoin);

module.exports = router;