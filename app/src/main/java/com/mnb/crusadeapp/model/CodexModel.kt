package com.mnb.crusadeapp.model

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mnb.crusadeapp.data.*
import com.mnb.crusadeapp.data.Unit
import java.io.BufferedReader
import java.io.File

class CodexModel() {

    val TAG: String = "CodexModel"

    private var codex: Codex? = null

    fun loadCodex(context: Context?, codexName: String) {
        codex.let {
            if (it != null && it.name.equals(codexName)) {
                // codex already loaded
                return
            } else {
                Log.d(TAG, "CODEX ALREADY LOADED FOR $codexName")
            }
        }

        // load codex
        context.let {
            if (it != null) {
                var gson: Gson = Gson()

                val codexFile: File = File(it.getExternalFilesDir(null), codexName + ".json")

                val codexReader: BufferedReader = codexFile.bufferedReader()
                val codexJson: String = codexReader.use {
                    it.readText()
                }
                codex = gson.fromJson(codexJson, Codex::class.java)
            } else {
                Log.d(TAG, "NO CONTEXT TO LOAD $codexName")
            }
        }
    }

    fun getCodex(): Codex? {
        return codex
    }

    fun setCodex(codex: Codex) {
        this.codex = codex
    }

    fun getUnitFromCodex(unitName: String): Unit? {
        codex.let {
            if (it != null) {
                for (u: Unit in it.units) {
                    if (u.name.equals(unitName)) {
                        return u
                    }
                }
            }
        }
        return null
    }

    fun getAbilityFromCodex(abilityName: String): Ability? {
        codex.let {
            if (it != null) {
                for (a: Ability in it.abilities) {
                    if (a.name.equals(abilityName)) {
                        return a
                    }
                }
            }
        }
        return null
    }

    fun getModelFromUnit(unitName: String, modelName: String): Model? {
        codex.let {
            if (it != null) {
                for (u: Unit in it.units) {
                    if (u.name.equals(unitName)) {
                        for (m: Model in u.models) {
                            if (m.name.equals(modelName)) {
                                return m
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    fun getWeaponFromUnit(unitName: String, weaponName: String): Weapon? {
        codex.let {
            if (it != null) {
                for (u: Unit in it.units) {
                    if (u.name.equals(unitName)) {
                        for (w: Weapon in u.weapons) {
                            if (w.name.equals(weaponName)) {
                                return w
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    fun getAbilityFromUnit(unitName: String, abilityName: String): Ability? {
        codex.let {
            if (it != null) {
                for (u: Unit in it.units) {
                    if (u.name.equals(unitName)) {
                        for (a: Ability in u.abilities) {
                            if (a.name.equals(abilityName)) {
                                return a
                            }
                        }
                    }
                }
            }
        }
        return null
    }
}