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
        binding.searchbtn.setOnClickListener{
            navController.navigate(R.id.action_wardrobeFragment_to_searchFragment)
        }


        loadTopList()

        binding.radioGroup.addOnButtonCheckedListener { group, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when(group.checkedButtonId){
                R.id.button_top -> loadTopList()
                R.id.button_bottom -> loadBottomList()
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
                    findNavController().navigate(R.id.action_wardrobeFragment_to_doCodiFragment,bundle)
                    Log.e("","topIndex = ${viewModel.topSelectedCheckBox.value}, bottomIndex = ${viewModel.bottomSelectedCheckBox.value}")
                }
                else
                    binding.radioGroup.check(binding.buttonTop.id)
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


}


