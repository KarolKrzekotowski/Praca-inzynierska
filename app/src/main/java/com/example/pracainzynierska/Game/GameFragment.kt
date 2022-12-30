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

/**
 * Wyświetlany jest tutaj widok rozgrywki
 * Rozgrywana jest tutaj gra w makao
 *
 */
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

    lateinit var shuffledListener : ValueEventListener
     var gameoverListener: ValueEventListener = (object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.value.toString() == "true"){

                stopAllListeners()
                quitRef.cancel()
                job2?.cancel()
                Toast.makeText(context,"Zabrakło kart",Toast.LENGTH_SHORT).show()
                runBlocking {
                    if (isHost){
                        delay(2000)
                        table.removeValue()
                        resetSettings()
                    }
                }


                Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)
            }

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
    // nasłuchiwacz zmiany ostatniej karty w firebase
     var lastCardListener: ValueEventListener  = (object:ValueEventListener {
         override fun onDataChange(snapshot: DataSnapshot) {
             // nazwa karty

             val card = splitCardFromFire(snapshot.child("name").value.toString())
             // czyszczenie list z żądaniami i ustawianie nowych
             lastCard.add(card)
             cardsToTake.clear()
             ordersList.clear()
             primaryCard = card
             cardsToTake.add( snapshot.child("ToTake").value.toString().toInt())
             // ustawienie nowego żądania
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
             // zmiana obrazka
             val cardFactory = BitmapFactory.decodeResource(resources,card.image)
             bitmap = Bitmap.createScaledBitmap(cardFactory,cardWidth!!,cardHeight!!,false)
             binding.LastUsed.setImageBitmap(bitmap)
         }

         override fun onCancelled(error: DatabaseError) {
         }
     })
    // nasłuchiwacz zmiany kolejki
     var turnListener:ValueEventListener =  (object :ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {

            val whoseTurn =snapshot.value.toString()
            // zmiana koloru w RecyclerView kolejek
            whoseTurnAdapter.changeColor(whoseTurn)
            // moja kolejka
            if(whoseTurn == currentUser){
                // jeśli nie sotję
                if (IamFrozen==0){
                    // pobierz zaktualizowane nieużyte  karty  z Firebase i zaktualizuj ich listę u mnie
                    table.child("MainDeck").get().addOnSuccessListener{
                        gameDeck.deck.clear()


                        for (child in it.children){
                            gameDeck.deck.add(splitCardFromFire(child.value.toString()))

                        }
                    }
                    // odblokuj przyciski
                    for (button in lockableButtons){
                        button.isEnabled = true


                    }
                    myTurn = true
                    // warunek sprawdza czy zmieniłem aplikacje na inną
                    // jeżeli wciągu 2 minut wrócę do gry, wyłącza się, jeżeli nie to wychodzę z gry i wracam do menu głównego
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
                // jeśli dalej stoję, zmień osobę
                else{

                        IamFrozen -=1
                        nextPlayerTurn()


                }


            }
            // nie moja kolej - zablokuj przyciski
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
    // nasłuchiwacz czy ktoś wygrał
     var wonListener:ValueEventListener =  (object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val wonPlayers = mutableListOf<Player>()
            for (snap in snapshot.children) {
                wonPlayers.add(Player(snap.value.toString(), mutableListOf<Cards>(), false))
            }
            // usunięcie zwycięzczy z listy graczy
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

            // zostałem tylko ja w rozgrywce, przegrana
            if (playersList.size == 1 && currentUser != check) {
                Lost()
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
    // ktoś opuścił rozgrywkę - wyjście/wyłączenie/rozłączenie
     val quitListener: ValueEventListener  = (object :ValueEventListener{

        override fun onDataChange(snapshot: DataSnapshot) {
            table.child("Host").get().addOnSuccessListener {

                val host = it.value.toString()
                var washe = false
                var sizeOfquiters = 0
                //dla zwykłych

                

                // wcześniej się rozłączyłem
                // powtórz stopowanie nasłuchiwaczy dla pewności, że nie wyrzucą błędu
                if (dc == 1){
                    stopAllListeners()
                    dc = 0
                    return@addOnSuccessListener
                }
                // przejście po każdym graczu, który wyszedł z gry
                for (snap in snapshot.children){
                    sizeOfquiters +=1
                    // to był gospodarz - przeniesienie jego kart do kart użytych podczas rozgrywki i przekazanie jego praw kolejenej osobie na liście
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
                    if (isHost){
                        table.child("Turn").get().addOnSuccessListener {
                            if(it.value.toString() == snap.value.toString()){
                                for (player in playersList.indices){
                                    if (playersList[player].email == snap.value.toString()){
                                        if (player == playersList.size -1){
                                            table.child("Turn").setValue(playersList[0].email)
                                        }
                                        else{
                                            table.child("Turn").setValue(playersList[player+1].email)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // usunięcie gracza z listy graczy

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
                    // kolejny gracz

                }
                // to nie był host
                // host przenosi karty tego gracza do kart użytych
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


    /**
     * Tworzony jest tutaj widok rozgrywki i ustawianie nasłuchiwaczy
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @see Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // włączenie sprawdzacza połączenia do internetu
        callNetworkConnection()
        // ustawienie zapisu do Firebase gdy utracimy połączenie
        quitRef = table.child("Quit").child(currentUser).onDisconnect()
        quitRef.setValue(currentUser)
        // zabezpieczenie przed przypadkowo klikniętą strzałką wstecz
        val back = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val alertDialogBuilder = AlertDialog.Builder(requireContext())
                alertDialogBuilder.setTitle("Opuszczenie")
                alertDialogBuilder.setMessage("Czy na pewno chcesz wyjść z gry?")
                // dobrowolne opuszczenie rozgrywki
                // wyłączenie nasłuchiwaczy
                // resetowanie ustawień
                // usunięcie zabezpieczeń przed rozłączeniem

                alertDialogBuilder.setPositiveButton("Opuść"){_,_ ->
                    stopAllListeners()
                    table.child("Quit").child(currentUser).setValue(currentUser)
                    resetSettings()
                    quitRef.cancel()
                    job2?.cancel()

                    if (myTurn){
                        nextPlayerTurn()
                    }
                    //wyjście
                    Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)
                }
                // zostanie w grze
                alertDialogBuilder.setNegativeButton("Zostań"){_,_ ->

                }
                val alert = alertDialogBuilder.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,back)

        // high < width
        //przygotowania wstepne, rozmiarowanie elementów
        // przygotowanie mapy kart
        val high = resources.displayMetrics.heightPixels
        val width = resources.displayMetrics.widthPixels
        cardWidth = (resources.displayMetrics.widthPixels * 0.1).toInt()
        cardHeight = (resources.displayMetrics.heightPixels *0.25).toInt()
        cardMap()
        prepareFullDeck()
        gameDeck = Deck(fullDeck.copyDeck())
        instance = this
        binding = GameFragmentBinding.inflate(inflater, container, false)
        // dodanie przycisków do przycisków wyłączonych
        lockableButtons.add(binding.undo)
        lockableButtons.add(binding.takeACard)
        lockableButtons.add(binding.endMove)
        for (button in lockableButtons){
            button.isEnabled = false
        }
        binding.MyCards.isEnabled = false

        val view = binding.root
        val myPlayer = table.child("Players").child(currentUser)
        // czy jestem hostem
        if (currentUser === GuestFragment.ownerEmail){
            isHost = true
            myPlayer.child("Host").setValue("true")

        }
        else{
            myPlayer.child("Host").setValue("false")
        }
        // ustawienie adapterów do RecyclerViews czyja kolej i moich kart
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

        // pobranie listy graczy
        table.child("Players").get().addOnSuccessListener {
                for (player in it.children){
                    val mail = player.child(email).value.toString()
                    playersList.add(Player(mail, mutableListOf<Cards>(),false))
                }
                whoseTurnAdapter.setData(playersList)
            basePlayersNumber = playersList.size
            currentPlayersNumber = basePlayersNumber
            // jeśli jestem gospodarzem
            if (isHost){
                // mieszaj karty
                gameDeck.Shuffle()
                // rozdaj karty
                gameDeck.DealTheCards(playersList)
                // wybierz pierwszą kartę
                val firstCard = gameDeck.TakeOneCard()
                val firstCardFactory = BitmapFactory.decodeResource(resources,firstCard.image)
                bitmap = Bitmap.createScaledBitmap(firstCardFactory,cardWidth!!,cardHeight!!,false)
                binding.LastUsed.setImageBitmap(bitmap)
                primaryCard = firstCard
                // dodaj nieużyte karty do Firebase
                for(i in gameDeck.deck.indices){
                    table.child("MainDeck").child(i.toString()).setValue(buildCardToFire(gameDeck.deck[i]))
                }
                // dodaj pierwszą kartę do Firebase do LastCard i do UsedCards
                table.child("LastCard").child("name").setValue(buildCardToFire(firstCard))
                table.child("LastCard").child("order").child("colour").setValue(null)
                table.child("LastCard").child("order").child("type").setValue(null)
                table.child("LastCard").child("order").child("freze").setValue(0)
                table.child("LastCard").child("ToTake").setValue(0)
                table.child("UsedCards").setValue(buildCardToFire(firstCard))
                // wylosuj czyja kolej
                table.child("Turn").setValue(playersList[Random.nextInt(0,playersList.size)].email)

            }
        }
        table.child("Won").addValueEventListener(wonListener)
        // opóźnij działania o 2 sekundy
        Handler(Looper.getMainLooper()).postDelayed({
            // pobierz moje karty
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
            // uruchom nasłuchiwacze
            table.child("LastCard").addValueEventListener(lastCardListener)
            table.child("Turn").addValueEventListener(turnListener)
            table.child("Quit").addValueEventListener(quitListener)

        },2000)


        table.child("noCards").addValueEventListener(noCardsListener)
        table.child("GameOver").addValueEventListener(gameoverListener)
        // ustaw działania przycisków
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
    // funkcja hosta, która tasuje użyte karty i dodaje do zbioru nieużytych
    /**
     * Funkcja wykonywana przez gospodarza, gdy zabraknie kart nieużytych do pobrania
     * Karty użyte są tasowane i dodawane do kupki kart nieużytych
     *
     */
    fun addCardstoMainDeck(){

        table.child("UsedCards").get().addOnSuccessListener {
            val newDeck  = mutableListOf<Cards>()
            for (i in it.children){

                newDeck.add(splitCardFromFire(i.value.toString()))
            }
            newDeck.shuffle()
            gameDeck.deck.addAll(newDeck)
            UpdateMainDeckInFire()

            table.child("UsedCards").removeValue()
        }

    }
    // funkcja tworząca pełną talię kart
    /**
     * funkcja tworzy pełną talię kart składającą się z obiektów typu Cards
     *
     */
    fun prepareFullDeck(){
        fullDeck = Deck(mutableListOf())
        for (type in cardType){
            for (color in cardColor){
                val img = java.lang.StringBuilder().apply {
                    append(color)
                    append("_")
                    append(type)

                }.toString()
                // dodawanie karty do pełnej talii - kolor, typ, położenie zdjęcia
                fullDeck.add(Cards(color,type,map[img]!!))

            }
        }
//        fullDeck.add(Cards("black","joker",R.drawable.black_joker))
//        fullDeck.add(Cards("red","joker",R.drawable.red_joker))
    }
    // mapa kart klucz - kolor_typ, wartość - położenie obrazka
    /**
     * Funkcja mapująca położenie obrazków kart
     *
     */
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
    // cofnij ruch
    /**
     * Funkcja cofająca ruch gracza
     *
     */
    fun undoMove(){
        // nie rzuciłeś żadnej karty
        if (usedThisRound.isEmpty()){
            Toast.makeText(context,"nie wykonałeś ruchu",Toast.LENGTH_SHORT).show()
            return
        }
        // rzuciłeś tylko 1, wróc do ustawień pierwotnych
        if (usedThisRound.size ==1){
            allowedType.clear()
            allowedType.add("joker")
        }
        // usuń z listy żądań i ciągnieć ostatnią zmianę
        if (ordersList.size >1){
            ordersList.removeAt(ordersList.lastIndex)
        }
        if (cardsToTake.size>1){
            cardsToTake.removeAt(cardsToTake.lastIndex)
        }
        // karta wraca do ręki
        myDeck.add(usedThisRound[usedThisRound.lastIndex])
        // usunięcie karty z listy kart użytych w turze
        usedThisRound.removeAt(usedThisRound.lastIndex)

        myCardsAdapter.notifyDataSetChanged()
        // odświeżenie obrazka ostatniej karty
        if (usedThisRound.isEmpty()){
            createBitmapOnStack(primaryCard!!)

        }

        else{
            createBitmapOnStack(usedThisRound[usedThisRound.lastIndex])

        }



    }
    // ustawienie obrazka ostatniej karty
    /**
     * Funkcja ustawia grafikę karty na środek
     *
     * @param card Karta
     */
    fun createBitmapOnStack(card: Cards){
        val cardFactory = BitmapFactory.decodeResource(resources,card.image)
        val bitmap = Bitmap.createScaledBitmap(cardFactory,cardWidth!!,cardHeight!!,false)
        binding.LastUsed.setImageBitmap(bitmap)
    }
    // zakończ ruch
    /**
     * Funkcja wykonywana, gdy gracz zakończy swój ruch
     *
     */
    fun endMove(){
        val used = usedThisRound.size
        var frozing = 0
        if (ordersList.size ==1){
            frozing = ordersList[0].freeze!!
        }
        //  muszę stać kolejki
        if (frozing !=0){
            IamFrozen = frozing
            IamFrozen -=1
            // wymarz żądanie stania kolejki
            table.child("LastCard").child("order").child("freze").setValue(0)
            // kolejny gracz
            nextPlayerTurn()
            usedThisRound.clear()
            helpCard = false
            myCardsAdapter.notifyDataSetChanged()

            myTurn = false
            allowedType.clear()
            allowedType.add("joker")
            return

        }
        //usuwanie każdej karty użytej przeze mnie podczas gry
        for(card in usedThisRound){
            decreaseMyDeck(buildCardToFire(card))
        }
        // liczba kart do ciągnięcia
        sum = cardsToTake.sum()
        //nic nie użyliśmy
        if (used==0){
            if (sum==0){
                sum+=1
            }
            if (helpCard){
                sum -=1
            }

            // nie ma wystarczająco kart do ciągnięcia
            if (gameDeck.deck.size < sum){

                table.child("noCards").setValue(true)
                shuffledListener =  (object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value.toString() == true.toString()){
                            gameDeck.deck.clear()
                            table.child("MainDeck").get().addOnSuccessListener {
                                for (child in it.children){
                                    gameDeck.deck.add(splitCardFromFire(child.value.toString()))
                                }
                                if(gameDeck.deck.size < sum ){
                                    table.child("GameOver").setValue(true)
                                    table.child("Shuffled").removeEventListener(this)
                                }
                                else{
                                    GiveCardsEndMove(sum)

                                    UpdateMainDeckInFire()
                                    myCardsAdapter.notifyDataSetChanged()
                                    table.child("Shuffled").removeEventListener(this)
                                }


                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
                table.child("Shuffled").addValueEventListener(shuffledListener!!)
            }
            // jest wystarczająco kart do ciągnięcia
            else{
                // ciagniej karty
                GiveCardsEndMove(sum)
                // odśwież firebase
                UpdateMainDeckInFire()
            }
            // coś użyliśmy
        }else{
            // odśwież żądania, poprzez zwiększenie/ustawienie nowych
            table.child("LastCard").child("ToTake").setValue(sum)
            table.child("LastCard").child("order").child("colour").setValue(ordersList[ordersList.lastIndex].colour)
            table.child("LastCard").child("order").child("type").setValue(ordersList[ordersList.lastIndex].type)
            var freze = 0
            for (i in ordersList){
                freze += i.freeze!!
            }
            table.child("LastCard").child("order").child("freze").setValue(freze)
            // odśwież karty w firebase
            UpdateMainDeckInFire()
        }

        if (!usedThisRound.isEmpty()){
            val c1 = usedThisRound[usedThisRound.lastIndex].colour
            val t1 = usedThisRound[usedThisRound.lastIndex].type
            //zmiana kolejki, gdy rzuciliśmy króla wino
            if (t1 == "king" && c1 == "spades" ){
                previousPlayerTurn()
            }
            // kolejny gracz
            else{
                nextPlayerTurn()
            }
            // kolejny gracz
        }else{
            nextPlayerTurn()
        }
        // reset list i ustawień turowych
        usedThisRound.clear()
        helpCard = false
        myCardsAdapter.notifyDataSetChanged()
        myTurn = false
        allowedType.clear()
        allowedType.add("joker")
        // nie mam już kart - wygrałem
        if (myDeck.size ==0){
            Won()
        }
    }
    // wygrałem
    /**
     * Funkcja wykonywana, gdy graczowi skończą się karty.
     * Wychodzi z rozgrywki i zapisuje dane o niej do wewnętrznej bazy danych
     *
     */
    fun Won(){
        won = true
        // usuń sprawdzanie internetu i wpisy do bazy offline
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
        // dopisz mnie do tablicy zwycięzców
        table.child("Won").child(currentUser).setValue(currentUser)
        // resetuj ustawienia
        resetSettings()

        //wyjście z gry
        Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)
    }
    //przegrałem
    /**
     * Funkcja wykonywana, gdy gracz został sam w rozgrywce
     * Wychodzi z rozgrywki, zapisuje dane o niej do wewnętrznej bazy danych
     */
    fun Lost(){

        // usuń sprawdzanie internetu i wpisy do bazy offline
        quitRef.cancel()
        job2?.cancel()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val formatted = current.format(formatter)
        //wpis do bazy
        historyDataViewModelFactory = HistoryDataViewModelFactory(activity?.application!!)
        historyDataViewModel = ViewModelProvider(this, historyDataViewModelFactory).get(
            HistoryDataViewModel::class.java)
        historyDataViewModel.insert(HistoryData(0,currentPlayersNumber-playersList.size+1,formatted,basePlayersNumber))
        resetSettings()
        //wyjście
        loser = true

        Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)

    }
    // wyłącz wszystkie nasłuchiwacze
    /**
     * Funkcja wyłącza wszystkie nasłuchiwacze
     *
     */
    fun stopAllListeners(){
        if (::shuffledListener.isInitialized){
            table.child("Shuffled").removeEventListener(shuffledListener)
        }

        table.child("noCards").removeEventListener(noCardsListener)
        table.child("Quit").removeEventListener(quitListener)
        table.child("GameOver").removeEventListener(gameoverListener)
        table.child("LastCard").removeEventListener(lastCardListener)
        table.child("Turn").removeEventListener(turnListener)
        table.child("Won").removeEventListener(wonListener)
    }

    /**
     *
     *@see Fragment.onStop
     * Nadpisana część funkcji wyłącza nasłuchiwacze w przypadku zwycięstwa lub porażki
     * W przypadku wyjścia, ale nie wyłączenia aplikacji zaznacza to
     */
    override fun onStop() {
        // byłem ostatnią osobą przy stole
        if (loser){
            // usuń nasłuchiwacze
            stopAllListeners()
            // usuń stół z firebase
            table.removeValue()
            //wygrałem
        }else if (won){
            // wyłącz nasłuchiwacze
            stopAllListeners()
        }
        // w innym przypadku  zmieniliśmy aplikację lub wyłączyliśmy ją
//        table.child("LastCard").removeEventListener(lastCardListener)
        stop = 1
        super.onStop()
    }


    // sprawdzanie czy mamy internet co 0.2s przy użyciu kotlin Coroutines
    /**
     * Funkcja, która sprawdza co 0.2 czy użytkownik wciąż jest połączony z bazą danych,
     * jeżeli nie to wychodzi z rocgrywki, resetuje ustawienia i wyłącza nasłuchiwacze
     *
     */
    private fun callNetworkConnection() {
        job2 = GlobalScope.launch {
            var checker = true
            while (checker){
                // straciliśmy połączenie - wyjście z pętli
                if (!isDeviceOnline(requireActivity().applicationContext)){
                    checker = false
                    dc = 1
                }
                delay(200)
            }
            //wyłącz nasłuchiwacze
            stopAllListeners()
            // resetuj ustwienia
            resetSettings()
            // zmień wątek na główny przejdź do menu
            withContext(Dispatchers.Main){
                Navigation.findNavController(binding.root).navigate(R.id.action_game_fragment_to_mainFragment)
            }
        }
    }
    // czy urządzenie jest połączone z internetem
    /**
     * Funkcja sprawdza czy użytkownik jest połączony z internetem
     *
     * @param context kontekst aplikacji
     * @return true/false
     */
     fun isDeviceOnline(context: Context): Boolean {
        val connManager = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities =  connManager.getNetworkCapabilities(connManager.activeNetwork)
            return networkCapabilities != null
        } else {
            // starsze telefony
            val activeNetwork = connManager.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true && activeNetwork.isAvailable
        }

    }

    /**
     * @see Fragment.onStart
     * Nadpisana część funkcji sprawdza, czy użytkownik wrócił do aplikacji jeżeli tak, to włączane są nasłuchiwacze i wyłączana
     * jest korutyna, która przy zbyt długim wyjściu z aplikacji przeniosłaby go do głównego menu
     *
     */
    override fun onStart() {

        // wcześniej zmieniliśmy aplikację, ale znowu ją uruchomilismy
        if (stop == 1){
            // wyłącz korutyne, która czekałą 2 minuty po nastaniu naszej kolejki
            licznikstopu?.cancel()
            // dodaj listenery z powrotem
            quitRef.setValue(currentUser)
//            table.child("Quit").addValueEventListener(quitListener)
//            table.child("GameOver").addValueEventListener(gameoverListener)
//            table.child("LastCard").addValueEventListener(lastCardListener)
//            table.child("Turn").addValueEventListener(turnListener)
//            table.child("noCards").addValueEventListener(noCardsListener)
//            table.child("Won").addValueEventListener(wonListener)
        }
        stop = 0

        super.onStart()
    }
    // resetowanie ustawień w companion objects
    /**
     * Funkcja resetuje ustawienia dotyczące rozgrywki w companion objects
     *
     */
    fun resetSettings(){
        myRef.child(game).removeValue()
        WaitingRoomForGuestsFragment.roomName = ""
        GuestFragment.ownerEmail= ""
        IamFrozen  = 0
        isHost = false
        start = 0

    }

    /**
     * Funkcja dobiera graczowi karty i kończy jego ruch
     *
     * @param sum liczba kart do ciągnięcia
     */
    // ciągnij karty, kończ ruch
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
    // kolej kolejnego gracza
    /**
     * Funkcja zmienia w Firebase kto wykonuje ruch na następnego w kolejce po graczu gracza
     *
     */
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
    // kolej poprzedniego gracza
    /**
     *
     *  Funkcja zmienia w Firebase kto wykonuje ruch na gracza, który wykonywał ruch przed obecnym graczem
     */
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
    // weź jedną kartę
    /**
     * Funkcja dobiera 1 kartę graczowi
     *
     */
    fun takeOne(){
        // nie ma tyle kart w nieużytych
        if (gameDeck.deck.size<=1){
            // kotlin Coroutines blokująca główny wątek
            runBlocking {
                val donde = false
                table.child("noCards").setValue(true)
                table.child("Shuffled").addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // host już zmienił
                        if (snapshot.value.toString() == true.toString()){
                            // odśwież karty nieużyte
                            gameDeck.deck.clear()
                            table.child("MainDeck").get().addOnSuccessListener {
                                for (child in it.children){
                                    gameDeck.deck.add(splitCardFromFire(child.value.toString()))
                                }
                                // weź jedną kartę
                                val oneCard = gameDeck.TakeOneCard()
                                myDeck.add(oneCard)
                                val builtCard = buildCardToFire(oneCard)
                                UpdateMainDeckInFire()
                                table.child("Players").child(currentUser).child("deck").child(builtCard).setValue(builtCard)
                                myCardsAdapter.notifyDataSetChanged()
                                table.child("Shuffled").removeEventListener(this)
                                table.child("Shuffled").removeValue()
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
        // ciągnij 1 kartę i zaznacz to w firebase
        else{
            val oneCard =gameDeck.TakeOneCard()
            myDeck.add(oneCard)
            val builtCard = buildCardToFire(oneCard)
            table.child("Players").child(currentUser).child("deck").child(builtCard).setValue(builtCard)
            helpCard = true
            myCardsAdapter.notifyDataSetChanged()
        }
    }
    // odśwież karty nieużyte w firebase
    /**
     * Funkcja aktualizująca karty nieużyte w Firebase
     *
     */
    fun UpdateMainDeckInFire(){
        table.child("MainDeck").removeValue()
        for (card in gameDeck.deck.indices){
            table.child("MainDeck").child(card.toString()).setValue(buildCardToFire(gameDeck.deck[card]))
        }
    }
    // zwiększ liczbę moich kart w firebase
    /**
     * Funkcja zwiększa zbiór kart gracza w Firebase
     *
     * @param card Karta
     */
    fun increaseMyDeck(card: String){
        table.child("Players").child(currentUser).child("deck").child(card).setValue(card)
    }
    // usuń moją kartę z  firebase i dodaj ją jako ostatnio użyta i do użytych kart
    /**
     * Funkcja przenosi kartę gracza do zbioru kart użytych w Firebase
     *
     * @param card karta
     */
    fun decreaseMyDeck(card: String){

        table.child("Players").child(currentUser).child("deck").child(card).removeValue()
        table.child("UsedCards").child(card).setValue(card)

        table.child("LastCard").child("name").setValue(card)

    }
    // stwórz napis z karty do Firebase
    /**
     * Funkcja zamieniająca infromacje zawarte o karcie na napis do wpisu w Firebase
     *
     *
     * @param card karta
     * @return napis złączenia koloru i typu karty oddzielony '_'
     */
    fun buildCardToFire(card: Cards):String{
        return card.colour+"_"+card.type
    }
    // zamień nazwę karty w firebase do obiektu
    /**
     * Funkcja zamienia napis w Firebase na kartę
     *
     * @param string napis zawierający informacje o karcie z Firebase
     * @return instancja karty
     */
    fun splitCardFromFire(string: String): Cards{

        val delim = "_"
        val arr = string.split(delim).toTypedArray()
        val card = Cards(arr[0],arr[1],map[string]!!)
        return card
    }


    companion object{
        private lateinit var instance:GameFragment
        // lista żądań
        var ordersList = mutableListOf<Order>()
        // lista zwykłych kart
        val types = arrayOf("5","6","7","8","9","10","queen","nic")
        // czy jestem host
        var isHost = false
        // lista  kolorów
        val colors = arrayOf("clubs", "diamonds", "hearts", "spades")
        var IamFrozen :Int = 0
        var start = 0
        var dc = 0


        // referencja stołu gry
        val table =  myRef.parent!!.parent!!.child(liveGames).child(WaitingRoomForGuestsFragment.roomName)
        // lista z dozwolonymi typami kart do rzucenia
        val allowedType = mutableListOf<String>("joker")
        // karta wybrana z moich kart do rzucenia na środek
        /**
         * Funkcja wyrzuca na środek wybraną kartę, gdy nie koliduje z zasadami gry w makao
         *
         * @param view widok
         * @param model instancja karty
         */
        fun chooseCard(view: View, model: Cards){
            val lastCard = instance.lastCard[instance.lastCard.lastIndex]
            // nie moja kolej - nic nie rób
            if (!instance.myTurn){
                return
            }
            // nowa karta ,sprawdzanie czy stara była specjalna

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

                    if (lastCard.type =="jack"){
                        if (ordersList[0].type == model.type){
                            useCard(model)
                            ordersList.add(Order(null,null,0))
                        }else{
                            Toast.makeText(instance.context,"Zły typ",Toast.LENGTH_SHORT).show()

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

                        }
                    }else{
                        Toast.makeText(instance.context,"Zły typ",Toast.LENGTH_SHORT).show()

                    }
                }

            }



        }
        // zmień grafikę ostatniej karty, na kartę którą rzuciłem
        // usuń karte z moich kart
        // dodaj kartę do kart użytych
        // dodaj typy kart jakie mogę rzucać w tej turze
        /**
         * Funckja zmienia obraz karty na środku na wybraną
         *
         * @param model wybrana karta
         */
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
        // żądanie jopka jakiegoś typu
        /**
         * Funkcja dodaje wybrane żądanie typu kart do listy żądań
         *
         */
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
        // żadanie koloru przez asa
        /**
         * Funkcja dodaje wybrane żądanie koloru kart do listy żądań
         *
         */
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
        // rzucenie 4
        /**
         *  Funkcja dodaje kolejkę do przeczekania do listy żądań
         *
         */
        fun makeFreze(){
            ordersList.add(Order(null,null,1))
        }

        fun doJoker(){

        }
    }
}