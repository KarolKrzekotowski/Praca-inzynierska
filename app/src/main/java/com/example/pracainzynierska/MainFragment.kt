package com.example.pracainzynierska

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.pracainzynierska.Data.HistoryData
import com.example.pracainzynierska.Data.HistoryDataViewModel
import com.example.pracainzynierska.Data.HistoryDataViewModelFactory
import com.example.pracainzynierska.Game.GameFragment
import com.example.pracainzynierska.databinding.FragmentMainBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 *  Wyświetlany jest tutaj widok głównego menu
 *
 *
 */
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ){
            res ->
        this.onSignInResult(res)
    }

    /**
     * funkcja ustawia referencje do bazydanych firebase,
     * odblokowywuje przyciski w razie powodzenia
     *
     * @param result informacje o użytkowniku i logowaniu
     */
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){

        if (result.resultCode == RESULT_OK) {

            // Zalogowano popawnie
            //nazawa użytkownika
            user = FirebaseAuth.getInstance().currentUser!!
            // ustawianie referencji do bazy danych  Firebase
            val database = Firebase.database("https://praca-inzynierska-a6652-default-rtdb.europe-west1.firebasedatabase.app/")
            userRef = database.getReference("users/" + (Firebase.auth.currentUser!!.email?.replace('.', ' ') ?: 0))
            userRef.child("messages").child("0").setValue("0")
            auth = FirebaseAuth.getInstance()

            // Odblokowanie przycisków i wyświetlenie jako kto użytkownik się zalogował
            Toast.makeText(requireContext(), "Witaj " + user.displayName, Toast.LENGTH_SHORT).show()
            binding.loggeduser.text = "Zalogowano jako: " + user.displayName
            binding.joinGame.isEnabled = true
            binding.newGame.isEnabled = true
            binding.Friends.isEnabled = true
            binding.historyOfGames.isEnabled = true


        } else {
            // Niepowodzenie logowania
            // komunikat o niepowodzeniu
            Toast.makeText(requireContext(), "Logowanie nie powiodło się", Toast.LENGTH_SHORT).show()
            // blokada przycisków
            binding.joinGame.isEnabled = false
            binding.newGame.isEnabled = false
            binding.Friends.isEnabled = false
            binding.historyOfGames.isEnabled = false



        }
    }

    /**
     * funkcja tworząca widok głównego menu

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

        // bindowanie widoku
        binding = FragmentMainBinding.inflate(inflater,container,false)
        val view = binding.root
        // ustawianie akcji przejścia dla każdego przycisku
        // przycisk znajomi
        binding.Friends.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_friendsFragment)
        }
        //przycisk historia gier
        binding.historyOfGames.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_history_fragment)
        }
        // przycisk stwórz grę
        binding.newGame.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_host_fragment)
        }
        //przycisk dołącz do gry
        binding.joinGame.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_guest_fragment)

        }
        // przycisk zasady
        binding.rules.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_rulesFragment)
        }
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        //przycisk logowania
        binding.login.setOnClickListener {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }
        // automatyczne logowanie
        if (!isUserInitialised()) {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)

        }


        return view

    }


    companion object {
        // referencja użytkownika w bazie Firebase
        lateinit var userRef: DatabaseReference

        /**
         * funkcja informuje, czy użytkownik jest zalogowany
         *
         * @return true lub false
         */
        fun isUserInitialised() = ::userRef.isInitialized
        // zwraca referencje użytkownika w Firebase
        /**
         * funkcja zwracająca referencje użytkownika w firebase
         *
         * @return referencja użytkownika w firebase
         */
        fun getMyRef(): DatabaseReference
        {
            return userRef
        }
    }


}