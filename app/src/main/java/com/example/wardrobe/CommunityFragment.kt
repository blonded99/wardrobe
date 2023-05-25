package com.example.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wardrobe.adapters.CommunityLikedRecyclerViewAdapter
import com.example.wardrobe.adapters.CommunityMainRecyclerViewAdapter
import com.example.wardrobe.databinding.FragmentCommunityBinding
import com.example.wardrobe.viewmodel.CommunityItem
import com.example.wardrobe.viewmodel.CommunityViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommunityFragment : Fragment() {
    private lateinit var binding: FragmentCommunityBinding
    protected lateinit var navController: NavController
    private val viewModel by viewModels<CommunityViewModel>()


    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    val db = Firebase.firestore
    // Set(코디) Collection Ref
    val setColRef = db.collection("set")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val adapter_community_main = CommunityMainRecyclerViewAdapter(viewModel,context,this)
        val adapter_community_liked = CommunityLikedRecyclerViewAdapter(viewModel,context,this)

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

        loadCommunityMainList()

        binding.searchProductBtn.setOnClickListener {
            val bundle = bundleOf("searchcommunity" to binding.editSearch.text.toString())
            navController.navigate(R.id.action_communityFragment_to_communitySearchFragment, bundle)
//            navController.navigate(R.id.action_wardrobeFragment_to_searchFragment)

        }

        binding.radioGroup.addOnButtonCheckedListener { group, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when(group.checkedButtonId){
                R.id.button_community_main -> loadCommunityMainList()
                R.id.button_community_liked -> loadCommunityLikedList()
            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }

    private fun loadCommunityMainList(){
        binding.recyclerViewCommunityLiked.visibility = View.GONE
        binding.recyclerViewCommunityMain.visibility = View.VISIBLE
        viewModel.deleteCommunityItem("main")
        setColRef.whereNotEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    if(doc["public"] == true) {
                        val tempList = doc["likedUser"] as List<String>?
                        if (!tempList.isNullOrEmpty()) {
                            if (tempList.contains(currentUID))
                                viewModel.addCommunityItem(CommunityItem(doc["imageRef"].toString(),true), "main")
                            else
                                viewModel.addCommunityItem(CommunityItem(doc["imageRef"].toString(),false), "main")
                        }
                        else
                            viewModel.addCommunityItem(CommunityItem(doc["imageRef"].toString(),false), "main")
                    }
                }
            }
    }

    private fun loadCommunityLikedList(){
        binding.recyclerViewCommunityMain.visibility = View.GONE
        binding.recyclerViewCommunityLiked.visibility = View.VISIBLE
        viewModel.deleteCommunityItem("liked")
        setColRef.whereNotEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    if(doc["public"] == true) {
                        val tempList = doc["likedUser"] as List<String>?
                        if (!tempList.isNullOrEmpty()) {
                            if (tempList.contains(currentUID))
                                viewModel.addCommunityItem(CommunityItem(doc["imageRef"].toString(),true), "liked")
                        }
                    }
                }
            }
    }



}


