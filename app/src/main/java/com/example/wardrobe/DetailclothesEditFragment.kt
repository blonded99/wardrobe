package com.example.wardrobe

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.DTO.TopBottomDTO
import com.example.wardrobe.databinding.FragmentDetailClothesEditBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DetailclothesEditFragment : Fragment() {
    private lateinit var binding: FragmentDetailClothesEditBinding
    private lateinit var storage: FirebaseStorage
    private var storageImageRef = ""

    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    var clothesInfo = TopBottomDTO()

    val db = Firebase.firestore
    // Top(상의) Collection Ref
    val topColRef = db.collection("top")
    // Bottom(하의) Collection Ref
    val bottomColRef = db.collection("bottom")

    var isTop : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage

        val bundle = arguments
        if (bundle != null) {
            storageImageRef = bundle.getString("imageRef", "")
            isTop = bundle.getBoolean("isTop",true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailClothesEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setClothesInfo(isTop)


        binding.radioGroupThickness.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.button_thickness_thick -> clothesInfo.thickness = "thick"
                R.id.button_thickness_medium -> clothesInfo.thickness = "medium"
                R.id.button_thickness_thin -> clothesInfo.thickness = "thin"
            }
        }

        binding.radioGroupSeason.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.button_season_spring_fall -> clothesInfo.season = "spring&fall"
                R.id.button_season_summer -> clothesInfo.season = "summer"
                R.id.button_season_winter -> clothesInfo.season = "winter"
            }
        }

        binding.radioGroupLength.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.button_length_short -> clothesInfo.length = "short"
                R.id.button_length_long -> clothesInfo.length = "long"
                R.id.button_length_other -> clothesInfo.length = "other"
            }
        }

        binding.radioGroupSize.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.button_size_xs -> clothesInfo.size = "xs"
                R.id.button_size_s -> clothesInfo.size = "s"
                R.id.button_size_m -> clothesInfo.size = "m"
                R.id.button_size_l -> clothesInfo.size = "l"
                R.id.button_size_xl -> clothesInfo.size = "xl"
            }
        }

        binding.buttonSave.setOnClickListener {
            clothesInfo.userID = currentUID
            clothesInfo.imageRef = storageImageRef
            clothesInfo.brand = binding.editTextBrandName.text.toString()
            clothesInfo.memo = binding.editTextMemo.text.toString()
            if(binding.editTextHashtag.text.startsWith("#")){
                val tempList = binding.editTextHashtag.text.split("#"," ")
                clothesInfo.hashtag = tempList.filter{
                    !(it.equals("") || it.equals(" "))
                }
                println(clothesInfo.hashtag)
            }


            if(isTop) {
                topColRef.whereEqualTo("imageRef",storageImageRef).get()
                    .addOnSuccessListener {
                        for(doc in it){
                            topColRef.document(doc.id)
                                .set(clothesInfo)
                        }
                        findNavController().popBackStack()
                    }

            }
            else{
                bottomColRef.whereEqualTo("imageRef",storageImageRef).get()
                    .addOnSuccessListener {
                        for(doc in it){
                            bottomColRef.document(doc.id)
                                .set(clothesInfo)
                        }
                        findNavController().popBackStack()
                    }
            }

        }


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }

    private fun setClothesInfo(isTop: Boolean){

        if(!storageImageRef.equals("")){
            val imageRef = storage.reference.child(storageImageRef)
            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                binding.ivClothes.setImageBitmap(bmp)
            }
        }

        var target = "top"
        if(isTop == false)
            target = "bottom"

        db.collection(target).whereEqualTo("imageRef",storageImageRef).get()
            .addOnSuccessListener {
                for(doc in it){
                    when(doc["thickness"]){
                        "thick" -> binding.buttonThicknessThick.isChecked = true
                        "medium" -> binding.buttonThicknessMedium.isChecked = true
                        "thin" -> binding.buttonThicknessThin.isChecked = true
                    }
                    when(doc["season"]){
                        "spring&fall" -> binding.buttonSeasonSpringFall.isChecked = true
                        "summer" -> binding.buttonSeasonSummer.isChecked = true
                        "winter" -> binding.buttonSeasonWinter.isChecked = true
                    }
                    when(doc["length"]){
                        "long" -> binding.buttonLengthLong.isChecked = true
                        "short" -> binding.buttonLengthShort.isChecked = true
                        "other" -> binding.buttonLengthOther.isChecked = true
                    }
                    when(doc["size"]){
                        "xs" -> binding.buttonSizeXs.isChecked = true
                        "s" -> binding.buttonSizeS.isChecked = true
                        "m" -> binding.buttonSizeM.isChecked = true
                        "l" -> binding.buttonSizeL.isChecked = true
                        "xl" -> binding.buttonSizeXl.isChecked = true
                    }

                    if(doc["brand"].toString().isNotBlank())
                        binding.editTextBrandName.setText(doc["brand"].toString())

                    if(doc["memo"].toString().isNotBlank())
                        binding.editTextMemo.setText(doc["memo"].toString())

                    val tempList = doc["hashtag"] as List<String>?
                    if (tempList != null) {
                        if(tempList.isNotEmpty()) {
                            tempList.forEach {
                                binding.editTextHashtag.text.append("#")
                                binding.editTextHashtag.text.append(it)
                            }
                        }
                    }
                }
        }
    }



}


