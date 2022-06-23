package com.example.blchatclone

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.blchatclone.databinding.ActivityProfileBinding
import com.example.blchatclone.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private const val TAG = "ProfileSettingsActivity"
class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var storageRef: StorageReference
    private lateinit var imageURI : Uri
    private lateinit var resultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storageRef = FirebaseStorage.getInstance().reference

        resultLauncher = this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK) {
                imageURI= it.data?.data!!
                uploadImageToFirebase(imageURI)
            }
        }

        binding.ivBack.setOnClickListener {
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity)
            finish()
        }

        binding.ivPlus.setOnClickListener {
            val intentGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            resultLauncher.launch(intentGallery)
        }

        binding.btnSave.setOnClickListener {
            if(binding.etUserName.text != null || binding.etUserName.text != null){
                val status = binding.etStatus.text.toString()
                val name = binding.etUserName.text.toString()
                val userID = auth.uid

                val data = HashMap<String, Any>()
                data["userName"] = name
                data["status"] = status

                db.reference.child("Users")
                    .child("$userID").updateChildren(data)

            }else{
                Toast.makeText(this, "Empty Fields!", Toast.LENGTH_SHORT).show()
            }

        }

        loadUserData()
    }

    private fun uploadImageToFirebase(imageURI: Uri) {
        //Need Storage Reference, Image Uri and filename
        val userID = auth.uid
        val imageDirRef = storageRef.child("Users/$userID/profile.jpg")

        imageDirRef.putFile(imageURI).addOnSuccessListener {

            imageDirRef.downloadUrl.addOnSuccessListener {uri ->

                db.reference.child("Users")
                    .child("$userID")
                    .child("profilePic")
                    .setValue(uri.toString())
            }
        }
    }

    private fun loadUserData() {
        val userID = auth.uid
        db.reference.child("Users")
            .child("$userID")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(Users::class.java)

                    if (user != null) {
                        Glide.with(this@ProfileSettingsActivity)
                            .load(user.profilePic)
                            .centerCrop()
                            .placeholder(R.drawable.avatar3)
                            .into(binding.ivUserProfile)

                        binding.etUserName.setText(user.userName)
                        binding.etStatus.setText(user.status)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}