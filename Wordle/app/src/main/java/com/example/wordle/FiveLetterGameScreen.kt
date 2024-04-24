package com.example.wordle

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.contains
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.wordle.databinding.ActivityChannel1Binding
import com.example.wordle.databinding.ActivityFiveLetterGameScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.ktx.Firebase
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.util.Locale

class FiveLetterGameScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private  lateinit var  binding: ActivityFiveLetterGameScreenBinding
    private lateinit var timerf: CountDownTimer
    private lateinit var timers: CountDownTimer
    private lateinit var timerf2 :CountDownTimer
    private lateinit var timers2 :CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFiveLetterGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val database = FirebaseDatabase.getInstance().reference
        auth = Firebase.auth
        val odabul = database.child("Oyun Kanalı 1").child("Channel 1").child("rooms")
        val currentUser = auth.currentUser
        odabul.child("1").child("endflag1").setValue(false)
        odabul.child("1").child("endflag2").setValue(false)
        val wordlist = readBesharf( this, "besharf.txt")


        binding.wordtextinput.isVisible=false
        binding.wordButton.isVisible=false
        binding.timertextView.isVisible=false
        binding.timertextView2.isVisible=false
        binding.exitRoomInGameButton.isVisible = true
        binding.scoreTextview.isVisible=false

        var sayacf = false
        var sayacs = false
        var sayacs2 = false
        var sayacf2 = false
        var sayacdurf=false
        var sayacdurs =false

        var timerpuans = ""
        var timerpuanf = ""


        // oda 1 için özelleştirmek adına kullanıcı bu odada mı bilgisini al
        odabul.child("1").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val fplayer = snapshot.child("fplayerName").getValue(String::class.java)
                val splayer = snapshot.child("splayerName").getValue(String::class.java)
                val fplayerScore = snapshot.child("fplayerScore").getValue(Int::class.java).toString()
                val splayerScore = snapshot.child("splayerScore").getValue(Int::class.java).toString()
                if(snapshot.child("gameInfo").getValue(String::class.java)=="Joined")
                {
                    binding.textView3.isVisible=false
                    binding.wordtextinput.isVisible=true
                    binding.wordButton.isVisible=true
                    binding.exitRoomInGameButton.isVisible=false
                    if(fplayer == currentUser!!.email && sayacf==false)
                    {
                        binding.timertextView.isVisible=true
                        timerCountdownfp()
                        sayacf=true
                    }
                    else if(splayer==currentUser.email && sayacs==false)
                    {
                        binding.timertextView2.isVisible = true
                        timerCountdownsp()
                        sayacs=true
                    }
                    odabul.child("1").child("fplayerReady").setValue(false)
                    odabul.child("1").child("splayerReady").setValue(false)

                }
                else if(snapshot.child("gameInfo").getValue(String::class.java)=="Bitti")
                {

                    if(fplayer==currentUser!!.email && sayacdurf==false && sayacf2==true)
                    {
                        stopTimerf2()
                        sayacdurf=true
                    }
                    else if(splayer==currentUser.email && sayacdurs==false && sayacs2==true)
                    {
                        stopTimers2()
                        sayacdurs=true
                    }
                    binding.exitRoomInGameButton.isVisible=true
                    binding.scoreTextview.text = fplayer+ ":" + fplayerScore + "\n" + splayer+ ":" + splayerScore
                    for(i in 0 until binding.layout1.childCount)
                    {
                        binding.layout1.getChildAt(i).isEnabled=false
                    }
                    for(i in 0 until binding.layout2.childCount)
                    {
                        binding.layout2.getChildAt(i).isEnabled=false
                    }
                    for(i in 0 until binding.layout3.childCount)
                    {
                        binding.layout3.getChildAt(i).isEnabled=false
                    }
                    for(i in 0 until binding.layout4.childCount)
                    {
                        binding.layout4.getChildAt(i).isEnabled=false
                    }
                    for(i in 0 until binding.layout5.childCount)
                    {
                        binding.layout5.getChildAt(i).isEnabled=false
                    }
                    binding.scoreTextview.isVisible = true
                    binding.revengeButton.isVisible=true
                    binding.wordtextinput.isVisible=false
                    //veritabanını temizle
                    odabul.child("1").child("endflag1").setValue(false)
                    odabul.child("1").child("endflag2").setValue(false)
                    odabul.child("1").child("fplayerWord").setValue("empty")
                    odabul.child("1").child("splayerWord").setValue("empty")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        binding.wordButton.setOnClickListener {
        var playerWord = binding.playerWord.text.toString().uppercase(Locale("tr","TR"))
        if(checkField())
        {
            var roomid1 = odabul.child("1")
            var roomid2 = odabul.child("2")

            roomid1.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid && snapshot.child("gameInfo").getValue(String::class.java) == "Joined")
                    {
                         timerpuanf = binding.timertextView.text.toString()

                        stopTimerf()
                        roomid1.child("splayerWord").setValue(playerWord)
                        binding.wordtextinput.isEnabled=false
                        binding.textView3.isVisible=true

                    }
                    else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid && snapshot.child("gameInfo").getValue(String::class.java) == "Joined")
                    {
                         timerpuans = binding.timertextView2.text.toString()

                        stopTimers()
                        roomid1.child("fplayerWord").setValue(playerWord)
                        binding.wordtextinput.isEnabled=false
                        binding.textView3.isVisible=true

                    }

                    if(snapshot.child("gameInfo").getValue(String::class.java)=="Bitti")
                    {
                        playerWord = "empty"
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            roomid1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("fplayerWord").getValue(String::class.java)!="empty" && snapshot.child("splayerWord").getValue(String::class.java)!="empty")
                    {
                        binding.wordtextinput.isVisible=false
                        binding.wordButton.isVisible=false
                        binding.wLayout.isVisible=true
                        binding.textView3.isVisible=false
                        if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid && sayacf2==false)
                        {
                            timerCountdownfp2()
                            sayacf2=true
                        }
                        else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid && sayacs2==false)
                        {
                            timerCountdownsp2()
                            sayacs2=true
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }

        }
        binding.exitRoomInGameButton.setOnClickListener {
            var oda1 = odabul.child("1")
            var oda2 = odabul.child("2")
            if(currentUser!=null)
            {
                val playerID = currentUser.uid



                oda1.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("fplayerID").getValue(String::class.java)==playerID)
                        {
//
                            oda1.child("fplayerID").setValue("empty")
                            oda1.child("fplayerName").setValue("empty")
                            oda1.child("fplayerScore").setValue(0)
                            oda1.child("splayerScore").setValue(0)
                            oda1.child("gameInfo").setValue("Başlamadı")
                            oda1.child("gameSit").setValue("Boş")
                            oda1.child("fplayerReady").setValue(false)
                            oda1.child("splayerReady").setValue(false)
                            val intent = Intent(this@FiveLetterGameScreen, Channel1::class.java)
                            startActivity(intent)
                            finish()

                        }
                        else if(snapshot.child("splayerID").getValue(String::class.java)==playerID)
                        {
                            oda1.child("splayerID").setValue("empty")
                            oda1.child("splayerName").setValue("empty")
                            oda1.child("fplayerScore").setValue(0)
                            oda1.child("splayerScore").setValue(0)
                            oda1.child("gameInfo").setValue("Başlamadı")
                            oda1.child("gameSit").setValue("Boş")
                            val intent = Intent(this@FiveLetterGameScreen, Channel1::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                oda2.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("splayerID").getValue(String::class.java)==playerID)
                        {
//
                            oda2.child("splayerID").setValue("empty")
                            oda2.child("splayerName").setValue("empty")
                            oda2.child("fplayerScore").setValue(0)
                            oda2.child("splayerScore").setValue(0)
                            oda2.child("gameInfo").setValue("Başlamadı")
                            oda2.child("gameSit").setValue("Boş")
                            val intent = Intent(this@FiveLetterGameScreen, Channel1::class.java)
                            startActivity(intent)
                            finish()


                        }
                        else if(snapshot.child("fplayerID").getValue(String::class.java)==playerID)
                        {
//
                            oda2.child("fplayerID").setValue("empty")
                            oda2.child("fplayerName").setValue("empty")
                            oda2.child("fplayerScore").setValue(0)
                            oda2.child("splayerScore").setValue(0)
                            oda2.child("gameInfo").setValue("Başlamadı")
                            oda2.child("gameSit").setValue("Boş")
                            val intent = Intent(this@FiveLetterGameScreen, Channel1::class.java)
                            startActivity(intent)
                            finish()


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
        binding.revengeButton.setOnClickListener {
            var oda1 = odabul.child("1")
            var oda2 = odabul.child("2")

            if(currentUser!=null)
            {
                val playerID = currentUser.uid
                oda1.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("fplayerID").getValue(String::class.java)==playerID)
                        {
                            oda1.child("fplayerReady").setValue(true)
                        }
                        else if (snapshot.child("splayerID").getValue(String::class.java)==playerID)
                        {
                            oda1.child("splayerReady").setValue(true)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                oda1.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("fplayerReady").getValue(Boolean::class.java)==true && snapshot.child("splayerReady").getValue(Boolean::class.java)==true && snapshot.child("gameInfo").getValue(String::class.java)=="Bitti")
                        {
                            oda1.child("gameInfo").setValue("Joined")
                            val intent = Intent(this@FiveLetterGameScreen, FiveLetterGameScreen::class.java)
                            startActivity(intent)
                            finish()


                        }
                    }


                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            }
        }
        depo()
        binding.w5.addTextChangedListener {
            val oda1 = odabul.child("1")
            var  word = ""
            var puan = 0

            oda1.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid)
                    {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    }
                    else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                    {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }
                    wordBul(binding.w1,binding.w2,binding.w3,binding.w4,binding.w5,word,0)

                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w1)
                    wtexts.add(binding.w2)
                    wtexts.add(binding.w3)
                    wtexts.add(binding.w4)
                    wtexts.add(binding.w5)

                    for (i in wtexts.indices)
                    {
                        if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                        else if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#FFD700"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        binding.w10.addTextChangedListener {
            val oda1 = odabul.child("1")
            var  word = ""
            var puan = 0

            oda1.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid)
                    {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    }
                    else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                    {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }
                    wordBul(binding.w6,binding.w7,binding.w8,binding.w9,binding.w10,word,1)
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w6)
                    wtexts.add(binding.w7)
                    wtexts.add(binding.w8)
                    wtexts.add(binding.w9)
                    wtexts.add(binding.w10)

                    for (i in wtexts.indices)
                    {
                        if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                        else if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#FFD700"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        binding.w15.addTextChangedListener {
            val oda1 = odabul.child("1")
            var  word = ""
            var puan = 0
            oda1.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid)
                    {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    }
                    else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                    {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }

                    wordBul(binding.w11,binding.w12,binding.w13,binding.w14,binding.w15,word,2)
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w11)
                    wtexts.add(binding.w12)
                    wtexts.add(binding.w13)
                    wtexts.add(binding.w14)
                    wtexts.add(binding.w15)

                    for (i in wtexts.indices)
                    {
                        if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                        else if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#FFD700"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        binding.w20.addTextChangedListener {
            val oda1 = odabul.child("1")
            var  word = ""
            var puan=0
            oda1.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid)
                    {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    }
                    else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                    {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }

                    wordBul(binding.w16,binding.w17,binding.w18,binding.w19,binding.w20,word,3)
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w16)
                    wtexts.add(binding.w17)
                    wtexts.add(binding.w18)
                    wtexts.add(binding.w19)
                    wtexts.add(binding.w20)

                    for (i in wtexts.indices)
                    {
                        if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                        else if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#FFD700"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        var flag1=false
        var flag2=false
        binding.w25.addTextChangedListener {
            val oda1 = odabul.child("1")
            var  word = ""
            var puan=0
            val anlamlıKelime = StringBuilder()
            anlamlıKelime.append(binding.w21.text.toString().uppercase(Locale("tr","TR")))
            anlamlıKelime.append(binding.w22.text.toString().uppercase(Locale("tr","TR")))
            anlamlıKelime.append(binding.w23.text.toString().uppercase(Locale("tr","TR")))
            anlamlıKelime.append(binding.w24.text.toString().uppercase(Locale("tr","TR")))
            anlamlıKelime.append(binding.w25.text.toString().uppercase(Locale("tr","TR")))
            oda1.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid)
                    {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    }
                    else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                    {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan+timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }

                    wordBul(binding.w21,binding.w22,binding.w23,binding.w24,binding.w25,word,0)
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w21)
                    wtexts.add(binding.w22)
                    wtexts.add(binding.w23)
                    wtexts.add(binding.w24)
                    wtexts.add(binding.w25)

                    for (i in wtexts.indices)
                    {
                        if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                        else if((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#FFD700"))
                        {
                            if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("fplayerPuan").setValue(puan)
                            }
                            else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                            {
                                puan = puan+5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }
                    if(snapshot.child("gameInfo").getValue(String::class.java)!="Bitti" && wordlist.contains(anlamlıKelime.toString()))
                    {
                        if(snapshot.child("fplayerID").getValue(String::class.java)==currentUser.uid)
                        {
                            oda1.child("endflag1").setValue(true)
                        }
                        else if(snapshot.child("splayerID").getValue(String::class.java)==currentUser.uid)
                        {
                            oda1.child("endflag2").setValue(true)
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            oda1.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("gameInfo").getValue(String::class.java)!="Bitti" && snapshot.child("endflag1").getValue(Boolean::class.java)==true
                        && snapshot.child("endflag2").getValue(Boolean::class.java)==true)
                    {
                        val fpuan = snapshot.child("fplayerPuan").getValue(Int::class.java)
                        val spuan = snapshot.child("splayerPuan").getValue(Int::class.java)
                        if(fpuan!! > spuan!! && snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid)
                        {
                            var a = snapshot.child("fplayerScore").getValue(Double::class.java)
                            a = a!! + 0.5
                            oda1.child("fplayerScore").setValue(a)
                            oda1.child("gameInfo").setValue("Bitti")
                        }
                        else if(spuan > fpuan && snapshot.child("splayerID").getValue(String::class.java)==currentUser!!.uid)
                        {
                            var  a = snapshot.child("splayerScore").getValue(Double::class.java)
                            a = a!! + 0.5
                            oda1.child("splayerScore").setValue(a)
                            oda1.child("gameInfo").setValue("Bitti")
                        }
                        else if(spuan == fpuan && snapshot.child("fplayerID").getValue(String::class.java)==currentUser!!.uid)
                        {
                            var a1 = snapshot.child("fplayerScore").getValue(Double::class.java)
                            a1 = a1!! + 0.5
                            oda1.child("fplayerScore").setValue(a1)
                            var  a = snapshot.child("splayerScore").getValue(Double::class.java)
                            a = a!! + 0.5
                            oda1.child("splayerScore").setValue(a)
                            oda1.child("gameInfo").setValue("Bitti")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }



        // Game End
        fun rematchStart(){

        }

    }
    // oyun bitirme kısmı
    // oyun bitince veritabanı yenilenmesi
    // oda 2 için de düzenleme ve odaların çakışması

    private fun checkField():Boolean{
        val playerWord = binding.playerWord.text.toString().uppercase(Locale("tr","TR"))
        val wordlist = readBesharf( this, "besharf.txt")
        if(playerWord=="")
        {
            binding.wordtextinput.error="Bu alan boş bırakılamaz"
            return false
        }
        else if(playerWord.length!=5)
        {
            binding.wordtextinput.error="5 harfli bir kelime giriniz"
            return false
        }
        else if(wordlist.contains(playerWord))
        {
            return true
        }
        else if(!wordlist.contains(playerWord)) {
            binding.wordtextinput.error= "Anlamlı bir kelime giriniz"
            return false
        }


        return true
    }

    private fun wordjump(w1:EditText,w2:EditText)
    {
        w1.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s?.length==1)
                {
                    w2.requestFocus()
                }
            }

        })
    }

    private fun depo()
    {
        wordjump(binding.w1,binding.w2)
        wordjump(binding.w2,binding.w3)
        wordjump(binding.w3,binding.w4)
        wordjump(binding.w4,binding.w5)



        wordjump(binding.w6,binding.w7)
        wordjump(binding.w7,binding.w8)
        wordjump(binding.w8,binding.w9)
        wordjump(binding.w9,binding.w10)



        wordjump(binding.w11,binding.w12)
        wordjump(binding.w12,binding.w13)
        wordjump(binding.w13,binding.w14)
        wordjump(binding.w14,binding.w15)



        wordjump(binding.w16,binding.w17)
        wordjump(binding.w17,binding.w18)
        wordjump(binding.w18,binding.w19)
        wordjump(binding.w19,binding.w20)



        wordjump(binding.w21,binding.w22)
        wordjump(binding.w22,binding.w23)
        wordjump(binding.w23,binding.w24)
        wordjump(binding.w24,binding.w25)





    }
    private fun wordBul(w1: EditText,w2: EditText,w3:EditText,w4:EditText,w5:EditText,word:String, temp:Int)
    {
        val w1text = w1.text.toString().uppercase(Locale("tr","TR"))
        val w2text = w2.text.toString().uppercase(Locale("tr","TR"))
        val w3text = w3.text.toString().uppercase(Locale("tr","TR"))
        val w4text = w4.text.toString().uppercase(Locale("tr","TR"))
        val w5text = w5.text.toString().uppercase(Locale("tr","TR"))

        val anlamlıKelime = StringBuilder()
        anlamlıKelime.append(w1text)
        anlamlıKelime.append(w2text)
        anlamlıKelime.append(w3text)
        anlamlıKelime.append(w4text)
        anlamlıKelime.append(w5text)

        val wordlist = readBesharf( this, "besharf.txt")

        val mutableList = mutableListOf<String>()
        mutableList.add(w1text)
        mutableList.add(w2text)
        mutableList.add(w3text)
        mutableList.add(w4text)
        mutableList.add(w5text)

        val wtexts = mutableListOf<EditText>()
        wtexts.add(w1)
        wtexts.add(w2)
        wtexts.add(w3)
        wtexts.add(w4)
        wtexts.add(w5)

        val layouts = mutableListOf<LinearLayout>()
        layouts.add(binding.layout2)
        layouts.add(binding.layout3)
        layouts.add(binding.layout4)
        layouts.add(binding.layout5)



        val word1 = word.get(0).toString()
        val word2 = word.get(1).toString()
        val word3 = word.get(2).toString()
        val word4 = word.get(3).toString()
        val word5 = word.get(4).toString()





        if(wordlist.contains(anlamlıKelime.toString()))
        {

        for(i in mutableList.indices){
            if(mutableList[i] == word[i].toString())
            {
            wtexts[i].setBackgroundColor(Color.parseColor("#00FF00"))
            }
        }
        for(i in mutableList.indices){
            if (mutableList[i]!=word[i].toString() && word.contains(mutableList[i])  )
            {
                wtexts[i].setBackgroundColor(Color.parseColor("#FFD700"))
                for(j in mutableList.indices)
                {
                    if((wtexts[j].background as? ColorDrawable)?.color == Color.parseColor("#00FF00") && mutableList[i] == word[j].toString()   )
                    {
                        wtexts[i].setBackgroundColor(Color.parseColor("#FFFFFF"))

                    }

                }

            }
        }
        var a = false;
        for (i in mutableList.indices){
            for(j in mutableList.indices)
            {
                if((wtexts[j].background as? ColorDrawable)?.color == Color.parseColor("#FFD700") && mutableList[j] == word[i].toString()   )
                {
                        if(a==true)
                        {
                            wtexts[j].setBackgroundColor(Color.parseColor("#FFFFFF"))
                        }

                        a=true

                }


            }

            a=false
        }
            layouts[temp].isVisible=true
            wordjump(binding.w5,binding.w6)
            wordjump(binding.w10,binding.w11)
            wordjump(binding.w15,binding.w16)
            wordjump(binding.w20,binding.w21)

        // 6 kelimeliyi düzelt. Süre koy.
        }
        else{
            Toast.makeText(this,"Anlamlı bir kelime giriniz",Toast.LENGTH_SHORT).show()

        }

        fun oyunBitir(){
            var son = true
            val database = FirebaseDatabase.getInstance().reference

            val odabul = database.child("Oyun Kanalı 1").child("Channel 1").child("rooms")
            for(a in wtexts){
                val color = (a.background as? ColorDrawable)?.color

                if(color != Color.parseColor("#00FF00"))
                {
                    son = false
                    break
                }
            }


            if(son){
                odabul.child("1").child("gameInfo").setValue("Bitti")

                // single mı olmalı eventvalue mi
            odabul.child("1").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("fplayerID").getValue(String::class.java) == auth.currentUser?.uid)
                {
                    var a = snapshot.child("fplayerScore").getValue(Int::class.java)
                    a = a!! + 1
                    odabul.child("1").child("fplayerScore").setValue(a)


                }
                if(snapshot.child("splayerID").getValue(String::class.java) == auth.currentUser?.uid)
                {
                    var a = snapshot.child("splayerScore").getValue(Int::class.java)
                        a = a!! + 1
                        odabul.child("1").child("splayerScore").setValue(a)

                }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            }

        }
        oyunBitir()

    }
     private fun readBesharf(context:Context, file:String): List<String>{
         val assetManager = context.assets
         val input = assetManager.open(file)

        return input.bufferedReader().use { it.readLines() }
    }
    private fun timerCountdownfp(){
        timerf = object : CountDownTimer(60_000,1_000){
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished/1000
                binding.timertextView.text = second.toString()
            }

            override fun onFinish() {
                Toast.makeText(this@FiveLetterGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
                end()
            }

        }.start()
    }
    private fun timerCountdownsp(){
        timers = object : CountDownTimer(60_000,1_000){
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished/1000
                binding.timertextView2.text = second.toString()
            }

            override fun onFinish() {
                Toast.makeText(this@FiveLetterGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
                end()
            }

        }.start()
    }

    private fun timerCountdownfp2(){
        timerf2 = object : CountDownTimer(300_000,1_000){
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished/1000
                binding.timertextView.text = second.toString()
            }

            override fun onFinish() {
                Toast.makeText(this@FiveLetterGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
                end()
            }

        }.start()
    }
    private fun timerCountdownsp2(){
        timers2 = object : CountDownTimer(300_000,1_000){
            override fun onTick(millisUntilFinished: Long) {
                val second = millisUntilFinished/1000
                binding.timertextView2.text = second.toString()
            }

            override fun onFinish() {
                Toast.makeText(this@FiveLetterGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
                end()
            }

        }.start()
    }

    fun end()
    {
        val database = FirebaseDatabase.getInstance().reference
        auth = Firebase.auth
        val odabul = database.child("Oyun Kanalı 1").child("Channel 1").child("rooms")
        val currentUser = auth.currentUser

        odabul.child("1").child("gameInfo").setValue("Bitti")
        odabul.child("1").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("fplayerID")
                        .getValue(String::class.java) == auth.currentUser?.uid
                ) {
                    var a = snapshot.child("splayerScore").getValue(Int::class.java)
                    a = a!! + 1
                    odabul.child("1").child("splayerScore").setValue(a)

                }
                if (snapshot.child("splayerID")
                        .getValue(String::class.java) == auth.currentUser?.uid
                ) {
                    var a = snapshot.child("fplayerScore").getValue(Int::class.java)
                    a = a!! + 1
                    odabul.child("1").child("fplayerScore").setValue(a)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

//    override fun onStop() {
//        super.onStop()
//        end()
//    }

    private fun stopTimerf(){
        if(timerf.start() !=null)
        {
            timerf.cancel()
        }
        else {
            println("Başlamadı")
        }
    }
    private fun stopTimerf2(){
        if(timerf2.start() !=null)
        {
            timerf2.cancel()
        }
        else {
            println("Başlamadı")
        }
    }
    private fun stopTimers(){
        if(timers.start() !=null)
        {
            timers.cancel()
        }
        else {
            println("Başlamadı")
        }
    }
    private fun stopTimers2(){
        if(timers2.start() !=null)
        {
            timers2.cancel()
        }
        else {
            println("Başlamadı")
        }

    }


}


// iki oyuncu da kelimeyi yazmadan oyun başlamasın
