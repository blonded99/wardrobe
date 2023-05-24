package com.example.wardrobe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wardrobe.adapters.WardrobeBottomRecyclerViewAdapter
import com.example.wardrobe.adapters.WardrobeRecyclerViewAdapter
import com.example.wardrobe.databinding.FragmentSearchBinding
import com.example.wardrobe.viewmodel.Item
import com.example.wardrobe.viewmodel.WardrobeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment: Fragment(){
    private lateinit var binding:FragmentSearchBinding
    private val viewModel by viewModels<WardrobeViewModel> ()
    private lateinit var adapter_top:WardrobeRecyclerViewAdapter
    protected lateinit var navController: NavController

    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    val db = Firebase.firestore
    // Top(상의) Collection Ref
    val topColRef = db.collection("top")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        initView()
        return binding.root
    }
    private fun initView() {
        navController = findNavController()

        adapter_top = WardrobeRecyclerViewAdapter(viewModel, context, this)
        val adapter_bottom = WardrobeBottomRecyclerViewAdapter(viewModel,context,this)

        binding.recyclerViewTop.adapter = adapter_top
        binding.recyclerViewTop.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)

        binding.recyclerViewBottom.adapter = adapter_bottom
        binding.recyclerViewBottom.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)

        viewModel.topItemsListData.observe(viewLifecycleOwner){
            adapter_top.notifyDataSetChanged()
        }

        viewModel.bottomItemsListData.observe(viewLifecycleOwner){
            adapter_bottom.notifyDataSetChanged()
        }

        binding.searchProductBtn.setOnClickListener {
            Log.d("search", "input: ${binding.editSearch.text.toString()}")
            searchBottomList()
            searchtopList()
        }

        binding.radioGroup.addOnButtonCheckedListener { group, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when(group.checkedButtonId){
                R.id.button_top -> searchtopList()
                R.id.button_bottom -> searchBottomList()
            }
        }

        viewModel.topSelectedCheckBox.observe(viewLifecycleOwner) { position ->
            if(viewModel.topSelectedCheckBox.value != null) {
                if (viewModel.bottomSelectedCheckBox.value != null) {
                    val bundle = Bundle()
//                    bundle.putInt("topIndex", viewModel.topSelectedCheckBox.value!!)
//                    bundle.putInt("bottomIndex", viewModel.bottomSelectedCheckBox.value!!)
                    bundle.putString("topRef",viewModel.topItems[viewModel.topSelectedCheckBox.value!!].clothesImageUrl)
                    bundle.putString("bottomRef",viewModel.bottomItems[viewModel.bottomSelectedCheckBox.value!!].clothesImageUrl)
                    findNavController().navigate(R.id.action_searchFragment_to_doCodiFragment,bundle)
                }
                else
                    binding.radioGroup.check(binding.buttonBottom.id)
            }
        }

        viewModel.bottomSelectedCheckBox.observe(viewLifecycleOwner) { position ->
            if(viewModel.bottomSelectedCheckBox.value != null) {
                if (viewModel.topSelectedCheckBox.value != null) {
                    val bundle = Bundle()
//                    bundle.putInt("topIndex", viewModel.topSelectedCheckBox.value!!)
//                    bundle.putInt("bottomIndex", viewModel.bottomSelectedCheckBox.value!!)
                    bundle.putString("topRef",viewModel.topItems[viewModel.topSelectedCheckBox.value!!].clothesImageUrl)
                    bundle.putString("bottomRef",viewModel.bottomItems[viewModel.bottomSelectedCheckBox.value!!].clothesImageUrl)
                    findNavController().navigate(R.id.action_searchFragment_to_doCodiFragment,bundle)
                    Log.e("","topIndex = ${viewModel.topSelectedCheckBox.value}, bottomIndex = ${viewModel.bottomSelectedCheckBox.value}")
                }
                else
                    binding.radioGroup.check(binding.buttonTop.id)
            }
        }
    }
    private fun searchtopList(){
        binding.recyclerViewBottom.visibility = View.GONE
        binding.recyclerViewTop.visibility = View.VISIBLE
        viewModel.deleteAllWardrobeItem("top")
        loadseasonList()
        loadsizeList()
        loadbrandList()
        loadtagList()
        loadlengthList()
        loadthicknessList()
    }
    private fun searchBottomList(){
        binding.recyclerViewTop.visibility = View.GONE
        binding.recyclerViewBottom.visibility = View.VISIBLE
        viewModel.deleteAllWardrobeItem("bottom")
        loadbottomseasonList()
        loadbottomsizeList()
        loadbottombrandList()
        loadbottomtagList()
        loadbottomlengthList()
        loadbottomthicknessList()
    }
    private fun loadseasonList() {
        val seasonColRef = db.collection("top")
        seasonColRef.whereEqualTo("season", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"top")
                }
            }
    }
    private fun loadsizeList() {
        val seasonColRef = db.collection("top")
        seasonColRef.whereEqualTo("size", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"top")
                }
            }
    }
    private fun loadbrandList() {
        val seasonColRef = db.collection("top")
        seasonColRef.whereEqualTo("brand", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"top")
                }
            }
    }
    private fun loadtagList() {
        val seasonColRef = db.collection("top")
        seasonColRef.whereEqualTo("tag", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"top")
                }
            }
    }
    private fun loadlengthList() {
        val seasonColRef = db.collection("top")
        seasonColRef.whereEqualTo("length", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"top")
                }
            }
    }
    private fun loadthicknessList() {
        val seasonColRef = db.collection("top")
        seasonColRef.whereEqualTo("thickness", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"top")
                }
            }
    }

    private fun loadbottomseasonList() {
        val seasonColRef = db.collection("bottom")
        seasonColRef.whereEqualTo("season", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"bottom")
                }
            }
    }
    private fun loadbottomsizeList() {
        val seasonColRef = db.collection("bottom")
        seasonColRef.whereEqualTo("size", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"bottom")
                }
            }
    }
    private fun loadbottombrandList() {
        val seasonColRef = db.collection("bottom")
        seasonColRef.whereEqualTo("brand", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"bottom")
                }
            }
    }
    private fun loadbottomtagList() {
        val seasonColRef = db.collection("bottom")
        seasonColRef.whereEqualTo("tag", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"bottom")
                }
            }
    }
    private fun loadbottomlengthList() {
        val seasonColRef = db.collection("bottom")
        seasonColRef.whereEqualTo("length", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"bottom")
                }
            }
    }
    private fun loadbottomthicknessList() {
        val seasonColRef = db.collection("bottom")
        seasonColRef.whereEqualTo("thickness", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"bottom")
                }
            }
    }

}