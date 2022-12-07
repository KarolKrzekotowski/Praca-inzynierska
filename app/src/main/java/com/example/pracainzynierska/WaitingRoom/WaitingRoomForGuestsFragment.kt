package com.example.pracainzynierska.WaitingRoom

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.*
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.Guest.GuestFragment
import com.example.pracainzynierska.databinding.FragmentWaitingRoomForGuestsBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener



class WaitingRoomForGuestsFragment : Fragment() {

    private lateinit var binding: FragmentWaitingRoomForGuestsBinding
    private lateinit var adapter: WaitingRoomAdapter
    lateinit var kicklistener : ChildEventListener
    lateinit var startlistener: ValueEventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWaitingRoomForGuestsBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        val view = binding.root
        val rv1 = binding.rvWaitingPlayers
        rv1.layoutManager = LinearLayoutManager(requireContext())
        val options = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting), Friends::class.java)
            .build()
        adapter = WaitingRoomAdapter(options)
        rv1.adapter = adapter

        binding.LeaveRoom.setOnClickListener {
            myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting).child(
                currentUser).removeValue()
//            Navigation.findNavController(view).navigate(R.id.action_waitingRoomForGuestsFragment_to_guest_fragment)
        }
        // jeśli zostaniemy wyrzuceni
        kicklistener()
        startGameListener()



        return view
    }
    fun kicklistener(){

        kicklistener =myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting).child(currentUser)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onChildRemoved(snapshot: DataSnapshot) {
                    Log.i("hej",snapshot.value.toString())

                    Navigation.findNavController(binding.root).navigate(R.id.action_waitingRoomForGuestsFragment_to_guest_fragment)

                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }
                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
    fun startGameListener(){

         startlistener =myRef.child(game).child("Room").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                roomName = snapshot.value.toString()
                if (roomName !="null"){
                    Log.i("skurwysyn", snapshot.toString())
                    Log.i("skurwysyn", roomName)

                    Navigation.findNavController(binding.root).navigate(R.id.action_waitingRoomForGuestsFragment_to_game_fragment)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


//        myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child("Playing").addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (snapshot.value.toString() == 1.toString()){
//                    myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child("Playing").removeEventListener(this)
//                    Navigation.findNavController(binding.root).navigate(R.id.action_waitingRoomForGuestsFragment_to_game_fragment)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })

    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myRef.child(game).child("Room").removeEventListener(startlistener)
        myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting)
            .child(currentUser).removeEventListener(kicklistener)
        adapter.stopListening()
    }

    override fun onPause() {
        super.onPause()
        Log.i("hej","hej")
    }
    companion object{
        var roomName = ""
    }

}