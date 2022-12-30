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


/**
 * Wyświetlany jest tutaj widok  poczekalni dla gości
 *
 */
class WaitingRoomForGuestsFragment : Fragment() {

    private lateinit var binding: FragmentWaitingRoomForGuestsBinding
    private lateinit var adapter: WaitingRoomAdapter
    lateinit var kicklistener : ChildEventListener
    lateinit var startlistener: ValueEventListener

    /**
     * funkcja tworząca widok poczekalni dla gości
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

        binding = FragmentWaitingRoomForGuestsBinding.inflate(inflater,container,false)
        val view = binding.root
        // ustawienie adapter do RecyclerView
        val rv1 = binding.rvWaitingPlayers
        rv1.layoutManager = LinearLayoutManager(requireContext())
        val options = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting), Friends::class.java)
            .build()
        adapter = WaitingRoomAdapter(options)
        rv1.adapter = adapter
        // opuszczenie poczekalni
        binding.LeaveRoom.setOnClickListener {
            myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting).child(
                currentUser).removeValue()
//            Navigation.findNavController(view).navigate(R.id.action_waitingRoomForGuestsFragment_to_guest_fragment)
        }
        // zostaniemy wyrzuceni
        kicklistener()
        // gra się rozpocznie
        startGameListener()



        return view
    }
    // nasłuchujemy, czy zostaliśmy wyrzuceni z poczekalni
    /**
     * włączanie nasłuchiwacza bazy firebase, który sprawdza czy zostaliśmy wyrzuceni przez gospodarza
     *
     */
    fun kicklistener(){

        kicklistener =myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting).child(currentUser)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                }
                override fun onChildRemoved(snapshot: DataSnapshot) {

                    Navigation.findNavController(binding.root).navigate(R.id.action_waitingRoomForGuestsFragment_to_guest_fragment)

                }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

                }
                override fun onCancelled(error: DatabaseError) {

                }

            })

    }
    // nasłuchujemy, czy gra się rozpoczęła
    /**
     * ustawienie nasłuchiwacza sprawdzającego czy gospodarz rozpoczął grę
     *
     */
    fun startGameListener(){

         startlistener =myRef.child(game).child("Room").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                roomName = snapshot.value.toString()
                if (roomName !="null"){

                    Navigation.findNavController(binding.root).navigate(R.id.action_waitingRoomForGuestsFragment_to_game_fragment)
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    /**
     * funckja startuje nasłuchiwanie dla adaptera
     *
     */
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    // usuwanie i stopowanie nasłuchiwaczy
    /**
     * funkcja usuwa i pauzuje nasłuchiwacze
     *
     */
    override fun onStop() {
        super.onStop()
        myRef.child(game).child("Room").removeEventListener(startlistener)
        myRef.parent!!.child(GuestFragment.ownerEmail).child(game).child(waiting)
            .child(currentUser).removeEventListener(kicklistener)
        adapter.stopListening()
    }


    companion object{

        var roomName = ""
    }

}