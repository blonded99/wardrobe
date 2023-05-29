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
import com.example.wardrobe.viewmodel.HomeItem
import com.example.wardrobe.viewmodel.TempHomeItem
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModels<HomeViewModel>()

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter_weather = HomeRecyclerViewAdapter(viewModel,context,this)
        val adapter_community = HomeRecyclerViewAdapter2(viewModel,context,this)


        // 날씨에 따른 추천 부분 옷
        binding.recyclerViewRecommend.adapter = adapter_weather
        binding.recyclerViewRecommend.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // 최신 커뮤니티 부분 옷
        binding.recyclerViewCommunity.adapter = adapter_community
        binding.recyclerViewCommunity.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        viewModel.HomeweatherItemsListData.observe(viewLifecycleOwner){
            adapter_weather.notifyDataSetChanged()
        }

        viewModel.HomecommunityItemsListData.observe(viewLifecycleOwner){
            adapter_community.notifyDataSetChanged()
        }

        loadHomeCommunityList()

        //임시
        viewModel.addHomeWeatherItem(TempHomeItem(R.drawable.test_top))
        viewModel.addHomeWeatherItem(TempHomeItem(R.drawable.test_bottom))

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }


    private fun loadHomeCommunityList() {
        viewModel.deleteHomeCommunityItem()
        setColRef.whereNotEqualTo("userID",currentUID).get()
            .addOnSuccessListener {
                for(doc in it){
                    if(doc["public"] == true) {
                        viewModel.addHomeCommunityItem(HomeItem(doc["imageRef"].toString()))
                    }
                }
            }
    }


}


