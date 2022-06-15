package com.example.blchatclone.models

data class Users(var profilePic: String? = null,
            var userName: String,
            var email: String,
            var password: String,
            var userId: String,
            var lastMessage: String? = null,
            var status: String? = null) {

    //Empty Constructor
    constructor() : this("", "", "", "", "", "", "")

    constructor(email: String, password: String, userName: String) : this(){
        this.email = email
        this.password = password
        this.userName = userName
    }
}