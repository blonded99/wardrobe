package com.example.wardrobe

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.DTO.SetDTO
import com.example.wardrobe.databinding.FragmentAddCodiBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class AddCodiFragment : Fragment() {
    private lateinit var binding: FragmentAddCodiBinding
    private lateinit var storage: FirebaseStorage

    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    var clothesInfo = SetDTO()

    private var storageImageRef = ""
    var topRef = ""
    var bottomRef = ""

    val db = Firebase.firestore
    // Set(코디) Collection Ref
    val setColRef = db.collection("set")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        storage = Firebase.storage

        val bundle = arguments
        if (bundle != null) {
            storageImageRef = bundle.getString("imageRef", "")
            topRef = bundle.getString("topRef","")
            bottomRef = bundle.getString("bottomRef","")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCodiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCodiImage()


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
            clothesInfo.topRef = topRef
            clothesInfo.bottomRef = bottomRef
            clothesInfo.imageRef = storageImageRef

            setColRef.add(clothesInfo).addOnSuccessListener {
                Snackbar.make(binding.root,"FIRESTORE ADD SUCCESS", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addCodiFragment_to_wardrobeFragment)
                findNavController().clearBackStack(R.id.action_addCodiFragment_to_wardrobeFragment)
            }
                .addOnFailureListener {
                    Snackbar.make(binding.root,"FIRESTORE ADD FAILED", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addCodiFragment_to_wardrobeFragment)
                    findNavController().clearBackStack(R.id.action_addCodiFragment_to_wardrobeFragment)
                }

        }

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }


    private fun setCodiImage(){
        if(!storageImageRef.equals("")){
            val imageRef = storage.reference.child(storageImageRef)
            imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                binding.ivCodi.setImageBitmap(bmp)
            }
        }
    }


}


