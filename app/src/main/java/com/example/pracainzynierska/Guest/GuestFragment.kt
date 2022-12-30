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

/**
 * Wyświetlany jest tutaj widok zaproszeń do gier
 *
 */
class GuestFragment:Fragment() {
    private lateinit var binding: GuestFragmentBinding
    private lateinit var adapter: InvitationToGameAdapter

    /**
     * Funkcja tworząca widok zaproszeń do gier
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

        binding = GuestFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        instance = this
        // przycisk opuszczenia widoku z zaproszeniami do gier
        binding.button2.setOnClickListener {
            Navigation.findNavController(view).navigate(
                R.id.action_guest_fragment_to_mainFragment
            )
        }
        // ustawienie RecyclerView z zaproszeniami
        val rv2 = binding.invitationToGame
        rv2.layoutManager = LinearLayoutManager(requireContext())
        val options2 = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(invites),Friends::class.java)
            .build()

        adapter = InvitationToGameAdapter(options2)
        rv2.adapter=adapter

        // dopilnowanie, aby wyczyścić stare dane jeżeli jakieś zostały w bazie danych, w przypadku niestandardowych wyjść z gry
        myRef.child(game).removeValue()
        return view
    }

    /**
     * Funkcja wzbogacona o włączanie nasłuchiwaczy
     * @see Fragment.onStart
     */
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    /**
     * @see Fragment.onStop
     * Funkcja wzbogacona o zatrzymywanie nasłuchiwaczy
     */
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }




    companion object{
        private lateinit var instance: GuestFragment
        var ownerEmail:String = ""
        // dołączenie do poczekalni z zaproszenia
        /**
         * Funkcja dołącza gracza do poczekalni gracza z zaproszenia
         *
         * @param view widok
         * @param model model Friends
         */
        fun joinTheGame(view: View, model: Friends){
            val newModel = model.email.replace(".", " ")
            ownerEmail = newModel
            myRef.child(invites).child(newModel).removeValue()
            myRef.parent?.child(newModel)?.child(game)?.child(waiting)?.child(currentUser)?.child(email)?.setValue(currentUser)
            Navigation.findNavController(instance.binding.root).navigate(R.id.action_guest_fragment_to_waitingRoomForGuestsFragment)
        }
    }
}