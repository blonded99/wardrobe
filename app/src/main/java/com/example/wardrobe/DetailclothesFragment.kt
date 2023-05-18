package com.example.wardrobe

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wardrobe.databinding.FragmentDetailClothesBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DetailclothesFragment : Fragment() {
    private lateinit var binding: FragmentDetailClothesBinding
    private lateinit var storage: FirebaseStorage
    private var storageImageRef = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage

        val bundle = arguments
        if (bundle != null)
            storageImageRef = bundle.getString("imageRef", "")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailClothesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if(!storageImageRef.equals("")){
            val imageRef = storage.reference.child(storageImageRef)
            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                binding.ivClothes.setImageBitmap(bmp)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }



}


