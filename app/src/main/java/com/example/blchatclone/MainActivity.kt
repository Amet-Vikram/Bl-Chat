package com.example.blchatclone

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.blchatclone.adapters.FragmentAdapter
import com.example.blchatclone.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var  db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = fragmentAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "CHATS"
                1 -> tab.text = "STATUS"
                2 -> tab.text = "CALLS"
            }
        }.attach()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                val intentToProfileActivity = Intent(this, ProfileSettingsActivity::class.java)
                startActivity(intentToProfileActivity)
            }
            R.id.groupChat -> {
//                Toast.makeText(this, "Group Chat", Toast.LENGTH_SHORT).show()
                requestNewGroup()
            }
            R.id.logout -> {
                auth.signOut()
                val intentToSignInActivity = Intent(this, SignInActivity::class.java)
                startActivity(intentToSignInActivity)
                finish()
            }
            R.id.crash -> {
                throw RuntimeException("Test Crash")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestNewGroup() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.AlertDialog).setTitle("Enter Group Name: ")

        val etGroupName = EditText(this)
        etGroupName.hint = "e.g. RSS Yuva Sangh"

        dialogBuilder.setView(etGroupName)

        dialogBuilder.setPositiveButton("Create", DialogInterface.OnClickListener { dialog, which ->
             if(etGroupName.text.isNotEmpty()){
                 val groupName = etGroupName.text.toString()
//                 createNewGroup(groupName)
             }else{
                 etGroupName.error = "Please enter a group name"
                 etGroupName.requestFocus()
             }
        })

        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        dialogBuilder.show()
    }

//    private fun createNewGroup(groupName: String) {
//        db.reference.child("Groups").child(groupName)
//    }
}