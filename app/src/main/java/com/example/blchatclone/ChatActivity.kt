package com.example.blchatclone

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blchatclone.adapters.ChatAdapter
import com.example.blchatclone.databinding.ActivityChatBinding
import com.example.blchatclone.models.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "ChatActivity"
class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var  db: FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private var messageList = ArrayList<Messages>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        val senderID = auth.uid
        val receiverID = intent.getStringExtra("userID")
        val userName = intent.getStringExtra("userName")
        val profilePic = intent.getStringExtra("profilePic")
        val senderRoom = senderID + receiverID
        val receiverRoom = receiverID + senderID

        binding.tvUserName.text = userName

        Glide.with(this)
            .load(profilePic)
            .centerCrop()
            .placeholder(R.drawable.avatar3)
            .into(binding.receiverProfilePic)

        binding.btnBack.setOnClickListener {
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity)
            finish()
        }

        chatAdapter = ChatAdapter(messageList, this, receiverID!!)
        binding.rcvChat.adapter = chatAdapter
        binding.rcvChat.layoutManager = LinearLayoutManager(this)

        binding.ivSendText.setOnClickListener {
            val messageBody = binding.etChatText.text.toString()
            val currentDate = Date()
            val time: String = DateFormat.format("dd-MM-yyyy h:mm a", currentDate.time).toString()
            val message = senderID?.let { id -> Messages(id, messageBody, time) }
            sendMessageToFireStore(senderRoom, receiverRoom, message)
            binding.etChatText.setText("")
        }

        db.reference.child("Chats")
            .child(senderRoom)
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for(data in snapshot.children){
                        val message = data.getValue(Messages::class.java)
                        message?.messageID = data.key.toString()
                        if (message != null) {
                            messageList.add(message)
                        }
                        Log.d(TAG, "${messageList.size} is the Size")
                        chatAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun sendMessageToFireStore(senderRoom: String, receiverRoom: String, message: Messages?) {
        db.reference.child("Chats")
            .child(senderRoom)
            .push()
            .setValue(message)
            .addOnSuccessListener {
                db.reference.child("Chats")
                    .child(receiverRoom)
                    .push()
                    .setValue(message)
                    .addOnSuccessListener {
                    Log.d(TAG, "Message Stored !")
                }
        }
    }
}