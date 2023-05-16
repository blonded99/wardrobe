package com.example.wardrobe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wardrobe.databinding.FragmentAddclothesBinding
import com.example.wardrobe.databinding.FragmentCommunityBinding

class AddClothes : Fragment(){
    private lateinit var binding: FragmentAddclothesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddclothesBinding.inflate(inflater, container, false)
        return binding.root
    }


}