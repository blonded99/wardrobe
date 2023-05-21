package com.example.wardrobe

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.DTO.SetDTO
import com.example.wardrobe.databinding.FragmentDetailCodiBinding
import com.example.wardrobe.databinding.FragmentDetailCodiEditBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DetailCodiEditFragment : Fragment() {
    private lateinit var binding: FragmentDetailCodiEditBinding
    private lateinit var storage: FirebaseStorage
    private var storageImageRef = ""

    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    var clothesInfo = SetDTO()

    val db = Firebase.firestore
    // Set(코디) Collection Ref
    val setColRef = db.collection("set")

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
        binding = FragmentDetailCodiEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        setClothesInfo()

        binding.radioGroupSeason.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.button_season_spring_fall -> clothesInfo.season = "spring&fall"
                R.id.button_season_summer -> clothesInfo.season = "summer"
                R.id.button_season_winter -> clothesInfo.season = "winter"
            }
        }

        binding.radioGroupIsPublic.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.button_public -> clothesInfo.isPublic = true
                R.id.button_private -> clothesInfo.isPublic = false
            }
        }


        binding.buttonSave.setOnClickListener {
            clothesInfo.userID = currentUID
            clothesInfo.memo = binding.editTextMemo.text.toString()
            clothesInfo.imageRef = storageImageRef
            if(binding.editTextHashtag.text.startsWith("#")){
                val tempList = binding.editTextHashtag.text.split("#"," ")
                clothesInfo.hashtag = tempList.filter{
                    !(it.equals("") || it.equals(" "))
                }
            }

            setColRef.whereEqualTo("imageRef",storageImageRef).get()
                .addOnSuccessListener {
                    for(doc in it){
                        setColRef.document(doc.id)
                            .set(clothesInfo)
                    }
                    findNavController().popBackStack()
                }

        }


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
                binding.ivCodi.setImageBitmap(bmp)
            }
        }

        setColRef.whereEqualTo("imageRef",storageImageRef).get()
            .addOnSuccessListener {
                for(doc in it){
                    clothesInfo.bottomRef = doc["bottomRef"].toString()
                    clothesInfo.topRef = doc["topRef"].toString()

                    when(doc["season"]){
                        "spring&fall" -> binding.buttonSeasonSpringFall.isChecked = true
                        "summer" -> binding.buttonSeasonSummer.isChecked = true
                        "winter" -> binding.buttonSeasonWinter.isChecked = true
                    }
                    when(doc["public"]){ // 왜?
                        true -> binding.buttonPublic.isChecked = true
                        false -> binding.buttonPrivate.isChecked = true
                    }
                    if(doc["memo"].toString().isNullOrBlank())
                        binding.editTextMemo.setHint("메모를 입력해주세요.")
                    else
                        binding.editTextMemo.setText(doc["memo"].toString())

                    binding.editTextHashtag.text.clear()
                    val tempList = doc["hashtag"] as List<String>
                    if(tempList.isEmpty())
                        binding.editTextHashtag.setHint("해시태그를 #로 구분하여 입력해주세요.")
                    else{
                        tempList.forEach {
                            binding.editTextHashtag.text.append("#")
                            binding.editTextHashtag.text.append(it)
                        }
                    }
                }
        }
    }



}


