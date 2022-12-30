package com.example.pracainzynierska

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pracainzynierska.databinding.FragmentRulesBinding

/**
 * Wyświetlane są tutaj zasady gry w makao
 *
 */
class RulesFragment :Fragment() {
    private lateinit var binding: FragmentRulesBinding

    /**
     * funkcja tworzy widok zasad gry
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @see Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // bidnowanie widoku zasad
        binding = FragmentRulesBinding.inflate(inflater,container,false)
        return binding.root
    }
}