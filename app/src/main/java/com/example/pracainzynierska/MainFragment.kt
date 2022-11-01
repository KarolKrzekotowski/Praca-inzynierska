package com.example.pracainzynierska

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.pracainzynierska.Data.HistoryData
import com.example.pracainzynierska.Data.HistoryDataViewModel
import com.example.pracainzynierska.Data.HistoryDataViewModelFactory
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        val response = result.idpResponse
        Log.i("kod", result.toString())
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            user = FirebaseAuth.getInstance().currentUser!!

            val database = Firebase.database("https://praca-inzynierska-a6652-default-rtdb.europe-west1.firebasedatabase.app/")
            userRef = database.getReference("users/" + (Firebase.auth.currentUser!!.email?.replace('.', ' ') ?: 0))
            userRef.child("friends")
            userRef.child("messages").child("0").setValue("0")

            auth = FirebaseAuth.getInstance()
            Toast.makeText(requireContext(), "Witaj " + user.displayName, Toast.LENGTH_SHORT).show()

        } else {
            Toast.makeText(requireContext(), "Logowanie nie powiodło się", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        // for this fragment
        binding = FragmentMainBinding.inflate(inflater,container,false)
        val view = binding.root
        binding.Friends.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_friendsFragment)
        }
        binding.historyOfGames.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_history_fragment)
        }
        binding.newGame.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_host_fragment)
        }
        binding.joinGame.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_mainFragment_to_guest_fragment)

        }
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        if (!isUserInitialised()) {
            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)

        }
        val historyDataViewModelFactory = HistoryDataViewModelFactory(activity?.application!!)
        val historyDataViewModel = ViewModelProvider(this, historyDataViewModelFactory).get(
            HistoryDataViewModel::class.java)

        return view

    }

    companion object {
        lateinit var userRef: DatabaseReference
        fun isUserInitialised() = ::userRef.isInitialized


        public fun getMyRef(): DatabaseReference
        {

            return userRef
        }
    }


}