package com.example.wardrobe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wardrobe.adapters.CodiAllRecyclerViewAdapter
import com.example.wardrobe.adapters.WardrobeRecyclerViewAdapter
import com.example.wardrobe.databinding.FragmentCodisearchBinding
import com.example.wardrobe.databinding.FragmentSearchBinding
import com.example.wardrobe.viewmodel.CodiItem
import com.example.wardrobe.viewmodel.CodiViewModel
import com.example.wardrobe.viewmodel.Item
import com.example.wardrobe.viewmodel.WardrobeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CodiSearchFragment : Fragment(){
    private lateinit var binding: FragmentCodisearchBinding
    private val viewModel by viewModels<CodiViewModel> ()
    private lateinit var adapter_codi: CodiAllRecyclerViewAdapter
    protected lateinit var navController: NavController

    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    val db = Firebase.firestore
    val setColRef = db.collection("set")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodisearchBinding.inflate(inflater,container,false)
        initView()

        return binding.root
    }
    private fun initView(){
        val text = requireArguments().getString("searchcodi")
        binding.editSearch.setText(text)
        searchcodiList()

        navController = findNavController()


        adapter_codi = CodiAllRecyclerViewAdapter(viewModel,context,this)

        binding.recyclerViewCodi.adapter = adapter_codi
        binding.recyclerViewCodi.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        viewModel.CodiAllItemsListData.observe(viewLifecycleOwner){
            adapter_codi.notifyDataSetChanged()
        }

        binding.searchProductBtn.setOnClickListener {
            Log.d("search", "input: ${binding.editSearch.text.toString()}")
            searchcodiList()
        }

    }
    private fun searchcodiList(){
        viewModel.deleteCodiItem("all")
        loadseasonList()
        loadmemoList()
        loadtagList()
    }

    private fun loadseasonList() {
        if (binding.editSearch.text.toString().equals("여름")) {
            setColRef.whereEqualTo("season", "summer").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()), "all")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("겨울")) {
            setColRef.whereEqualTo("season", "summer").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()), "all")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("가을")){
            setColRef.whereEqualTo("season", "spring&fall").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()), "all")
                        }
                    }
                }
        }
        else if(binding.editSearch.text.toString().equals("봄")){
            setColRef.whereEqualTo("season", "spring&fall").get()
                .addOnSuccessListener {
                    for (doc in it) {
                        if (doc["userID"] == currentUID) {
                            viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()), "all")
                        }
                    }
                }
        }
    }
    private fun loadmemoList() {
        setColRef.whereEqualTo("memo", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if (doc["userID"] == currentUID) {
                        viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()), "all")
                    }                }
            }
    }
    private fun loadtagList() {
        setColRef.whereArrayContains("hashtag", binding.editSearch.text.toString()).get()
            .addOnSuccessListener {
                for (doc in it) {
                    if (doc["userID"] == currentUID) {
                        viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()), "all")
                    }                }
            }
    }

}