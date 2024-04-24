package com.example.wordle

class Room {
    var ID:Int=0
    var roomName:String=""
    //var playersID:MutableList<String> = mutableListOf()
    //var playersName:MutableList<String> = mutableListOf()
    var fplayerID:String=""
    var fplayerName:String=""
    var fplayerWord:String=""
    var fplayerScore:Int=0
    var splayerID:String=""
    var splayerName:String=""
    var splayerWord:String=""
    var splayerScore:Int=0
    var gameSit:String=""
    var gameInfo:String=""

    constructor(ID:Int, roomName:String,fplayerID:String,fplayerName: String, fplayerWord:String,fplayerScore:Int,splayerID:String,splayerName:String,splayerWord:String,splayerScore:Int, gameSit:String, gameInfo:String)
    {
        this.ID=ID
        this.roomName=roomName
        this.fplayerID=fplayerID
        this.fplayerName=fplayerName
        this.fplayerWord=fplayerWord
        this.fplayerScore=fplayerScore
        this.splayerID=splayerID
        this.splayerName=splayerName
        this.splayerWord=splayerWord
        this.splayerScore=splayerScore
        //this.playersID=playersID
        //this.playersName = playersName
        this.gameSit=gameSit
        this.gameInfo=gameInfo
    }
}

var rooms:Map<String, Room> = mapOf()