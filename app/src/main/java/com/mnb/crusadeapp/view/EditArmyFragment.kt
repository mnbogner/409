package com.mnb.crusadeapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mnb.crusadeapp.R
import com.mnb.crusadeapp.data.Ability
import com.mnb.crusadeapp.data.Army
import com.mnb.crusadeapp.data.Codex
import com.mnb.crusadeapp.data.Unit
import com.mnb.crusadeapp.rules.ArmyCalculator
import com.mnb.crusadeapp.viewmodel.ArmyViewModel
import com.mnb.crusadeapp.viewmodel.CodexViewModel

class EditArmyFragment : Fragment() {

    val TAG: String = "EditArmyFragment"

    private var codexParam: String? = null
    private var armyParam: String? = null
    private var actionParam: String? = null

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val codexModel: CodexViewModel by activityViewModels()
    private var codexLoaded: Boolean = false
    private val armyModel: ArmyViewModel by activityViewModels()
    private var armyLoaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codexModel.setContext(context)
        armyModel.setContext(context)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // in here you can do logic when backPress is clicked
                Log.d(TAG, "BACK PRESSED")
                if ("view".equals(actionParam)) {
                    Log.d(TAG, "JUST VIEWING, DON'T SAVE")
                } else {
                    val armyName: String? = armyModel.getArmyData().value?.name
                    if (armyName != null) {
                        Log.d(TAG, "SAVE ARMY: " + armyName)
                        armyModel.saveArmy()
                    } else {
                        Log.d(TAG, "CAN'T SAVE, NAME NULL")
                    }
                }
                findNavController().popBackStack()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_army_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codexParam = arguments?.getString("codex_param")
        armyParam = arguments?.getString("army_param")
        actionParam = arguments?.getString("action_param")

        for (s: String in requireArguments().keySet()) {
            Log.d(TAG, "BUNDLE(2): $s")
        }
        Log.d(TAG, "ARGUMENTS(2): $codexParam, $armyParam")

