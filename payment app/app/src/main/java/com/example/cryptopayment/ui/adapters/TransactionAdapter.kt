package com.example.cryptopayment.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cryptopayment.R
import com.example.cryptopayment.data.Transaction
import com.example.cryptopayment.data.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val context: Context) : 
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    
    private var transactions: List<Transaction> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val typeIcon: TextView = view.findViewById(R.id.transactionTypeIcon)
        val title: TextView = view.findViewById(R.id.transactionTitle)
        val date: TextView = view.findViewById(R.id.transactionDate)
        val amount: TextView = view.findViewById(R.id.transactionAmount)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        
        // Set type icon
        when (transaction.type) {
            TransactionType.SEND -> {
                holder.typeIcon.text = "↑"
                holder.amount.text = "-${transaction.amount} ${transaction.cryptoCurrency}"
                holder.amount.setTextColor(context.getColor(R.color.error_red))
            }
            TransactionType.RECEIVE -> {
                holder.typeIcon.text = "↓"
                holder.amount.text = "+${transaction.amount} ${transaction.cryptoCurrency}"
                holder.amount.setTextColor(context.getColor(R.color.success_green))
            }
        }
        
        // Set title (merchant name or address)
        holder.title.text = transaction.merchantName ?: transaction.recipient?.take(8) ?: ""
        
        // Set date
        holder.date.text = dateFormat.format(transaction.timestamp)
    }
    
    override fun getItemCount() = transactions.size
    
    fun setTransactions(transactions: List<Transaction>) {
        this.transactions = transactions
        notifyDataSetChanged()
    }
}