package com.example.wardrobe

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.set
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
import com.example.wardrobe.viewmodel.CommunityItem
import com.example.wardrobe.viewmodel.Item
import com.example.wardrobe.viewmodel.WardrobeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment: Fragment(){
    private lateinit var binding:FragmentSearchBinding
    private val viewModel by viewModels<WardrobeViewModel> ()
    protected lateinit var navController: NavController

//    val inputtext = binding.editSearch.text.toString()

    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    val db = Firebase.firestore
    // Top(상의) Collection Ref
    val topColRef = db.collection("top")
    val bottomColRef = db.collection("bottom")


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

        val text = requireArguments().getString("search")
        binding.editSearch.setText(text)
//        searchtopList()

        navController = findNavController()

        val adapter_top = WardrobeRecyclerViewAdapter(viewModel, context, this)
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

        binding.radioGroup.addOnButtonCheckedListener { group, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            if (group.checkedButtonId == R.id.button_top) {
                binding.searchProductBtn.setOnClickListener {
                    searchtopList()
                }
            } else if (group.checkedButtonId == R.id.button_bottom)
                binding.searchProductBtn.setOnClickListener {
                    searchBottomList()
                }
            when(group.checkedButtonId){
                R.id.button_top -> searchtopList()
                R.id.button_bottom -> searchBottomList()
            }
        }

        viewModel.isCodiMode.observe(viewLifecycleOwner){
            when(binding.radioGroup.checkedButtonId){
                R.id.button_top -> adapter_top.notifyDataSetChanged()
                R.id.button_bottom -> adapter_bottom.notifyDataSetChanged()
            }
            if(it==true)
                binding.floatingActionButtonCodi.setImageResource(R.drawable.button_return)
            else
                binding.floatingActionButtonCodi.setImageResource(R.drawable.button_fab_codi)
        }

        var isFABOpen = false

        binding.floatingActionButton.setOnClickListener {
            if (!isFABOpen) {
                isFABOpen = true
                binding.floatingActionButton.animate()
                    .rotation(45f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()

                binding.floatingActionButtonAdd.visibility = View.VISIBLE
                binding.floatingActionButtonCodi.visibility = View.VISIBLE
            } else {
                isFABOpen = false
                binding.floatingActionButton.animate()
                    .rotation(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .start()

                binding.floatingActionButtonAdd.visibility = View.INVISIBLE
                binding.floatingActionButtonCodi.visibility = View.INVISIBLE
            }
        }

        binding.floatingActionButtonAdd.setOnClickListener {
            navController.navigate(R.id.action_wardrobeFragment_to_addclothesFragment)
        }

        binding.floatingActionButtonCodi.setOnClickListener {
            viewModel.isCodiMode.value = viewModel.isCodiMode.value != true
        }

        viewModel.topSelectedCheckBox.observe(viewLifecycleOwner) { position ->
            if(viewModel.topSelectedCheckBox.value != null) {
                if (viewModel.bottomSelectedCheckBox.value != null) {
                    val bundle = Bundle()
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
        if (binding.editSearch.text.toString().equals("여름")) {
            topColRef.whereEqualTo("season", "summer").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("겨울")) {
            topColRef.whereEqualTo("season", "winter").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("가을")){
            topColRef.whereEqualTo("season", "spring&fall").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("봄")){
            topColRef.whereEqualTo("season", "spring&fall").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
    }
    private fun loadsizeList() {
        topColRef.whereEqualTo("size", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if(doc["userID"] == currentUID) {
                        viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                    }
                }
            }
    }
    private fun loadbrandList() {
        topColRef.whereEqualTo("brand", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if(doc["userID"] == currentUID) {
                        viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                    }
                }
            }
    }
    private fun loadtagList() {
        topColRef.whereArrayContains("hashtag", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if(doc["userID"] == currentUID) {
                        viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                    }
                }
            }
    }
    private fun loadlengthList() {
        if (binding.editSearch.text.toString().equals("김")) {
            topColRef.whereEqualTo("length", "long").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("짧음")) {
            topColRef.whereEqualTo("length", "short").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("기타")){
            topColRef.whereEqualTo("length", "other").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
    }
    private fun loadthicknessList() {
        if (binding.editSearch.text.toString().equals("두꺼움")) {
            topColRef.whereEqualTo("thickness", "thick").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("보통")) {
            topColRef.whereEqualTo("thickness", "medium").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("얇음")){
            topColRef.whereEqualTo("thickness", "thin").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "top")
                        }
                    }
                }
        }
    }

    private fun loadbottomseasonList() {
        if (binding.editSearch.text.toString().equals("여름")) {
            bottomColRef.whereEqualTo("season", "summer").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("겨울")) {
            bottomColRef.whereEqualTo("season", "winter").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("봄")){
            bottomColRef.whereEqualTo("season", "spring&fall").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("가을")){
            bottomColRef.whereEqualTo("season", "spring&fall").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
    }
    private fun loadbottomsizeList() {
        bottomColRef.whereEqualTo("size", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if(doc["userID"] == currentUID) {
                        viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                    }
                }
            }
    }
    private fun loadbottombrandList() {
        bottomColRef.whereEqualTo("brand", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if(doc["userID"] == currentUID) {
                        viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                    }
                }
            }
    }
    private fun loadbottomtagList() {
        bottomColRef.whereArrayContains("hashtag", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if(doc["userID"] == currentUID) {
                        viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                    }
                }
            }
    }
    private fun loadbottomlengthList() {
        if (binding.editSearch.text.toString().equals("김")) {
            bottomColRef.whereEqualTo("length", "long").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("짧음")) {
            bottomColRef.whereEqualTo("length", "short").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("기타")){
            bottomColRef.whereEqualTo("length", "other").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
    }
    private fun loadbottomthicknessList() {
        if (binding.editSearch.text.toString().equals("두꺼움")) {
            bottomColRef.whereEqualTo("thickness", "thick").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("보통")) {
            bottomColRef.whereEqualTo("thickness", "medium").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("얇음")){
            bottomColRef.whereEqualTo("thickness", "thin").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addWardrobeItem(Item(doc["imageRef"].toString()), "bottom")
                        }
                    }
                }
        }
    }

}