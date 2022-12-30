package com.example.pracainzynierska.History

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Data.HistoryData
import com.example.pracainzynierska.R

/**
 * Adapter RecyclerView historii rozgrywek urządzenia
 *
 */
class HistoryRecyclerAdapter():RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder>() {

    private var HistoryList = emptyList<HistoryData>()
    var context:Context?= null

    /**
     *  RecyclerView Holder na  dane o rozgrywkach
     * @param itemView dane rozgrywki do dodania do ViewHolder
     */
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var miejsce: TextView =itemView.findViewById(R.id.place)
        var date: TextView = itemView.findViewById(R.id.date)
        var no_of_players: TextView = itemView.findViewById(R.id.no_of_players)

    }

    /**
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.history_item,parent,false)
        return ViewHolder(view)
    }

    /**
     * Wyświetla dane o rozgrywkach na określonych pozycjach
     *
     * @see RecyclerView.Adapter.onBindViewHolder
     */
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {


        val hisotry = HistoryList[position]
        with(viewHolder){
            miejsce.text = hisotry.position.toString()
            date.text = hisotry.date
            no_of_players.text = hisotry.NumberOfPlayers.toString()
        }
    }

    /**
     *
     * @see RecyclerView.Adapter.getItemCount
     */
    override fun getItemCount(): Int {
        return HistoryList.size
    }
    // funkcja zmienia listę historii danych
    /**
     *  Ustawia dane do listy HistoryList
     * @param data Lista obiektów HistoryData
     */
    fun setData(data: List<HistoryData>){
        this.HistoryList = data
        notifyDataSetChanged()
    }



}