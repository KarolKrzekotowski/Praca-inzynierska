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
import com.example.pracainzynierska.WaitingRoom.WaitingRoomForGuestsFragment
import com.example.pracainzynierska.databinding.HostFragmentBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.TimeUnit

/**
 *
 * Wyświetlany jest tu widok tworzenia gry.
 *
 *
 */
class HostFragment:Fragment() {
    private lateinit var readyToInviteAdapter: ReadyToInviteAdapter
    private lateinit var playersAdapter: PlayersInRoomAdapter
    private lateinit var binding: HostFragmentBinding
    private val playersList = mutableListOf<Friends>()
    private val readyToPlayList = mutableListOf<Friends>()
    var roomName = currentUser
    lateinit var  waitingListener : ValueEventListener

    /**
     * Funkcja tworząca widok tworzenia gry
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
        // ustawianie widoku
        binding = HostFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        instance = this
        GuestFragment.ownerEmail= currentUser
        myRef.child(game).child(waiting).child(currentUser).child(email).setValue(currentUser)
        // ustawienie RecyclerViews
        setupRv1()
        setupRv2()
        // opuszczenie pokoju
        binding.closeRoom.setOnClickListener {
            quitTheRoom()
        }
        // rozpoczęcie gry
        /**
         * Tworzenie nowej rozgrywki
         */

        binding.beginTheGame.setOnClickListener {
            if (
                playersList.size>4 || playersList.size<2){
                Toast.makeText(requireContext(),"Maksymalna liczba graczy to: 4\nMinimalna liczba graczy to 2",Toast.LENGTH_SHORT).show()

            }
            else{

                for (player in playersList){
                    if (player.email != currentUser){
                        roomName +='_'
                        roomName += player.email

                    }

                }

                val table = myRef.parent!!.parent!!.child(liveGames).child(roomName)
                table.removeValue()
                WaitingRoomForGuestsFragment.roomName = roomName
                    for (friend in playersList){
                        table.child("Players").child(friend.email).child(email).setValue(friend.email)
                        myRef.parent!!.child(friend.email).child(game).child("Room").setValue(roomName)
                    }
                    table.child("Host").setValue(currentUser)
//                    myRef.child(game).child("Playing").setValue(1.toString())

                    Navigation.findNavController(view).navigate(R.id.action_host_fragment_to_game_fragment)

            }
        }
        return view
    }


    // ustawienie adapter do RecyclerView z graczami w poczekalni
    /**
     * funkcja ustawiająca RecyclerView z graczamiw poczeklani
     *
     */
    fun setupRv1(){
        val rv1 = binding.rvAcceptedGame
        rv1.layoutManager = (LinearLayoutManager(requireContext()))
        playersAdapter = PlayersInRoomAdapter()
        rv1.adapter = playersAdapter
        // nasłuchiwanie czy ktoś dołączył
        waitingListener = myRef.child(game).child(waiting).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                playersList.clear()
                for (man in snapshot.children){
                    playersList.add(Friends(man.child(email).value.toString()))
                }
                playersAdapter.setData(playersList)

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    // ustawienie RecyclerView z możliwymi do zaproszenia'
    /**
     * Funkcja ustawiająca RecyclerView ze znajomymi
     *
     */
    fun setupRv2(){
        val rv2 = binding.rvPossibleToInvite
        rv2.layoutManager = LinearLayoutManager(requireContext())
        readyToInviteAdapter = ReadyToInviteAdapter()
        rv2.adapter = readyToInviteAdapter
        // pobranie listy znajomych i dodanie jej do adaptera
        myRef.child(friends).child(friendsList).get().addOnSuccessListener {
            for(snapshot in it.children){
                readyToPlayList.add(Friends(snapshot.child(email).value.toString()))
            }
            readyToInviteAdapter.setData(readyToPlayList)
        }

    }

    /**
     * Funkcja usuwa poczekalnię w Firebase i cofa użytkownika do widoku głównego menu
     *
     */
    fun quitTheRoom(){
        // usunięcie poczekalni
        myRef.child(game).child(waiting).removeValue()
        // powrót do głównego fragmentu
        Navigation.findNavController(binding.root).navigate(R.id.action_hostfragment_to_mainFragment)
    }


    companion object{

        private lateinit var instance:HostFragment

        // wysłanie zaproszenia do gry
        /**
         * Funkcja zapraszania znajomych do poczekalni
         *
         * @param view widok
         * @param model obiekt typu Friends
         */
        fun InviteToGame(view: View,model:Friends){
            val newModel = model.email.replace("."," ")
            myRef.parent?.child(newModel)?.child(invites)?.child(currentUser)?.child(email)?.setValue(currentUser)
        }
        // usunięcie znajomego z gry
        /**
         * funckja usuwania znajomych z poczekalni
         *
         * @param view widok
         * @param model obiekt typu Friends
         */
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