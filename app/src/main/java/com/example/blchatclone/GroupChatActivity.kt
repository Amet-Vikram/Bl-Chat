package com.example.blchatclone

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.text.format.DateFormat
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blchatclone.adapters.ChatAdapter
import com.example.blchatclone.databinding.ActivityChatBinding
import com.example.blchatclone.databinding.ActivityGroupChatBinding
import com.example.blchatclone.models.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "GroupChatActivity"
class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding
    private lateinit var  db: FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var chatAdapter: ChatAdapter
    private var messageList = ArrayList<Messages>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        binding.btnBack.setOnClickListener {
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity)
            finish()
        }

        binding.tvUserName.text = "Group Chat"

        db.reference.child("Group Chat").addValueEventListener(object: ValueEventListener {
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
                TODO("Not yet implemented")
            }

        })

        chatAdapter = ChatAdapter(messageList, this, null)
        binding.rcvChat.adapter = chatAdapter
        binding.rcvChat.layoutManager = LinearLayoutManager(this)

        binding.ivSendText.setOnClickListener {
            val messageBody = binding.etChatText.text.toString()
            val senderID = auth.uid
            val currentDate = Date()
            val time: String = DateFormat.format("dd-MM-yyyy h:mm a", currentDate.time).toString()
            val messageObject = Messages(senderID!!, messageBody, time)

            sendMessageToFireStore(messageObject)
            binding.etChatText.setText("")
        }
    }

    private fun sendMessageToFireStore( message: Messages?) {
        db.reference.child("Group Chat")
            .push()
            .setValue(message)
            .addOnSuccessListener {
                db.reference.child("Chats")
                    .push()
                    .setValue(message)
                    .addOnSuccessListener {
                        Log.d(TAG, "Message Stored !")
                    }
            }
    }
}