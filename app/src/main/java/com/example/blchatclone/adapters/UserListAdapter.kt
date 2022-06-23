package com.example.blchatclone.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blchatclone.ChatActivity
import com.example.blchatclone.R
import com.example.blchatclone.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(userList: ArrayList<Users>, context: Context): RecyclerView.Adapter<UserListAdapter.UserChatHolder>() {

    private var userList = ArrayList<Users>()
    private var context: Context

    init {
        this.userList = userList
        this.context = context
    }

    class UserChatHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val profilePic: CircleImageView = itemView.findViewById(R.id.ivUserProfilePic)
        val userName: TextView = itemView.findViewById(R.id.tvChatUserName)
        val userLastText: TextView = itemView.findViewById(R.id.tvChatLastText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_chat, parent, false)
        return UserChatHolder(view)
    }

    override fun onBindViewHolder(holder: UserChatHolder, position: Int) {
        val currentItem = userList[position]

        val roomID = FirebaseAuth.getInstance().uid + currentItem.userId

        FirebaseDatabase.getInstance().reference.child("Chats")
            .child(roomID)
            .orderByChild("timeStamp")
            .limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        for(data in snapshot.children){
                            holder.userLastText.text = data.child("message").value.toString()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        Glide.with(context)
            .load(currentItem.profilePic)
            .centerCrop()
            .placeholder(R.drawable.avatar3)
            .into(holder.profilePic)

        holder.userName.text = currentItem.userName
        holder.userLastText.text = currentItem.lastMessage

        holder.itemView.setOnClickListener {
            val intentToChatActivity = Intent(context, ChatActivity::class.java)

            intentToChatActivity.putExtra("userID", currentItem.userId)
            intentToChatActivity.putExtra("profilePic", currentItem.profilePic)
            intentToChatActivity.putExtra("userName", currentItem.userName)

            context.startActivity(intentToChatActivity)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}