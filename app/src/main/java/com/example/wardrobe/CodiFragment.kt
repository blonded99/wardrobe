package com.example.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wardrobe.adapters.CodiAllRecyclerViewAdapter
import com.example.wardrobe.adapters.CodiPrivateRecyclerViewAdapter
import com.example.wardrobe.adapters.CodiPublicRecyclerViewAdapter
import com.example.wardrobe.adapters.WardrobeBottomRecyclerViewAdapter
import com.example.wardrobe.adapters.WardrobeRecyclerViewAdapter
import com.example.wardrobe.databinding.FragmentCodiBinding
import com.example.wardrobe.viewmodel.CodiItem
import com.example.wardrobe.viewmodel.CodiViewModel
import com.example.wardrobe.viewmodel.Item
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CodiFragment : Fragment() {
    private lateinit var binding: FragmentCodiBinding
    protected lateinit var navController: NavController


    private val viewModel by viewModels<CodiViewModel>()

    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    val db = Firebase.firestore
    // Set(코디) Collection Ref
    val setColRef = db.collection("set")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCodiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val adapter_codi_all = CodiAllRecyclerViewAdapter(viewModel,context,this)
        val adapter_codi_public = CodiPublicRecyclerViewAdapter(viewModel,context,this)
        val adapter_codi_private = CodiPrivateRecyclerViewAdapter(viewModel,context,this)

        binding.recyclerViewAllCodi.adapter = adapter_codi_all
        binding.recyclerViewAllCodi.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.recyclerViewPublicCodi.adapter = adapter_codi_public
        binding.recyclerViewPublicCodi.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.recyclerViewPrivateCodi.adapter = adapter_codi_private
        binding.recyclerViewPrivateCodi.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        viewModel.CodiAllItemsListData.observe(viewLifecycleOwner){
            adapter_codi_all.notifyDataSetChanged()
        }

        viewModel.CodiPublicItemsListData.observe(viewLifecycleOwner){
            adapter_codi_public.notifyDataSetChanged()
        }

        viewModel.CodiPrivateItemsListData.observe(viewLifecycleOwner){
            adapter_codi_private.notifyDataSetChanged()
        }

        loadCodiAllList()

        binding.radioGroup.addOnButtonCheckedListener { group, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when(group.checkedButtonId){
                R.id.button_all_codi -> loadCodiAllList()
                R.id.button_public_codi -> loadCodiPublicList()
                R.id.button_private_codi -> loadCodiPrivateList()
            }
        }
        binding.searchProductBtn.setOnClickListener {
            val bundle = bundleOf("searchcodi" to binding.editSearch.text.toString())
            navController.navigate(R.id.action_codiFragment_to_codiSearchFragment, bundle)
//            navController.navigate(R.id.action_wardrobeFragment_to_searchFragment)

        }


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun loadCodiAllList(){
        binding.recyclerViewPublicCodi.visibility = View.GONE
        binding.recyclerViewPrivateCodi.visibility = View.GONE
        binding.recyclerViewAllCodi.visibility = View.VISIBLE
        viewModel.deleteCodiItem("all")
        setColRef.whereEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()),"all")
                }
            }
    }

    private fun loadCodiPublicList(){
        binding.recyclerViewPrivateCodi.visibility = View.GONE
        binding.recyclerViewAllCodi.visibility = View.GONE
        binding.recyclerViewPublicCodi.visibility = View.VISIBLE
        viewModel.deleteCodiItem("public")
        setColRef.whereEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    if(doc["public"] == true)
                        viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()),"public")
                }
            }
    }

    private fun loadCodiPrivateList(){
        binding.recyclerViewAllCodi.visibility = View.GONE
        binding.recyclerViewPublicCodi.visibility = View.GONE
        binding.recyclerViewPrivateCodi.visibility = View.VISIBLE
        viewModel.deleteCodiItem("private")
        setColRef.whereEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    if(doc["public"] == false)
                        viewModel.addCodiItem(CodiItem(doc["imageRef"].toString()),"private")
                }
            }
    }


}


