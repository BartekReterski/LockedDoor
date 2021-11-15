package com.ld.lockeddoor.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ld.lockeddoor.R
import com.ld.lockeddoor.adapters.ReminderAdapter
import com.ld.lockeddoor.models.ReminderModel
import io.realm.Realm
import io.realm.RealmConfiguration
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private var pressedTime:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inicjalizacja bazy danych Realm
        Realm.init(this)

        showReminderTaskList()
    }



    private fun showReminderTaskList(){

        try{
            val config = RealmConfiguration.Builder().name("yourReminder.realm").build()
            val realm = Realm.getInstance(config)

            val readAllRealm=realm.where(ReminderModel::class.java).findAll()
            val recyclerView=findViewById<RecyclerView>(R.id.recyclerView)

            if(readAllRealm.size==1){

                Toast.makeText(this,"You can set notification to remember about your tasks",Toast.LENGTH_LONG).show()
            }


            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager=GridLayoutManager(context,3,RecyclerView.VERTICAL,false)
                adapter=ReminderAdapter(readAllRealm)

            }

        }catch (e:Exception){

            println("Something was wrong"+e.localizedMessage)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu,menu)

        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){

            R.id.menu_add_new_activity->{

                val intent=Intent(this,AddNewActivityTask::class.java)
                startActivity(intent)
            }

            R.id.menu_notifications->{

                val builder = AlertDialog.Builder(this,R.style.CustomAlertDialog)
                    .create()
                val view = layoutInflater.inflate(R.layout.notification_alert_dialog,null)
                val  button = view.findViewById<Button>(R.id.set_not_btn)
                builder.setView(view)
                button.setOnClickListener {
                    builder.dismiss()
                }
                builder.setCanceledOnTouchOutside(false)
                builder.show()
            }

        }
        return super.onOptionsItemSelected(item)
    }

  /*  override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
            finish()
        }
        pressedTime = System.currentTimeMillis()
    }*/

    override fun onResume() {
        showReminderTaskList()
        super.onResume()
    }
}