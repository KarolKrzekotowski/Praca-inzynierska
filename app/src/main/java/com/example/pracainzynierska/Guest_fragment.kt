package com.example.pracainzynierska

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pracainzynierska.databinding.FragmentFriendsBinding
import com.example.pracainzynierska.databinding.GuestFragmentBinding

class Guest_fragment:Fragment() {
    private lateinit var binding: GuestFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = GuestFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}