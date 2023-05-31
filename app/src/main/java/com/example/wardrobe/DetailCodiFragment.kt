package com.example.wardrobe

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.databinding.FragmentDetailCodiBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class DetailCodiFragment : Fragment() {
    private lateinit var binding: FragmentDetailCodiBinding
    private lateinit var storage: FirebaseStorage
    private var storageImageRef = ""

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
        binding = FragmentDetailCodiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        setClothesInfo()

        binding.buttonEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("imageRef",storageImageRef)
            findNavController().navigate(R.id.action_detailCodiFragment_to_detailCodiEditFragment,bundle)
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
                    when(doc["season"]){
                        "spring&fall" -> binding.buttonSeasonSpringFall.isChecked = true
                        "summer" -> binding.buttonSeasonSummer.isChecked = true
                        "winter" -> binding.buttonSeasonWinter.isChecked = true
                    }
                    when(doc["public"]){ // 왜?
                        true -> binding.buttonPublic.isChecked = true
                        false -> binding.buttonPrivate.isChecked = true
                    }
                    if(doc["memo"].toString().isNotBlank())
                        binding.editTextMemo.setText(doc["memo"].toString())

                    binding.editTextHashtag.text.clear()
                    val tempList = doc["hashtag"] as List<String>
                    if(tempList.isNotEmpty()) {
                        tempList.forEach {
                            binding.editTextHashtag.text.append("#")
                            binding.editTextHashtag.text.append(it)
                        }
                    }

                }
        }
    }

    private fun deleteClothes(){
        setColRef.whereEqualTo("imageRef",storageImageRef).get()
            .addOnSuccessListener {
                for(doc in it){
                    setColRef.document(doc.id).delete()
                        .addOnSuccessListener {
                            findNavController().popBackStack()
                        }
                }
            }
    }



}


