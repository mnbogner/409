package com.mnb.crusadeapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.mnb.crusadeapp.data.*
import java.io.BufferedReader
import java.io.File

class MainActivity : AppCompatActivity() {

    // open codex
    // -add unit
    // --add model
    // ---add weapons
    // ---list abilities
    // ---add abilities
    // ---hq/add trait
    // ---hq/add relic
    // --plus/minus model
    // --list abilities
    // -list abilities
    // -add faction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }
}