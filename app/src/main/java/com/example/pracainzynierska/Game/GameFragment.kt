package com.example.pracainzynierska.Game

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.Cards.Cards
import com.example.pracainzynierska.Guest.GuestFragment
import com.example.pracainzynierska.R
import com.example.pracainzynierska.currentUser
import com.example.pracainzynierska.databinding.FragmentWaitingRoomForGuestsBinding
import com.example.pracainzynierska.databinding.GameFragmentBinding
import com.example.pracainzynierska.liveGames
import com.example.pracainzynierska.myRef
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.join_the_game_item.*

class GameFragment:Fragment() {

    private lateinit var binding: GameFragmentBinding
    private  lateinit var table:DatabaseReference
    private lateinit var whoseTurnAdapter: WhoseTurnAdapter
    private lateinit var myCardsAdapter: MyCardsAdapter


    private var playersList = mutableListOf<Player>()
    private var myDeck = mutableListOf<Cards>()
    private val fullDeck = mutableListOf<Cards>()
    val cardType = listOf<String>("2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace")
    val cardColor = listOf<String>("clubs", "diamonds", "hearts", "spades")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        instance = this
        binding = GameFragmentBinding.inflate(inflater, container, false)

        val view = binding.root
        val myPlayer = table.child(currentUser)
        table = myRef.parent!!.parent!!.child(liveGames).child(GuestFragment.ownerEmail)

        if (currentUser === GuestFragment.ownerEmail){
            isHost = true
            myPlayer.child("Host").setValue("true")

        }
        else{
            myPlayer.child("Host").setValue("false")
        }
       for (type in cardType){
           for (color in cardColor){
               val img = java.lang.StringBuilder().apply {
                   append(color)
                   append("_")
                   append(type)
                   append(".png")
               }.toString()

               fullDeck.add(Cards(color,type, BitmapFactory.decodeResource(resources,
                   resources.getIdentifier(img,"drawable",requireActivity().packageName))))
           }
       }
        fullDeck.add(Cards("0","Joker",BitmapFactory.decodeResource(resources,R.drawable.black_joker)))
        fullDeck.add(Cards("0","Joker",BitmapFactory.decodeResource(resources,R.drawable.black_joker)))


        whoseTurnAdapter = WhoseTurnAdapter()
        val rv1 = binding.WhoseTurn
        rv1.adapter = whoseTurnAdapter
        rv1.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        table.get().addOnSuccessListener {
//              ustaw graczy
        }
        myCardsAdapter = MyCardsAdapter()
        val rv2 = binding.MyCards
        rv2.adapter = myCardsAdapter
        rv2.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        //otrzymaj karty

        return view
    }

    companion object{
        private lateinit var instance:GameFragment
        var isHost = false
    }
}