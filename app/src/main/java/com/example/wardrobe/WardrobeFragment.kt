package com.example.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.wardrobe.adapters.WardrobeRecyclerViewAdapter
import com.example.wardrobe.databinding.FragmentWardrobeBinding
import com.example.wardrobe.viewmodel.Item
import com.example.wardrobe.viewmodel.WardrobeViewModel

class WardrobeFragment : Fragment() {
    private lateinit var binding: FragmentWardrobeBinding

    private val viewModel by viewModels<WardrobeViewModel>()

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


//        viewModel.addItem(Item(R.drawable.sample_img1))
//        viewModel.addItem(Item(R.drawable.sample_img2))
//        viewModel.addItem(Item(R.drawable.sample_img3))
//        viewModel.addItem(Item(R.drawable.sample_img4))
//        viewModel.addItem(Item(R.drawable.sample_img5))
//        viewModel.addItem(Item(R.drawable.sample_img6))


        viewModel.addWardrobeItem(Item(R.drawable.test_top))
        viewModel.addWardrobeItem(Item(R.drawable.test_bottom))


        val adapter = WardrobeRecyclerViewAdapter(viewModel,context,this)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)

        viewModel.wardrobeItemsListData.observe(viewLifecycleOwner){
            WardrobeRecyclerViewAdapter(viewModel,context,this).notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }



}


