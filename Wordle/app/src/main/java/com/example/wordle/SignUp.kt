package com.example.wordle

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordle.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class SignUp : AppCompatActivity() {
    private  lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding



    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.signupButton.setOnClickListener{
        val mail = binding.Email.text.toString()
        val password= binding.Password.text.toString()
            if(checkField()){
                auth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        auth.signOut()
                        Toast.makeText(this, "Kayıt Oluşturuldu", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SignIn::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Log.e("Hata",it.exception.toString())
                    }

                }
            }
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

