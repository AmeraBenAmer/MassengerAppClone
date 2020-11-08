package com.example.messangerapp.model

data class Users(val name:String,
                val profileImage: String) {
    constructor():this("", "")
}