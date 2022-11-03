package com.example.pracainzynierska.Guest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.*
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.Friends.FriendsInvitationAdapter
import com.example.pracainzynierska.databinding.GuestFragmentBinding
import com.firebase.ui.database.FirebaseRecyclerOptions


class GuestFragment:Fragment() {
    private lateinit var binding: GuestFragmentBinding
    private lateinit var adapter: InvitationToGameAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = GuestFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        instance = this
        binding.button2.setOnClickListener {
            Navigation.findNavController(view).navigate(
                R.id.action_guest_fragment_to_mainFragment
            )
        }

        val rv2 = binding.invitationToGame
        rv2.layoutManager = LinearLayoutManager(requireContext())
        val options2 = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(invites),Friends::class.java)
            .build()

        adapter = InvitationToGameAdapter(options2)
        rv2.adapter=adapter



        return view
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    companion object{
        private lateinit var instance: GuestFragment
        var ownerEmail:String = ""
        fun joinTheGame(view: View, model: Friends){
            val newModel = model.email.replace(".", " ")
            ownerEmail = newModel
            myRef.child(invites).child(newModel).removeValue()
            myRef.parent?.child(newModel)?.child(game)?.child(waiting)?.child(currentUser)?.child(email)?.setValue(currentUser)
        }
    }
}