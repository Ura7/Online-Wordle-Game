package com.example.wordle

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordle.databinding.ActivityGameLobbyBinding
import com.example.wordle.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class GameLobby : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityGameLobbyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        binding.signoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,SignIn::class.java)
            startActivity(intent)
            finish()
        }

        binding.ch1Button.setOnClickListener {
            val intent = Intent(this, Channel1::class.java)
            startActivity(intent)
            finish()
        }
        binding.ch2Button.setOnClickListener {
            val intent = Intent(this,Channel2::class.java)
            startActivity(intent)
            finish()
        }
        binding.ch3Button.setOnClickListener {
            val intent = Intent(this,Channel3::class.java)
            startActivity(intent)
            finish()
        }
        binding.ch4Button.setOnClickListener {
            val intent = Intent(this,Channel4::class.java)
            startActivity(intent)
            finish()
        }

    }
}