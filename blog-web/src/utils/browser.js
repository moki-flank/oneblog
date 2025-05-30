// 用于获取浏览器相关信息的函数
function getBrowserInfo() {
    let userAgent = navigator.userAgent;
    let browserName;
    let browserVersion;
    if (userAgent.indexOf("Chrome") > -1) {
        browserName = "Chrome";
        let chromeVersionStart = userAgent.indexOf("Chrome/") + 7;
        let chromeVersionEnd = userAgent.indexOf(" ", chromeVersionStart);
        if (chromeVersionEnd === -1) {
            chromeVersionEnd = userAgent.length;
        }
        browserVersion = userAgent.substring(chromeVersionStart, chromeVersionEnd);
    } else if (userAgent.indexOf("Firefox") > -1) {
        browserName = "Firefox";
        let firefoxVersionStart = userAgent.indexOf("Firefox/") + 8;
        let firefoxVersionEnd = userAgent.indexOf(" ", firefoxVersionStart);
        if (firefoxVersionEnd === -1) {
            firefoxVersionEnd = userAgent.length;
        }
        browserVersion = userAgent.substring(firefoxVersionStart, firefoxVersionEnd);
    } else if (userAgent.indexOf("Safari") > -1 && userAgent.indexOf("Chrome") === -1) {
        browserName = "Safari";
        let safariVersionStart = userAgent.indexOf("Safari/") + 7;
        let safariVersionEnd = userAgent.indexOf(" ", safariVersionStart);
        if (safariVersionEnd === -1) {
            safariVersionEnd = userAgent.length;
        }
        browserVersion = userAgent.substring(safariVersionStart, safariVersionEnd);
    } else if (userAgent.indexOf("Edge") > -1) {
        browserName = "Edge";
        let edgeVersionStart = userAgent.indexOf("Edge/") + 5;
        let edgeVersionEnd = userAgent.indexOf(" ", edgeVersionStart);
        if (edgeVersionEnd === -1) {
            edgeVersionEnd = userAgent.length;
        }
        browserVersion = userAgent.substring(edgeVersionStart, edgeVersionEnd);
    } else {
        browserName = "Unknown";
        browserVersion = "Unknown";
    }
    return {
        name: browserName,
        version: browserVersion
    };
}

// 用于获取操作系统相关信息的函数
function getOSInfo() {
    let userAgent = navigator.userAgent;
    let osName;
    let osVersion;
    if (userAgent.indexOf("Windows NT") > -1) {
        osName = "Windows";
        let windowsVersionStart = userAgent.indexOf("Windows NT") + 10;
        let windowsVersionEnd = userAgent.indexOf(";", windowsVersionStart);
        if (windowsVersionEnd === -1) {
        windowsVersionEnd = userAgent.length;
        }
        osVersion = userAgent.substring(windowsVersionStart, windowsVersionEnd);
    } else if (userAgent.indexOf("Mac OS X") > -1) {
        osName = "Mac OS X";
        let macVersionStart = userAgent.indexOf("Mac OS X") + 8;
        let macVersionEnd = userAgent.indexOf(";", macVersionStart);
        if (macVersionEnd === -1) {
            macVersionEnd = userAgent.length;
        }
        osVersion = userAgent.substring(macVersionStart, macVersionEnd);
    } else if (userAgent.indexOf("Linux") > -1) {
        osName = "Linux";
        osVersion = "Unknown";
    } else {
        osName = "Unknown";
        osVersion = "Unknown";
    }
    return {
        name: osName,
        version: osVersion
    };
}

// 将两个函数导出，方便外部模块使用
export {
    getBrowserInfo,
    getOSInfo
};