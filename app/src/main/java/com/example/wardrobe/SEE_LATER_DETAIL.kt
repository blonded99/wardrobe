package com.example.wardrobe

//import android.Manifest
//import android.app.Activity
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.BitmapFactory
//import android.graphics.Typeface
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import com.example.wardrobe.databinding.ClothesAddBinding
//import com.example.wardrobe.databinding.ClothesDetailBinding
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import com.google.firebase.storage.ktx.storage
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.*
//import okhttp3.HttpUrl.Companion.toHttpUrl
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.RequestBody.Companion.asRequestBody
//import java.io.File
//
//
//class SEE_LATER_DETAIL : Fragment() {
//    private lateinit var binding: ClothesDetailBinding
//    private lateinit var storage: FirebaseStorage
//
//    val db = Firebase.firestore
//    // Top(상의) Collection Ref
//    val topColRef = db.collection("top")
//
//    // Top(하의) Collection Ref
//    val bottomColRef = db.collection("bottom")
//
//    companion object{
//        const val REQ_GALLERY = 1
//
//        var thickness_thick = 0
//        var thickness_normal = 0
//        var thickness_thin = 0
//
//        var category_top = 0
//        var category_bottom = 0
//        var category_coordination = 0
//
//        var clothestype_short = 0
//        var clothestype_long = 0
//        var clothestype_sleeveless = 0
//    }
//
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = ClothesDetailBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        storage = Firebase.storage
//        val storageRef = storage.reference
//
//        topColRef.whereEqualTo("length","긴팔").get()
//            .addOnSuccessListener {
//                for(doc in it){
//                    val imageRef = storageRef.child(doc["image"].toString())
//                    imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
//                        val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
//                        binding.imageViewDetail.setImageBitmap(bmp)
//                    }
//                    binding.thicknessTemp.text = doc["thickness"].toString()
//                    binding.typeTemp.text = doc["length"].toString()
//                }
//            }
//
//        bottomColRef.whereEqualTo("length","긴팔").get()
//            .addOnSuccessListener {
//                for(doc in it){
//                    val imageRef = storageRef.child(doc["image"].toString())
//                    imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
//                        val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
//                        binding.imageViewDetail.setImageBitmap(bmp)
//                    }
//                    binding.thicknessTemp.text = doc["thickness"].toString()
//                    binding.typeTemp.text = doc["length"].toString()
//                }
//            }
//
//
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//    }
//
//
//
//}
//
//
