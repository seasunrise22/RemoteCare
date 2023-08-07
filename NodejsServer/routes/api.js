const express = require('express');
const router = express.Router();
const { sendToReceiver, saveConnectState } = require('../controllers/api')

router.post('/call', sendToReceiver);
router.post('/saveConnectState', saveConnectState);

module.exports = router;