package com.example.messangerapp.model

import java.util.*

interface Message {

    val senderId: String
    val recipientId: String
    val date: Date
    val type: String
}