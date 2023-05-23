package com.example.wardrobe

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.wardrobe.databinding.FragmentDetailCommunityBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DetailCommunityFragment : Fragment() {
    private lateinit var binding: FragmentDetailCommunityBinding
    private lateinit var storage: FirebaseStorage
    private var storageImageRef = ""

    val db = Firebase.firestore
    // Set(코디) Collection Ref
    val setColRef = db.collection("set")
    // Top(상의) Collection Ref
    val topColRef = db.collection("top")
    // Bottom(하의) Collection Ref
    val bottomColRef = db.collection("bottom")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage

        val bundle = arguments
        if (bundle != null) {
            storageImageRef = bundle.getString("imageRef", "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClothesInfo()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }

    private fun setClothesInfo(){

        if(!storageImageRef.equals("")){
            val imageRef = storage.reference.child(storageImageRef)
            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                binding.ivCommunity.setImageBitmap(bmp)
            }
        }

        setColRef.whereEqualTo("imageRef",storageImageRef).get()
            .addOnSuccessListener {
                for(doc in it){
                    if(doc["topRef"].toString().isNotBlank()){
                        topColRef.whereEqualTo("imageRef",doc["topRef"].toString()).get()
                            .addOnSuccessListener {
                                for(doc in it){
                                    val brand = doc["brand"].toString()
                                    if(brand.isNotBlank()) {
                                        binding.tvTopBrand.text = brand
                                        return@addOnSuccessListener
                                    }
                                }
                            }

                    }

                    if(doc["bottomRef"].toString().isNotBlank()){
                        bottomColRef.whereEqualTo("imageRef",doc["bottomRef"].toString()).get()
                            .addOnSuccessListener {
                                for(doc in it){
                                    val brand = doc["brand"].toString()
                                    if(brand.isNotBlank()) {
                                        binding.tvBottomBrand.text = brand
                                        return@addOnSuccessListener
                                    }
                                }
                            }
                    }

                    when(doc["season"]){
                        "spring&fall" -> binding.tvSeasonContent.text = "봄&가을"
                        "summer" -> binding.tvSeasonContent.text = "여름"
                        "winter" -> binding.tvSeasonContent.text = "겨울"
                    }

                    if(doc["memo"].toString().isNotBlank())
                        binding.editTextMemo.setText(doc["memo"].toString())

                    binding.editTextHashtag.text.clear()
                    val tempList = doc["hashtag"] as List<String>?
                    if(!tempList.isNullOrEmpty()){
                        tempList.forEach {
                            binding.editTextHashtag.text.append("#")
                            binding.editTextHashtag.text.append(it)
                        }
                    }

                }
        }
    }



}


