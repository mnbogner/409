package com.mnb.crusadeapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnb.crusadeapp.data.*
import com.mnb.crusadeapp.data.Unit
import com.mnb.crusadeapp.model.ArmyModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ArmyViewModel() : ViewModel() {

    val TAG: String = "ArmyViewModel"

    private val armyData: MutableLiveData<Army> = MutableLiveData<Army>()
    private val armyList: MutableLiveData<List<String>> = MutableLiveData<List<String>>()
    private val armyModel: ArmyModel = ArmyModel()

    private var context: Context? = null

    fun setContext(context: Context?) {
        this.context = context
    }

    fun refreshData() {
        armyModel.getArmy().let {
            if (it != null) {
                Log.d(TAG, "REFRESH ARMY")
                armyData.postValue(it)
            } else {
                Log.d(TAG, "NO ARMY DATA TO REFRESH")
            }
        }
    }

    fun getArmyData(): LiveData<Army> {
        return armyData
    }

    fun getArmyList(): LiveData<List<String>> {
        return armyList
    }

    fun newArmy(armyName: String, codexName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            armyModel.newArmy(armyName, codexName)
            refreshData()
        }
    }

    fun loadArmy(armyName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            armyModel.loadArmy(context, armyName)
            refreshData()
        }
    }

    fun saveArmy() {
        viewModelScope.launch(Dispatchers.IO) {
            armyModel.saveArmy(context)
            loadArmyList()
        }
    }

    fun deleteArmy(armyName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            armyModel.deleteArmy(context, armyName)
            loadArmyList()
        }
    }

    fun loadArmyList() {

        Log.d(TAG, "LOAD ARMY LIST")

        val dir = context?.applicationContext?.getExternalFilesDir(null)
        if (dir != null && dir.exists()) {

            // get json codex files
            val fileList = dir.list { dir, file ->
                file.contains("army") && file.endsWith(".json")
            }
            if (fileList != null) {
                // drop suffix to get names
                armyList.postValue(fileList.map { it ->
                    it.removeSuffix(".json")
                })
            } else {
                Log.d(TAG, "ARMY LIST IS NULL FOR " + dir.path)
                return
            }
        } else {
            System.out.println("OOPS - " + " directory " + dir?.getPath() + " does not exist");
        }
    }

    fun addUnitToArmy(unit: Unit) {
        armyModel.addUnitToArmy(unit)
        refreshData()
    }

    fun removeUnitFromArmy(unit: Unit) {
        armyModel.removeUnitFromArmy(unit)
        refreshData()
    }

    fun addAbilityToArmy(ability: Ability) {
        armyModel.addAbilityToArmy(ability)
        refreshData()
    }

    fun removeAbilityFromArmy(ability: Ability) {
        armyModel.removeAbilityFromArmy(ability)
        refreshData()
    }

    fun addModelToUnit(unitName: String, model: Model) {
        armyModel.addModelToUnit(unitName, model)
        refreshData()
    }

    fun removeModelFromUnit(unitName: String, model: Model) {
        armyModel.removeModelFromUnit(unitName, model)
        refreshData()
    }

    fun addWeaponToUnit(unitName: String, weapon: Weapon) {
        Log.d(TAG, "ADD WEAPON: (NOTHING ADDED)")
        armyModel.addWeaponToUnit(unitName, weapon)
        refreshData()
    }

    fun removeWeaponFromUnit(unitName: String, weapon: Weapon) {
        Log.d(TAG, "ADD WEAPON: (NOTHING ADDED)")
        armyModel.removeWeaponFromUnit(unitName, weapon)
        refreshData()
    }

    fun addAbilityToUnit(unitName: String, ability: Ability) {
        armyModel.addAbilityToUnit(unitName, ability)
        refreshData()
    }

    fun removeAbilityFromUnit(unitName: String, ability: Ability) {
        armyModel.removeAbilityFromUnit(unitName, ability)
        refreshData()
    }
}