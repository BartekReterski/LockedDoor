package com.ld.lockeddoor.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import com.google.android.material.textfield.TextInputEditText
import com.ld.lockeddoor.R
import com.ld.lockeddoor.adapters.SpinnerAdapterIcon
import com.ld.lockeddoor.models.ReminderModel
import com.ld.lockeddoor.models.SpinnerModel
import io.realm.Realm
import io.realm.RealmConfiguration
import java.lang.Exception

class AddNewActivityTask : AppCompatActivity() {

    var dataImageId:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_task)
        supportActionBar?.title="Add new task"

        //inicjalizacja bazy danych Realm
        Realm.init(this)




        logicAddNewReminder()
    }

    private fun logicAddNewReminder(){

        try {

            val taskNameValue=findViewById<TextInputEditText>(R.id.task_name_value)
            val taskNameDsc=findViewById<TextInputEditText>(R.id.task_name_dsc_value)
            val addNewTaskBtn=findViewById<Button>(R.id.add_new_task_btn)
            val chooseIconSpinner=findViewById<AppCompatSpinner>(R.id.spinner_icon)

            chooseIconSpinner.adapter= SpinnerAdapterIcon(
                this, listOf(
                    SpinnerModel(R.drawable.ic_outline_door_front_128,"Doors front"),
                    SpinnerModel(R.drawable.ic_outline_door_front_128,"Doors front"),
                    SpinnerModel(R.drawable.ic_outline_door_front_128,"Doors front"),
                    SpinnerModel(R.drawable.ic_outline_door_front_128,"Doors front"),
                    SpinnerModel(R.drawable.ic_outline_door_front_128,"Doors front")

                )
            )

            chooseIconSpinner?.onItemSelectedListener=object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                   val spinnerModel:SpinnerModel= chooseIconSpinner.selectedItem as SpinnerModel
                    dataImageId=spinnerModel.image
                   val dataText=spinnerModel.description


                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                    println("Nothing selected")
                }

            }

            val config = RealmConfiguration.Builder().name("yourReminder.realm").build()
            val realm = Realm.getInstance(config)


            //sprawdzenie wszystkich rekordów w bazie danych
           /* val allData= realm.where(ReminderModel::class.java).findAll()
            allData.forEach{
                    reminderModel ->  println("nameValue: ${reminderModel.reminderText},${reminderModel.reminderTextDsc}")
            }*/

            addNewTaskBtn.setOnClickListener{

                if(taskNameValue.text.toString().isBlank() || taskNameDsc.text.toString().isBlank()){

                    Toast.makeText(this,"Please fill all data",Toast.LENGTH_SHORT).show()
                }
                else{

                    realm.beginTransaction()

                    val realmObject= realm.where(ReminderModel::class.java)
                        .equalTo("reminderText",taskNameValue.text.toString())
                        .findFirst()

                    if(realmObject==null) {

                        val newTaskReminder = realm.createObject(ReminderModel::class.java)
                        newTaskReminder.reminderText=taskNameValue.text.toString()
                        newTaskReminder.reminderTextDsc=taskNameDsc.text.toString()
                        newTaskReminder.reminderIcon= dataImageId
                        realm.commitTransaction()


                        Toast.makeText(this, "New task added ", Toast.LENGTH_LONG).show()

                        //przekierowanie do głównej aktywności po udanym procesie dodania rekordu do bazy danych
                        val intentFinishActivity= Intent(this, MainActivity::class.java)
                        intentFinishActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP and Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentFinishActivity)
                        finish()



                    }else{
                        Toast.makeText(this,"There is already in database item with this name", Toast.LENGTH_LONG).show()
                        realm.commitTransaction()
                    }

                }

            }



        }catch (e:Exception){

        }

    }
}