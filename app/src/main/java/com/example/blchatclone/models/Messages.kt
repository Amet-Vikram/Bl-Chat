package com.example.blchatclone.models

data class Messages(var uID: String, var message: String, var messageID: String, var timeStamp: String,) {
    constructor() : this("", "", "", "")

    constructor(uID: String, message: String, timeStamp: String): this(){
        this.uID = uID
        this.message = message
        this.timeStamp = timeStamp
    }

    constructor(uID: String, message: String): this(){
        this.uID = uID
        this.message = message
    }
}