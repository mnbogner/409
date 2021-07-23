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

        Log.d(TAG, "UNIT: ${unit.name}, ${unit.type}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        // 3 unit limit
        var unitNum: Int = 1;
        while (unitNum <= 3) {
            var numOk: Boolean = true
            for (u: Unit in unitList) {
                if (u.name.startsWith(unit.name) && u.name.endsWith("${unitNum}")) {
                    Log.d(TAG, "${u.name} ALREADY TAKEN")
                    numOk = false
                    break
                }
            }
            if (numOk) {
                Log.d(TAG, "NEW UNIT: ${unit.name} ${unitNum}")
                val unitCopy: Unit = Unit(
                    "${unit.name} ${unitNum}",
                    unit.type,
                    arrayOf<Model>(),
                    unit.track,
                    arrayOf<Weapon>(),
                    arrayOf<Ability>()
                )
                unitList.add(unitCopy)
                armyModel.setUnitList(unitList)
                break
            } else {
                unitNum++
            }
        }

        refreshData()
    }

    fun removeUnitFromArmy(unit: Unit) {

        Log.d(TAG, "UNIT: ${unit.name}, ${unit.type}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        for (u: Unit in unitList) {
            if (u.name.equals(unit.name)) {
                unitList.remove(u)
                armyModel.setUnitList(unitList)
                break
            }
        }

        refreshData()
    }

    fun addAbilityToArmy(ability: Ability) {

        val abilityList: MutableList<Ability> = armyModel.getAbilityList()
        for (a: Ability in abilityList) {
            if (a.name.equals(ability.name)) {
                // army abilities probably shouldn't allow multiples
                // a.count++
                // it.abilities = abilityList.toTypedArray()
                return
            }
        }
        val abilityCopy: Ability = Ability(
            ability.name,
            ability.description,
            ability.points,
            ability.power,
            1
        )
        abilityList.add(abilityCopy)
        armyModel.setAbilityList(abilityList)

        refreshData()
    }

    fun removeAbilityFromArmy(ability: Ability) {

        val abilityList: MutableList<Ability> = armyModel.getAbilityList()
        for (a: Ability in abilityList) {
            if (a.name.equals(ability.name)) {
                abilityList.remove(a)
                armyModel.setAbilityList(abilityList)
                break
            }
        }

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