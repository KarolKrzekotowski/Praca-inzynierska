package com.example.pracainzynierska.History

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Data.HistoryData
import com.example.pracainzynierska.R


class HistoryRecyclerAdapter():RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder>() {

    private var HistoryList = emptyList<HistoryData>()
    var context:Context?= null

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var miejsce: TextView =itemView.findViewById(R.id.place)
        var date: TextView = itemView.findViewById(R.id.date)
        var no_of_players: TextView = itemView.findViewById(R.id.no_of_players)

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)

        val view = inflater.inflate(R.layout.history_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
//
        val hisotry = HistoryList[position]
        val miejsce = viewHolder.miejsce
        val date = viewHolder.date
        val number = viewHolder.no_of_players

        miejsce.text = hisotry.position.toString()
        date.text = hisotry.date
        number.text = hisotry.NumberOfPlayers.toString()

//        viewHolder.itemView.setOnLongClickListener{
//            DayAddedActivitiyFragment.showPopup(it, activity )
//
//            return@setOnLongClickListener true
//        }

    }

    override fun getItemCount(): Int {
        return HistoryList.size
    }
    fun setData(data: List<HistoryData>){
        this.HistoryList = data
        notifyDataSetChanged()
    }

    fun getData(position: Int): HistoryData {
        return HistoryList[position]
    }


}