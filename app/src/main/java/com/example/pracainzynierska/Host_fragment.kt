package com.example.pracainzynierska

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pracainzynierska.databinding.GuestFragmentBinding
import com.example.pracainzynierska.databinding.HostFragmentBinding

class Host_fragment:Fragment() {

    private lateinit var binding: HostFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HostFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }
}