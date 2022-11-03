package com.example.pracainzynierska

import com.google.firebase.auth.FirebaseAuth

val myRef = MainFragment.getMyRef()
val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString().replace("."," ")
val game = "Game"
val waiting = "WaitingForGame"
val invites = "Invites"
val friends = "Friends"
val friendsList = "FriendsList"
val email = "email"
val pending = "Pending"