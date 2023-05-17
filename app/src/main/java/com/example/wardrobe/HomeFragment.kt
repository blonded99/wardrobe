package com.example.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wardrobe.adapters.HomeRecyclerViewAdapter
import com.example.wardrobe.adapters.HomeRecyclerViewAdapter2
import com.example.wardrobe.databinding.FragmentHomeBinding
import com.example.wardrobe.viewmodel.HomeViewModel
import com.example.wardrobe.viewmodel.Homeitem

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.addHomeCommunityItem(Homeitem(R.drawable.test_top))
        viewModel.addHomeCommunityItem(Homeitem(R.drawable.test_bottom))

        viewModel.addHomeWeatherItem(Homeitem(R.drawable.test_top))
        viewModel.addHomeWeatherItem(Homeitem(R.drawable.test_bottom))

        val adapter = HomeRecyclerViewAdapter(viewModel,context,this)
        val adapter_community = HomeRecyclerViewAdapter2(viewModel,context,this)


        // 날씨에 따른 추천 부분 옷
        binding.recyclerViewRecommend.adapter = adapter
        binding.recyclerViewRecommend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 최신 커뮤니티 부분 옷
        binding.recyclerViewCommunity.adapter = adapter_community
        binding.recyclerViewCommunity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        viewModel.HomeweatherItemsListData.observe(viewLifecycleOwner){
            HomeRecyclerViewAdapter(viewModel,context,this).notifyDataSetChanged()
        }

        viewModel.HomecommunityItemsListData.observe(viewLifecycleOwner){
            HomeRecyclerViewAdapter2(viewModel,context,this).notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }



}


