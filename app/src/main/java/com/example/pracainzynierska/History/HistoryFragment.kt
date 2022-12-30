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

/**
 * Wyświetlany jest tutaj widok historii rozgrywek
 *
 */
class HistoryFragment: Fragment() {

    private lateinit var adapter: HistoryRecyclerAdapter
    private lateinit var historyDataViewModel:HistoryDataViewModel
    private lateinit var historyDataViewModelFactory:HistoryDataViewModelFactory
    private lateinit var binding: HistoryFragmentBinding

    /**
     *
     * Funkcja tworząca widok historii rozgrywek
     * @see Fragment.onCreateView
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //bindowanie widoku historii rozgrywek
        binding = HistoryFragmentBinding.inflate(inflater,container,false)
        val view = binding.root

        // ustawianie adapter w RecyclerView
        adapter = HistoryRecyclerAdapter()
        val rv = binding.rwHistory
        rv.adapter = adapter
        rv.layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        // tworzenie obiektu do oglądania na żywo  historii rozgrywek w wewnętrznej bazy danych przy użyciu viewmodel i viewmodelfactory
        historyDataViewModelFactory = HistoryDataViewModelFactory(activity?.application!!)
        historyDataViewModel = ViewModelProvider(this, historyDataViewModelFactory).get(HistoryDataViewModel::class.java)
        // ustawianie danych do wyświetlenia w adapterze
        historyDataViewModel.allGamesHistory.observe(viewLifecycleOwner,{it->
            if(it!=null){
                adapter.setData(it)

            }
        })


        return view
    }
}