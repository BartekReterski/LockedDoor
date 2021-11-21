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
import java.util.*
import android.app.PendingIntent
import android.app.AlarmManager
import android.content.DialogInterface
import android.widget.Toast
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatDelegate
import com.ld.lockeddoor.BuildConfig
import com.ld.lockeddoor.services.AlarmNotificationReceiver
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.SdkInitializationListener
import com.mopub.common.logging.MoPubLog
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubView
import java.lang.String


class MainActivity : AppCompatActivity(),MoPubView.BannerAdListener {

    private var notificationId=0
    private var pressedTime:Long=0
    lateinit var sharedPreferences:SharedPreferences
    var hourShared = 0
    var minuteShared = 0
    private lateinit var moPubView: MoPubView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //wyłączenie czarnego motywu aplikacji
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        //reklamy baner

        val configuration: SdkConfiguration.Builder = SdkConfiguration.Builder("b195f8dd8ded45fe847ad89ed1d016da")
        if (BuildConfig.DEBUG) {
            configuration.withLogLevel(MoPubLog.LogLevel.DEBUG);
        } else {
            configuration.withLogLevel(MoPubLog.LogLevel.INFO);
        }

        MoPub.initializeSdk(this, configuration.build(), initSdkListener())
        moPubView =findViewById(R.id.bannerBookAdd)
        moPubView.setAdUnitId("b195f8dd8ded45fe847ad89ed1d016da")

        //inicjalizacja bazy danych Realm
        Realm.init(this)

        showReminderTaskList()
    }


    //inicjalizacja reklam mobup
    private fun initSdkListener(): SdkInitializationListener {

        return SdkInitializationListener {

            moPubView.loadAd()

        }
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

            //odebranie danych tymczasowych na temat godziny
            val sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE)
            hourShared = sharedPreferences1.getInt("hour", 0)
            minuteShared = sharedPreferences1.getInt("minute", 0)

            if(readAllRealm.size==1 && hourShared == 0 && minuteShared==0){

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

            R.id.menu_about_app->{
                aboutAppAndRateDialog()
            }

        }
        return super.onOptionsItemSelected(item)
    }

   /* override fun onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(baseContext, "Press back again to exit", Toast.LENGTH_SHORT).show()
            finish()
        }
        pressedTime = System.currentTimeMillis()
    }
*/

    @SuppressLint("DefaultLocale")
    private  fun notificationLogic() {

        try {

            val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                .create()
            val view = layoutInflater.inflate(R.layout.notification_alert_dialog, null)
            val closeImg = view.findViewById<ImageView>(R.id.close_button)
            val setNotBtn = view.findViewById<Button>(R.id.set_not_btn)
            val textViewTime = view.findViewById<TextView>(R.id.text_view_time_set)
            val textViewNotDsb=view.findViewById<TextView>(R.id.text_view_not_dsb)

            //odebranie danych tymczasowych na temat godziny
            val sharedPreferences1 = getSharedPreferences("PREFS", MODE_PRIVATE)
            hourShared = sharedPreferences1.getInt("hour", 0)
            minuteShared = sharedPreferences1.getInt("minute", 0)
            textViewTime.text = String.format(
                "Notification time is " + "%02d:%02d",
                hourShared,
                minuteShared
            )

            if(hourShared==0 && minuteShared==0){

                textViewTime.visibility=View.GONE
            }else{
                textViewTime.visibility=View.VISIBLE

                //zadeklarowanie przycisku usuwania notyfikacji jako underline
                textViewNotDsb.paintFlags=textViewNotDsb.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                textViewNotDsb.setTextColor(Color.BLUE)
                textViewNotDsb.visibility=View.VISIBLE
            }

            setNotBtn.setOnClickListener {

                val c = Calendar.getInstance()
                val mHour = c[Calendar.HOUR_OF_DAY]
                val mMinute = c[Calendar.MINUTE]
                val mSecond = c[Calendar.SECOND]

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
                        startTime[Calendar.SECOND] = 0
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

                        val intentAlarm = Intent(this, AlarmNotificationReceiver::class.java)
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
                        Toast.makeText(this,String.format(
                            "Notification time is " + "%02d:%02d",
                            hourShared,
                            minuteShared
                        ),Toast.LENGTH_LONG).show()

                        builder.dismiss()

                    }, mHour, mMinute, true
                )

                timePickerDialog.show()


            }

            textViewNotDsb.setOnClickListener{

                //usuniecie alarmu
                val intentAlarmDisable = Intent(this, AlarmNotificationReceiver::class.java)
                val pendingIntentAlarmDisable = PendingIntent.getBroadcast(
                    this,
                    notificationId,
                    intentAlarmDisable,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
                val alarmManagerNotDisable = (getSystemService(ALARM_SERVICE) as AlarmManager)
                alarmManagerNotDisable.cancel(pendingIntentAlarmDisable)

                //anulowanie  danych tymczasowych na temat wybranej godziny
                sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("hour")
                editor.remove("minute")
                editor.apply()
                Toast.makeText(this,"Notification removed", Toast.LENGTH_SHORT).show();
                builder.dismiss()


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
    private fun aboutAppAndRateDialog(){

        val dialogBuilder = AlertDialog.Builder(this)

        val versionCode = BuildConfig.VERSION_CODE
        val versionName = BuildConfig.VERSION_NAME
        val iconsUsedInApp=" Icons used in application:\n\n Icons made by Freepik\n Icons made by Pixelmeetup\n Icons made by Pixel perfect\n"

        // set message of alert dialog
        dialogBuilder.setMessage("Application version: $versionName$versionCode\n\nIf you enjoy using the app would you mind taking a moment to rate it? \n\n$iconsUsedInApp")
            .setCancelable(false)
            .setPositiveButton("OK", DialogInterface.OnClickListener {
                    dialog, _ -> dialog.cancel()
            })
            .setNeutralButton("Rate app", DialogInterface.OnClickListener {
                    dialog, id ->

                val appPackage= this.packageName

                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackage")))

                }catch (e:java.lang.Exception){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackage")));
                }
                dialog.cancel()


            })

        val alert = dialogBuilder.create()
        alert.setTitle(R.string.app_name)

        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(63,81,181))
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.rgb(63,81,181))

    }


    override fun onResume() {
        showReminderTaskList()
        super.onResume()
    }

    override fun onBannerLoaded(p0: MoPubView) {
        Toast.makeText(this,
            "Banner successfully loaded.", Toast.LENGTH_SHORT).show();
    }

    override fun onBannerFailed(p0: MoPubView?, p1: MoPubErrorCode?) {
        TODO("Not yet implemented")
    }

    override fun onBannerClicked(p0: MoPubView?) {
        TODO("Not yet implemented")
    }

    override fun onBannerExpanded(p0: MoPubView?) {
        TODO("Not yet implemented")
    }

    override fun onBannerCollapsed(p0: MoPubView?) {
        TODO("Not yet implemented")
    }
}
