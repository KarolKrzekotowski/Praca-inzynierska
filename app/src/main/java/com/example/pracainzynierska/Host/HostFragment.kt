package com.example.pracainzynierska.Host

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.*
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.Friends.FriendsAdapter
import com.example.pracainzynierska.Friends.FriendsFragment
import com.example.pracainzynierska.Guest.GuestFragment
import com.example.pracainzynierska.databinding.HostFragmentBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit


class HostFragment:Fragment() {
    private lateinit var readyToInviteAdapter: ReadyToInviteAdapter
    private lateinit var playersAdapter: PlayersInRoomAdapter
    private lateinit var binding: HostFragmentBinding
    private val playersList = mutableListOf<Friends>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HostFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        instance = this
        GuestFragment.ownerEmail= currentUser
        setupRv1()
        setupRv2()
        myRef.child(game).child(waiting).child(currentUser).child(email).setValue(currentUser)
        binding.closeRoom.setOnClickListener {
            quitTheRoom()
        }
        binding.beginTheGame.setOnClickListener {


            val table = myRef.parent!!.parent!!.child(liveGames).child(currentUser)


            val players = myRef.child(game).child(waiting)

            players.get().addOnSuccessListener {
                for (friend in it.children){
                    val guy = friend.child(email).value.toString()
                    playersList.add(Friends(guy))
                    }
                for (friend in playersList){
                    table.child(friend.email).child(email).setValue(friend.email)
                }
                myRef.child(game).child("Playing").setValue(1.toString())

                Navigation.findNavController(view).navigate(R.id.action_host_fragment_to_game_fragment)
                }

        }
        return view
    }



    fun setupRv1(){
        val rv1 = binding.rvAcceptedGame
        rv1.layoutManager = (LinearLayoutManager(requireContext()))
        val options = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(game).child(waiting), Friends::class.java)
            .build()
        playersAdapter = PlayersInRoomAdapter(options)
        rv1.adapter = playersAdapter
    }
    fun setupRv2(){
        val rv2 = binding.rvPossibleToInvite
        rv2.layoutManager = LinearLayoutManager(requireContext())
        val options2 = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(friends).child(friendsList), Friends::class.java)
            .build()
        readyToInviteAdapter = ReadyToInviteAdapter(options2)
        rv2.adapter = readyToInviteAdapter
    }

    fun quitTheRoom(){
        myRef.child(game).child(waiting).removeValue()
        Navigation.findNavController(binding.root).navigate(R.id.action_hostfragment_to_mainFragment)
    }
    override fun onStart() {
        super.onStart()
        playersAdapter.startListening()
        readyToInviteAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        playersAdapter.stopListening()
        readyToInviteAdapter.stopListening()
    }

    companion object{

        private lateinit var instance:HostFragment
        fun InviteToGame(view: View,model:Friends){
            val newModel = model.email.replace("."," ")
            myRef.parent?.child(newModel)?.child(invites)?.child(currentUser)?.child(email)?.setValue(currentUser)
        }
        fun KickFromTable(view: View,model: Friends){
            val newModel = model.email.replace(".", " ")
            if (currentUser.equals(model.email)){
                Toast.makeText(instance.requireContext(),"Nie można usunąć siebie z pokoju",Toast.LENGTH_SHORT).show()
                return
            }
            myRef.child(game).child(waiting).child(newModel).removeValue()

        }
    }
}