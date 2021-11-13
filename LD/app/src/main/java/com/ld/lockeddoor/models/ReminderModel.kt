package com.ld.lockeddoor.models

import io.realm.RealmObject

open class ReminderModel():RealmObject(){


    var reminderText:String?=null
    var reminderTextDsc:String?=null
    var reminderIcon:Int=0
    var reminderChecked:Boolean=false

}
