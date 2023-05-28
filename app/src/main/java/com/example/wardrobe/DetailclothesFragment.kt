package com.example.wardrobe

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.databinding.FragmentDetailClothesBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DetailclothesFragment : Fragment() {
    private lateinit var binding: FragmentDetailClothesBinding
    private lateinit var storage: FirebaseStorage
    private var storageImageRef = ""

    val db = Firebase.firestore

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
        binding = FragmentDetailClothesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        setClothesInfo(isTop)

        binding.buttonEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("imageRef",storageImageRef)
            bundle.putBoolean("isTop",isTop)
            findNavController().navigate(R.id.action_detailClothesFragment_to_detailClothesEditFragment,bundle)
        }

        binding.buttonTrash.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("삭제하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    deleteClothes()
                }
                .setNegativeButton("아니오") { _, _ ->
                }
                .show()
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

                    if(doc["brand"].toString().isNullOrBlank())
                        binding.editTextBrandName.setHint("브랜드를 입력해주세요.")
                    else
                        binding.editTextBrandName.setText(doc["brand"].toString())

                    if(doc["memo"].toString().isNullOrBlank())
                        binding.editTextMemo.setHint("메모를 입력해주세요.")
                    else
                        binding.editTextMemo.setText(doc["memo"].toString())

                    binding.editTextHashtag.text.clear()
                    val tempList = doc["hashtag"] as List<String>?
                    if(tempList.isNullOrEmpty())
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

    private fun deleteClothes(){
        var target = "top"
        if(!isTop)
            target = "bottom"
        db.collection(target).whereEqualTo("imageRef",storageImageRef).get()
            .addOnSuccessListener {
                for(doc in it){
                    db.collection(target).document(doc.id).delete()
                        .addOnSuccessListener {
                            findNavController().popBackStack()
                        }
                }
            }
    }



}


