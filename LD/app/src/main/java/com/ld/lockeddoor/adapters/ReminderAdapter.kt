package com.ld.lockeddoor.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bitvale.switcher.SwitcherC
import com.ld.lockeddoor.R
import com.ld.lockeddoor.models.ReminderModel
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class ReminderAdapter(private val info: List<ReminderModel>): RecyclerView.Adapter<ReminderAdapter.NewReminderViewHolder>() {

    var checkValue:Boolean=false
    var alertDialog: AlertDialog? = null

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
        holder.imageViewIcon.setImageResource(reminderIconId)

        //ustawienie na start wartości false z bazy danych realm
        if(!reminderCheckedValue){
            holder.switcherReminder.setChecked(false)
        }
        else{
            holder.switcherReminder.setChecked(true)
        }


        holder.switcherReminder.setOnCheckedChangeListener {


                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle("Check confirm")
                alertDialogBuilder.setMessage("Are you sure you want to mark $reminderName as Checked?")
                alertDialogBuilder.setPositiveButton("Set as checked") { _: DialogInterface, _: Int ->

                    holder.switcherReminder.setChecked(true)
                    Toast.makeText(context, "$reminderName set as checked",Toast.LENGTH_SHORT).show()
                    alertDialog?.dismiss()
                }
                alertDialogBuilder.setNegativeButton("Cancel") { _: DialogInterface, _: Int ->

                    holder.switcherReminder.setChecked(false)
                    alertDialog?.dismiss()
                }

                alertDialog = alertDialogBuilder.create()
                alertDialog?.show()

        }

        holder.moreButton.setOnClickListener {

            try {
                val popupMenu: PopupMenu = PopupMenu(context, holder.moreButton)
                popupMenu.menuInflater.inflate(R.menu.recycler_context_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {


                        R.id.context_menu_delete -> {
                            val dialogBuilder = AlertDialog.Builder(context)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Are you sure you want to delete this item: " + info[position].reminderText)
                                .setCancelable(false)
                                .setPositiveButton(
                                    "Yes",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        try {
                                            val results: RealmResults<ReminderModel> =
                                                realm.where(ReminderModel::class.java)
                                                    .equalTo(
                                                        "reminderText",
                                                        info[position].reminderText
                                                    ).findAll()
                                            realm.beginTransaction()
                                            results.deleteAllFromRealm()
                                            realm.commitTransaction()
                                            notifyItemRemoved(position)
                                            notifyItemRangeChanged(position, info.size)
                                            Toast.makeText(context, "Deleted", Toast.LENGTH_LONG)
                                                .show()
                                        } catch (ex: Exception) {
                                            println(ex.message)
                                        }

                                        dialog.cancel()
                                    })
                                .setNegativeButton(
                                    "Cancel",
                                    DialogInterface.OnClickListener { dialog, _ ->
                                        dialog.cancel()
                                    })

                            val alert = dialogBuilder.create()
                            alert.setTitle("Confirm Delete")
                            alert.show()
                            alert.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setTextColor(Color.rgb(255, 136, 0))
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                                .setTextColor(Color.rgb(255, 136, 0))


                        }


                    }
                    true
                })
                popupMenu.show()

            } catch (e: Exception) {

                println(e.localizedMessage)
            }

        }
    }


    class NewReminderViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {

        val textViewReminderName: TextView = itemView.findViewById(R.id.textViewReminderName)
        val textViewReminderDsc: TextView = itemView.findViewById(R.id.textViewReminderNameDsc)
        val switcherReminder: SwitcherC = itemView.findViewById(R.id.switcherIcon)
        val imageViewIcon:ImageView=itemView.findViewById(R.id.imageViewIcon)
        val moreButton:ImageView=itemView.findViewById(R.id.more_button)


    }
}