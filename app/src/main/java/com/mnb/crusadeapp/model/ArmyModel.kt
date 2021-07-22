package com.mnb.crusadeapp.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mnb.crusadeapp.data.*
import com.mnb.crusadeapp.data.Unit
import java.io.BufferedReader
import java.io.File

class ArmyModel() {

    // TODO: move logic up into view model?

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

    fun loadArmy(context: Context?, armyName: String) {
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

    fun addUnitToArmy(unit: Unit) {
        Log.d(TAG, "UNIT: ${unit.name}, ${unit.type}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
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
                        it.units = unitList.toTypedArray()
                        break
                    } else {
                        unitNum++
                    }
                }
            }
        }
    }

    fun removeUnitFromArmy(unit: Unit) {
        Log.d(TAG, "UNIT: ${unit.name}, ${unit.type}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
                for (u: Unit in it.units) {
                    if (u.name.equals(unit.name)) {
                        unitList.remove(u)
                    }
                }
                it.units = unitList.toTypedArray()
            }
        }
    }

    fun addAbilityToArmy(ability: Ability) {
        army.let {
            if (it != null) {
                val abilityList: MutableList<Ability> = it.abilities.toMutableList()
                for (a: Ability in abilityList) {
                    if (a.name.equals(ability.name)) {
                        // army abilities probably shouldn't allow multiples
                        // a.count++
                        // it.abilities = abilityList.toTypedArray()
                        return
                    }
                }
                val abilityCopy: Ability = Ability(ability.name, ability.description, ability.points, ability.power, 1)
                abilityList.add(abilityCopy)
                it.abilities = abilityList.toTypedArray()
            }
        }
    }

    fun removeAbilityFromArmy(ability: Ability) {
        army.let {
            if (it != null) {
                val abilityList: MutableList<Ability> = it.abilities.toMutableList()
                for (a: Ability in it.abilities) {
                    if (a.name.equals(ability.name)) {
                        abilityList.remove(a)
                    }
                }
                it.abilities = abilityList.toTypedArray()
            }
        }
    }

    fun addModelToUnit(unitName: String, model: Model) {
        Log.d(TAG, "ADD MODEL: ${model.name}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
                for (u: Unit in unitList) {
                    Log.d(TAG, "ADD MODEL: ${u.name} / $unitName")
                    if (u.name.equals(unitName)) {
                        val modelList: MutableList<Model> = u.models.toMutableList()
                        for (m: Model in modelList) {
                            if (m.name.equals(model.name)) {
                                m.count++
                                u.models = modelList.toTypedArray()
                                it.units = unitList.toTypedArray()
                                return
                            }
                        }
                        val modelCopy: Model = Model(model.name, model.m, model.ws, model.bs, model.s, model.t, model.w, model.a, model.ld, model.sv, model.points, model.power, 1, model.increment)
                        modelList.add(modelCopy)
                        u.models = modelList.toTypedArray()
                        it.units = unitList.toTypedArray()
                        return
                    }
                }
                Log.d(TAG, "ADD MODEL: (NOTHING ADDED)")
            } else {
                Log.d(TAG, "ADD MODEL: (ARMY IS NULL)")
            }
        }
    }

    fun removeModelFromUnit(unitName: String, model: Model) {
        Log.d(TAG, "REMOVE MODEL: ${model.name}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
                for (u: Unit in unitList) {
                    Log.d(TAG, "REMOVE MODEL: ${u.name} / $unitName")
                    if (u.name.equals(unitName)) {
                        val modelList: MutableList<Model> = u.models.toMutableList()
                        for (m: Model in u.models) {
                            if (m.name.equals(model.name)) {
                                m.count--
                                if (m.count < 1) {
                                    modelList.remove(m)
                                }
                                u.models = modelList.toTypedArray()
                                it.units = unitList.toTypedArray()
                                return
                            }
                        }
                    }
                }
                Log.d(TAG, "REMOVE MODEL: (NOTHING REMOVED)")
            } else {
                Log.d(TAG, "REMOVE MODEL: (ARMY IS NULL)")
            }
        }
    }

    fun addWeaponToUnit(unitName: String, weapon: Weapon) {
        Log.d(TAG, "ADD WEAPON: ${weapon.name}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
                for (u: Unit in unitList) {
                    Log.d(TAG, "ADD WEAPON: ${u.name} / $unitName")
                    if (u.name.equals(unitName)) {
                        val weaponList: MutableList<Weapon> = u.weapons.toMutableList()
                        for (w: Weapon in weaponList) {
                            if (w.name.equals(weapon.name)) {
                                w.count++
                                u.weapons = weaponList.toTypedArray()
                                it.units = unitList.toTypedArray()
                                return
                            }
                        }
                        val weaponCopy: Weapon = Weapon(weapon.name, weapon.type, weapon.range, weapon.s, weapon.ap, weapon.d, weapon.description, weapon.points, weapon.power, 1, weapon.alt)
                        weaponList.add(weaponCopy)
                        u.weapons = weaponList.toTypedArray()
                        it.units = unitList.toTypedArray()
                        return
                    }
                }
                Log.d(TAG, "ADD WEAPON: (NOTHING ADDED)")
            } else {
                Log.d(TAG, "ADD WEAPON: (ARMY IS NULL)")
            }
        }
    }

    fun removeWeaponFromUnit(unitName: String, weapon: Weapon) {
        Log.d(TAG, "REMOVE WEAPON: ${weapon.name}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
                for (u: Unit in unitList) {
                    Log.d(TAG, "REMOVE WEAPON: ${u.name} / $unitName")
                    if (u.name.equals(unitName)) {
                        val weaponList: MutableList<Weapon> = u.weapons.toMutableList()
                        for (w: Weapon in u.weapons) {
                            if (w.name.equals(weapon.name)) {
                                w.count--
                                if (w.count < 1) {
                                    weaponList.remove(w)
                                }
                                u.weapons = weaponList.toTypedArray()
                                it.units = unitList.toTypedArray()
                                return
                            }
                        }
                    }
                }
                Log.d(TAG, "REMOVE WEAPON: (NOTHING REMOVED)")
            } else {
                Log.d(TAG, "REMOVE WEAPON: (ARMY IS NULL)")
            }
        }
    }

    fun addAbilityToUnit(unitName: String, ability: Ability) {
        Log.d(TAG, "ADD ABILITY: ${ability.name}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
                for (u: Unit in unitList) {
                    Log.d(TAG, "ADD ABILITY: ${u.name} / $unitName")
                    if (u.name.equals(unitName)) {
                        val abilityList: MutableList<Ability> = u.abilities.toMutableList()
                        for (a: Ability in abilityList) {
                            if (a.name.equals(ability.name)) {
                                a.count++
                                u.abilities = abilityList.toTypedArray()
                                it.units = unitList.toTypedArray()
                                return
                            }
                        }
                        val abilityCopy: Ability = Ability(ability.name, ability.description, ability.points, ability.power, 1)
                        abilityList.add(abilityCopy)
                        u.abilities = abilityList.toTypedArray()
                        it.units = unitList.toTypedArray()
                        return
                    }
                }
                Log.d(TAG, "ADD ABILITY: (NOTHING ADDED)")
            } else {
                Log.d(TAG, "ADD ABILITY: (ARMY IS NULL)")
            }
        }
    }

    fun removeAbilityFromUnit(unitName: String, ability: Ability) {
        Log.d(TAG, "REMOVE ABILITY: ${ability.name}")
        army.let {
            if (it != null) {
                val unitList: MutableList<Unit> = it.units.toMutableList()
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
                                it.units = unitList.toTypedArray()
                                return
                            }
                        }
                    }
                }
                Log.d(TAG, "REMOVE ABILITY: (NOTHING REMOVED)")
            } else {
                Log.d(TAG, "REMOVE ABILITY: (ARMY IS NULL)")
            }
        }
    }
}