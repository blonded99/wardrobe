package com.example.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wardrobe.adapters.HomeRecyclerViewAdapter
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

//        binding.homecommunityrecyclerview.adapter = adapter
//        binding.homecommunityrecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        binding.homecommunityrecyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.weatherrecyclerview.adapter = adapter
        binding.weatherrecyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

//        binding.weatherrecyclerview.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

//        viewModel.HomecommunityItemsListData.observe(viewLifecycleOwner){
//            HomeRecyclerViewAdapter(viewModel,context,this).notifyDataSetChanged()
//        }
        viewModel.HomeweatherItemsListData.observe(viewLifecycleOwner){
            HomeRecyclerViewAdapter(viewModel,context,this).notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }



}


