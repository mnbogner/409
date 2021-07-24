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
        armyModel.newArmy(armyName, codexName)
        refreshData()
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

        Log.d(TAG, "ADD UNIT: ${unit.name}, ${unit.type}")

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

        Log.d(TAG, "REMOVE UNIT: ${unit.name}, ${unit.type}")

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

        Log.d(TAG, "ADD ABILITY: ${ability.name}")

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

        Log.d(TAG, "REMOVE ABILITY: ${ability.name}")

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

        Log.d(TAG, "ADD MODEL: ${model.name}, ${unitName}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        for (u: Unit in unitList) {
            if (u.name.equals(unitName)) {
                var modelToAdd: Model = Model(model.name, model.m, model.ws, model.bs, model.s, model.t, model.w, model.a, model.ld, model.sv, model.points, model.power, 1, model.increment)
                val modelList: MutableList<Model> = u.models.toMutableList()
                for (m: Model in modelList) {
                    if (m.name.equals(model.name)) {
                        m.count++
                        modelToAdd = m
                        modelList.remove(m)
                        break
                    }
                }
                modelList.add(modelToAdd)
                u.models = modelList.toTypedArray()
                armyModel.setUnitList(unitList)
                break
            }
        }

        refreshData()
    }

    fun removeModelFromUnit(unitName: String, model: Model) {

        Log.d(TAG, "REMOVE MODEL: ${model.name}, ${unitName}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        for (u: Unit in unitList) {
            if (u.name.equals(unitName)) {
                val modelList: MutableList<Model> = u.models.toMutableList()
                for (m: Model in u.models) {
                    if (m.name.equals(model.name)) {
                        m.count--
                        if (m.count < 1) {
                            modelList.remove(m)
                        }
                        u.models = modelList.toTypedArray()
                        armyModel.setUnitList(unitList)
                        break
                    }
                }
                break
            }
        }

        refreshData()
    }

    fun addWeaponToUnit(unitName: String, weapon: Weapon) {

        Log.d(TAG, "ADD WEAPON: ${weapon.name}, ${unitName}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        for (u: Unit in unitList) {
            if (u.name.equals(unitName)) {
                var weaponCopy: Weapon = Weapon(weapon.name, weapon.type, weapon.range, weapon.s, weapon.ap, weapon.d, weapon.description, weapon.points, weapon.power, 1, weapon.alt)
                val weaponList: MutableList<Weapon> = u.weapons.toMutableList()
                for (w: Weapon in weaponList) {
                    if (w.name.equals(weapon.name)) {
                        w.count++
                        weaponCopy = w
                        weaponList.remove(w)
                        break
                    }
                }
                weaponList.add(weaponCopy)
                u.weapons = weaponList.toTypedArray()
                armyModel.setUnitList(unitList)
                break
            }
        }

        refreshData()
    }

    fun removeWeaponFromUnit(unitName: String, weapon: Weapon) {

        Log.d(TAG, "REMOVE WEAPON: ${weapon.name}, ${unitName}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        for (u: Unit in unitList) {
            if (u.name.equals(unitName)) {
                val weaponList: MutableList<Weapon> = u.weapons.toMutableList()
                for (w: Weapon in u.weapons) {
                    if (w.name.equals(weapon.name)) {
                        w.count--
                        if (w.count < 1) {
                            weaponList.remove(w)
                        }
                        u.weapons = weaponList.toTypedArray()
                        armyModel.setUnitList(unitList)
                        break
                    }
                }
                break
            }
        }

        refreshData()
    }

    fun addAbilityToUnit(unitName: String, ability: Ability) {

        Log.d(TAG, "ADD ABILITY: ${ability.name}, ${unitName}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        for (u: Unit in unitList) {
            if (u.name.equals(unitName)) {
                var abilityToAdd: Ability = Ability(ability.name, ability.description, ability.points, ability.power, 1)
                val abilityList: MutableList<Ability> = u.abilities.toMutableList()
                for (a: Ability in abilityList) {
                    if (a.name.equals(ability.name)) {
                        a.count++
                        abilityToAdd = a
                        abilityList.remove(a)
                        break
                    }
                }
                abilityList.add(abilityToAdd)
                u.abilities = abilityList.toTypedArray()
                armyModel.setUnitList(unitList)
                break
            }
        }

        refreshData()
    }

    fun removeAbilityFromUnit(unitName: String, ability: Ability) {

        Log.d(TAG, "REMOVE ABILITY: ${ability.name}, ${unitName}")

        val unitList: MutableList<Unit> = armyModel.getUnitList()
        for (u: Unit in unitList) {
            Log.d(TAG, "REMOVE ABILITY: ${u.name} / $unitName")
            if (u.name.equals(unitName)) {
                val abilityList: MutableList<Ability> = u.abilities.toMutableList()
                for (a: Ability in u.abilities) {
                    if (a.name.equals(ability.name)) {
                        a.count--
                        if (a.count < 1) {
                            abilityList.remove(a)
                        }
                        u.abilities = abilityList.toTypedArray()
                        armyModel.setUnitList(unitList)
                        break
                    }
                }
                break
            }
        }

        refreshData()
    }
}