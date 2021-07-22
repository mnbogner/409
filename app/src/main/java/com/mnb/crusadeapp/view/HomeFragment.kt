package com.mnb.crusadeapp.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.mnb.crusadeapp.R
import com.mnb.crusadeapp.viewmodel.ArmyViewModel
import com.mnb.crusadeapp.viewmodel.CodexViewModel
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    val TAG: String = "HomeFragment"

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val armyModel: ArmyViewModel by activityViewModels()
    private val codexModel: CodexViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        armyModel.setContext(context)
        codexModel.setContext(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        armyModel.getArmyList().observe(viewLifecycleOwner, Observer<List<String>> { armyList ->
            populateArmyList(view, armyList)
        })
        codexModel.getCodexList().observe(viewLifecycleOwner, Observer<List<String>> { codexList ->
            populateCodexList(view, codexList)
        })

        armyModel.loadArmyList()
        codexModel.loadCodexList()
    }

    fun populateArmyList(view: View, armyList: List<String>) {
        val armyLayout: LinearLayout = view.findViewById(R.id.army_list) as LinearLayout
        armyLayout.removeAllViews()
        for (army: String in armyList) {
            Log.d(TAG, "ADD ITEM TO ARMY LIST: ${army}")
            val armyItem: View = layoutInflater.inflate(R.layout.home_army_item, null)
            val armyItemName: TextView = armyItem.findViewById(R.id.army_item_name) as TextView
            armyItemName.text = "${army}"
            val armyItemView: ImageView = armyItem.findViewById(R.id.army_item_view) as ImageView
            armyItemView.setOnClickListener {
                Log.d(TAG, "VIEW ARMY: ${army}")
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
                Log.d(TAG, "EDIT ARMY: ${army}")
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
                Log.d(TAG, "DELETE ARMY: ${army}")
                armyModel.deleteArmy(army)
            }
            armyLayout.addView(armyItem)
        }
    }

    fun populateCodexList(view: View, codexList: List<String>) {
        val codexLayout: LinearLayout = view.findViewById(R.id.codex_list) as LinearLayout
        codexLayout.removeAllViews()
        for (codex: String in codexList) {
            Log.d(TAG, "ADD ITEM TO CODEX LIST: ${codex}")
            val codexItem: View = layoutInflater.inflate(R.layout.home_codex_item, null)
            val codexItemName: TextView = codexItem.findViewById(R.id.codex_item_name) as TextView
            codexItemName.text = "${codex}"
            val codexItemNew: ImageView = codexItem.findViewById(R.id.codex_item_new) as ImageView
            codexItemNew.setOnClickListener {
                val dateFormatString: String = "hh-mm-MM-dd"
                val format: SimpleDateFormat = SimpleDateFormat(dateFormatString)
                val datePart: String = format.format(Date())
                val namePart: String = codex.replace("codex", "army")
                Log.d(TAG, "NEW ARMY: ${namePart}_${datePart}")
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
}
