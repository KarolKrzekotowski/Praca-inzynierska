package com.example.pracainzynierska.Game

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build


import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.*
import com.example.pracainzynierska.Cards.Cards
import com.example.pracainzynierska.Cards.Deck
import com.example.pracainzynierska.Data.HistoryData
import com.example.pracainzynierska.Data.HistoryDataViewModel
import com.example.pracainzynierska.Data.HistoryDataViewModelFactory

import com.example.pracainzynierska.Guest.GuestFragment
import com.example.pracainzynierska.R
import com.example.pracainzynierska.WaitingRoom.WaitingRoomForGuestsFragment
import com.example.pracainzynierska.databinding.GameFragmentBinding


import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.*


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class GameFragment:Fragment() {

    private lateinit var binding: GameFragmentBinding
    private lateinit var myCardsAdapter: MyCardsAdapter
    private lateinit var whoseTurnAdapter:WhoseTurnAdapter
    private var map = hashMapOf<String,Int>()
    private var playersList = mutableListOf<Player>()
    private var myDeck = mutableListOf<Cards>()
    private var usedThisRound = mutableListOf<Cards>()
    private var lockableButtons = mutableListOf<Button>()
    private var primaryCard:Cards?=null
    private lateinit var fullDeck:Deck
    private lateinit var gameDeck:Deck
    private lateinit var bitmap: Bitmap
    var sum = 0
    val cardType = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace")
    val cardColor = listOf("clubs", "diamonds", "hearts", "spades")
    var cardHeight :Int ?=null
    var cardWidth :Int?=null
    var helpCard:Boolean = false
    var myTurn : Boolean = false
    var cardsToTake = mutableListOf<Int>(0)
    var lastCard = mutableListOf<Cards>()
    var order:Order = Order(null,null,0)
    var basePlayersNumber = 0
    var currentPlayersNumber = 0
    private lateinit var historyDataViewModel:HistoryDataViewModel
    private lateinit var historyDataViewModelFactory:HistoryDataViewModelFactory
    var loser = false
    var won = false
     var shuffledListener: ValueEventListener =  (object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.value.toString() == true.toString()){
                gameDeck.deck.clear()
                table.child("MainDeck").get().addOnSuccessListener {
                    for (child in it.children){
                        gameDeck.deck.add(splitCardFromFire(child.value.toString()))
                    }
                    Log.i("dupczysko essa",gameDeck.deck.size.toString())
                    GiveCardsEndMove(sum)
                    Log.i("dupczysko essa2",gameDeck.deck.size.toString()+' '+ sum.toString())
                    UpdateMainDeckInFire()
                    myCardsAdapter.notifyDataSetChanged()
                    table.child("Shuffled").removeEventListener(this)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
        }
    })
     var gameoverListener: ValueEventListener = (object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.value.toString() == "NoCards"){
                resetSettings()
                Toast.makeText(context,"Zabrakło kart",Toast.LENGTH_SHORT).show()
            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
     var lastCardListener: ValueEventListener  = (object:ValueEventListener {
         override fun onDataChange(snapshot: DataSnapshot) {
             val card = splitCardFromFire(snapshot.child("name").value.toString())
             lastCard.add(card)
             cardsToTake.clear()
             ordersList.clear()
             primaryCard = card
             cardsToTake.add( snapshot.child("ToTake").value.toString().toInt())
             val checkf =snapshot.child("order").child("freze").value.toString().toInt()
             val checkc =snapshot.child("order").child("colour").value.toString()
             val checkt =snapshot.child("order").child("type").value.toString()
             if (checkc in colors){
                 if (card.type == "ace"){
                     order = Order(checkc,null,0)
                     binding.orders.text = "żądania: kolor " + checkc
                 }else{
                     binding.orders.text = "żądania: brak"
                     order = Order(null,null,0)
                 }
             }
             else if (card.type == "jack") {
                 order = Order(null, checkt, 0)
                 binding.orders.text = "żądania: typ " + checkt
             }
             else if (card.type == "4"){
                 if (checkf!=0){
                     order = Order(null,null,checkf)
                     binding.orders.text = "żądania: kolejki " + checkf
                 }else{
                     binding.orders.text = "żądania: brak"
                     order = Order(null,null,0)
                 }
             }
             else{
                 if (cardsToTake[0]==0){
                     binding.orders.text = "żądania: brak"
                 }else{
                     binding.orders.text = "żądania: ciągnij " + cardsToTake[0].toString()
                 }

                 order = Order(null,null,0)
             }
             ordersList.add(order)
             val cardFactory = BitmapFactory.decodeResource(resources,card.image)
             bitmap = Bitmap.createScaledBitmap(cardFactory,cardWidth!!,cardHeight!!,false)
             binding.LastUsed.setImageBitmap(bitmap)
         }

         override fun onCancelled(error: DatabaseError) {
         }
     })
     var turnListener:ValueEventListener =  (object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {

            val whoseTurn =snapshot.value.toString()
            whoseTurnAdapter.changeColor(whoseTurn)
            if(whoseTurn == currentUser){

                if (IamFrozen==0){
                    table.child("MainDeck").get().addOnSuccessListener{
                        gameDeck.deck.clear()
                        val hejo =  it.childrenCount
                        Log.i("fajne dupsko", hejo.toString())
                        for (child in it.children){
                            gameDeck.deck.add(splitCardFromFire(child.value.toString()))

                        }
                    }

                    for (button in lockableButtons){
                        button.isEnabled = true


                    }
                    myTurn = true
                    if (stop ==1){
                        licznikstopu =GlobalScope.launch {
                            val dispatcher = this.coroutineContext
                            CoroutineScope(dispatcher).launch {
                                delay(120000)
                                stopAllListeners()
                                resetSettings()
                                table.child("Quit").child(currentUser).setValue(currentUser)
                                quitRef.cancel()
                                Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)


                            }
                        }
                    }
                }
                else{

                    IamFrozen -=1
                    nextPlayerTurn()
                }


            }
            else{
                for (button in lockableButtons) {
                    button.isEnabled = false

                }
            }
        }
        override fun onCancelled(error: DatabaseError) {

        }
    })
     var noCardsListener:ValueEventListener = (object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if (isHost){
                if (snapshot.value.toString().toBoolean()){
                    addCardstoMainDeck()
                    table.child("Shuffled").setValue(true)
                    table.child("noCards").setValue(false)
                }

            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
     var wonListener:ValueEventListener =  (object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val wonPlayers = mutableListOf<Player>()
            for (snap in snapshot.children) {
                wonPlayers.add(Player(snap.value.toString(), mutableListOf<Cards>(), false))
            }
            var check = ""
            p1@ for (winner in wonPlayers) {
                for (player in playersList) {
                    if (winner.email == player.email) {
                        check = player.email
                        playersList.remove(player)
                        break@p1
                    }
                }
            }

            if (playersList.size == 1 && currentUser != check) {
                Lost()
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
     val quitListener: ValueEventListener  = (object :ValueEventListener{

        override fun onDataChange(snapshot: DataSnapshot) {
            table.child("Host").get().addOnSuccessListener {
                Log.i("mordadupa","mordadupa")
                val host = it.value.toString()
                var washe = false
                var sizeOfquiters = 0
                //dla zwykłych
                Log.i("mordadupa",currentUser)
                Log.i("mordadupa",host)
                Log.i("mordadupa",playersList.toString())
                


                if (dc == 1){
                    stopAllListeners()
                    dc = 0
                    return@addOnSuccessListener
                }

                for (snap in snapshot.children){
                    sizeOfquiters +=1
                    if (snap.value.toString() ==host){
                        washe = true
                        if (currentUser == playersList[1].email){
                            table.child("Host").setValue(currentUser)
                            isHost = true
                            table.child("Players").child(host).child("deck").get().addOnSuccessListener {
                                for (next in it.children){
                                    table.child("UsedCards").child(next.value.toString()).setValue(next.value)
                                }
                                table.child("Players").child(host).removeValue()
                            }
                        }
                    }
                    var tempplayers = mutableListOf<Player>()

                    for (player in playersList.indices){
                        if (playersList[player].email == snap.value.toString()){
                            tempplayers.add(playersList[player])
//                                    playersList.removeAt(player)
                        }
                    }
                    playersList.removeAll(tempplayers)
                    currentPlayersNumber = basePlayersNumber-sizeOfquiters
                    if (playersList.size == 1 ) {
                        job2?.cancel()
                        Lost()
                    }
                }
                if (!washe){
                    if (isHost){
                        for (snap in snapshot.children){
                            table.child("Players").child(host).child("deck").get().addOnSuccessListener {
                                for (next in it.children){
                                    table.child("UsedCards").child(next.value.toString()).setValue(next.value)
                                }
                                table.child("Players").child(host).removeValue()
                            }
                        }
                    }
                }
            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
    lateinit var quitRef :OnDisconnect
    var stop = 0
     var licznikstopu:Job?=null
    var job2 :Job?= null









    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        callNetworkConnection()
        quitRef = table.child("Quit").child(currentUser).onDisconnect()
        quitRef.setValue(currentUser)

        val back = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Opuszczenie")
                alertDialogBuilder.setMessage("Czy na pewno chcesz wyjść z gry?")
                alertDialogBuilder.setPositiveButton("Opuść"){_,_ ->
                    stopAllListeners()
                    table.child("Quit").child(currentUser).setValue(currentUser)
                    resetSettings()
                    quitRef.cancel()
                    job2?.cancel()
                    if(isHost){

                        var newbe = ""
                        for (player in playersList.indices){
                            if (playersList[player].email == currentUser){
                                if (player == playersList.size-1){
                                    newbe = playersList[0].email
                                }
                                else{
                                    newbe = playersList[player+1].email
                                }
                            }
                        }
                        table.child("Host").setValue(newbe)
                        if (myTurn){
                            nextPlayerTurn()
                        }
                    }
                    //wyjście
                    Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)
                }
                alertDialogBuilder.setNegativeButton("Zostań"){_,_ ->

                }
                val alert = alertDialogBuilder.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,back)

        // high < width
        val high = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels
        cardWidth = (resources.displayMetrics.widthPixels * 0.1).toInt()
        cardHeight = (resources.displayMetrics.heightPixels *0.25).toInt()
        cardMap()
        prepareFullDeck()
        gameDeck = Deck(fullDeck.copyDeck())
        instance = this
        binding = GameFragmentBinding.inflate(inflater, container, false)
        lockableButtons.add(binding.undo)
        lockableButtons.add(binding.takeACard)
        lockableButtons.add(binding.endMove)
        for (button in lockableButtons){
            button.isEnabled = false
        }
        binding.MyCards.isEnabled = false

        val view = binding.root
        val myPlayer = table.child("Players").child(currentUser)
        if (currentUser === GuestFragment.ownerEmail){
            isHost = true
            myPlayer.child("Host").setValue("true")

        }
        else{
            myPlayer.child("Host").setValue("false")
        }
        val rv1 = binding.WhoseTurn
        whoseTurnAdapter = WhoseTurnAdapter()
        rv1.adapter = whoseTurnAdapter
        rv1.layoutManager = LinearLayoutManager(requireContext())
        myCardsAdapter = MyCardsAdapter()
        val rv2 = binding.MyCards
        rv2.adapter = myCardsAdapter
        rv2.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        rv2.layoutParams.width =(width*0.6).toInt()
        rv2.layoutParams.height =(high*0.25).toInt()


        table.child("Players").get().addOnSuccessListener {
                for (player in it.children){
                    val mail = player.child(email).value.toString()
                    playersList.add(Player(mail, mutableListOf<Cards>(),false))
                }
                whoseTurnAdapter.setData(playersList)
            basePlayersNumber = playersList.size
            if (isHost){

                gameDeck.Shuffle()
                gameDeck.DealTheCards(playersList)
                val firstCard = gameDeck.TakeOneCard()
                val firstCardFactory = BitmapFactory.decodeResource(resources,firstCard.image)
                bitmap = Bitmap.createScaledBitmap(firstCardFactory,cardWidth!!,cardHeight!!,false)
                binding.LastUsed.setImageBitmap(bitmap)
                primaryCard = firstCard
                for(i in gameDeck.deck.indices){
                    table.child("MainDeck").child(i.toString()).setValue(buildCardToFire(gameDeck.deck[i]))
                }
                table.child("LastCard").child("name").setValue(buildCardToFire(firstCard))
                table.child("LastCard").child("order").child("colour").setValue(null)
                table.child("LastCard").child("order").child("type").setValue(null)
                table.child("LastCard").child("order").child("freze").setValue(0)
                table.child("LastCard").child("ToTake").setValue(0)
                table.child("UsedCards").setValue(buildCardToFire(firstCard))
                table.child("Turn").setValue(playersList[Random.nextInt(0,playersList.size)].email)

            }
        }
        Handler(Looper.getMainLooper()).postDelayed({

            table.child("Players").child(currentUser).child("deck").get().addOnSuccessListener {
            for (child in it.children){
                val cardv = child.value.toString()
                val delim = "_"
                val arr = cardv.split(delim).toTypedArray()
                val card = Cards(arr[0],arr[1],map[cardv]!!)
                myDeck.add(card)

            }
                myCardsAdapter.setData(myDeck)
        }
            table.child("LastCard").addValueEventListener(lastCardListener)
            table.child("Turn").addValueEventListener(turnListener)
            table.child("Quit").addValueEventListener(quitListener)

        },2000)



        table.child("GameOver").addValueEventListener(gameoverListener)
        binding.sortColor.setOnClickListener {
            myDeck.sortBy { it.colour }
            myCardsAdapter.notifyDataSetChanged()
        }
        binding.sortType.setOnClickListener {
            myDeck.sortBy { it.type }
            myCardsAdapter.notifyDataSetChanged()
        }
        binding.undo.setOnClickListener{
            undoMove()
        }
        binding.endMove.setOnClickListener {
            endMove()
        }
        binding.takeACard.setOnClickListener {
            if (!helpCard){
                takeOne()
            }
            else{
                Toast.makeText(context,"Już wziąłeś ratunkową kartę",Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    fun addCardstoMainDeck(){
        Log.i("dupczysko użyte","hej")
        table.child("UsedCards").get().addOnSuccessListener {
            val newDeck  = mutableListOf<Cards>()
            for (i in it.children){
                Log.i("dupczysko",i.toString())
                newDeck.add(splitCardFromFire(i.value.toString()))
            }
            newDeck.shuffle()
            gameDeck.deck.addAll(newDeck)
            UpdateMainDeckInFire()
            Log.i("dupczysko użyte2",gameDeck.deck.size.toString())
            table.child("UsedCards").removeValue()
        }

    }
    fun prepareFullDeck(){
        fullDeck = Deck(mutableListOf())
        for (type in cardType){
            for (color in cardColor){
                val img = java.lang.StringBuilder().apply {
                    append(color)
                    append("_")
                    append(type)

                }.toString()
                fullDeck.add(Cards(color,type,map[img]!!))

            }
        }
//        fullDeck.add(Cards("black","joker",R.drawable.black_joker))
//        fullDeck.add(Cards("red","joker",R.drawable.red_joker))
    }
    fun cardMap(){
        map["black_joker"] = R.drawable.black_joker
        map["clubs_10"] = R.drawable.clubs_10
        map["diamonds_6"] = R.drawable.diamonds_6
        map["hearts_king"] = R.drawable.hearts_king
        map["diamonds_7"] = R.drawable.diamonds_7
        map["hearts_queen"] = R.drawable.hearts_queen
        map["diamonds_8"] = R.drawable.diamonds_8
        map["clubs_2"] = R.drawable.clubs_2
        map["clubs_3"] = R.drawable.clubs_3
        map["diamonds_9"] = R.drawable.diamonds_9
        map["red_joker"] = R.drawable.red_joker
        map["clubs_4"] = R.drawable.clubs_4
        map["diamonds_ace"] = R.drawable.diamonds_ace
        map["spades_10"] = R.drawable.spades_10
        map["clubs_5"] = R.drawable.clubs_5
        map["diamonds_jack"] = R.drawable.diamonds_jack
        map["spades_2"] = R.drawable.spades_2
        map["clubs_6"] = R.drawable.clubs_6
        map["diamonds_king"] = R.drawable.diamonds_king
        map["spades_3"] = R.drawable.spades_3
        map["clubs_7"] = R.drawable.clubs_7
        map["diamonds_queen"] = R.drawable.diamonds_queen
        map["spades_4"] = R.drawable.spades_4
        map["clubs_8"] = R.drawable.clubs_8
        map["hearts_10"] = R.drawable.hearts_10
        map["spades_5"] = R.drawable.spades_5
        map["clubs_9"] = R.drawable.clubs_9
        map["hearts_2"] = R.drawable.hearts_2
        map["spades_6"] = R.drawable.spades_6
        map["clubs_ace"] = R.drawable.clubs_ace
        map["hearts_3"] = R.drawable.hearts_3
        map["spades_7"] = R.drawable.spades_7
        map["clubs_jack"] = R.drawable.clubs_jack
        map["hearts_4"] = R.drawable.hearts_4
        map["spades_8"] = R.drawable.spades_8
        map["clubs_king"] = R.drawable.clubs_king
        map["hearts_5"] = R.drawable.hearts_5
        map["spades_9"] = R.drawable.spades_9
        map["clubs_queen"] = R.drawable.clubs_queen
        map["hearts_6"] = R.drawable.hearts_6
        map["spades_ace"] = R.drawable.spades_ace
        map["diamonds_10"] = R.drawable.diamonds_10
        map["hearts_7"] = R.drawable.hearts_7
        map["spades_jack"] = R.drawable.spades_jack
        map["diamonds_2"] = R.drawable.diamonds_2
        map["hearts_8"] = R.drawable.hearts_8
        map["spades_king"] = R.drawable.spades_king
        map["diamonds_3"] = R.drawable.diamonds_3
        map["hearts_9"] = R.drawable.hearts_9
        map["spades_queen"] = R.drawable.spades_queen
        map["diamonds_4"] = R.drawable.diamonds_4
        map["hearts_ace"] = R.drawable.hearts_ace
        map["diamonds_5"] = R.drawable.diamonds_5
        map["hearts_jack"] = R.drawable.hearts_jack
    }
    fun undoMove(){
        if (usedThisRound.isEmpty()){
            Toast.makeText(context,"nie wykonałeś ruchu",Toast.LENGTH_SHORT).show()
            return
        }
        if (usedThisRound.size ==1){
            allowedType.clear()
            allowedType.add("joker")
        }
        if (ordersList.size >1){
            ordersList.removeAt(ordersList.lastIndex)
        }
        if (cardsToTake.size>1){
            cardsToTake.removeAt(cardsToTake.lastIndex)
        }

        myDeck.add(usedThisRound[usedThisRound.lastIndex])

        usedThisRound.removeAt(usedThisRound.lastIndex)

        myCardsAdapter.notifyDataSetChanged()

        if (usedThisRound.isEmpty()){
            createBitmapOnStack(primaryCard!!)

        }

        else{
            createBitmapOnStack(usedThisRound[usedThisRound.lastIndex])

        }



    }
    fun createBitmapOnStack(card: Cards){
        val cardFactory = BitmapFactory.decodeResource(resources,card.image)
        val bitmap = Bitmap.createScaledBitmap(cardFactory,cardWidth!!,cardHeight!!,false)
        binding.LastUsed.setImageBitmap(bitmap)
    }
    fun endMove(){
        val used = usedThisRound.size
        var frozing = 0
        if (ordersList.size ==1){
            frozing = ordersList[0].freeze!!
        }
        if (frozing !=0){
            IamFrozen = frozing
            IamFrozen -=1
            table.child("LastCard").child("order").child("freze").setValue(0)
            nextPlayerTurn()
            usedThisRound.clear()
            helpCard = false
            myCardsAdapter.notifyDataSetChanged()

            myTurn = false
            allowedType.clear()
            allowedType.add("joker")
            return

        }
        for(card in usedThisRound){
            decreaseMyDeck(buildCardToFire(card))
        }
        sum = cardsToTake.sum()
        //nic nie użyliśmy
        if (used==0){
            if (sum==0){
                sum+=1
            }
            if (helpCard){
                sum -=1
            }
            if (gameDeck.deck.size < sum){

                table.child("noCards").setValue(true)

                         shuffledListener
            }
            else{
                GiveCardsEndMove(sum)
                UpdateMainDeckInFire()
            }
        }else{
            table.child("LastCard").child("ToTake").setValue(sum)
            table.child("LastCard").child("order").child("colour").setValue(ordersList[ordersList.lastIndex].colour)
            table.child("LastCard").child("order").child("type").setValue(ordersList[ordersList.lastIndex].type)
            var freze = 0
            for (i in ordersList){
                freze += i.freeze!!
            }
            table.child("LastCard").child("order").child("freze").setValue(freze)
            UpdateMainDeckInFire()
        }
        //zmiana kolejki
        if (!usedThisRound.isEmpty()){
            val c1 = usedThisRound[usedThisRound.lastIndex].colour
            val t1 = usedThisRound[usedThisRound.lastIndex].type
            if (t1 == "king" && c1 == "spades" ){
                previousPlayerTurn()
            }
            else{
                nextPlayerTurn()
            }
        }else{
            nextPlayerTurn()
        }

        usedThisRound.clear()
        helpCard = false
        myCardsAdapter.notifyDataSetChanged()
        myTurn = false
        allowedType.clear()
        allowedType.add("joker")
        if (myDeck.size ==0){
            Won()
        }
    }
    fun Won(){
        won = true
        quitRef.cancel()
        job2?.cancel()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val formatted = current.format(formatter)
// wpis do bazy
        historyDataViewModelFactory = HistoryDataViewModelFactory(activity?.application!!)
        historyDataViewModel = ViewModelProvider(this, historyDataViewModelFactory).get(
            HistoryDataViewModel::class.java)
        historyDataViewModel.insert(HistoryData(0,currentPlayersNumber-playersList.size+1,formatted,basePlayersNumber))
        table.child("Won").child(currentUser).setValue(currentUser)
        resetSettings()

        //wyjście z gry
        Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)
    }
    fun Lost(){
        //wpis do bazy
        quitRef.cancel()
        job2?.cancel()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val formatted = current.format(formatter)

        historyDataViewModelFactory = HistoryDataViewModelFactory(activity?.application!!)
        historyDataViewModel = ViewModelProvider(this, historyDataViewModelFactory).get(
            HistoryDataViewModel::class.java)
        historyDataViewModel.insert(HistoryData(0,currentPlayersNumber-playersList.size+1,formatted,basePlayersNumber))
        resetSettings()
        //wyjście
        loser = true

        Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)

    }
    fun stopAllListeners(){
        table.child("Shuffled").removeEventListener(shuffledListener)
        table.child("noCards").removeEventListener(noCardsListener)
        table.child("Quit").removeEventListener(quitListener)
        table.child("GameOver").removeEventListener(gameoverListener)
        table.child("LastCard").removeEventListener(lastCardListener)
        table.child("Turn").removeEventListener(turnListener)
        table.child("Won").removeEventListener(wonListener)
    }
    override fun onStop() {

        if (loser){
            stopAllListeners()
            table.removeValue()
        }else if (won){
            stopAllListeners()
            Log.i("stopowanko", "elo mordzia")
        }
        stop = 1
        super.onStop()
    }



    private fun callNetworkConnection() {
        job2 = GlobalScope.launch {
            var checker = true
            while (checker){
                if (!isDeviceOnline(requireActivity().applicationContext)){
                    checker = false
                    dc = 1
                }
                delay(200)
            }
            stopAllListeners()
            resetSettings()
            withContext(Dispatchers.Main){
                Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)
            }
        }
    }
     fun isDeviceOnline(context: Context): Boolean {
        val connManager = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities =  connManager.getNetworkCapabilities(connManager.activeNetwork)
            return networkCapabilities != null
        } else {
            // below Marshmallow
            val activeNetwork = connManager.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true && activeNetwork.isAvailable
        }

    }

    override fun onStart() {


        if (stop == 1){
            Log.i("moradupa","moradupedczka")
            licznikstopu?.cancel()
            quitRef.setValue(currentUser)
            table.child("Quit").addValueEventListener(quitListener)
            table.child("GameOver").addValueEventListener(gameoverListener)
            table.child("LastCard").addValueEventListener(lastCardListener)
            table.child("Turn").addValueEventListener(turnListener)
            table.child("noCards").addValueEventListener(noCardsListener)
            table.child("Won").addValueEventListener(wonListener)
        }
        stop = 0

        super.onStart()
    }
    fun resetSettings(){
        myRef.child(game).removeValue()
        WaitingRoomForGuestsFragment.roomName = ""
        GuestFragment.ownerEmail= ""
        IamFrozen  = 0
        isHost = false
        start = 0

    }
    fun GiveCardsEndMove(sum:Int){
        var j =0
        while (j<sum){
            val oneCard = gameDeck.TakeOneCard()
            val builtCard = buildCardToFire(oneCard)
            myDeck.add(oneCard)
            increaseMyDeck(builtCard)
            j+=1
        }
        table.child("LastCard").child("ToTake").setValue(0)
        cardsToTake.clear()
        cardsToTake.add(0)
    }
    fun nextPlayerTurn(){
        for (player in playersList.indices){
            if (playersList[player].email == currentUser){
                if (player == playersList.size-1){
                    table.child("Turn").setValue(playersList[0].email)
                }
                else{
                    table.child("Turn").setValue(playersList[player+1].email)
                }
            }
        }
    }
    fun previousPlayerTurn(){
        for (player in playersList.indices){
            if (playersList[player].email == currentUser){
                if (player == 0){
                    table.child("Turn").setValue(playersList[playersList.size-1].email)
                }
                else{
                    table.child("Turn").setValue(playersList[player-1].email)
                }
            }
        }
    }
    fun takeOne(){
        if (gameDeck.deck.size<=1){
            GlobalScope.launch{
                table.child("noCards").setValue(true)
                table.child("Shuffled").addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value.toString() == true.toString()){
                            gameDeck.deck.clear()
                            table.child("MainDeck").get().addOnSuccessListener {
                                for (child in it.children){
                                    gameDeck.deck.add(splitCardFromFire(child.value.toString()))
                                }
                                val oneCard = gameDeck.TakeOneCard()
                                myDeck.add(oneCard)
                                val builtCard = buildCardToFire(oneCard)
                                UpdateMainDeckInFire()
                                table.child("Players").child(currentUser).child("deck").child(builtCard).setValue(builtCard)
                                myCardsAdapter.notifyDataSetChanged()
                                table.child("Shuffled").removeEventListener(this)
                            }
                        }
                        helpCard = true
                        myCardsAdapter.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            }
        else{
            val oneCard =gameDeck.TakeOneCard()
            myDeck.add(oneCard)
            val builtCard = buildCardToFire(oneCard)
            table.child("Players").child(currentUser).child("deck").child(builtCard).setValue(builtCard)
            helpCard = true
            myCardsAdapter.notifyDataSetChanged()
        }
    }
    fun UpdateMainDeckInFire(){
        table.child("MainDeck").removeValue()
        for (card in gameDeck.deck.indices){
            table.child("MainDeck").child(card.toString()).setValue(buildCardToFire(gameDeck.deck[card]))
        }
    }
    fun increaseMyDeck(card: String){
        table.child("Players").child(currentUser).child("deck").child(card).setValue(card)
    }
    fun decreaseMyDeck(card: String){

        table.child("Players").child(currentUser).child("deck").child(card).removeValue()
        table.child("UsedCards").child(card).setValue(card)

        table.child("LastCard").child("name").setValue(card)

    }
    fun buildCardToFire(card: Cards):String{
        return card.colour+"_"+card.type
    }
    fun splitCardFromFire(string: String): Cards{

        val delim = "_"
        val arr = string.split(delim).toTypedArray()
        val card = Cards(arr[0],arr[1],map[string]!!)
        return card
    }


    companion object{
        private lateinit var instance:GameFragment
        var ordersList = mutableListOf<Order>()
        val types = arrayOf("5","6","7","8","9","10","queen","nic")
        var isHost = false
        val colors = arrayOf("clubs", "diamonds", "hearts", "spades")
        var IamFrozen :Int = 0
        var start = 0
        var dc = 0



        val table =  myRef.parent!!.parent!!.child(liveGames).child(WaitingRoomForGuestsFragment.roomName)
        val allowedType = mutableListOf<String>("joker")

        fun chooseCard(view: View, model: Cards){
            val lastCard = instance.lastCard[instance.lastCard.lastIndex]

            if (!instance.myTurn){
                return
            }
            // nowa karta ,sprawdzanie czy stara była specjalna
            Log.i("pizda nad", allowedType.toString())
            if (allowedType.size ==1){
                when(lastCard.type){
                    "2","3","king" ->{
                        if (instance.cardsToTake[0]>1){
                            allowedType.apply {
                                add("2")
                                add("3")

                                add("king")

                            }
                        }
                    }
                    "4" -> {
                        if (ordersList[ordersList.lastIndex].freeze != 0){
                            allowedType.add("4")

                        }
                    }
                    "jack" ->{
                        if (ordersList[ordersList.lastIndex].type in types){
                            allowedType.add("jack")

                        }
                    }
                    "ace" ->{
                        if (ordersList[ordersList.lastIndex].colour in colors ) {
                            allowedType.apply {
                                add("ace")

                            }
                        }
                    }

                }
            }

            // nie była specjalna
            if (allowedType.size ==1){
                if (lastCard.type == model.type || lastCard.colour == model.colour){
                    when(model.type){
                        "2" ->{
                            instance.cardsToTake.add(2)
                            useCard(model)
                        }
                        "3" ->{
                            instance.cardsToTake.add(3)
                            useCard(model)
                        }
                        "4" ->{
                            makeFreze()
                            useCard(model)
                        }
                        "jack" ->{
                            orderType()
                            useCard(model)
                        }
                        "ace" ->{
                            orderColour()
                            useCard(model)
                        }
                        "king" ->{
                            Toast.makeText(instance.requireContext(),"siema byku 3",Toast.LENGTH_SHORT).show()
                                if (model.colour =="hearts"||model.colour =="spades"){
                                    instance.cardsToTake.add(5)
                                    useCard(model)
                                }
                                else{
                                    useCard(model)
                                }
                        }
                        "joker" ->{
                            doJoker()
                        }
                        else ->{
                            useCard(model)
                        }
                    }
                }
            }
            // była specjalna, ale już jakiejś użyliśmy
            else if (!instance.usedThisRound.isEmpty()){
                Log.i("pizda nad2", allowedType.toString())
                if (model.type == instance.usedThisRound[instance.usedThisRound.size-1].type|| model.type =="joker"){
                    when(model.type){
                        "2" ->{
                            instance.cardsToTake.add(2)
                            useCard(model)
                        }
                        "3" ->{
                            instance.cardsToTake.add(3)
                            useCard(model)
                        }
                        "4" ->{
                            makeFreze()
                            useCard(model)
                        }
                        "jack" ->{
                            orderType()
                            useCard(model)
                        }
                        "ace" ->{
                            orderColour()
                            useCard(model)
                        }
                        "king" ->{
                            Log.i("siema byku", instance.cardsToTake.toString())

                            if (instance.cardsToTake.size>1){
                                if (model.colour =="hearts"||model.colour =="spades"){
                                    instance.cardsToTake.add(5)
                                    useCard(model)
                                }
                                else{
                                    Toast.makeText(instance.context,"nie można użyć króla zwykłego po królu bitewnym",Toast.LENGTH_SHORT).show()
                                }
                            }
                            else{
                                if (model.colour =="hearts"||model.colour =="spades"){
                                    instance.cardsToTake.add(5)
                                }
                                useCard(model)
                            }



                        }
                        "joker" ->{
                            doJoker()
                        }
                        else ->{
                            useCard(model)
                        }
                    }
                }
            }
            // była specjalna  1-szy ruch
            else{
                Log.i("pizda nad3", allowedType.toString())
                if (model.type in allowedType){
                    when(model.type){
                        "2" ->{
                            if (lastCard.type== "2"){
                                instance.cardsToTake.add(2)
                                useCard(model)
                            }else if (lastCard.type == "3" || lastCard.type == "king") {
                                if (lastCard.colour == model.colour) {
                                    instance.cardsToTake.add(2)
                                    useCard(model)
                                } else {
                                    Toast.makeText(
                                        instance.context,
                                        "Zły kolor",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        "3" ->{
                            if (lastCard.type == "3"){
                                instance.cardsToTake.add(3)
                                useCard(model)
                            }
                            else if (lastCard.type == "2" || lastCard.type =="king"){
                                if (lastCard.colour == model.colour) {
                                    instance.cardsToTake.add(3)
                                    useCard(model)
                                } else {
                                    Toast.makeText(
                                        instance.context,
                                        "Zły kolor",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        "king" ->{
                            if (model.colour =="hearts"||model.colour =="spades" ){
                                when(lastCard.type){
                                    "2","3"->{
                                        if (lastCard.colour ==model.colour){
                                            instance.cardsToTake.add(5)
                                            useCard(model)
                                        }
                                        else{
                                            Toast.makeText(
                                                instance.context,
                                                "Zły kolor",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    "king" ->{
                                        instance.cardsToTake.add(5)
                                        useCard(model)
                                    }
                                }
                            }
                            else{
                                Toast.makeText(instance.requireContext(),"Siema byku",Toast.LENGTH_SHORT).show()
                            }
                        }
                        "4" ->{
                            makeFreze()
                            useCard(model)
                        }
                        "jack" ->{
                            orderType()
                            useCard(model)
                        }
                        "ace" ->{
                            orderColour()
                            useCard(model)
                        }
                        "joker" ->{
                            doJoker()
                        }
                    }
                }
                else{
                    Log.i("pizda nad4", allowedType.toString())
                    if (lastCard.type =="jack"){
                        if (ordersList[0].type == model.type){
                            useCard(model)
                            ordersList.add(Order(null,null,0))
                        }else{
                            Toast.makeText(instance.context,"Zły typ",Toast.LENGTH_SHORT).show()
                            Log.i("kurwiszoni", allowedType.toString())
                            Log.i("kurwiszoni", model.type)
                        }
                    }else if (lastCard.type == "ace"){
                        if (ordersList[0].colour == model.colour){
                            when(model.type){
                                "2" -> { instance.cardsToTake.add(2)
                                    ordersList.add(Order(null,null,0))}
                                "3" -> {instance.cardsToTake.add(3)
                                    ordersList.add(Order(null,null,0))}
                                "4" -> {
                                    makeFreze()
                                }
                                "jack" -> {
                                    orderType()
                                }
                                "king" -> {
                                    if(model.colour =="hearts"||model.colour =="spades" ){
                                        instance.cardsToTake.add(5)
                                    }else{
                                        ordersList.add(Order(null,null,0))
                                    }
                                }
                                else ->{
                                    ordersList.add(Order(null,null,0))
                                }



                            }
                            useCard(model)

                        }else{
                            Toast.makeText(instance.context,"Zły typ",Toast.LENGTH_SHORT).show()
                            Log.i("kurwiszoni", allowedType.toString())
                            Log.i("kurwiszoni", model.type)
                        }
                    }else{
                        Toast.makeText(instance.context,"Zły typ",Toast.LENGTH_SHORT).show()
                        Log.i("kurwiszoni", allowedType.toString())
                        Log.i("kurwiszoni", model.type)
                    }
                }

            }



        }
        fun useCard(model: Cards){
            val bitmapFactory = BitmapFactory.decodeResource(instance.resources,model.image)
            val bitmap = Bitmap.createScaledBitmap(bitmapFactory, instance.cardWidth!!, instance.cardHeight!!,false)
            instance.binding.LastUsed.setImageBitmap(bitmap)
            instance.usedThisRound.add(model)
            instance.myDeck.remove(model)
            instance.myCardsAdapter.notifyDataSetChanged()
            allowedType.apply {
                clear()
                add("joker")
                add(model.type)
            }
            instance.lastCard.add(model)
        }
        fun orderType(){
            val builder = AlertDialog.Builder(instance.context)
            builder.setTitle("wybierz typ karty do rządania")

            builder.setItems(types){
                dialog,x ->
                run {
                    if (x != 7) {

                        ordersList.add(Order(null, types[x],0))
                    }
                }

        }
            val dialog = builder.create()
            dialog.show()
        }
        fun orderColour(){
            val builder = AlertDialog.Builder(instance.context)
            builder.setTitle("wybierz kolor do rządania")
            builder.setItems(colors){
                    dialog,x ->
                run {
                    ordersList.add(Order(colors[x],null,0))
                }
            }
            val dialog = builder.create()
            dialog.show()
        }

        fun makeFreze(){
            ordersList.add(Order(null,null,1))
        }

        fun doJoker(){

        }
    }
}