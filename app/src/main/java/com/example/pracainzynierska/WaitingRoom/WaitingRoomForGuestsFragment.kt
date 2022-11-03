package com.example.pracainzynierska.WaitingRoom

import android.os.Bundle
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


class WaitingRoomForGuestsFragment : Fragment() {

    private lateinit var binding: FragmentWaitingRoomForGuestsBinding
    private lateinit var adapter: WaitingRoomAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
            Navigation.findNavController(view).navigate(R.id.action_waitingRoomForGuestsFragment_to_guest_fragment)

        }

        return view
    }


}