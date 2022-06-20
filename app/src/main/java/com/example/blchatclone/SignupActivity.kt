package com.example.blchatclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.blchatclone.databinding.ActivitySignupBinding
import com.example.blchatclone.models.Users
import com.example.blchatclone.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "SignupActivity"
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var progressBar: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Hide Action Bar
        supportActionBar?.hide()
        //Firebase properties
        db = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        progressBar = ProgressDialog(this)
        progressBar.setTitle("Creating Account")
        progressBar.setMessage("Hold up, while we register you!")
//        progressBar.animate()

        binding.btnSignup.setOnClickListener{
            userValidation()
        }

        binding.tvAlreadySignup.setOnClickListener {
            val intentToSignInActivity = Intent(this, SignInActivity::class.java)
            startActivity(intentToSignInActivity)
        }
    }

    private fun userValidation() {
        val validator = Validator()
        val userName = binding.etUsername.text.toString()
        val userPassword = binding.etPassword.text.toString()
        val userEmail = binding.etEmail.text.toString().trim()

        if(userName.isEmpty() || userPassword.isEmpty() || userEmail.isEmpty()){
            Toast.makeText(this, "Enter Credentials!", Toast.LENGTH_SHORT).show()
        }
        when{
            !validator.validateEmail(userEmail) -> {
                binding.etEmail.error = "Invalid Email"
                binding.etEmail.requestFocus()
            }
            !validator.validatePassword(userPassword) -> {
                binding.etPassword.error = "At least - 8 chars and 1 [A-Z, 0-9, !@#..]"
                binding.etPassword.requestFocus()
            }
            else -> {
                progressBar.show()
                userSignUp(userEmail, userPassword, userName)
//                Toast.makeText(this, "Registering!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun userSignUp(email: String, password: String, userName: String) {
        val intentToSignInActivity = Intent(this, SignInActivity::class.java)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            progressBar.dismiss()
            if(it.isSuccessful){
                val user = Users(email, password, userName)
                val userId = it.result.user?.uid

                //Add to database
                if (userId != null) {
                    db.reference.child("Users").child(userId).setValue(user)
                    //Navigate to SignInActivity
                    startActivity(intentToSignInActivity)
                    finish()
                }else{
                    Log.d(TAG, "UserId not found")
                }
                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}