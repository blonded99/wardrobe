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
import com.example.wardrobe.adapters.WardrobeBottomRecyclerViewAdapter
import com.example.wardrobe.adapters.WardrobeRecyclerViewAdapter
import com.example.wardrobe.databinding.FragmentWardrobeBinding
import com.example.wardrobe.viewmodel.Item
import com.example.wardrobe.viewmodel.WardrobeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WardrobeFragment : Fragment() {
    private lateinit var binding: FragmentWardrobeBinding
    protected lateinit var navController: NavController

    private val viewModel by viewModels<WardrobeViewModel>()


    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    val db = Firebase.firestore
    // Top(상의) Collection Ref
    val topColRef = db.collection("top")
    // Bottom(하의) Collection Ref
    val bottomColRef = db.collection("bottom")
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
        binding = FragmentWardrobeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


        val adapter_top = WardrobeRecyclerViewAdapter(viewModel,context,this)
        val adapter_bottom = WardrobeBottomRecyclerViewAdapter(viewModel,context,this)
//        val adapter_set = WardrobeSetRecyclerViewAdapter(viewModel,context,this)

        binding.recyclerViewTop.adapter = adapter_top
        binding.recyclerViewTop.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)

        binding.recyclerViewBottom.adapter = adapter_bottom
        binding.recyclerViewBottom.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)

//        binding.recyclerViewSet.adapter = adapter_set
//        binding.recyclerViewSet.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)


        viewModel.topItemsListData.observe(viewLifecycleOwner){
            adapter_top.notifyDataSetChanged()
        }

        viewModel.bottomItemsListData.observe(viewLifecycleOwner){
            adapter_bottom.notifyDataSetChanged()
        }

//        viewModel.setItemsListData.observe(viewLifecycleOwner){
//            adapter_set.notifyDataSetChanged()
//        }

        binding.floatingActionButton.setOnClickListener {
            navController.navigate(R.id.action_wardrobeFragment_to_addclothesFragment)
        }

        loadTopList()

        binding.radioGroup.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.button_top -> loadTopList()
                R.id.button_bottom -> loadBottomList()
//                R.id.button_set -> loadSetList()
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
                    findNavController().navigate(R.id.action_wardrobeFragment_to_doCodiFragment,bundle)
                }
                else
                    binding.buttonBottom.isChecked = true
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
                    findNavController().navigate(R.id.action_wardrobeFragment_to_doCodiFragment,bundle)
                    Log.e("","topIndex = ${viewModel.topSelectedCheckBox.value}, bottomIndex = ${viewModel.bottomSelectedCheckBox.value}")
                }
                else
                    binding.buttonTop.isChecked = true
            }
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }

    private fun loadTopList(){
        binding.recyclerViewBottom.visibility = View.GONE
//        binding.recyclerViewSet.visibility = View.GONE
        binding.recyclerViewTop.visibility = View.VISIBLE
        viewModel.deleteAllWardrobeItem("top")
        topColRef.whereEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"top")
                }
            }
    }

    private fun loadBottomList(){
//        binding.recyclerViewSet.visibility = View.GONE
        binding.recyclerViewTop.visibility = View.GONE
        binding.recyclerViewBottom.visibility = View.VISIBLE
        viewModel.deleteAllWardrobeItem("bottom")
        bottomColRef.whereEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"bottom")
                }
            }
    }

//    private fun loadSetList(){
//        binding.recyclerViewTop.visibility = View.GONE
//        binding.recyclerViewBottom.visibility = View.GONE
//        binding.recyclerViewSet.visibility = View.VISIBLE
//        viewModel.deleteAllWardrobeItem("set")
//        setColRef.whereEqualTo("userID",currentUID).get()
//            .addOnSuccessListener {
//                for(doc in it){
//                    viewModel.addWardrobeItem(Item(doc["imageRef"].toString()),"set")
//                }
//            }
//    }




}


