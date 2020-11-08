package com.example.messangerapp.model

import java.util.*

data class TextMessage(val text: String,
                       override val senderId: String,
                       override val recipientId: String,
                       override val date: Date,
                       override val type: String = MessageType.Text): Message{

//    when use firebase must be put constructor
    constructor():this("", "", "", Date())
}
