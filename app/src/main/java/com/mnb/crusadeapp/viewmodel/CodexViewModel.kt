package com.mnb.crusadeapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnb.crusadeapp.data.*
import com.mnb.crusadeapp.data.Unit
import com.mnb.crusadeapp.model.CodexModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodexViewModel() : ViewModel() {

    val TAG: String = "CodexViewModel"

    private val codexData: MutableLiveData<Codex> = MutableLiveData<Codex>()
    private val codexList: MutableLiveData<List<String>> = MutableLiveData<List<String>>()
    private val codexModel: CodexModel = CodexModel()

    private var context: Context? = null

    fun setContext(context: Context?) {
        this.context = context
    }

    fun refreshData() {
        codexModel.getCodex().let {
            if (it != null) {
                Log.d(TAG, "REFRESH CODEX")
                codexData.postValue(it)
            } else {
                Log.d(TAG, "NO CODEX DATA TO REFRESH")
            }
        }
    }

    fun getCodexData(): LiveData<Codex> {
        return codexData
    }

    fun getCodexList(): LiveData<List<String>> {
        return codexList
    }

    fun loadCodex(codexName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            codexModel.loadCodex(context, codexName)
            refreshData()
        }
    }

    fun loadCodexList() {

        Log.d(TAG, "LOAD CODEX LIST")

        // currently looking in download directory for codex files
        val dir = context?.applicationContext?.getExternalFilesDir(null)
        if (dir != null && dir.exists()) {

            // get json codex files
            val fileList = dir.list { dir, file ->
                file.contains("codex") && file.endsWith(".json")
            }
            if (fileList != null) {
                // drop suffix to get names
                codexList.postValue(fileList.map { it ->
                    it.removeSuffix(".json")
                })
            } else {
                Log.d(TAG, "CODEX LIST IS NULL FOR " + dir.path)
                return
            }
        } else {
            System.out.println("OOPS - " + " directory " + dir?.getPath() + " does not exist");
        }
    }

    fun getUnitFromCodex(unitName: String): Unit? {
        return codexModel.getUnitFromCodex(unitName)
    }

    fun getAbilityFromCodex(abilityName: String): Ability? {
        return codexModel.getAbilityFromCodex((abilityName))
    }

    fun getModelFromUnit(unitName: String, modelName: String): Model? {
        return codexModel.getModelFromUnit(unitName, modelName)
    }

    fun getWeaponFromUnit(unitName: String, weaponName: String): Weapon? {
        return codexModel.getWeaponFromUnit(unitName, weaponName)
    }

    fun getAbilityFromUnit(unitName: String, abilityName: String): Ability? {
        return codexModel.getAbilityFromUnit(unitName, abilityName)
    }
}