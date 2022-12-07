package com.example.pracainzynierska.History

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pracainzynierska.Data.HistoryDataViewModel
import com.example.pracainzynierska.Data.HistoryDataViewModelFactory

import com.example.pracainzynierska.databinding.HistoryFragmentBinding

class HistoryFragment: Fragment() {

    private lateinit var adapter: HistoryRecyclerAdapter
    private lateinit var historyDataViewModel:HistoryDataViewModel
    private lateinit var historyDataViewModelFactory:HistoryDataViewModelFactory
    private lateinit var binding: HistoryFragmentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HistoryFragmentBinding.inflate(inflater,container,false)
        val view = binding.root

        adapter = HistoryRecyclerAdapter()
        val rv = binding.rwHistory
        rv.adapter = adapter
        rv.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        historyDataViewModelFactory = HistoryDataViewModelFactory(activity?.application!!)
        historyDataViewModel = ViewModelProvider(this, historyDataViewModelFactory).get(HistoryDataViewModel::class.java)

        historyDataViewModel.allGamesHistory.observe(viewLifecycleOwner,{it->
            if(it!=null){
                adapter.setData(it)

            }
        })


        return view
    }
}