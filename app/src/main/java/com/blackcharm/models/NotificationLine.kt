package com.blackcharm.models

class NotificationLine(val senderUid: String, val text: String, val seen: Int, val timestamp: Int) {
    var keyVal = ""
        get() = field

    var notificationseen = 0
        get() = field

    constructor() : this("", "", 1, 1)
}
