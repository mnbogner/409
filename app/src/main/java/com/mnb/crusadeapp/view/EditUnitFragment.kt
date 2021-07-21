package com.mnb.crusadeapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mnb.crusadeapp.R
import com.mnb.crusadeapp.data.*
import com.mnb.crusadeapp.data.Unit
import com.mnb.crusadeapp.databinding.EditArmyModelBinding
import com.mnb.crusadeapp.databinding.EditArmyWeaponBinding
import com.mnb.crusadeapp.rules.ArmyCalculator
import com.mnb.crusadeapp.viewmodel.ArmyViewModel
import com.mnb.crusadeapp.viewmodel.CodexViewModel

class EditUnitFragment : Fragment() {
    val TAG: String = "EditUnitFragment"

    private var codexParam: String? = null
    private var armyParam: String? = null
    private var unitParam: String? = null
    private var actionParam: String? = null

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val codexModel: CodexViewModel by activityViewModels()
    private val armyModel: ArmyViewModel by activityViewModels()

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_unit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codexParam = arguments?.getString("codex_param")
        armyParam = arguments?.getString("army_param")
        unitParam = arguments?.getString("unit_param")
        actionParam = arguments?.getString("action_param")

        for (s: String in requireArguments().keySet()) {
            Log.d(TAG, "BUNDLE(2): $s")
        }
        Log.d(TAG, "ARGUMENTS(2): $codexParam, $armyParam, $unitParam")

        // set up observers
        armyModel.getArmyData().observe(viewLifecycleOwner, Observer<Army> { army ->
            // Update the UI
            Log.d(TAG, "ARMY OBSERVER TRIGGERED")
            for (u: Unit in army.units) {
                Log.d(TAG, "CHECK: ${u.name} / $unitParam")
                if (unitParam?.equals(u.name) ?: false) {
                    setupUnitLists(u, view)
                }
            }
        })
        codexModel.getCodexData().observe(viewLifecycleOwner, Observer<Codex> { codex ->
            // Update the UI
            Log.d(TAG, "CODEX OBSERVER TRIGGERED")
            for (u: Unit in codex.units) {
                Log.d(TAG, "CHECK: ${u.name} / $unitParam")
                if (unitParam?.startsWith(u.name) ?: false) {
                    setupCodexLists(u, view)
                }
            }
        })

