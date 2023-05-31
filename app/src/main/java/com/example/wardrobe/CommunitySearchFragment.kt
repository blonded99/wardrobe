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
import com.example.wardrobe.adapters.CommunityLikedRecyclerViewAdapter
import com.example.wardrobe.adapters.CommunityMainRecyclerViewAdapter
import com.example.wardrobe.databinding.FragmentCommunitysearchBinding
import com.example.wardrobe.viewmodel.CommunityViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommunitySearchFragment:Fragment(){
    private lateinit var binding: FragmentCommunitysearchBinding
    private val viewModel by viewModels<CommunityViewModel> ()
    protected lateinit var navController: NavController

    val adapter_community_main = CommunityMainRecyclerViewAdapter(viewModel,context,this)
    val adapter_community_liked = CommunityLikedRecyclerViewAdapter(viewModel,context,this)

    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    val db = Firebase.firestore
    // Set(코디) Collection Ref
    val setColRef = db.collection("set")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunitysearchBinding.inflate(inflater,container,false)

        initView()
        return binding.root
    }
    private fun initView(){
        val text = requireArguments().getString("searchcommunity")
        binding.editSearch.setText(text)
//        searchCommunityMainList()

        navController = findNavController()

        binding.recyclerViewCommunityMain.adapter = adapter_community_main
        binding.recyclerViewCommunityMain.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.recyclerViewCommunityLiked.adapter = adapter_community_liked
        binding.recyclerViewCommunityLiked.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        viewModel.communityMainItemsListData.observe(viewLifecycleOwner){
            adapter_community_main.notifyDataSetChanged()
        }

        viewModel.communityLikedItemsListData.observe(viewLifecycleOwner){
            adapter_community_liked.notifyDataSetChanged()
        }
        binding.radioGroup.addOnButtonCheckedListener { group, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when(group.checkedButtonId){
                R.id.button_community_main -> searchCommunityMainList()
                R.id.button_community_liked -> searchCommunityLikedList()
            }
        }
        binding.searchProductBtn.setOnClickListener {
            Log.d("search", "input: ${binding.editSearch.text.toString()}")
            searchCommunityMainList()
            searchCommunityLikedList()
        }

    }
    private fun searchCommunityMainList(){
        binding.recyclerViewCommunityLiked.visibility = View.GONE
        binding.recyclerViewCommunityMain.visibility = View.VISIBLE
        viewModel.deleteCommunityItem("main")

    }
    private fun searchCommunityLikedList(){
        binding.recyclerViewCommunityMain.visibility = View.GONE
        binding.recyclerViewCommunityLiked.visibility = View.VISIBLE
        viewModel.deleteCommunityItem("liked")

    }

//    private fun loadseasonList() {
//        setColRef.whereEqualTo("season", binding.editSearch.text.toString()).get()
//            .addOnSuccessListener {
//                for (doc in it) {
//                    viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()),"all")
//                }
//            }
//    }
//    private fun loadmemoList() {
//        setColRef.whereEqualTo("memo", binding.editSearch.text.toString()).get()
//            .addOnSuccessListener {
//                for (doc in it) {
//                    viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()),"all")
//                }
//            }
//    }
//    private fun loadtagList() {
//        setColRef.whereArrayContains("hashtag", binding.editSearch.text.toString()).get()
//            .addOnSuccessListener {
//                for (doc in it) {
//                    viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()),"all")
//                }
//            }
//    }


}