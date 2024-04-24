package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordle.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()
        auth = Firebase.auth
        binding.signinButton.setOnClickListener{
        val mail = binding.Email.text.toString()
        val password = binding.Password.text.toString()
            //email yerine kullanıcı adı yapılabilir

        if(checkField())
        {
            auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener{
                if(it.isSuccessful)
                {
                    Toast.makeText(this,"Giriş Yapıldı",Toast.LENGTH_SHORT).show()
                    //oyun lobisine git
                    val intent = Intent(this, GameLobby::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this,"E-posta veya şifre hatalı",Toast.LENGTH_SHORT).show()
                    Log.e("error:", it.exception.toString())
                }
            }
        }

        }
        binding.signuprootButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun checkField():Boolean{
        val mail = binding.Email.text.toString()
        if(binding.Email.text.toString()=="")
        {
            binding.textInputLayoutEmail.error="Bu alan zorunludur!"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches())
        {
            binding.textInputLayoutEmail.error="Yanlış formatta bir mail girdiniz."
            return false
        }
        if (binding.Password.text.toString()=="")
        {
            binding.textInputLayoutPassword.error="Bu alan boş bırakılamaz."
        }


        return true
    }






}