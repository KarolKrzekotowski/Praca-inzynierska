package com.example.pracainzynierska.Host

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.*
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.Friends.FriendsAdapter
import com.example.pracainzynierska.Friends.FriendsFragment
import com.example.pracainzynierska.databinding.HostFragmentBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth


class HostFragment:Fragment() {
    private lateinit var readyToInviteAdapter: ReadyToInviteAdapter
    private lateinit var playersAdapter: PlayersInRoomAdapter
    private lateinit var binding: HostFragmentBinding
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
        setupRv1()
        setupRv2()
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
            myRef.child(game).child(waiting).child(newModel).removeValue()

        }
    }
}