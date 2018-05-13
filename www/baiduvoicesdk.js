var exec = require('cordova/exec');

exports.init = function () {
  exec(null, null, "baiduvoicesdk", "init",
    []);
};

exports.start = function (success, error) {
  exec(success, error, "baiduvoicesdk", "start",
    []);
};

exports.stop = function () {
  exec(null, null, "baiduvoicesdk", "stop",
    []);
};
exports.announce = function (word, success, error) {
  exec(success, error, "baiduvoicesdk", "announce",
    [word]);
};
// exports.startPlay = function () {
//   exec(null, null, "AudioRecorder", "startPlay",
//     []);
// };
// exports.stopPlay = function () {
//   exec(null, null, "AudioRecorder", "stopPlay",
//     []);
// };