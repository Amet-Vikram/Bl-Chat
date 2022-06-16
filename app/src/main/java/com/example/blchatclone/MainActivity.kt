package com.example.blchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.blchatclone.adapters.FragmentAdapter
import com.example.blchatclone.databinding.ActivityMainBinding
import com.example.blchatclone.fragments.CallsFragment
import com.example.blchatclone.fragments.ChatFragment
import com.example.blchatclone.fragments.StatusFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            R.id.groupChat -> {
                Toast.makeText(this, "Group Chat", Toast.LENGTH_SHORT).show()
            }
            R.id.logout -> {
                auth.signOut()
                val intentToSignInActivity = Intent(this, SignInActivity::class.java)
                startActivity(intentToSignInActivity)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}