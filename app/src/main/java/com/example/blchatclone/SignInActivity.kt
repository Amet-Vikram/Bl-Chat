package com.example.blchatclone

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.blchatclone.databinding.ActivitySignInBinding
import com.example.blchatclone.databinding.ActivitySignupBinding
import com.example.blchatclone.models.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

private const val TAG = "SignInActivity"
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var progressBar: ProgressDialog
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        progressBar = ProgressDialog(this)
        progressBar.setTitle("Logging in")
        progressBar.setMessage("Validating your account")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogin.setOnClickListener {
            userSignIn()
        }

        if(auth.currentUser != null){
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity)
            finish()
        }

        binding.tvSignup.setOnClickListener {
            val intentToSignupActivity = Intent(this, SignupActivity::class.java)
            startActivity(intentToSignupActivity)
        }

        binding.btnGoogleSignIn.setOnClickListener {
            googleSignIn()
        }
    }

    private fun userSignIn() {
        val userMail = binding.etEmailLogin.text.toString()
        val userPassword = binding.etPasswordLogin.text.toString()
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        if(userMail.isEmpty() || userPassword.isEmpty()){
            Toast.makeText(this, "Enter credentials first.", Toast.LENGTH_SHORT).show()
        }else{
            progressBar.show()
            auth.signInWithEmailAndPassword(userMail, userPassword).addOnCompleteListener {
                progressBar.dismiss()
                if(it.isSuccessful){
                    startActivity(intentToMainActivity)
                    finish()
                }else{
                    Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun googleSignIn(){
        val intentToGoogleClient = googleSignInClient.signInIntent
        startActivityForResult(intentToGoogleClient, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult((ApiException::class.java))
                Log.d(TAG, "Firebase Auth With Google: ${account.id}")
                fireBaseAuthWithGoogle(account.idToken)
            }catch (e: ApiException){
                Log.w(TAG, "Google Sign-in Failed $e")
            }
        }
    }

    private fun fireBaseAuthWithGoogle(idToken: String?) {
        val credentials = GoogleAuthProvider.getCredential(idToken, null)
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        auth.signInWithCredential(credentials).addOnCompleteListener {
            if(it.isSuccessful){
                val firebaseUser = auth.currentUser
                val user = Users()
                //Store the user details in the firebase database
                if (firebaseUser != null) {
                    user.userId = firebaseUser.uid
                    user.userName = firebaseUser.displayName.toString()
                    user.profilePic = firebaseUser.photoUrl.toString()

                    db.reference.child("Users").child(firebaseUser.uid).setValue(user)
                }
                //Navigate to Main Activity
                startActivity(intentToMainActivity)
                finish()
                Log.d(TAG, "signInWithCredential:success")
            }else{
                Log.w(TAG, "signInWithCredential:failed", it.exception)
            }
        }
    }
}