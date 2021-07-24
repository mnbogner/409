package com.mnb.crusadeapp.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mnb.crusadeapp.data.*
import com.mnb.crusadeapp.data.Unit
import java.io.BufferedReader
import java.io.File

class ArmyModel() {

    val TAG: String = "ArmyModel"

    private var army: Army? = null

    fun newArmy(armyName: String, codexName: String) {
        army.let {
            if (it != null && it.name.equals(armyName)) {
                // army already created
                return
            }
        }

        // create empty army
        army = Army(armyName, codexName, arrayOf<Unit>(), arrayOf<Ability>())
    }

    suspend fun loadArmy(context: Context?, armyName: String) {
        army.let {
            if (it != null && it.name.equals(armyName)) {
                // army already loaded
                return
            }
        }

        // load army
        context.let {
            if (it != null) {
                var gson: Gson = Gson()

                val armyFile: File = File(it.getExternalFilesDir(null), armyName + ".json")

                val armyReader: BufferedReader = armyFile.bufferedReader()
                val armyJson: String = armyReader.use {
                    it.readText()
                }
                army = gson.fromJson(armyJson, Army::class.java)
            } else {
                Log.d(TAG, "NO CONTEXT TO LOAD $armyName")
            }
        }
    }

    suspend fun saveArmy(context: Context?) {
        army.let {
            if (it == null) {
                // nothing to save
                return
            }
        }

        context.let {
            val baseName = army?.name
            if (it != null && baseName != null) {
                // save army
                var gson: Gson = Gson()

                Log.d(TAG, "SAVING ${baseName}.json")

                val armyFile: File = File(it.getExternalFilesDir(null), "${baseName}.json")

                val armyJson: String = gson.toJson(army)
                armyFile.writeText(armyJson)
            }
        }
    }

    suspend fun deleteArmy(context: Context?, armyName: String) {

        context.let {
            if (it != null) {
                // delete army
                var gson: Gson = Gson()

                Log.d(TAG, "DELETING ${armyName}.json")

                val armyFile: File = File(it.getExternalFilesDir(null), "${armyName}.json")

                if (armyFile != null && armyFile.exists()) {
                    armyFile.delete()
                }
            }
        }
    }

    fun getArmy(): Army? {
        return army
    }

    fun setArmy(army: Army) {
        this.army = army
    }

    fun getUnitList(): MutableList<Unit> {
        army.let {
            if (it != null) {
                return it.units.toMutableList()
            }
        }
        return mutableListOf<Unit>()
    }

    fun setUnitList(unitList: List<Unit>) {
        army.let {
            if (it != null) {
                it.units = unitList.toTypedArray()
            }
        }
    }

    fun getAbilityList(): MutableList<Ability> {
        army.let {
            if (it != null) {
                return it.abilities.toMutableList()
            }
        }
        return mutableListOf<Ability>()
    }

    fun setAbilityList(abilityList: List<Ability>) {
        army.let {
            if (it != null) {
                it.abilities = abilityList.toTypedArray()
            }
        }
    }
}