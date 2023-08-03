const express = require('express');
const router = express.Router();

router.get('/call', (req, res) => {
    res.send('api/call is called');
});

module.exports = router;