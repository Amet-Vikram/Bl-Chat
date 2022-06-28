package com.example.blchatclone.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blchatclone.R
import com.example.blchatclone.models.Messages
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val SENDER_VIEW_TYPE = 1
private const val RECEIVER_VIEW_TYPE = 2
private const val TAG = "ChatAdapter"
class ChatAdapter(messageList: ArrayList<Messages>, context: Context, receiverID: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var messageList = ArrayList<Messages>()
    private var context: Context
    private var receiverID: String?

    init {
        this.messageList = messageList
        this.context = context
        this.receiverID = receiverID
    }

    class ReceiverViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val userName: TextView = itemView.findViewById(R.id.receiverName)
        val receiverMessage: TextView = itemView.findViewById(R.id.receiverText)
        val receiverTime: TextView = itemView.findViewById(R.id.receiverTime)
    }

    class SenderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val senderMessage: TextView = itemView.findViewById(R.id.senderText)
        val senderTime: TextView = itemView.findViewById(R.id.senderTime)
    }

    override fun getItemViewType(position: Int): Int {
        return if(messageList[position].uID == FirebaseAuth.getInstance().uid){
            SENDER_VIEW_TYPE
        }else{
            RECEIVER_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == SENDER_VIEW_TYPE){
            val view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false)
            return SenderViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false)
            return ReceiverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
//        Log.d(TAG, "Size of the list = ${messageList.size}")

        holder.itemView.setOnLongClickListener {
           AlertDialog.Builder(context)
                .setTitle("Are you sure you want to DELETE this text?")
                .setPositiveButton("Yes") { dialog, which ->
                    val db = FirebaseDatabase.getInstance()
                    val senderRoomID = FirebaseAuth.getInstance().uid + receiverID

                    db.reference.child("Chats")
                        .child(senderRoomID)
                        .child(currentMessage.messageID)
                        .setValue(null)

                }.setNegativeButton("No") { dialog, which ->
                   dialog.dismiss()
               }.show()
            false
        }

        if(holder.javaClass == SenderViewHolder::class.java){
            val viewHolder = holder as SenderViewHolder
            viewHolder.senderMessage.text = currentMessage.message
            viewHolder.senderTime.text = currentMessage.timeStamp
        }else{
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.receiverMessage.text = currentMessage.message
            viewHolder.receiverTime.text = currentMessage.timeStamp
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


}