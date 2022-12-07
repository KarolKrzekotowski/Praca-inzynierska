package com.example.pracainzynierska

import com.google.firebase.auth.FirebaseAuth

val myRef = MainFragment.getMyRef()
val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString().replace("."," ")
const val game = "Game"
const val waiting = "WaitingForGame"
const val invites = "Invites"
const val friends = "Friends"
const val friendsList = "FriendsList"
const val email = "email"
const val pending = "Pending"
const val liveGames = "LiveGames"


