package com.example.wordle

class Channel {
    var ID:Int=0
   // var channelName:String=""
    var rooms:MutableMap<String, Room>

    constructor(ID:Int, rooms:MutableMap<String, Room>)
    {
        this.ID = ID
       // this.channelName = channelName
        this.rooms = rooms
    }


}