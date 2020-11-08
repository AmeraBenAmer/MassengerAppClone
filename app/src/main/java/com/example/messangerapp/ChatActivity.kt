package com.example.messangerapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.messangerapp.model.*
import com.example.messangerapp.recycleview.RecipientImageMessageItem
import com.example.messangerapp.recycleview.RecipientTextMessageItem
import com.example.messangerapp.recycleview.SenderImageMessageItem
import com.example.messangerapp.recycleview.SenderTextMessageItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity() {

   private lateinit var mCurrentChatChannelId: String

    private val storageInstance: FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firestoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentImageRef: StorageReference
    get() = storageInstance.reference

    private val chatChannelCollectionRef =
        firestoreInstance.collection("chatChannels")

//     Vars
//    private lateinit var currentUser: Users

    private lateinit var recipientUserId :String
    //        val for id to user request message
    private var currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    private val messageAdapter by lazy { GroupAdapter<GroupieViewHolder>() }


    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "users/${FirebaseAuth.getInstance().currentUser?.uid}"
        )


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        image_back_chat_activity.setOnClickListener {
            finish()
        }

        //        change color icons in state bar M sdk = 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }  else{
            window.statusBarColor = Color.WHITE
        }

//        getUserInfo{user ->
//            currentUserId = user
//        }


        val username = intent.getStringExtra("user_name")
        val profileImage = intent.getStringExtra("profile_image")
         recipientUserId =  intent.getStringExtra("uid")

        user_name_chat_activity.text = username

//        send message
        fab_send_image.setOnClickListener {
            val myIntentImage = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(myIntentImage, "Select Image"), 2)

        }

        createChatChannel{channelId ->

            mCurrentChatChannelId = channelId

            getMessages(channelId)


//             if (edit_text_message_activity.text.toString().isNotEmpty()){
//                //        Send Messages
//                btn_send_message.setOnClickListener {
//                    val messageSend = TextMessage(
//                        edit_text_message_activity.text.toString(),
//                        currentUserId,
//                        recipientUserId,
//                        Calendar.getInstance().time)
//                    sendMessage(channelId, messageSend)
//                    edit_text_message_activity.setText("")
//                }
//            }else{
//                btn_send_message.isEnabled = false
//            }
            btn_send_message.setOnClickListener {

                if (edit_text_message_activity.text.toString().isNotEmpty()) {

                    val messageSend =
                        TextMessage(
                            edit_text_message_activity.text.toString(),
                            currentUserId,
                            recipientUserId,
                            Calendar.getInstance().time
                        )
                    sendMessage(channelId, messageSend)
                    edit_text_message_activity.setText("")
                } else {
                    Toast.makeText(this, "Empty", Toast.LENGTH_LONG).show()
                }
            }

        }

//        initial recycle view
        chat_users_rv.apply {
            adapter = messageAdapter
        }

        if (profileImage!!.isNotEmpty()){
            Glide.with(this)
                .load(storageInstance.getReference(profileImage))
                .into(user_image_chat_activity)
        }else{
            user_image_chat_activity.setImageResource(R.drawable.ic_account_circle)
        }

        image_back_chat_activity.setOnClickListener {
            finish()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 2 && requestCode == Activity.RESULT_OK && data != null && data.data != null){

            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImagePath)
            val outputSteam = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 25, outputSteam)

            val selectedImageBytes =  outputSteam.toByteArray()

            uploadImage(selectedImageBytes){path->
                val imageMessage =  ImageMessage(path, mCurrentChatChannelId, recipientUserId, Calendar.getInstance().time )
                chatChannelCollectionRef.document(mCurrentChatChannelId).collection("messages").add(imageMessage)

                sendMessage(currentUserId, imageMessage)

            }


        }

    }

    private fun uploadImage(selectedImageBytes: ByteArray, onSuccess: (imagePath: String) -> Unit) {

       val refPath = currentImageRef.child("${FirebaseAuth.getInstance().currentUser!!.uid}/images/${UUID.nameUUIDFromBytes(selectedImageBytes)}")
        refPath.putBytes(selectedImageBytes)
            .addOnCompleteListener{
                if (it.isSuccessful){

                    onSuccess(refPath.path)
                    Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()

                }else{

                    Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
                }
            }

    }

    private fun sendMessage(channelId: String, messageSend: Message) {

        chatChannelCollectionRef.document(channelId).collection("messages")
            .add(messageSend)

    }
    private fun createChatChannel(onComplete:(channelId: String)-> Unit){

        firestoreInstance.collection("users")
//                uid for user went start conversation
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("sharedChat")
//                uid for anther user
            .document(recipientUserId)
            .get()
            .addOnSuccessListener {document ->
                //        if document exists -> get field name is channelId
                     if (document.exists()){
                         onComplete(document["channelId"] as String)
                         return@addOnSuccessListener
                     }


                val newChatChannel = firestoreInstance.collection("users").document()

        //      store newChatChannel this val to two users
                firestoreInstance.collection("users")
                    .document(recipientUserId)
                    .collection("sharedChat")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChatChannel.id))


                firestoreInstance.collection("users")
                    .document(currentUserId)
                    .collection("sharedChat")
                    .document(recipientUserId)
                    .set(mapOf("channelId" to newChatChannel.id))

                onComplete(newChatChannel. id)
            }
    }




    private fun getMessages(channelId: String) {
        val query = chatChannelCollectionRef
            .document(channelId)
            .collection("messages")
            .orderBy("date", Query.Direction.DESCENDING)
        query.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

            messageAdapter.clear()

            querySnapshot!!.documents.forEach { document ->


                if (document["type"] == MessageType.Text) {
                    val textMessage = document.toObject(TextMessage::class.java)
                    if (textMessage?.senderId == currentUserId) {
                        messageAdapter.add(
                            SenderTextMessageItems(
                                document.toObject(TextMessage::class.java)!!,
                                document.id,
                                this
                            )
                        )
                    } else {
                        messageAdapter.add(
                            RecipientTextMessageItem(
                                document.toObject(TextMessage::class.java)!!,
                                document.id,
                                this
                            )
                        )
                    }
                } else {
                    val imageMessage = document.toObject(ImageMessage::class.java)
                        if (imageMessage?.senderId == currentUserId) {
                            messageAdapter.add(
                                SenderImageMessageItem(
                                    document.toObject(ImageMessage::class.java)!!,
                                    document.id,
                                    this
                                )
                            )
                        } else {
                            messageAdapter.add(
                                RecipientImageMessageItem(
                                    document.toObject(ImageMessage::class.java)!!,
                                    document.id,
                                    this
                                )
                            )
                        }
                }

            }

        }
    }
}