        // set up observers
        armyModel.getArmyData().observe(viewLifecycleOwner, Observer<Army> { army ->

            armyLoaded = true

            // load corresponding codex if needed
            // TODO: don't load codex on view
            if ("view".equals(actionParam) || "edit".equals(actionParam)) {
                if (codexLoaded) {
                    Log.d(TAG, "CODEX ALREADY LOADED")
                } else {
                    Log.d(TAG, "LOAD CODEX ${army.codex}")
                    codexModel.loadCodex(army.codex)
                }
            }

            // Update the UI
            val armyName: TextView = view.findViewById(R.id.army_name) as TextView
            armyName.text = army.name
            val armyPwrPts: TextView = view.findViewById(R.id.army_pwr_pts) as TextView
            armyPwrPts.text = "PWR:${ArmyCalculator.getArmyPower(army)}/PTS:${ArmyCalculator.getArmyPoints(army)}"
            val armyUnitList: List<Unit> = army.units.sorted()
            val armyUnitLayout: LinearLayout = view.findViewById(R.id.army_unit_list) as LinearLayout
            armyUnitLayout.removeAllViews()
            if (armyUnitList != null && armyUnitList.isNotEmpty()) {
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
                    if (!"view".equals(actionParam)) {
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
            } else {
                Log.d(TAG, "ARMY UNIT LIST IS NULL OR EMPTY")
            }
            val armyAbilityList: List<Ability> = army.abilities.sorted()
            val armyAbilityLayout: LinearLayout = view.findViewById(R.id.army_ability_list) as LinearLayout
            armyAbilityLayout.removeAllViews()
            if (armyAbilityList != null && armyAbilityList.isNotEmpty()) {
                for (a: Ability in armyAbilityList) {
                    Log.d(TAG, "ADD ITEM TO ARMY LIST FOR ${a.name}")
                    val armyAbilityItem: View = layoutInflater.inflate(R.layout.edit_army_army_item, null)
                    val armyAbilityItemName: TextView = armyAbilityItem.findViewById(R.id.army_item_name) as TextView
                    armyAbilityItemName.text = "${a.name}"
                    val armyAbilityItemPwrPts: TextView = armyAbilityItem.findViewById(R.id.army_item_pwr_pts) as TextView
                    armyAbilityItemPwrPts.text = ""
                    val armyAbilityItemText: TextView = armyAbilityItem.findViewById(R.id.army_item_text) as TextView
                    armyAbilityItemText.text = "${a.description}"

                    val armyAbilityItemRemove: ImageView = armyAbilityItem.findViewById(R.id.army_item_remove) as ImageView
                    if (!"view".equals(actionParam)) {
                        armyAbilityItemRemove.setOnClickListener {
                            Log.d(TAG, "REMOVE ABILITY FROM ARMY LIST: ${a.name}")
                            armyModel.removeAbilityFromArmy(a)
                        }
                    } else {
                        armyAbilityItemRemove.visibility = View.GONE
                    }

                    armyAbilityLayout.addView(armyAbilityItem)
                }
            } else {
                Log.d(TAG, "ARMY UNIT LIST IS NULL OR EMPTY")
            }
        })
        codexModel.getCodexData().observe(viewLifecycleOwner, Observer<Codex> { codex ->

            codexLoaded = true

            // load corresponding codex if needed
            if ("new".equals(actionParam)) {
                if (armyLoaded) {
                    Log.d(TAG, "ARMY ALREADY LOADED")
                } else {
                    Log.d(TAG, "NEW ARMY ${armyParam}")
                    armyModel.newArmy("${armyParam}", "${codexParam}")
                }
            }

            // Update the UI
            val codexUnitLayout: LinearLayout = view.findViewById(R.id.codex_unit_list) as LinearLayout
            val codexAbilityLayout: LinearLayout = view.findViewById(R.id.codex_ability_list) as LinearLayout
            if (!"view".equals(actionParam)) {
                val codexName: TextView = view.findViewById(R.id.codex_name) as TextView
                codexName.text = codex.name
                val codexUnitList: List<Unit> = codex.units.sorted()
                if (codexUnitList != null && codexUnitList.isNotEmpty()) {
                    codexUnitLayout.removeAllViews()
                    for (u: Unit in codexUnitList) {
                        Log.d(TAG, "ADD ITEM TO CODEX LIST FOR ${u.name}")

                        val codexUnitItem: View =
                            layoutInflater.inflate(R.layout.edit_army_codex_item, null)
                        val codexUnitItemName: TextView =
                            codexUnitItem.findViewById(R.id.codex_item_name) as TextView
                        codexUnitItemName.text = "${u.name}"
                        val codexUnitItemPwrPts: TextView =
                            codexUnitItem.findViewById(R.id.codex_item_pwr_pts) as TextView
                        codexUnitItemPwrPts.text = "PWR:${ArmyCalculator.getDefaultPower(u)}/PTS:${
                            ArmyCalculator.getDefaultPoints(u)
                        }"

                        val codexUnitItemAdd: ImageView =
                            codexUnitItem.findViewById(R.id.codex_item_add) as ImageView
                        codexUnitItemAdd.setOnClickListener {
                            Log.d(TAG, "ADD ITEM FROM ARMY LIST: ${u.name}")
                            armyModel.addUnitToArmy(u)
                        }

                        codexUnitLayout.addView(codexUnitItem)
                    }
                } else {
                    Log.d(TAG, "CODEX UNIT LIST IS NULL OR EMPTY")
                }
                val codexAbilityList: List<Ability> = codex.abilities.sorted()
                if (codexAbilityList != null && codexAbilityList.isNotEmpty()) {
                    codexAbilityLayout.removeAllViews()
                    for (a: Ability in codexAbilityList) {
                        Log.d(TAG, "ADD ITEM TO CODEX LIST FOR ${a.name}")

                        val codexAbilityItem: View =
                            layoutInflater.inflate(R.layout.edit_army_codex_item, null)
                        val codexAbilityItemName: TextView =
                            codexAbilityItem.findViewById(R.id.codex_item_name) as TextView
                        codexAbilityItemName.text = "${a.name}"
                        val codexAbilityItemPwrPts: TextView =
                            codexAbilityItem.findViewById(R.id.codex_item_pwr_pts) as TextView
                        codexAbilityItemPwrPts.text = ""

                        val codexAbilityItemAdd: ImageView =
                            codexAbilityItem.findViewById(R.id.codex_item_add) as ImageView
                        codexAbilityItemAdd.setOnClickListener {
                            Log.d(TAG, "ADD ABILITY FROM ARMY LIST: ${a.name}")
                            armyModel.addAbilityToArmy(a)
                        }

                        codexAbilityLayout.addView(codexAbilityItem)
                    }
                } else {
                    Log.d(TAG, "CODEX UNIT LIST IS NULL OR EMPTY")
                }
            } else {
                val codexHeader: ConstraintLayout = view.findViewById(R.id.codex_header) as ConstraintLayout
                val codexUnitHeader: TextView = view.findViewById(R.id.codex_units) as TextView
                val codexAbilityHeader: TextView = view.findViewById(R.id.codex_abilities) as TextView
                codexHeader.visibility = View.GONE
                codexUnitHeader.visibility = View.GONE
                codexUnitLayout.visibility = View.GONE
                codexAbilityHeader.visibility = View.GONE
                codexAbilityLayout.visibility = View.GONE
            }
        })
        // load data
        if ("new".equals(actionParam)) {

            codexModel.loadCodex(codexParam ?: "foo")

        } else if ("edit".equals(actionParam)) {

            armyModel.loadArmy(armyParam ?: "foo")

        } else if ("view".equals(actionParam)) {

            armyModel.loadArmy(armyParam ?: "foo")

        } else {
            Log.d(TAG, "UNKNOWN ACTION: " + actionParam)
        }
    }
}