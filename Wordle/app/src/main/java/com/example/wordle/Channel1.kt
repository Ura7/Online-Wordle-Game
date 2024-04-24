package com.example.wordle

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.renderscript.Sampler.Value
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.wordle.databinding.ActivityChannel1Binding
import com.example.wordle.databinding.ActivityGameLobbyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.snapshots
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class Channel1 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChannel1Binding


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChannel1Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        val database = FirebaseDatabase.getInstance().reference
        val odabul = database.child("Oyun Kanalı 1").child("Channel 1").child("rooms")
        val currentUser = auth.currentUser
        var owner = ""
        var guest = ""
        var roomcheck = false
//        val id = 1
//       val channelName = "Channel 1"
//       val room1 = Room(1, "Room1", "empty", "empty","empty", 0,"empty", "empty", "empty",0,"Boş", "Başlamadı")
//      val room2 = Room(2, "Room2", "empty", "empty", "empty",0,"empty", "empty", "empty",0,"Boş", "Başlamadı")
//
//     val rooms: MutableMap<String, Room> = mutableMapOf(
//         "1" to room1,
//         "2" to room2
//      )
//
//      database.child("Oyun Kanalı 1").child(channelName.toString()).setValue(Channel(id, rooms))









        binding.joinButton1.setOnClickListener {

        var roomid= odabul.child("1")

            if(currentUser!=null)
            {
                val playerID = currentUser.uid
                val playerName = currentUser.email
                roomid.addListenerForSingleValueEvent(object :ValueEventListener{
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
                            val intent = Intent(this@Channel1,FiveLetterGameScreen::class.java)
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
                                val intent = Intent(this@Channel1,FiveLetterGameScreen::class.java)
                                startActivity(intent)
                                finish()
                                binding.joinButton1.isVisible = false
                                binding.joinButton2.isVisible = false
                            }
                            else
                            {
                                Toast.makeText(this@Channel1,"Zaten Odadasın!",Toast.LENGTH_LONG).show()
                            }

                        }
                        else if(fplayerid!="empty" && splayerid!="empty")
                        {
                            Toast.makeText(this@Channel1,"Oda Dolu",Toast.LENGTH_LONG).show()
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
                roomid.addListenerForSingleValueEvent(object :ValueEventListener{
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
                            val intent = Intent(this@Channel1,FiveLetterGameScreen::class.java)
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
                                val intent = Intent(this@Channel1,FiveLetterGameScreen::class.java)
                                startActivity(intent)
                                finish()
                                binding.joinButton1.isVisible = false
                                binding.joinButton2.isVisible = false
                            }
                            else
                            {
                                Toast.makeText(this@Channel1,"Zaten Odadasın!",Toast.LENGTH_LONG).show()
                            }

                        }
                        else if(fplayerid!="empty" && splayerid!="empty")
                        {
                            Toast.makeText(this@Channel1,"Oda Dolu",Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }


                })
            }


        }
        // odadan çıkışı düzenle
        binding.exitroomButton.setOnClickListener {
          var oda1 = odabul.child("1")
          var oda2 = odabul.child("2")
          if(currentUser!=null)
          {
          val playerID = currentUser.uid
          val playerName = currentUser.email.toString()
              val intent = Intent(this@Channel1,GameLobby::class.java)
              startActivity(intent)
              finish()

          oda1.addListenerForSingleValueEvent(object :ValueEventListener{
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

              oda2.addListenerForSingleValueEvent(object :ValueEventListener{
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
//        var room1 = odabul.child("1")
//        var room2 = odabul.child("2")
//
//
//            if(currentUser!=null)
//            {
//                room1.addListenerForSingleValueEvent(object :ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if(snapshot.child("gameSit").getValue(String::class.java)=="Dolu")
//                        {
//
//
//                        var room1fp = snapshot.child("fplayerID").getValue(String::class.java)
//                        var room1sp = snapshot.child("splayerID").getValue(String::class.java)
//                        if(room1fp == currentUser.uid)
//                        {
//                            owner = currentUser.uid
//                            binding.gameStartButton1.isVisible=true
//                        }
//                        else if(room1sp == currentUser.uid){
//
//                            guest = currentUser.uid
//
//                        }
//
//                        }
//                        else
//                        {
//
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//
//                room2.addListenerForSingleValueEvent(object:ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if(snapshot.child("gameSit").getValue(String::class.java)=="Dolu")
//                        {
//
//
//                        var room2fp = snapshot.child("fplayerID").getValue(String::class.java)
//                        var room2sp = snapshot.child("splayerID").getValue(String::class.java)
//                        if(room2fp == currentUser.uid)
//                        {
//                            owner = currentUser.uid
//                            binding.gameStartButton1.isVisible=true
//                        }
//                        else if(room2sp==currentUser.uid)
//                        {
//                            guest = currentUser.uid
//                        }
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//
//
//            }


        // oyun başlat butonuyla çalıştırma??
        binding.gameStartButton1.setOnClickListener {
            if(roomcheck==true)
            {
                if(currentUser!!.uid==owner ||  currentUser!!.uid==guest)
                {
                    val intent = Intent(this,FiveLetterGameScreen::class.java)
                    val handler = Handler()
                    val delay: Long = 5000
                    handler.postDelayed({
                        startActivity(intent)
                    },delay)
                    finish()


                }
            }

        }



        }



    }

