package com.ld.lockeddoor.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ld.lockeddoor.R

class AddNewActivityTask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_task)
        supportActionBar?.title="Add new task"
    }
}