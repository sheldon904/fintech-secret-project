package com.example.cryptopayment.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptopayment.R
import com.example.cryptopayment.data.StatusUpdate
import com.example.cryptopayment.data.StatusUpdateType
import java.text.SimpleDateFormat
import java.util.Locale

class StatusUpdateAdapter : 
    RecyclerView.Adapter<StatusUpdateAdapter.StatusUpdateViewHolder>() {
    
    private var statusUpdates: List<StatusUpdate> = emptyList()
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    
    class StatusUpdateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val statusIcon: ImageView = view.findViewById(R.id.statusIcon)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val statusTime: TextView = view.findViewById(R.id.statusTime)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusUpdateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_status_update, parent, false)
        return StatusUpdateViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: StatusUpdateViewHolder, position: Int) {
        val update = statusUpdates[position]
        
        // Set icon based on status type
        when (update.type) {
            StatusUpdateType.SUCCESS -> {
                holder.statusIcon.setImageResource(R.drawable.ic_check_circle)
            }
            StatusUpdateType.WARNING -> {
                holder.statusIcon.setImageResource(R.drawable.ic_pending)
            }
            StatusUpdateType.ERROR -> {
                holder.statusIcon.setImageResource(R.drawable.ic_error)
            }
            StatusUpdateType.INFO -> {
                holder.statusIcon.setImageResource(R.drawable.ic_pending)
            }
        }
        
        // Set text and time
        holder.statusText.text = update.message
        holder.statusTime.text = timeFormat.format(update.timestamp)
    }
    
    override fun getItemCount() = statusUpdates.size
    
    fun setStatusUpdates(updates: List<StatusUpdate>) {
        this.statusUpdates = updates
        notifyDataSetChanged()
    }
}