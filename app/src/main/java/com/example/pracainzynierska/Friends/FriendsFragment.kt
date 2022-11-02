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
import com.example.pracainzynierska.MainFragment
import com.example.pracainzynierska.R
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
    val pending = "Pending"
    var alreadyFriend = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater,container,false)
        val view = binding.root
        instance = this

        val rv1 = binding.rvFriends
        val rv2 = binding.rvInvitations

        rv1.layoutManager = LinearLayoutManager(requireContext())
        rv2.layoutManager = LinearLayoutManager(requireContext())

        val options = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child("FriendsList"),Friends::class.java)
            .build()
        friendsAdapter = FriendsAdapter(options)
        rv1.adapter = friendsAdapter

        val options2 = FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(myRef.child(pending),Friends::class.java)
            .build()




        friendsInvitationAdapter = FriendsInvitationAdapter(options2)

        rv2.adapter=friendsInvitationAdapter

        setupRv1()
        setupRv2()
        binding.invite.setOnClickListener {
            inviteFriend()
        }
        Log.i("chujnia", myRef.child(pending).toString())
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

    }
    fun setupRv2(){

    }

    fun inviteFriend() {
        //jeżeli to nie ja
        if (binding.emailEditText.text.toString() != FirebaseAuth.getInstance().currentUser?.email
            || binding.emailEditText.text.toString() != ""
        ) {
//            Log.i("kurestwo", myRef.toString())
//            Log.i("kurestwo", FirebaseAuth.getInstance().currentUser?.email!!)
            val email = binding.emailEditText.text.replace(Regex("\\."), " ")
            //czy ja już mam go w znajomych
            myRef.child("FriendsList").get().addOnSuccessListener {

                for (friend in it.children) {
                    Log.i("cipecka",friend.toString())
                    val newFriend = friend.value.toString().replace("{email=","").replace("}","").replace(" ",".")
//                    Log.i("cipecka",friend.value.toString().replace("{email=","").replace("}"," "))
                    Log.i("cipeczka",newFriend)
                    Log.i("cipeczka", binding.emailEditText.text.toString())
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
                myRef.parent?.parent?.get()?.addOnSuccessListener {
                    for (mail in it.children) {
                        if (mail.key == email) {
                            exists = true
                        }
                    }
                    if (exists) {
                        myRef.parent?.parent?.child(email)?.child("Friends")?.child(pending)?.
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
        val myRef = MainFragment.getMyRef().child("Friends")
        val currentUser = FirebaseAuth.getInstance().currentUser?.email.toString().replace("."," ")

        fun AddToFriends(view: View, model: Friends){
            val newModel = model.email.replace(".", " ")
            myRef.child("FriendsList").child(newModel).child("email").setValue(newModel)
            myRef.parent?.parent?.child(newModel)?.child("Friends")?.child("FriendsList")?.child(
                currentUser)?.child("email")?.setValue(currentUser)
            myRef.child(instance.pending).child(newModel).child("email").removeValue()
        }

        fun DeclineInvitation(view: View,model: Friends){
            myRef.child(instance.pending).child(model.email.replace(".", " ")).removeValue()
        }

        fun DeleteFromFriends(view:View,model: Friends){
            val newModel = model.email.replace(".", " ")
            myRef.child("FriendsList").child(newModel).removeValue()
            myRef.parent?.parent?.child(newModel)?.child("Friends")?.child("FriendsList")?.child(currentUser)?.removeValue()
        }
    }



}