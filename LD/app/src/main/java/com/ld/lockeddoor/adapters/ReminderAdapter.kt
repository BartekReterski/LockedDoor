package com.ld.lockeddoor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bitvale.switcher.SwitcherC
import com.bitvale.switcher.SwitcherX
import com.ld.lockeddoor.R
import com.ld.lockeddoor.models.ReminderModel
import io.realm.Realm
import io.realm.RealmConfiguration

class ReminderAdapter(private val info: List<ReminderModel>): RecyclerView.Adapter<ReminderAdapter.NewReminderViewHolder>() {

    private var checkVisibility:Boolean=false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reminder_items, parent, false)
        return NewReminderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return info.size
    }

    override fun onBindViewHolder(holder: NewReminderViewHolder, position: Int) {


        val config = RealmConfiguration.Builder().name("yourReminder.realm").build()
        val realm = Realm.getInstance(config)

        val context=holder.itemView.context

        //wartości listy

        val reminderName=info[position].reminderText
        val reminderNameDsc=info[position].reminderTextDsc
        val reminderIconId=info[position].reminderIcon
        val reminderCheckedValue= info[position].reminderChecked

        holder.textViewReminderName.text=reminderName
        holder.textViewReminderDsc.text=reminderNameDsc

    }

    class NewReminderViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

        val textViewReminderName: TextView = itemView.findViewById(R.id.textViewReminderName)
        val textViewReminderDsc: TextView = itemView.findViewById(R.id.textViewReminderNameDsc)
        val switcherReminder: SwitcherC = itemView.findViewById(R.id.switcherIcon)


    }
}