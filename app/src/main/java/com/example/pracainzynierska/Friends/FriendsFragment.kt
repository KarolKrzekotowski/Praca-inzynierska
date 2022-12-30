package com.example.pracainzynierska.Friends

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.*
import com.example.pracainzynierska.databinding.FragmentFriendsBinding
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.delay

/**
 * Wyśwetlany jest tutaj widok znajomych
 *
 */
class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var friendsInvitationAdapter: FriendsInvitationAdapter
    var exists = false
    var alreadyFriend = false

    /**
     * @see Fragment.onCreateView
     *  Funkcja tworzy widok znajomych
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // bindowanie widoku
        binding = FragmentFriendsBinding.inflate(inflater,container,false)
        val view = binding.root
        instance = this
        // ustawianie RecyclerView
        setupRv1()
        setupRv2()

        // ustawianie akcji wykonywanej podczas wysyłania zaproszenia do znajomych
        binding.invite.setOnClickListener {
            inviteFriend()
        }
        return view
    }


    // nadpisanie funkcji onStart,
    /**
     * @see Fragment.onStart
     *  nadpisana część włącza nasłuchiwacze adapterów
     */
    override fun onStart() {
        super.onStart()
        // W momencie uruchomienia widoku zacznij nasłuchiwać dane z Firebase dla RecyclerViews
        friendsAdapter.startListening()
        friendsInvitationAdapter.startListening()
    }
    // nadpisanie funkcji onDestroy
    /**
     * @see onDestroy
     * naspisana część wyłącza nasłuchiwacze adapterów
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        // W momencie wyłączenia widoku przestań nasłuchiwać dane z Firebase dla RecyclerViews
        friendsAdapter.stopListening()
        friendsInvitationAdapter.stopListening()
    }
    // funkcja ustawiająca RecyclerView z listą przyjaciół
    /**
     * Funkcja ustawia RecyclerView z listą przyjaciół
     *
     */
    fun setupRv1(){
        val rv1 = binding.rvFriends
        rv1.layoutManager = LinearLayoutManager(requireContext())
        // Jak nasłuchiwać Firebase
        val options = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(friends).child(friendsList),Friends::class.java)
            .build()
        // ustawienie danych wyświetlanych przez RecyclerView
        friendsAdapter = FriendsAdapter(options)
        rv1.adapter = friendsAdapter
    }
    // funkcja ustawiająca RecyclerView z listą zaproszeń
    /**
     * Funkcja ustawia RecyclerView z listą zaproszeń do grona znajomych
     *
     */
    fun setupRv2(){
        val rv2 = binding.rvInvitations
        rv2.layoutManager = LinearLayoutManager(requireContext())
        // Jak nasłuchiwać Firebase
        val options2 = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(friends).child(pending),Friends::class.java)
            .build()
        // ustawienie danych wyświetlanych przez RecyclerView
        friendsInvitationAdapter = FriendsInvitationAdapter(options2)
        rv2.adapter=friendsInvitationAdapter
    }
    // funkcja zapraszania do znajomych
    /**
     * Funckja wysyłania zaproszeń do innych graczy, zarejestrowanych w aplikacji
     *
     */
    fun inviteFriend() {
        //jeżeli to nie ja
        if (binding.emailEditText.text.toString() != FirebaseAuth.getInstance().currentUser?.email
            || binding.emailEditText.text.toString() != ""
        ) {
            val email = binding.emailEditText.text.replace(Regex("\\."), " ")
            //czy ja już mam go w znajomych
            myRef.child(friends).child(friendsList).get().addOnSuccessListener {
                for (friend in it.children) {
                    val newFriend = friend.value.toString().replace("{email=","").replace("}","").replace(" ",".")

                    // Sprawdzanie czy to już jest znajomy
                    if (binding.emailEditText.text.toString() == newFriend
                            .replace(' ', '.')
                    ) {
                        alreadyFriend = true
                        Toast.makeText(
                            requireContext(),
                            "Ten użytkownik jest już twoim znajomym!",
                            Toast.LENGTH_LONG
                        ).show()

                        return@addOnSuccessListener
                    }
                }
                // sprawdzaenie czy użytkownik istnieje w Bazie danych Firebase
                myRef.parent?.get()?.addOnSuccessListener {
                    for (mail in it.children) {
                        if (mail.key == email) {
                            exists = true
                        }
                    }
                    // istnieje - wyślij zaproszenie
                    if (exists) {
                        myRef.parent?.child(email)?.child(friends)?.child(pending)?.
                        child(FirebaseAuth.getInstance().currentUser?.email!!.replace("."," "))
                            ?.child("email")
                            ?.setValue(FirebaseAuth.getInstance().currentUser?.email!!)
                        Toast.makeText(requireContext(),"Zaproszono do znajomych",Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                }
            }
        }
    }

    companion object{
        private lateinit var instance:FriendsFragment


        // funkcja dodająca osobę, która nas zapraszała do znajomych
        /**
         * Funkcja akceptuje zaproszenie do znajomych
         *
         * @param view
         * @param model osoba zapraszająca
         */
        fun AddToFriends(view: View, model: Friends){
            val newModel = model.email.replace(".", " ")
            // ją do moich znajomych
            myRef.child(friends).child(friendsList).child(newModel).child(email).setValue(newModel)
            // mnie do jej znajomych
            myRef.parent?.child(newModel)?.child(friends)?.child(friendsList)?.child(currentUser)?.child(email)?.setValue(currentUser)
            // usuwanie zaproszenia
            myRef.child(friends).child(pending).child(newModel).child(email).removeValue()
        }
        // odrzucenie zaproszenia do znajaomych
        /**
         * Funkcja odrzuca zaproszenie do zanjomych
         *
         * @param view
         * @param model osoba zapraszająca
         */
        fun DeclineInvitation(view: View,model: Friends){
            myRef.child(friends).child(pending).child(model.email.replace(".", " ")).removeValue()
        }
        // usuwanie znajomego z ich grona
        /**
         * Funkcja usuwa osobę z listy znajomych
         *
         * @param view
         * @param model osoba do usunięcia ze znajomych
         */
        fun DeleteFromFriends(view:View,model: Friends){
            val newModel = model.email.replace(".", " ")
            // usuń  go z mojej listy
            myRef.child(friends).child(friendsList).child(newModel).removeValue()
            // usuń mnie z jego listy
            myRef.parent?.child(newModel)?.child(friends)?.child(friendsList)?.child(currentUser)?.removeValue()
        }
    }



}