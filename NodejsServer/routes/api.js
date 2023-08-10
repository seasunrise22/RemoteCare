const express = require('express');
const router = express.Router();
const { checkReceiverLogin } = require('../controllers/api')

router.get('/checkReceiverLogin', checkReceiverLogin);

module.exports = router;