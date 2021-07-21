package com.mnb.crusadeapp.view

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.mnb.crusadeapp.R
import com.mnb.crusadeapp.data.Ability
import com.mnb.crusadeapp.data.Codex
import com.mnb.crusadeapp.data.Unit
import com.mnb.crusadeapp.rules.ArmyCalculator
import com.mnb.crusadeapp.viewmodel.ArmyViewModel
import com.mnb.crusadeapp.viewmodel.CodexViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    val TAG: String = "HomeFragment"

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val codexModel: CodexViewModel by activityViewModels()
    private val armyModel: ArmyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codexModel.setContext(context)
        armyModel.setContext(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        armyModel.getArmyList().observe(viewLifecycleOwner, Observer<List<String>> { armyList ->
            Log.d(TAG, "ARMY LIST OBSERVER TRIGGERED, NUMBER OF ITEMS: " + armyList.size)
            populateArmyList(view, armyList)
        })
        codexModel.getCodexList().observe(viewLifecycleOwner, Observer<List<String>> { codexList ->
            Log.d(TAG, "CODEX LIST OBSERVER TRIGGERED, NUMBER OF ITEMS: " + codexList.size)
            populateCodexList(view, codexList)
        })

        armyModel.loadArmyList()
        codexModel.loadCodexList()

        //populateArmyList(view)
        //populateCodexList(view)

        /*
        view.findViewById<Button>(R.id.button_one)?.setOnClickListener {
            val argBundle = bundleOf(
                "codex_param" to "mechanicus_codex",
                "army_param" to "mechanicus_army",
                "action_param" to "edit"
            )
            Navigation.findNavController(view).navigate(
                R.id.action_homeFragment_to_editArmyFragment,
                argBundle
            )
        }
        view.findViewById<Button>(R.id.button_two)?.setOnClickListener {
            val argBundle = androidx.core.os.bundleOf(
                "codex_param" to "mechanicus_codex",
                "army_param" to "mechanicus_army",
                "action_param" to "view"
            )
            androidx.navigation.Navigation.findNavController(view).navigate(
                com.mnb.crusadeapp.R.id.action_homeFragment_to_editArmyFragment,
                argBundle
            )
        }
        */


        /*
        val codexList = ArrayList<String>()
        // currently looking in download directory for shared codexes
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (dir.exists()) {
            // get json codex files
            val fileList = dir.list { dir, file ->
                file.endsWith(".codex.json")
            }
            // drop suffix to get names
            codexList.addAll(fileList.map { it ->
                it.removeSuffix(".codex.json")
            })
        } else {
            System.out.println("OOPS - " + " directory " + dir.getPath() + " does not exist");
        }

         */


        /*
        for (u: Unit in armyUnitList) {
                    Log.d(TAG, "ADD ITEM TO ARMY LIST FOR ${u.name}")
                    val armyUnitItem: View = layoutInflater.inflate(R.layout.edit_army_army_item, null)
                    val armyUnitItemName: TextView = armyUnitItem.findViewById(R.id.army_item_name) as TextView
                    armyUnitItemName.text = "${u.name}"
                    val armyUnitItemPwrPts: TextView = armyUnitItem.findViewById(R.id.army_item_pwr_pts) as TextView
                    armyUnitItemPwrPts.text =  "PWR:${ArmyCalculator.getUnitPower(u)}/PTS:${ArmyCalculator.getUnitPoints(u)}"
                    val armyUnitItemText: TextView = armyUnitItem.findViewById(R.id.army_item_text) as TextView
                    armyUnitItemText.visibility = View.GONE

                    val armyUnitItemRemove: ImageView = armyUnitItem.findViewById(R.id.army_item_remove) as ImageView
                    if ("edit".equals(actionParam)) {
                        armyUnitItemRemove.setOnClickListener {
                            Log.d(TAG, "REMOVE ITEM FROM ARMY LIST: ${u.name}")
                            armyModel.removeUnitFromArmy(u)
                        }
                    } else {
                        armyUnitItemRemove.visibility = View.GONE
                    }

                    armyUnitItem.setOnClickListener {
                        val argBundle = bundleOf(
                            "codex_param" to army.codex,
                            "army_param" to army.name,
                            "unit_param" to u.name,
                            "action_param" to actionParam)
                        Navigation.findNavController(view).navigate(
                            R.id.action_editArmyFragment_to_editUnitFragment,
                            argBundle
                        )
                    }
                    armyUnitLayout.addView(armyUnitItem)
                }
         */
    }

    fun populateArmyList(view: View, armyList: List<String>) {

        /*
        val armyList = ArrayList<String>()
        // currently looking in app directory for army files
        val dir = context?.applicationContext?.getExternalFilesDir(null)
        //val dir = context?.applicationContext?.filesDir
        if (dir != null && dir.exists()) {

            val tempList = dir.list { dir, file -> true }
            if (tempList != null) {
                Log.d(TAG, "DIR: " + dir.path)
                Log.d(TAG, "TEMP LIST SIZE: " + tempList.size)
                for (s: String in tempList) {
                    Log.d(TAG, "FOUND FILE " + s)
                }
            } else {
                Log.d(TAG, "TEMP LIST IS NULL FOR " + dir.path)
                return
            }

            // get json codex files
            val fileList = dir.list { dir, file ->
                file.contains("army") && file.endsWith(".json")
            }
            if (fileList != null) {
                // drop suffix to get names
                armyList.addAll(fileList.map { it ->
                    it.removeSuffix(".json")
                })
            } else {
                Log.d(TAG, "ARMY LIST IS NULL FOR " + dir.path)
                return
            }
        } else {
            System.out.println("OOPS - " + " directory " + dir?.getPath() + " does not exist");
        }
        */

        val armyLayout: LinearLayout = view.findViewById(R.id.army_list) as LinearLayout
        armyLayout.removeAllViews()
        for (army: String in armyList) {
            Log.d(TAG, "ADD ITEM TO ARMY LIST FOR ${army}")
            val armyItem: View = layoutInflater.inflate(R.layout.home_army_item, null)
            val armyItemName: TextView = armyItem.findViewById(R.id.army_item_name) as TextView
            armyItemName.text = "${army}"
            val armyItemView: ImageView = armyItem.findViewById(R.id.army_item_view) as ImageView
            armyItemView.setOnClickListener {
                Log.d(TAG, "VIEW ARMY FOR ARMY ${army}")
                val argBundle = bundleOf(
                    "codex_param" to "foo",
                    "army_param" to army,
                    "action_param" to "view"
                )
                Navigation.findNavController(view).navigate(
                    R.id.action_homeFragment_to_editArmyFragment,
                    argBundle
                )
            }
            val armyItemEdit: ImageView = armyItem.findViewById(R.id.army_item_edit) as ImageView
            armyItemEdit.setOnClickListener {
                Log.d(TAG, "EDIT ARMY FOR ARMY ${army}")
                val argBundle = bundleOf(
                    "codex_param" to "foo",
                    "army_param" to army,
                    "action_param" to "edit"
                )
                Navigation.findNavController(view).navigate(
                    R.id.action_homeFragment_to_editArmyFragment,
                    argBundle
                )
            }
            val armyItemDelete: ImageView = armyItem.findViewById(R.id.army_item_delete) as ImageView
            armyItemDelete.setOnClickListener {
                Log.d(TAG, "DELETE ARMY FOR ARMY ${army}")
                armyModel.deleteArmy(army)
            }
            armyLayout.addView(armyItem)
        }
    }

    fun populateCodexList(view: View, codexList: List<String>) {

        /*
        val codexList = ArrayList<String>()
        // currently looking in download directory for codex files
        val dir = context?.applicationContext?.getExternalFilesDir(null)
        //val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (dir != null && dir.exists()) {

            val tempList = dir.list { dir, file -> true }
            if (tempList != null) {
                Log.d(TAG, "DIR: " + dir.path)
                Log.d(TAG, "TEMP LIST SIZE: " + tempList.size)
                for (s: String in tempList) {
                    Log.d(TAG, "FOUND FILE " + s)
                }
            } else {
                Log.d(TAG, "TEMP LIST IS NULL FOR " + dir.path)
                return
            }

            // get json codex files
            val fileList = dir.list { dir, file ->
                file.contains("codex") && file.endsWith(".json")
            }
            if (fileList != null) {
                // drop suffix to get names
                codexList.addAll(fileList.map { it ->
                    it.removeSuffix(".json")
                })
            } else {
                Log.d(TAG, "CODEX LIST IS NULL FOR " + dir.path)
                return
            }
        } else {
            System.out.println("OOPS - " + " directory " + dir?.getPath() + " does not exist");
        }
        */

        val codexLayout: LinearLayout = view.findViewById(R.id.codex_list) as LinearLayout
        codexLayout.removeAllViews()
        for (codex: String in codexList) {
            Log.d(TAG, "ADD ITEM TO CODEX LIST FOR ${codex}")
            val codexItem: View = layoutInflater.inflate(R.layout.home_codex_item, null)
            val codexItemName: TextView = codexItem.findViewById(R.id.codex_item_name) as TextView
            codexItemName.text = "${codex}"
            val codexItemNew: ImageView = codexItem.findViewById(R.id.codex_item_new) as ImageView
            codexItemNew.setOnClickListener {
                Log.d(TAG, "NEW ARMY FOR CODEX ${codex}")

                val dateFormatString: String = "hh-mm-MM-dd"
                val format: SimpleDateFormat = SimpleDateFormat(dateFormatString)
                val datePart: String = format.format(Date())
                val namePart: String = codex.replace("codex", "army")
                Log.d(TAG, "NEW ARMY ${namePart}_${datePart}")

                val argBundle = bundleOf(
                    "codex_param" to codex,
                    "army_param" to "${namePart}_${datePart}",
                    "action_param" to "new"
                )
                Navigation.findNavController(view).navigate(
                    R.id.action_homeFragment_to_editArmyFragment,
                    argBundle
                )
            }
            codexLayout.addView(codexItem)
        }
    }

    /*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    */
}
