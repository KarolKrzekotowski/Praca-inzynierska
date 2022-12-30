package com.example.pracainzynierska.Friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.databinding.FriendsInvitationItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

/**
 * Adapter RecyclerView na Zaproszenia do grona znajomych
 *
 * @param options Zapytanie do Firebase o dane do wyświetlenia
 */
class FriendsInvitationAdapter(options: FirebaseRecyclerOptions<Friends>) :
    FirebaseRecyclerAdapter<Friends,FriendsInvitationAdapter.ViewHolder>(options) {
    /**
     *  RecyclerView Holder na widoki zaproszeń
     * @property binding zawiera email i przyciski do akceptowania i odrzucania zaproszeń
     */
    inner class ViewHolder(val binding: FriendsInvitationItemBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int): ViewHolder {

        val binding = FriendsInvitationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    /**
     * Wyświetla email, przyciski odrzucenia i akceptowania zaproszeń do grona znajomych
     * wyświetlane na określonych pozycjach
     *@see RecyclerView.Adapter.onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        with(holder){
            // ustawianie danych modelu do widoku
            binding.friendEmail.text = model.email
            // zaproszenie odrzucone
            binding.Reject.setOnClickListener {
                FriendsFragment.DeclineInvitation(it,model)
            }
            //zaproszenie zaakceptowane
            binding.acceptToFriends.setOnClickListener {
                FriendsFragment.AddToFriends(it,model)

            }
        }
    }
}