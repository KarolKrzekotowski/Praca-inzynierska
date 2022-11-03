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
import kotlinx.android.synthetic.main.fragment_friends.*
import kotlinx.coroutines.delay


class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var friendsInvitationAdapter: FriendsInvitationAdapter
    var exists = false

    var alreadyFriend = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater,container,false)
        val view = binding.root
        instance = this
        setupRv1()
        setupRv2()

        binding.invite.setOnClickListener {
            inviteFriend()
        }

        return view
    }



    override fun onStart() {
        super.onStart()
        friendsAdapter.startListening()
        friendsInvitationAdapter.startListening()
    }
    override fun onDestroy() {
        super.onDestroy()
        friendsAdapter.stopListening()
        friendsInvitationAdapter.stopListening()
    }

    fun setupRv1(){
        val rv1 = binding.rvFriends
        rv1.layoutManager = LinearLayoutManager(requireContext())
        val options = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(friends).child(friendsList),Friends::class.java)
            .build()
        friendsAdapter = FriendsAdapter(options)
        rv1.adapter = friendsAdapter
    }
    fun setupRv2(){
        val rv2 = binding.rvInvitations
        rv2.layoutManager = LinearLayoutManager(requireContext())
        val options2 = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(friends).child(pending),Friends::class.java)
            .build()

        friendsInvitationAdapter = FriendsInvitationAdapter(options2)
        rv2.adapter=friendsInvitationAdapter
    }

    fun inviteFriend() {
        //jeżeli to nie ja
        if (binding.emailEditText.text.toString() != FirebaseAuth.getInstance().currentUser?.email
            || binding.emailEditText.text.toString() != ""
        ) {
            Log.i("kacper", myRef.toString())
            Log.i("kacper", FirebaseAuth.getInstance().currentUser?.email!!)

            val email = binding.emailEditText.text.replace(Regex("\\."), " ")
            //czy ja już mam go w znajomych

            myRef.child(friends).child(friendsList).get().addOnSuccessListener {
                Log.i("kacper", friendsList)
                for (friend in it.children) {
                    Log.i("kacper",friend.toString())
                    val newFriend = friend.value.toString().replace("{email=","").replace("}","").replace(" ",".")


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
                myRef.parent?.get()?.addOnSuccessListener {
                    for (mail in it.children) {
                        if (mail.key == email) {
                            exists = true
                        }
                    }
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



        fun AddToFriends(view: View, model: Friends){
            val newModel = model.email.replace(".", " ")
            myRef.child(friends).child(friendsList).child(newModel).child(email).setValue(newModel)
            myRef.parent?.child(newModel)?.child(friends)?.child(friendsList)?.child(currentUser)?.child(email)?.setValue(currentUser)
            myRef.child(friends).child(pending).child(newModel).child(email).removeValue()
        }

        fun DeclineInvitation(view: View,model: Friends){
            myRef.child(friends).child(pending).child(model.email.replace(".", " ")).removeValue()
        }

        fun DeleteFromFriends(view:View,model: Friends){
            val newModel = model.email.replace(".", " ")
            myRef.child(friends).child(friendsList).child(newModel).removeValue()
            myRef.parent?.child(newModel)?.child(friends)?.child(friendsList)?.child(currentUser)?.removeValue()
        }
    }



}