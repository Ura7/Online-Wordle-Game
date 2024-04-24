package com.example.wordle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.wordle.databinding.ActivityChannel2Binding
import com.example.wordle.databinding.ActivityChannel3Binding
import com.example.wordle.databinding.ActivityChannel4Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class Channel4 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChannel4Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChannel4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        val database = FirebaseDatabase.getInstance().reference
        val odabul = database.child("Oyun Kanalı 1").child("Channel 4").child("rooms")
        val currentUser = auth.currentUser
        var owner = ""
        var guest = ""
        var roomcheck = false

//          val id = 4
//      val channelName = "Channel 4"
//     val room1 = Room(1, "Room1", "empty", "empty","empty",0, "empty", "empty", "empty",0,"Boş", "Başlamadı")
//    val room2 = Room(2, "Room2", "empty", "empty", "empty",0,"empty", "empty", "empty",0,"Boş", "Başlamadı")
//
//     val rooms: MutableMap<String, Room> = mutableMapOf(
//        "1" to room1,
//       "2" to room2
//     )
//
//      database.child("Oyun Kanalı 1").child(channelName.toString()).setValue(Channel(id, rooms))


        binding.joinButton1.setOnClickListener {
            var roomid= odabul.child("1")

            if(currentUser!=null)
            {
                val playerID = currentUser.uid
                val playerName = currentUser.email
                roomid.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var fplayerid = snapshot.child("fplayerID").getValue(String::class.java)
                        var splayerid = snapshot.child("splayerID").getValue(String::class.java)
                        if(fplayerid=="empty")
                        {
                            roomid.child("fplayerID").setValue(playerID)
                            roomid.child("fplayerName").setValue(playerName)
                            owner = playerID
                            binding.joinButton1.isVisible = false
                            binding.joinButton2.isVisible = false
                            val intent = Intent(this@Channel4,SixLetterSabitGameScreen::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else if(fplayerid!="empty" && splayerid=="empty")
                        {
                            if(fplayerid!=playerID)
                            {
                                roomid.child("splayerID").setValue(playerID)
                                roomid.child("splayerName").setValue(playerName)
                                guest = playerID
                                roomid.child("gameSit").setValue("Dolu")
                                roomcheck == true
                                roomid.child("gameInfo").setValue("Joined")
                                val intent = Intent(this@Channel4,SixLetterSabitGameScreen::class.java)
                                startActivity(intent)
                                finish()
                                binding.joinButton1.isVisible = false
                                binding.joinButton2.isVisible = false
                            }
                            else
                            {
                                Toast.makeText(this@Channel4,"Zaten Odadasın!", Toast.LENGTH_LONG).show()
                            }

                        }
                        else if(fplayerid!="empty" && splayerid!="empty")
                        {
                            Toast.makeText(this@Channel4,"Oda Dolu", Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }


                })
            }

        }

        binding.joinButton2.setOnClickListener {
            var roomid= odabul.child("2")

            if(currentUser!=null)
            {
                val playerID = currentUser.uid
                val playerName = currentUser.email
                roomid.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var fplayerid = snapshot.child("fplayerID").getValue(String::class.java)
                        var splayerid = snapshot.child("splayerID").getValue(String::class.java)
                        if(fplayerid=="empty")
                        {
                            roomid.child("fplayerID").setValue(playerID)
                            roomid.child("fplayerName").setValue(playerName)
                            owner = playerID
                            binding.joinButton1.isVisible = false
                            binding.joinButton2.isVisible = false
                            binding.gameStartButton1.isVisible=true
                            val intent = Intent(this@Channel4,FiveLetterSabitGameScreen::class.java)
                            startActivity(intent)
                            finish()

                        }
                        else if(fplayerid!="empty" && splayerid=="empty")
                        {
                            if(fplayerid!=playerID)
                            {
                                roomid.child("splayerID").setValue(playerID)
                                roomid.child("splayerName").setValue(playerName)
                                guest = playerID
                                roomid.child("gameSit").setValue("Dolu")
                                roomcheck==true
                                roomid.child("gameInfo").setValue("Joined")
                                val intent = Intent(this@Channel4,FiveLetterSabitGameScreen::class.java)
                                startActivity(intent)
                                finish()
                                binding.joinButton1.isVisible = false
                                binding.joinButton2.isVisible = false
                            }
                            else
                            {
                                Toast.makeText(this@Channel4,"Zaten Odadasın!", Toast.LENGTH_LONG).show()
                            }

                        }
                        else if(fplayerid!="empty" && splayerid!="empty")
                        {
                            Toast.makeText(this@Channel4,"Oda Dolu", Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }


                })
            }
        }

        binding.exitroomButton.setOnClickListener {
            var oda1 = odabul.child("1")
            var oda2 = odabul.child("2")
            if(currentUser!=null)
            {
                val playerID = currentUser.uid
                val playerName = currentUser.email.toString()
                val intent = Intent(this@Channel4,GameLobby::class.java)
                startActivity(intent)
                finish()

                oda1.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("fplayerID").getValue(String::class.java)==playerID)
                        {
//                      oda1.child(playerID).removeValue()
//                      oda1.child(playerName).removeValue()
                            oda1.child("fplayerID").setValue("empty")
                            oda1.child("fplayerName").setValue("empty")
                            oda1.child("gameSit").setValue("Boş")
                            binding.joinButton1.isVisible=true
                            binding.joinButton2.isVisible=true
                        }
                        else if(snapshot.child("splayerID").getValue(String::class.java)==playerID)
                        {
                            oda1.child("splayerID").setValue("empty")
                            oda1.child("splayerName").setValue("empty")
                            oda1.child("gameSit").setValue("Boş")
                            binding.joinButton1.isVisible=true
                            binding.joinButton2.isVisible=true
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

                oda2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.child("splayerID").getValue(String::class.java)==playerID)
                        {
//                          oda2.child(playerID).removeValue()
//                          oda2.child(playerName).removeValue()
                            oda2.child("splayerID").setValue("empty")
                            oda2.child("splayerName").setValue("empty")
                            oda2.child("gameSit").setValue("Boş")
                            binding.joinButton1.isVisible=true
                            binding.joinButton2.isVisible=true
                        }
                        else if(snapshot.child("fplayerID").getValue(String::class.java)==playerID)
                        {
//                      oda1.child(playerID).removeValue()
//                      oda1.child(playerName).removeValue()
                            oda2.child("fplayerID").setValue("empty")
                            oda2.child("fplayerName").setValue("empty")
                            oda2.child("gameSit").setValue("Boş")
                            binding.joinButton1.isVisible=true
                            binding.joinButton2.isVisible=true
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }


    }
}