package com.example.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.wardrobe.databinding.FragmentCommunityBinding
import com.example.wardrobe.viewmodel.Item
import com.example.wardrobe.viewmodel.WordrobeViewModel

class CommunityFragment : Fragment() {
    private lateinit var binding: FragmentCommunityBinding

    private val viewModel by viewModels<WordrobeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        viewModel.addCommunityItem(Item(R.drawable.test_top))
        viewModel.addCommunityItem(Item(R.drawable.test_bottom))
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

//        val adapter = WardrobeRecyclerViewAdapter(viewModel,context,this)

//        binding.recyclerView.adapter = adapter
//        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
//
//        viewModel.itemsListData.observe(viewLifecycleOwner){
//            WardrobeRecyclerViewAdapter(viewModel,context,this).notifyDataSetChanged()
//        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }



}


