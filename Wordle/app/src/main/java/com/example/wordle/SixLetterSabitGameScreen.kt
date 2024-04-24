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
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.wordle.databinding.ActivityChannel2Binding
import com.example.wordle.databinding.ActivitySixLetterGameScreenBinding
import com.example.wordle.databinding.ActivitySixLetterSabitGameScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.lang.StringBuilder
import java.util.Locale
import kotlin.random.Random

class SixLetterSabitGameScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySixLetterSabitGameScreenBinding
    private lateinit var timerf: CountDownTimer
    private lateinit var timers: CountDownTimer
    private lateinit var timerf2: CountDownTimer
    private lateinit var timers2: CountDownTimer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySixLetterSabitGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val database = FirebaseDatabase.getInstance().reference
        auth = Firebase.auth
        val odabul = database.child("Oyun Kanalı 1").child("Channel 4").child("rooms")
        val currentUser = auth.currentUser
        odabul.child("1").child("endflag1").setValue(false)
        odabul.child("1").child("endflag2").setValue(false)
        val wordlist = readAltiharf(this, "altiharf.txt")

        binding.wordLayout.isVisible = false
        binding.wordButton.isVisible = false
        binding.timertextView.isVisible = false
        binding.timertextView2.isVisible = false
        binding.exitRoomInGameButton.isVisible = true
        binding.scoreTextview.isVisible = false

        var sayacf = false
        var sayacs = false
        var sayacs2 = false
        var sayacf2 = false
        var sayacdurf = false
        var sayacdurs = false

        var timerpuans = ""
        var timerpuanf = ""

        odabul.child("1").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fplayer = snapshot.child("fplayerName").getValue(String::class.java)
                val splayer = snapshot.child("splayerName").getValue(String::class.java)
                val fplayerScore =
                    snapshot.child("fplayerScore").getValue(Int::class.java).toString()
                val splayerScore =
                    snapshot.child("splayerScore").getValue(Int::class.java).toString()
                if (snapshot.child("gameInfo").getValue(String::class.java) == "Joined") {
                    binding.textView3.isVisible = false
                    binding.wordLayout.isVisible = true
                    binding.wordButton.isVisible = true
                    binding.exitRoomInGameButton.isVisible = false
                    if (fplayer == currentUser!!.email && sayacf == false) {
                        binding.timertextView.isVisible = true
                        timerCountdownfp()
                        sayacf = true
                    } else if (splayer == currentUser.email && sayacs == false) {
                        binding.timertextView2.isVisible = true
                        timerCountdownsp()
                        sayacs = true
                    }
                    odabul.child("1").child("fplayerReady").setValue(false)
                    odabul.child("1").child("splayerReady").setValue(false)
                } else if (snapshot.child("gameInfo").getValue(String::class.java) == "Bitti") {
                    if (fplayer == currentUser!!.email && sayacdurf == false && sayacf2 == true) {
                        stopTimerf2()
                        sayacdurf = true
                    } else if (splayer == currentUser.email && sayacdurs == false && sayacs2 == true) {
                        stopTimers2()
                        sayacdurs = true
                    }
                    binding.exitRoomInGameButton.isVisible = true
                    binding.scoreTextview.text =
                        fplayer + ":" + fplayerScore + "\n" + splayer + ":" + splayerScore
                    for (i in 0 until binding.layout1.childCount) {
                        binding.layout1.getChildAt(i).isEnabled = false
                    }
                    for (i in 0 until binding.layout2.childCount) {
                        binding.layout2.getChildAt(i).isEnabled = false
                    }
                    for (i in 0 until binding.layout3.childCount) {
                        binding.layout3.getChildAt(i).isEnabled = false
                    }
                    for (i in 0 until binding.layout4.childCount) {
                        binding.layout4.getChildAt(i).isEnabled = false
                    }
                    for (i in 0 until binding.layout5.childCount) {
                        binding.layout5.getChildAt(i).isEnabled = false
                    }
                    for (i in 0 until binding.layout6.childCount) {
                        binding.layout6.getChildAt(i).isEnabled = false
                    }
                    binding.scoreTextview.isVisible = true
                    binding.revengeButton.isVisible = true
                    binding.wordLayout.isVisible = false
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
            var playerWord = binding.word1.text.toString()
                .uppercase(Locale("tr", "TR")) + binding.word2.text.toString()
                .uppercase(Locale("tr", "TR")) +
                    binding.word3.text.toString()
                        .uppercase(Locale("tr", "TR")) + binding.word4.text.toString()
                .uppercase(Locale("tr", "TR")) + binding.word5.text.toString()
                .uppercase(Locale("tr", "TR")) +
                    binding.word6.text.toString().uppercase(Locale("tr", "TR"))
            if (checkField()) {
                var roomid1 = odabul.child("1")
                var roomid2 = odabul.child("2")

                roomid1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("fplayerID")
                                .getValue(String::class.java) == currentUser!!.uid && snapshot.child(
                                "gameInfo"
                            ).getValue(String::class.java) == "Joined"
                        ) {
                            timerpuanf = binding.timertextView.text.toString()
                            roomid1.child("splayerWord").setValue(playerWord)
                            binding.word1.isEnabled = false
                            binding.word2.isEnabled = false
                            binding.word3.isEnabled = false
                            binding.word4.isEnabled = false
                            binding.word5.isEnabled = false
                            binding.word6.isEnabled = false
                            binding.textView3.isVisible = true
                            stopTimerf()
                        } else if (snapshot.child("splayerID")
                                .getValue(String::class.java) == currentUser.uid && snapshot.child("gameInfo")
                                .getValue(String::class.java) == "Joined"
                        ) {
                            timerpuans = binding.timertextView2.text.toString()
                            roomid1.child("fplayerWord").setValue(playerWord)
                            binding.word1.isEnabled = false
                            binding.word2.isEnabled = false
                            binding.word3.isEnabled = false
                            binding.word4.isEnabled = false
                            binding.word5.isEnabled = false
                            binding.word6.isEnabled = false
                            binding.textView3.isVisible = true
                            stopTimers()
                        }
                        if (snapshot.child("gameInfo").getValue(String::class.java) == "Bitti") {
                            playerWord = "empty"
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                roomid1.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("fplayerWord")
                                .getValue(String::class.java) != "empty" && snapshot.child("splayerWord")
                                .getValue(String::class.java) != "empty"
                        ) {
                            binding.wordLayout.isVisible = false
                            binding.wordButton.isVisible = false
                            binding.wLayout.isVisible = true
                            binding.textView3.isVisible = false
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser!!.uid && sayacf2 == false
                            ) {
                                timerCountdownfp2()
                                sayacf2 = true
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid && sayacs2 == false
                            ) {
                                timerCountdownsp2()
                                sayacs2 = true
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
            if (currentUser != null) {
                val playerID = currentUser.uid



                oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("fplayerID").getValue(String::class.java) == playerID) {
//
                            oda1.child("fplayerID").setValue("empty")
                            oda1.child("fplayerName").setValue("empty")
                            oda1.child("fplayerScore").setValue(0)
                            oda1.child("splayerScore").setValue(0)
                            oda1.child("gameInfo").setValue("Başlamadı")
                            oda1.child("gameSit").setValue("Boş")
                            oda1.child("fplayerReady").setValue(false)
                            oda1.child("splayerReady").setValue(false)
                            val intent = Intent(this@SixLetterSabitGameScreen, Channel4::class.java)
                            startActivity(intent)
                            finish()

                        } else if (snapshot.child("splayerID")
                                .getValue(String::class.java) == playerID
                        ) {
                            oda1.child("splayerID").setValue("empty")
                            oda1.child("splayerName").setValue("empty")
                            oda1.child("fplayerScore").setValue(0)
                            oda1.child("splayerScore").setValue(0)
                            oda1.child("gameInfo").setValue("Başlamadı")
                            oda1.child("gameSit").setValue("Boş")
                            val intent = Intent(this@SixLetterSabitGameScreen, Channel4::class.java)
                            startActivity(intent)
                            finish()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                oda2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("splayerID").getValue(String::class.java) == playerID) {
//
                            oda2.child("splayerID").setValue("empty")
                            oda2.child("splayerName").setValue("empty")
                            oda2.child("fplayerScore").setValue(0)
                            oda2.child("splayerScore").setValue(0)
                            oda2.child("gameInfo").setValue("Başlamadı")
                            oda2.child("gameSit").setValue("Boş")
                            val intent = Intent(this@SixLetterSabitGameScreen, Channel4::class.java)
                            startActivity(intent)
                            finish()


                        } else if (snapshot.child("fplayerID")
                                .getValue(String::class.java) == playerID
                        ) {
//
                            oda2.child("fplayerID").setValue("empty")
                            oda2.child("fplayerName").setValue("empty")
                            oda2.child("fplayerScore").setValue(0)
                            oda2.child("splayerScore").setValue(0)
                            oda2.child("gameInfo").setValue("Başlamadı")
                            oda2.child("gameSit").setValue("Boş")
                            val intent = Intent(this@SixLetterSabitGameScreen, Channel4::class.java)
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

            if (currentUser != null) {
                val playerID = currentUser.uid
                oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("fplayerID").getValue(String::class.java) == playerID) {
                            oda1.child("fplayerReady").setValue(true)
                        } else if (snapshot.child("splayerID")
                                .getValue(String::class.java) == playerID
                        ) {
                            oda1.child("splayerReady").setValue(true)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                oda1.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("fplayerReady")
                                .getValue(Boolean::class.java) == true && snapshot.child("splayerReady")
                                .getValue(Boolean::class.java) == true && snapshot.child("gameInfo")
                                .getValue(String::class.java) == "Bitti"
                        ) {
                            oda1.child("gameInfo").setValue("Joined")
                            val intent = Intent(
                                this@SixLetterSabitGameScreen,
                                SixLetterSabitGameScreen::class.java
                            )
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
        binding.w6.addTextChangedListener {
            val oda1 = odabul.child("1")
            var word = ""
            var puan = 0
            oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("fplayerID")
                            .getValue(String::class.java) == currentUser!!.uid
                    ) {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    } else if (snapshot.child("splayerID")
                            .getValue(String::class.java) == currentUser.uid
                    ) {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }
                    wordBul(
                        binding.w1,
                        binding.w2,
                        binding.w3,
                        binding.w4,
                        binding.w5,
                        binding.w6,
                        word,
                        0
                    )

                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w1)
                    wtexts.add(binding.w2)
                    wtexts.add(binding.w3)
                    wtexts.add(binding.w4)
                    wtexts.add(binding.w5)
                    wtexts.add(binding.w6)

                    for (i in wtexts.indices) {
                        if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00")) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        } else if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor(
                                "#FFD700"
                            )
                        ) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        binding.w12.addTextChangedListener {
            val oda1 = odabul.child("1")
            var word = ""
            var puan = 0
            oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("fplayerID")
                            .getValue(String::class.java) == currentUser!!.uid
                    ) {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    } else if (snapshot.child("splayerID")
                            .getValue(String::class.java) == currentUser.uid
                    ) {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }
                    wordBul(
                        binding.w7,
                        binding.w8,
                        binding.w9,
                        binding.w10,
                        binding.w11,
                        binding.w12,
                        word,
                        1
                    )
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w7)
                    wtexts.add(binding.w8)
                    wtexts.add(binding.w9)
                    wtexts.add(binding.w10)
                    wtexts.add(binding.w11)
                    wtexts.add(binding.w12)

                    for (i in wtexts.indices) {
                        if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00")) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        } else if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor(
                                "#FFD700"
                            )
                        ) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        binding.w18.addTextChangedListener {
            val oda1 = odabul.child("1")
            var word = ""
            var puan = 0
            oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("fplayerID")
                            .getValue(String::class.java) == currentUser!!.uid
                    ) {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    } else if (snapshot.child("splayerID")
                            .getValue(String::class.java) == currentUser.uid
                    ) {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }


                    wordBul(
                        binding.w13,
                        binding.w14,
                        binding.w15,
                        binding.w16,
                        binding.w17,
                        binding.w18,
                        word,
                        2
                    )
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w13)
                    wtexts.add(binding.w14)
                    wtexts.add(binding.w15)
                    wtexts.add(binding.w16)
                    wtexts.add(binding.w17)
                    wtexts.add(binding.w18)

                    for (i in wtexts.indices) {
                        if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00")) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        } else if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor(
                                "#FFD700"
                            )
                        ) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        binding.w24.addTextChangedListener {
            val oda1 = odabul.child("1")
            var word = ""
            var puan = 0
            oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("fplayerID")
                            .getValue(String::class.java) == currentUser!!.uid
                    ) {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    } else if (snapshot.child("splayerID")
                            .getValue(String::class.java) == currentUser.uid
                    ) {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }

                    wordBul(
                        binding.w19,
                        binding.w20,
                        binding.w21,
                        binding.w22,
                        binding.w23,
                        binding.w24,
                        word,
                        3
                    )
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w19)
                    wtexts.add(binding.w20)
                    wtexts.add(binding.w21)
                    wtexts.add(binding.w22)
                    wtexts.add(binding.w23)
                    wtexts.add(binding.w24)

                    for (i in wtexts.indices) {
                        if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00")) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        } else if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor(
                                "#FFD700"
                            )
                        ) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }
        binding.w30.addTextChangedListener {
            val oda1 = odabul.child("1")
            var word = ""
            var puan = 0
            oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("fplayerID")
                            .getValue(String::class.java) == currentUser!!.uid
                    ) {
                        word = snapshot.child("fplayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    } else if (snapshot.child("splayerID")
                            .getValue(String::class.java) == currentUser.uid
                    ) {
                        word = snapshot.child("splayerWord").getValue(String::class.java).toString()
                        puan = puan + timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }

                    wordBul(
                        binding.w25,
                        binding.w26,
                        binding.w27,
                        binding.w28,
                        binding.w29,
                        binding.w30,
                        word,
                        4
                    )
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w25)
                    wtexts.add(binding.w26)
                    wtexts.add(binding.w27)
                    wtexts.add(binding.w28)
                    wtexts.add(binding.w29)
                    wtexts.add(binding.w30)

                    for (i in wtexts.indices) {
                        if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor("#00FF00")) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        } else if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor(
                                "#FFD700"
                            )
                        ) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
        binding.w36.addTextChangedListener {
            val oda1 = odabul.child("1")
            var word = ""
            var puan = 0
            val anlamlıKelime = StringBuilder()
            anlamlıKelime.append(binding.w31.text.toString().uppercase(Locale("tr", "TR")))
            anlamlıKelime.append(binding.w32.text.toString().uppercase(Locale("tr", "TR")))
            anlamlıKelime.append(binding.w33.text.toString().uppercase(Locale("tr", "TR")))
            anlamlıKelime.append(binding.w34.text.toString().uppercase(Locale("tr", "TR")))
            anlamlıKelime.append(binding.w35.text.toString().uppercase(Locale("tr", "TR")))
            anlamlıKelime.append(binding.w36.text.toString().uppercase(Locale("tr", "TR")))
            oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("fplayerID")
                            .getValue(String::class.java) == currentUser!!.uid
                    ) {
                        word = snapshot.child("fplayerWord").getValue(String::class.java)
                            .toString()
                        puan = puan + timerpuanf.toInt()
                        oda1.child("fplayerPuan").setValue(puan)
                    } else if (snapshot.child("splayerID")
                            .getValue(String::class.java) == currentUser.uid
                    ) {
                        word = snapshot.child("splayerWord").getValue(String::class.java)
                            .toString()
                        puan = puan + timerpuans.toInt()
                        oda1.child("splayerPuan").setValue(puan)
                    }

                    wordBul(
                        binding.w31,
                        binding.w32,
                        binding.w33,
                        binding.w34,
                        binding.w35,
                        binding.w36,
                        word,
                        0
                    )
                    val wtexts = mutableListOf<EditText>()
                    wtexts.add(binding.w31)
                    wtexts.add(binding.w32)
                    wtexts.add(binding.w33)
                    wtexts.add(binding.w34)
                    wtexts.add(binding.w35)
                    wtexts.add(binding.w36)
                    for (i in wtexts.indices) {
                        if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor(
                                "#00FF00"
                            )
                        ) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 10
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        } else if ((wtexts[i].background as? ColorDrawable)?.color == Color.parseColor(
                                "#FFD700"
                            )
                        ) {
                            if (snapshot.child("fplayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("fplayerPuan").setValue(puan)
                            } else if (snapshot.child("splayerID")
                                    .getValue(String::class.java) == currentUser.uid
                            ) {
                                puan = puan + 5
                                oda1.child("splayerPuan").setValue(puan)
                            }
                        }
                    }
                    if (snapshot.child("gameInfo")
                            .getValue(String::class.java) != "Bitti" && wordlist.contains(
                            anlamlıKelime.toString()
                        )
                    ) {
                        if (snapshot.child("fplayerID")
                                .getValue(String::class.java) == currentUser.uid
                        ) {
                            oda1.child("endflag1").setValue(true)
                        } else if (snapshot.child("splayerID")
                                .getValue(String::class.java) == currentUser.uid
                        ) {
                            oda1.child("endflag2").setValue(true)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            oda1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("gameInfo")
                            .getValue(String::class.java) != "Bitti" && snapshot.child("endflag1")
                            .getValue(Boolean::class.java) == true
                        && snapshot.child("endflag2").getValue(Boolean::class.java) == true
                    ) {
                        val fpuan = snapshot.child("fplayerPuan").getValue(Int::class.java)
                        val spuan = snapshot.child("splayerPuan").getValue(Int::class.java)
                        if (fpuan!! > spuan!! && snapshot.child("fplayerID")
                                .getValue(String::class.java) == currentUser!!.uid
                        ) {
                            var a = snapshot.child("fplayerScore").getValue(Double::class.java)
                            a = a!! + 0.5
                            oda1.child("fplayerScore").setValue(a)
                            oda1.child("gameInfo").setValue("Bitti")
                        } else if (spuan > fpuan && snapshot.child("splayerID")
                                .getValue(String::class.java) == currentUser!!.uid
                        ) {
                            var a = snapshot.child("splayerScore").getValue(Double::class.java)
                            a = a!! + 0.5
                            oda1.child("splayerScore").setValue(a)
                            oda1.child("gameInfo").setValue("Bitti")
                        } else if (spuan == fpuan && snapshot.child("fplayerID")
                                .getValue(String::class.java) == currentUser!!.uid
                        ) {
                            var a1 = snapshot.child("fplayerScore").getValue(Double::class.java)
                            a1 = a1!! + 0.5
                            oda1.child("fplayerScore").setValue(a1)
                            var a = snapshot.child("splayerScore").getValue(Double::class.java)
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



    fun sabitUret() {
        val wordtexts = mutableListOf<EditText>()
        wordtexts.add(binding.word1)
        wordtexts.add(binding.word2)
        wordtexts.add(binding.word3)
        wordtexts.add(binding.word4)
        wordtexts.add(binding.word5)
        wordtexts.add(binding.word6)

        var randomWT = wordtexts[Random.nextInt(wordtexts.size)]

        val sabitWords = listOf("a", "e", "i", "o", "l", "m", "n", "t", "r")

        var randomSW = sabitWords[Random.nextInt(sabitWords.size)]


        randomWT.setText(randomSW)
        randomWT.isEnabled = false

        wordjump(binding.word1, binding.word2)
        wordjump(binding.word2, binding.word3)
        wordjump(binding.word3, binding.word4)
        wordjump(binding.word4, binding.word5)
        wordjump(binding.word5, binding.word6)
    }
    sabitUret()
    }


    // oyun bitirme kısmı
    // oyun bitince veritabanı yenilenmesi
    // oda 2 için de düzenleme ve odaların çakışması

    private fun checkField():Boolean{
        var playerWord = binding.word1.text.toString().uppercase(Locale("tr","TR")) + binding.word2.text.toString().uppercase(Locale("tr","TR")) +
                binding.word3.text.toString().uppercase(Locale("tr","TR")) + binding.word4.text.toString().uppercase(Locale("tr","TR")) +binding.word5.text.toString().uppercase(Locale("tr","TR")) +
                binding.word6.text.toString().uppercase(Locale("tr","TR"))
        val wordlist = readAltiharf( this, "altiharf.txt")
        if(playerWord=="")
        {
            Toast.makeText(this, "Bu Alan Boş Bırakılamaz", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(playerWord.length!=6)
        {
            Toast.makeText(this, "6 Harfli Bir Kelime Giriniz", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(wordlist.contains(playerWord))
        {
            return true
        }
        else if(!wordlist.contains(playerWord)) {
            Toast.makeText(this, "Anlamlı Bir Kelime Giriniz", Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }

    private fun wordjump(w1: EditText, w2: EditText)
    {
        w1.addTextChangedListener(object : TextWatcher {
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
        wordjump(binding.w5,binding.w6)



        wordjump(binding.w7,binding.w8)
        wordjump(binding.w8,binding.w9)
        wordjump(binding.w9,binding.w10)
        wordjump(binding.w10,binding.w11)
        wordjump(binding.w11,binding.w12)



        wordjump(binding.w13,binding.w14)
        wordjump(binding.w14,binding.w15)
        wordjump(binding.w15,binding.w16)
        wordjump(binding.w16,binding.w17)
        wordjump(binding.w17,binding.w18)



        wordjump(binding.w19,binding.w20)
        wordjump(binding.w20,binding.w21)
        wordjump(binding.w21,binding.w22)
        wordjump(binding.w22,binding.w23)
        wordjump(binding.w23,binding.w24)



        wordjump(binding.w25,binding.w26)
        wordjump(binding.w26,binding.w27)
        wordjump(binding.w27,binding.w28)
        wordjump(binding.w28,binding.w29)
        wordjump(binding.w29,binding.w30)



        wordjump(binding.w31,binding.w32)
        wordjump(binding.w32,binding.w33)
        wordjump(binding.w33,binding.w34)
        wordjump(binding.w34,binding.w35)
        wordjump(binding.w35,binding.w36)




    }
    private fun wordBul(w1: EditText, w2: EditText, w3: EditText, w4: EditText, w5: EditText,w6:EditText, word:String, temp:Int)
    {
        val w1text = w1.text.toString().uppercase(Locale("tr","TR"))
        val w2text = w2.text.toString().uppercase(Locale("tr","TR"))
        val w3text = w3.text.toString().uppercase(Locale("tr","TR"))
        val w4text = w4.text.toString().uppercase(Locale("tr","TR"))
        val w5text = w5.text.toString().uppercase(Locale("tr","TR"))
        val w6Text = w6.text.toString().uppercase(Locale("tr","TR"))

        val anlamlıKelime = StringBuilder()
        anlamlıKelime.append(w1text)
        anlamlıKelime.append(w2text)
        anlamlıKelime.append(w3text)
        anlamlıKelime.append(w4text)
        anlamlıKelime.append(w5text)
        anlamlıKelime.append(w6Text)

        val wordlist = readAltiharf( this, "altiharf.txt")

        val word1 = word.get(0).toString()
        val word2 = word.get(1).toString()
        val word3 = word.get(2).toString()
        val word4 = word.get(3).toString()
        val word5 = word.get(4).toString()

        val mutableList = mutableListOf<String>()
        mutableList.add(w1text)
        mutableList.add(w2text)
        mutableList.add(w3text)
        mutableList.add(w4text)
        mutableList.add(w5text)
        mutableList.add(w6Text)

        val wtexts = mutableListOf<EditText>()
        wtexts.add(w1)
        wtexts.add(w2)
        wtexts.add(w3)
        wtexts.add(w4)
        wtexts.add(w5)
        wtexts.add(w6)

        val layouts = mutableListOf<LinearLayout>()
        layouts.add(binding.layout2)
        layouts.add(binding.layout3)
        layouts.add(binding.layout4)
        layouts.add(binding.layout5)
        layouts.add(binding.layout6)

        var fplayerP = 0
        var splayerP = 0

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
            wordjump(binding.w6,binding.w7)
            wordjump(binding.w12,binding.w13)
            wordjump(binding.w18,binding.w19)
            wordjump(binding.w24,binding.w25)
            wordjump(binding.w30,binding.w31)


        }
        else {
            Toast.makeText(this,"Anlamlı Bir Kelime Giriniz",Toast.LENGTH_SHORT).show()
        }
        fun oyunBitir(){
            var son = true
            val database = FirebaseDatabase.getInstance().reference

            val odabul = database.child("Oyun Kanalı 1").child("Channel 4").child("rooms")
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
    private fun readAltiharf(context: Context, file:String): List<String>{
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
                Toast.makeText(this@SixLetterSabitGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@SixLetterSabitGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@SixLetterSabitGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@SixLetterSabitGameScreen, "Süre bitti", Toast.LENGTH_SHORT).show()
                end()
            }

        }.start()
    }

    fun end()
    {
        val database = FirebaseDatabase.getInstance().reference
        auth = Firebase.auth
        val odabul = database.child("Oyun Kanalı 1").child("Channel 4").child("rooms")
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
// sunucu 2 ye de girince oyun başlıyor direkt. Boş olmasına rağmen
// kişi kelime tahmin ederken hep büyük yazsın