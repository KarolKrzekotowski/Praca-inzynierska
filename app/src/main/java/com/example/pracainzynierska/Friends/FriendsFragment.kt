package com.example.pracainzynierska.Friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.MainFragment
import com.example.pracainzynierska.R
import com.example.pracainzynierska.databinding.FragmentFriendsBinding
import com.google.firebase.auth.FirebaseAuth


class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var friendsInvitationAdapter: FriendsInvitationAdapter
    val myRef = MainFragment.getMyRef()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(inflater,container,false)
        val view = binding.root

        val rv1 = binding.rvFriends
        val rv2 = binding.rvInvitations

        friendsAdapter = FriendsAdapter()
        friendsInvitationAdapter = FriendsInvitationAdapter()

        rv1.layoutManager = LinearLayoutManager(requireContext())
        rv2.layoutManager = LinearLayoutManager(requireContext())


        setupRv1()
        setupRv2()


        return view
    }
    fun setupRv1(){
        myRef.child("Friends").child("List").get().addOnSuccessListener {
            val list = mutableListOf<Friends>()
            for (friend in it.children){
                list.add(Friends(friend.value.toString().replace(' ','.')))
            }
            friendsAdapter.setData(list)
        }
    }

    fun deleteFriend(view: View) {
        val myRef = MainFragment.getMyRef()

        //delete me from friends friend list
        val myEmail = FirebaseAuth.getInstance().currentUser?.email
        myRef.parent?.child(friendsAdapter.getFriend(view.id - 1).email.replace('.', ' '))?.child("Friends")?.get()?.addOnSuccessListener {
            for (friend in it.children) {
                if (friend.value.toString().replace(' ', '.') == myEmail)
                {
                    myRef.parent?.child(friendsAdapter.getFriend(view.id - 1).email.replace('.', ' '))?.child("Friends")?.child(friend.key!!)?.setValue(null)
                }
            }
            //delete messages to that friend
            myRef.parent?.child(friendsAdapter.getFriend(view.id - 1).email.replace('.', ' '))?.child("messages")?.get()?.addOnSuccessListener {
                for (message in it.children) {
                    if (message.child("sender").value == myEmail)
                    {
                        myRef.parent?.child(friendsAdapter.getFriend(view.id - 1).email.replace('.', ' '))?.child("messages")?.child(message.key!!)?.setValue(null)
                    }
                }

                //delete messages from that friend
                myRef.child("messages").get().addOnSuccessListener {
                    for (message in it.children) {
                        if (message.child("sender").value == friendsAdapter.getFriend(view.id - 1).email) {
                            myRef.child("messages").child(message.key!!).setValue(null)
                        }
                    }

                    //delete friend from my friend list
                    myRef.child("Friends").get().addOnSuccessListener {
                        for (friend in it.children) {
                            if (friend.value.toString().replace(' ', '.') == friendsAdapter.getFriend(view.id - 1).email)
                            {
                                myRef.child("Friends").child(friend.key!!).setValue(null)
                            }
                        }

                        Toast.makeText(requireContext(), "Usunięto z listy znajomych: " + friendsAdapter.getFriend(view.id - 1), Toast.LENGTH_LONG).show()
                        friendsAdapter.deleteFriend(view.id - 1)
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Niepowodzenie: $it", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Niepowodzenie: $it", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener {
                Toast.makeText(requireContext(), "Niepowodzenie: $it", Toast.LENGTH_SHORT).show()
            }

        }?.addOnFailureListener {
            Toast.makeText(requireContext(), "Niepowodzenie: $it", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupRv2(){
        myRef.child("Friends").child("Pending").get().addOnSuccessListener {
            val list = mutableListOf<Friends>()
            for (friend in it.children){
                list.add(Friends(friend.value.toString().replace(' ','.')))
            }
            friendsAdapter.setData(list)
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"Niepowodzenie:$it",Toast.LENGTH_SHORT).show()
        }
    }



}