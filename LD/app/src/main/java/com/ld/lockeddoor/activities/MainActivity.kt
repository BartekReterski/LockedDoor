package com.ld.lockeddoor.activities

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ld.lockeddoor.R
import com.ld.lockeddoor.adapters.ReminderAdapter
import com.ld.lockeddoor.models.ReminderModel
import io.realm.Realm
import io.realm.RealmConfiguration
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.app.PendingIntent

import com.ld.lockeddoor.services.AlarmReceiver
import android.app.AlarmManager
import android.app.TimePickerDialog.OnTimeSetListener
import android.widget.Toast

import android.content.SharedPreferences

import android.widget.TimePicker
import org.w3c.dom.Text
import java.lang.String


class MainActivity : AppCompatActivity() {

    private var notificationId=0
    lateinit var sharedPreferences:SharedPreferences
    var hourShared = 0
    var minuteShared = 0

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


            if(!readAllRealm.isNullOrEmpty()){

                val textviewHelper = findViewById<TextView>(R.id.textViewHelper)
                val imageviewHelper = findViewById<ImageView>(R.id.imageViewHelper)

                textviewHelper.visibility = View.GONE
                imageviewHelper.visibility = View.GONE
            }

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
                notificationLogic()
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


    @SuppressLint("DefaultLocale")
    private  fun notificationLogic() {

        try {

            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.notification_alert_dialog, null)
            val closeImg = view.findViewById<ImageView>(R.id.close_button)
            val setNotBtn = view.findViewById<Button>(R.id.set_not_btn)
            val textViewTime = view.findViewById<TextView>(R.id.text_view_time_set)

            //odebranie danych tymczasowych na temat godziny
            val sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE)
            hourShared = sharedPreferences1.getInt("hour", 0)
            minuteShared = sharedPreferences1.getInt("minute", 0)
            textViewTime.text = String.format(
                "Notification time is " + "%02d:%02d",
                hourShared,
                minuteShared
            )

            setNotBtn.setOnClickListener {

                val c = Calendar.getInstance()
                val mHour = c[Calendar.HOUR_OF_DAY]
                val mMinute = c[Calendar.MINUTE]
                val mSecond = c[Calendar.SECOND]


                // Intent
                val intent = Intent(this@MainActivity, AlarmReceiver::class.java)
                intent.putExtra("notificationId", notificationId)


                // PendingIntent
                val pendingIntent = PendingIntent.getBroadcast(
                    this@MainActivity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT
                )

                // AlarmManager

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

                val timePickerDialog = TimePickerDialog(
                    this@MainActivity,
                    { view, hourOfDay, minute -> //wyslanie danych tymczasowych na temat wybranej godziny
                        sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putInt("hour", hourOfDay)
                        editor.putInt("minute", minute)
                        editor.apply()

                        // ustawienie czasu alarmu
                        val startTime = Calendar.getInstance()
                        startTime[Calendar.HOUR_OF_DAY] = hourOfDay
                        startTime[Calendar.MINUTE] = minute
                        startTime[Calendar.SECOND, ] = 0
                        val alarmStartTime = startTime.timeInMillis

                        //odebranie danych tymczasowych na temat godziny
                        val sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE)
                        hourShared = sharedPreferences1.getInt("hour", 0)
                        minuteShared = sharedPreferences1.getInt("minute", 0)
                        textViewTime.text = String.format(
                            "Notification time is " + "%02d:%02d",
                            hourShared,
                            minuteShared
                        )

                        // ustawienie alarmu

                        val intentAlarm = Intent(this, AlarmReceiver::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        val pendingIntentAlarm = PendingIntent.getBroadcast(
                            this,
                            notificationId,
                            intentAlarm,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )

                        val alarmManagerNot = (getSystemService(ALARM_SERVICE) as AlarmManager)
                        alarmManagerNot.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            alarmStartTime,
                            AlarmManager.INTERVAL_DAY,
                            pendingIntentAlarm
                        )
                        builder.dismiss()

                    }, mHour, mMinute, true
                )

                timePickerDialog.show()


            }



            closeImg.setOnClickListener {
                builder.dismiss()
            }

            builder.setView(view)

            builder.setCanceledOnTouchOutside(false)
            builder.show()

        }catch (e:Exception){

            println(e.localizedMessage)
        }
    }
    override fun onResume() {
        showReminderTaskList()
        super.onResume()
    }
}
