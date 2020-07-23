/*global cordova, module*/

module.exports = {
    execute: function (cmd, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "ShellExec", "execute", [cmd]);
    }
};