        // load data
        codexModel.loadCodex(codexParam ?: "foo")
        armyModel.loadArmy(armyParam ?: "foo")

    }

    fun setupUnitLists(unit: Unit, view: View) {
        Log.d(TAG, "SETUP: ${unit.name}")


        val inflater = activity?.layoutInflater
        val view = view

        // bail out if inflater/view aren't available (ie: before onCreateView is called)
        if (inflater == null) {
            System.out.println(" state observer called, but inflater is null")
            return
        }
        if (view == null) {
            System.out.println(" state observer called, but view is null")
            return
        }

        val unitName: TextView = view.findViewById(R.id.unit_name) as TextView
        unitName.text = unit.name
        val unitPwrPts: TextView = view.findViewById(R.id.unit_pwr_pts) as TextView
        unitPwrPts.text = "PWR:${ArmyCalculator.getUnitPower(unit)}/PTS:${ArmyCalculator.getUnitPoints(unit)}"

        val unitModelList: List<Model> = unit.models.sorted()
        val unitModelLayout: LinearLayout =
            view.findViewById(R.id.unit_model_list) as LinearLayout
        unitModelLayout.removeAllViews()
        if (unitModelList != null && unitModelList.isNotEmpty()) {

            // use item with default values as header row
            val header = EditArmyModelBinding.inflate(inflater)
            unitModelLayout.addView(header.root)

            for (m: Model in unitModelList) {
                Log.d(TAG, "ADD ITEM TO unit LIST FOR ${m.name}")


                val binding = EditArmyModelBinding.inflate(inflater)
                var nameString = m.name
                if (m.count > 1) {
                    nameString = nameString + " x" + m.count
                }
                binding.modelPoints = (m.points * m.count).toString()
                binding.modelPower = (m.power * m.count).toString()
                binding.modelName = nameString
                binding.modelMove = m.m
                binding.modelWeapon = m.ws
                binding.modelBallistic = m.bs
                binding.modelStrength = m.s
                binding.modelToughness = m.t
                binding.modelWounds = m.w
                binding.modelAttacks = m.a
                binding.modelLeadership = m.ld
                binding.modelSave = m.sv
                unitModelLayout.addView(binding.root)


                /*
                val unitModelItem: View =
                    layoutInflater.inflate(R.layout.edit_army_codex_unit, null)
                val unitModelItemName: TextView =
                    unitModelItem.findViewById(R.id.codex_unit_item_name) as TextView
                unitModelItemName.text = "${m.name} x${m.count}"

                unitModelItem.setOnClickListener {
                    Log.d(TAG, "UNIT MODEL CLICK")
                    armyModel.removeModelFromUnit(unitParam ?: "foo", m)
                }

                unitModelLayout.addView(unitModelItem)
                */
            }
        } else {
            Log.d(TAG, "unit MODEL LIST IS NULL OR EMPTY")
        }


        val unitWeaponList: List<Weapon> = unit.weapons.sorted()
        val unitWeaponLayout: LinearLayout =
            view.findViewById(R.id.unit_weapon_list) as LinearLayout
        unitWeaponLayout.removeAllViews()
        if (unitWeaponList != null && unitWeaponList.isNotEmpty()) {


            // use item with default values as header row
            val header = EditArmyWeaponBinding.inflate(inflater)
            unitWeaponLayout.addView(header.root)


            for (w: Weapon in unitWeaponList) {
                Log.d(TAG, "ADD ITEM TO unit LIST FOR ${w.name}")


                val binding = EditArmyWeaponBinding.inflate(inflater)
                var nameString = w.name
                if (w.count > 1) {
                    if (nameString.contains(" - ")) {
                        val nameParts: List<String> = nameString.split(" - ")
                        nameString = nameParts[0] + " x" + w.count + " - " + nameParts[1]
                    } else {
                        nameString = nameString + " x" + w.count
                    }
                }
                binding.weaponPoints = (w.points * w.count).toString()
                binding.weaponPower = (w.power * w.count).toString()
                binding.weaponName = nameString
                binding.weaponRange = w.range
                binding.weaponType = w.type
                binding.weaponStrength = w.s
                binding.weaponPenetration = w.ap
                binding.weaponDamage = w.d
                binding.weaponDescription = w.description
                unitWeaponLayout.addView(binding.root)

                if (w.alt.size > 0) {
                    for (alt: Weapon in w.alt) {
                        val altBinding = EditArmyWeaponBinding.inflate(inflater)
                        var altString = alt.name
                        if (w.count > 1) {
                            if (altString.contains(" - ")) {
                                val altParts: List<String> = altString.split(" - ")
                                altString = altParts[0] + " x" + w.count + " - " + altParts[1]
                            } else {
                                altString = altString + " x" + w.count
                            }
                        }
                        altBinding.weaponPoints = (alt.points * alt.count).toString()
                        altBinding.weaponPower = (alt.power * alt.count).toString()
                        altBinding.weaponName = altString
                        altBinding.weaponRange = alt.range
                        altBinding.weaponType = alt.type
                        altBinding.weaponStrength = alt.s
                        altBinding.weaponPenetration = alt.ap
                        altBinding.weaponDamage = alt.d
                        altBinding.weaponDescription = alt.description
                        unitWeaponLayout.addView(altBinding.root)
                    }
                }


                /*
                val unitWeaponItem: View =
                    layoutInflater.inflate(R.layout.edit_army_codex_unit, null)
                val unitWeaponItemName: TextView =
                    unitWeaponItem.findViewById(R.id.codex_unit_item_name) as TextView
                unitWeaponItemName.text = "${w.name} x${w.count}"

                unitWeaponItem.setOnClickListener {
                    Log.d(TAG, "UNIT WEAPON CLICK")
                    armyModel.removeWeaponFromUnit(unitParam ?: "foo", w)
                }

                unitWeaponLayout.addView(unitWeaponItem)
                */
            }
        } else {
            Log.d(TAG, "unit WEAPON LIST IS NULL OR EMPTY")
        }
        val unitAbilityList: List<Ability> = unit.abilities.sorted()
        val unitAbilityLayout: LinearLayout =
            view.findViewById(R.id.unit_ability_list) as LinearLayout
        unitAbilityLayout.removeAllViews()
        if (unitAbilityList != null && unitAbilityList.isNotEmpty()) {
            for (a: Ability in unitAbilityList) {
                Log.d(TAG, "ADD ITEM TO unit LIST FOR ${a.name}")

                val armyAbilityItem: View = layoutInflater.inflate(R.layout.edit_unit_army_item, null)
                val armyAbilityItemName: TextView = armyAbilityItem.findViewById(R.id.army_item_name) as TextView
                if (a.count > 1) {
                    armyAbilityItemName.text = "${a.name} x${a.count}"
                } else {
                    armyAbilityItemName.text = "${a.name}"
                }
                val armyAbilityItemPwrPts: TextView = armyAbilityItem.findViewById(R.id.army_item_pwr_pts) as TextView
                if (a.power > 0 || a.points > 0) {
                    armyAbilityItemPwrPts.text = "PWR:${a.power}/PTS:${a.points}"
                } else {
                    armyAbilityItemPwrPts.text = ""
                }
                val armyAbilityItemText: TextView = armyAbilityItem.findViewById(R.id.army_item_text) as TextView
                armyAbilityItemText.text = a.description

                /*
                val unitAbilityItem: View =
                    layoutInflater.inflate(R.layout.edit_army_codex_unit, null)
                val unitAbilityItemName: TextView =
                    unitAbilityItem.findViewById(R.id.codex_unit_item_name) as TextView
                unitAbilityItemName.text = "${a.name} x${a.count}"
                */

                /*
                unitAbilityItem.setOnClickListener {
                    Log.d(TAG, "UNIT ABILITY CLICK")
                    armyModel.removeAbilityFromUnit(unitParam ?: "foo", a)
                }
                */

                unitAbilityLayout.addView(armyAbilityItem)
            }
        } else {
            Log.d(TAG, "unit ABILITY LIST IS NULL OR EMPTY")
        }
    }

    fun setupCodexLists(unit: Unit, view: View) {
        Log.d(TAG, "SETUP: ${unit.name}")
        val codexName: TextView = view.findViewById(R.id.codex_name) as TextView
        codexName.text = unit.name
        val codexModelLayout: LinearLayout =
            view.findViewById(R.id.codex_model_list) as LinearLayout
        val codexWeaponLayout: LinearLayout =
            view.findViewById(R.id.codex_weapon_list) as LinearLayout
        val codexAbilityLayout: LinearLayout =
            view.findViewById(R.id.codex_ability_list) as LinearLayout
        if (!"view".equals(actionParam)) {
            val codexModelList: List<Model> = unit.models.sorted()
            if (codexModelList != null && codexModelList.isNotEmpty()) {
                codexModelLayout.removeAllViews()
                for (m: Model in codexModelList) {
                    Log.d(TAG, "ADD ITEM TO CODEX LIST FOR ${m.name}")

                    /*
                val codexModelItem: View =
                    layoutInflater.inflate(R.layout.edit_army_codex_unit, null)
                val codexModelItemName: TextView =
                    codexModelItem.findViewById(R.id.codex_unit_item_name) as TextView
                codexModelItemName.text = m.name
                */


                    val codexModelItem: View =
                        layoutInflater.inflate(R.layout.edit_unit_codex_model, null)
                    val codexModelItemName: TextView =
                        codexModelItem.findViewById(R.id.codex_model_name) as TextView
                    codexModelItemName.text = "${m.name}"
                    val codexModelItemPwrPts: TextView =
                        codexModelItem.findViewById(R.id.codex_model_pwr_pts) as TextView
                    if (m.power > 0 || m.points > 0) {
                        codexModelItemPwrPts.text =
                            "PWR:${m.power}per${m.increment}/PTS:${m.points}"
                    } else {
                        codexModelItemPwrPts.text = ""
                    }

                    val codexModelItemAdd: ImageView =
                        codexModelItem.findViewById(R.id.codex_model_add) as ImageView
                    codexModelItemAdd.setOnClickListener {
                        Log.d(TAG, "CODEX MODEL ADD CLICK")
                        armyModel.addModelToUnit(unitParam ?: "foo", m)
                    }
                    val codexModelItemRemove: ImageView =
                        codexModelItem.findViewById(R.id.codex_model_remove) as ImageView
                    codexModelItemRemove.setOnClickListener {
                        Log.d(TAG, "CODEX MODEL REMOVE CLICK")
                        armyModel.removeModelFromUnit(unitParam ?: "foo", m)
                    }

                    /*
                codexModelItem.setOnClickListener {
                    Log.d(TAG, "CODEX MODEL CLICK")
                    armyModel.addModelToUnit(unitParam ?: "foo", m)
                }
                */

                    codexModelLayout.addView(codexModelItem)
                }
            } else {
                Log.d(TAG, "CODEX MODEL LIST IS NULL OR EMPTY")
            }
            val codexWeaponList: List<Weapon> = unit.weapons.sorted()
            if (codexWeaponList != null && codexWeaponList.isNotEmpty()) {
                codexWeaponLayout.removeAllViews()
                for (w: Weapon in codexWeaponList) {
                    Log.d(TAG, "ADD ITEM TO CODEX LIST FOR ${w.name}")

                    /*
                val codexWeaponItem: View =
                    layoutInflater.inflate(R.layout.edit_army_codex_unit, null)
                val codexWeaponItemName: TextView =
                    codexWeaponItem.findViewById(R.id.codex_unit_item_name) as TextView

                val shortName = w.name.split(" - ")[0]

                codexWeaponItemName.text = shortName
                */


                    val codexWeaponItem: View =
                        layoutInflater.inflate(R.layout.edit_unit_codex_weapon, null)
                    val codexWeaponItemName: TextView =
                        codexWeaponItem.findViewById(R.id.codex_weapon_name) as TextView
                    val shortName = w.name.split(" - ")[0]
                    codexWeaponItemName.text = "${shortName}"
                    val codexWeaponItemPwrPts: TextView =
                        codexWeaponItem.findViewById(R.id.codex_weapon_pwr_pts) as TextView
                    if (w.power > 0 || w.points > 0) {
                        codexWeaponItemPwrPts.text = "PWR:${w.power}/PTS:${w.points}"
                    } else {
                        codexWeaponItemPwrPts.text = ""
                    }

                    val codexWeaponItemAdd: ImageView =
                        codexWeaponItem.findViewById(R.id.codex_weapon_add) as ImageView
                    codexWeaponItemAdd.setOnClickListener {
                        Log.d(TAG, "CODEX WEAPON ADD CLICK")
                        armyModel.addWeaponToUnit(unitParam ?: "foo", w)
                    }
                    val codexWeaponItemRemove: ImageView =
                        codexWeaponItem.findViewById(R.id.codex_weapon_remove) as ImageView
                    codexWeaponItemRemove.setOnClickListener {
                        Log.d(TAG, "CODEX WEAPON REMOVE CLICK")
                        armyModel.removeWeaponFromUnit(unitParam ?: "foo", w)
                    }

                    /*
                codexWeaponItem.setOnClickListener {
                    Log.d(TAG, "CODEX WEAPON CLICK")
                    armyModel.addWeaponToUnit(unitParam ?: "foo", w)
                }
                */

                    codexWeaponLayout.addView(codexWeaponItem)
                }
            } else {
                Log.d(TAG, "CODEX WEAPON LIST IS NULL OR EMPTY")
            }
            val codexAbilityList: List<Ability> = unit.abilities.sorted()
            if (codexAbilityList != null && codexAbilityList.isNotEmpty()) {
                codexAbilityLayout.removeAllViews()
                for (a: Ability in codexAbilityList) {
                    Log.d(TAG, "ADD ITEM TO CODEX LIST FOR ${a.name}")

                    /*
                val codexAbilityItem: View =
                    layoutInflater.inflate(R.layout.edit_army_codex_unit, null)
                val codexAbilityItemName: TextView =
                    codexAbilityItem.findViewById(R.id.codex_unit_item_name) as TextView
                codexAbilityItemName.text = a.name
                */

                    val codexAbilityItem: View =
                        layoutInflater.inflate(R.layout.edit_unit_codex_item, null)
                    val codexAbilityItemName: TextView =
                        codexAbilityItem.findViewById(R.id.codex_item_name) as TextView
                    codexAbilityItemName.text = "${a.name}"
                    val codexAbilityItemPwrPts: TextView =
                        codexAbilityItem.findViewById(R.id.codex_item_pwr_pts) as TextView
                    if (a.power > 0 || a.points > 0) {
                        codexAbilityItemPwrPts.text = "PWR:${a.power}/PTS:${a.points}"
                    } else {
                        codexAbilityItemPwrPts.text = ""
                    }

                    val codexAbilityItemAdd: ImageView =
                        codexAbilityItem.findViewById(R.id.codex_item_add) as ImageView
                    codexAbilityItemAdd.setOnClickListener {
                        Log.d(TAG, "CODEX ABILITY ADD CLICK")
                        armyModel.addAbilityToUnit(unitParam ?: "foo", a)
                    }
                    val codexAbilityItemRemove: ImageView =
                        codexAbilityItem.findViewById(R.id.codex_item_remove) as ImageView
                    codexAbilityItemRemove.setOnClickListener {
                        Log.d(TAG, "CODEX ABILITY REMOVE CLICK")
                        armyModel.removeAbilityFromUnit(unitParam ?: "foo", a)
                    }

                    /*
                codexAbilityItem.setOnClickListener {
                    Log.d(TAG, "CODEX ABILITY CLICK")
                    armyModel.addAbilityToUnit(unitParam ?: "foo", a)
                }
                */

                    codexAbilityLayout.addView(codexAbilityItem)
                }
            } else {
                Log.d(TAG, "CODEX ABILITY LIST IS NULL OR EMPTY")
            }
        } else {
            val codexHeader: ConstraintLayout = view.findViewById(R.id.codex_header) as ConstraintLayout
            val codexModelHeader: TextView = view.findViewById(R.id.codex_models) as TextView
            val codexWeaponHeader: TextView = view.findViewById(R.id.codex_weapons) as TextView
            val codexAbilityHeader: TextView = view.findViewById(R.id.codex_abilities) as TextView
            codexHeader.visibility = View.GONE
            codexModelHeader.visibility = View.GONE
            codexModelLayout.visibility = View.GONE
            codexWeaponHeader.visibility = View.GONE
            codexWeaponLayout.visibility = View.GONE
            codexAbilityHeader.visibility = View.GONE
            codexAbilityLayout.visibility = View.GONE
        }
    }
}