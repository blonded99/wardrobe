package com.example.wardrobe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.databinding.ClothesAddBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class MainFragment : Fragment() {
    private lateinit var binding: ClothesAddBinding
    private lateinit var storage: FirebaseStorage

    val db = Firebase.firestore
    // Top(상의) Collection Ref
    val topColRef = db.collection("top")

    // Top(하의) Collection Ref
    val bottomColRef = db.collection("bottom")

    private var list = ArrayList<String>() // post image 넘어오는 array

    var strRef = ""

    companion object{
        const val REQ_GALLERY = 1

        var thickness_thick = 0
        var thickness_normal = 0
        var thickness_thin = 0

        var category_top = 0
        var category_bottom = 0
        var category_coordination = 0

        var clothestype_short = 0
        var clothestype_long = 0
        var clothestype_sleeveless = 0
    }

    private val imageResultSingle = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            result?.data?.let { it ->
                if (it.clipData != null) {   // 사진 여러장 선택
                    val count = it.clipData!!.itemCount
                    if (count > 1) {
                        // 아래 toast를 다른 표시 방법으로 변경할 것
                        Toast.makeText(context, "사진은 1장만 선택 가능합니다.", Toast.LENGTH_SHORT)
                            .show()
                        return@registerForActivityResult
                    }

                    for (i in 0 until count) {
                        val imageUri = getRealPathFromURI(it.clipData!!.getItemAt(i).uri)

                        // Uri로 할거
                        doSomething(imageUri)


                    }
                } else {    // 사진 1장 선택
                    val imageUri = getRealPathFromURI(it.data!!)


                    // Uri로 할거
                    doSomething(imageUri)


                }

            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ClothesAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonClothesAdd.setOnClickListener {
            selectGallery()
        }

        binding.buttonThicknessThick.setOnClickListener {
            if(thickness_thick == 0) {
                thickness_thick = 1
                binding.buttonThicknessThick.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                thickness_thick = 0
                binding.buttonThicknessThick.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }
        }

        binding.buttonThicknessNormal.setOnClickListener {
            if(thickness_normal == 0) {
                thickness_normal = 1
                binding.buttonThicknessNormal.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                thickness_normal = 0
                binding.buttonThicknessNormal.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }
        }

        binding.buttonThicknessThin.setOnClickListener {
            if(thickness_thin == 0) {
                thickness_thin = 1
                binding.buttonThicknessThin.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                thickness_thin = 0
                binding.buttonThicknessThin.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }
        }

        binding.buttonCategoryTop.setOnClickListener {
            if(category_top == 0) {
                category_top = 1
                binding.buttonCategoryTop.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                category_top = 0
                binding.buttonCategoryTop.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }

        }

        binding.buttonCategoryBottom.setOnClickListener {
            if(category_bottom == 0) {
                category_bottom = 1
                binding.buttonCategoryBottom.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                category_bottom = 0
                binding.buttonCategoryBottom.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }

        }

        binding.buttonCategoryCoordination.setOnClickListener {
            if(category_coordination == 0) {
                category_coordination = 1
                binding.buttonCategoryCoordination.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                category_coordination = 0
                binding.buttonCategoryCoordination.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }

        }

        binding.buttonClothestypeShort.setOnClickListener {
            if (clothestype_short == 0) {
                clothestype_short = 1
                binding.buttonClothestypeShort.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                clothestype_short = 0
                binding.buttonClothestypeShort.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }
        }

        binding.buttonClothestypeLong.setOnClickListener {
            if (clothestype_long == 0) {
                clothestype_long = 1
                binding.buttonClothestypeLong.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                clothestype_long = 0
                binding.buttonClothestypeLong.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }

        }

        binding.buttonClothestypeSleeveless.setOnClickListener {
            if (clothestype_sleeveless == 0) {
                clothestype_sleeveless = 1
                binding.buttonClothestypeSleeveless.setBackgroundResource(R.drawable.icon_clothes_type_select_navy)
            }
            else {
                clothestype_sleeveless = 0
                binding.buttonClothestypeSleeveless.setBackgroundResource(R.drawable.icon_clothes_type_select_white)
            }

        }

        binding.buttonClothesSave.setOnClickListener {

            var thickness = ""
            var length = ""
            if(thickness_thick == 1)
                thickness = "두꺼움"
            else if(thickness_normal == 1)
                thickness = "보통"
            else
                thickness = "얇음"

            if(clothestype_short == 1)
                length = "반팔"
            else if(clothestype_long == 1)
                length = "긴팔"
            else
                length = "민소매"

            val itemMap = hashMapOf(
                "thickness" to thickness,
                "length" to length,
                "image" to strRef,
            )

            if(category_top == 1){
                topColRef.add(itemMap).addOnSuccessListener {
                    Log.e("","firestore add success")
                    findNavController().navigate(R.id.action_mainFragment_to_detailFragment)
                }
            }
            else if(category_bottom == 1){
                bottomColRef.add(itemMap).addOnSuccessListener {
                    Log.e("","firestore add success")
                    findNavController().navigate(R.id.action_mainFragment_to_detailFragment)
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


    fun getRealPathFromURI(uri: Uri): String{

        val buildName = Build.MANUFACTURER
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(uri, proj, null, null, null)
        if(cursor!!.moveToFirst()){
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }

    fun selectGallery(){
        list.clear()

        var readPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        var writePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // 권한요청 안 뜨는 오류 때문에 임시로. 실제로는 이렇게 하면 안됨.
        readPermission = 1
        writePermission = 1

        if(readPermission == PackageManager.PERMISSION_DENIED || writePermission == PackageManager.PERMISSION_DENIED){
            Log.e("","readPerm = ${readPermission}")
            Log.e("","writePerm = ${writePermission}")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
                REQ_GALLERY
            )
        }else{
            //사진 1장 선택
            var intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            imageResultSingle.launch(intent)

        }
    }


    // 일단 여기서 세팅까지 한번에
    suspend fun removeBackground(path: String){
        storage = Firebase.storage
        val storageRef = storage.reference
        var imageRef: StorageReference
        val itemsString = mutableListOf<String>()

        if (context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
                REQ_GALLERY
            )

        }

        // 갤러리 열 때 권한요청이 뜨지 않는 오류가 있어서 일단 임시로 파일 path를 다른 곳으로 지정
        val tempPath = "/data/data/com.example.wardrobe/cloth.png"
        val file = File(tempPath)

        val client = OkHttpClient().newBuilder().build()
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("image/*".toMediaType())
            )
            .build()
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:5000/seg_clothes".toHttpUrl())
            .method("POST", requestBody)
            .addHeader("Content-Type", "multipart/form-data")
            .build()
        val response: Response = client.newCall(request).execute()
        Log.e("","response= ${response}")
//        response.close()
        Snackbar.make(binding.root,"Completed", Snackbar.LENGTH_SHORT).show()


        listImageDialog()

        withContext(Dispatchers.Main) {
            // handle the response on the main thread
        }
    }

    fun doSomething(imageUri: String){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val path = imageUri
                removeBackground(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }



    // storage에서 이미지 받아와서(일단 랜덤) 세팅까지
    private fun listImageDialog(){
        storage = Firebase.storage
        val storageRef = storage.reference

        storage.reference.listAll()
            .addOnSuccessListener {
                for(i in it.items){
                    strRef = i.name
                    if(strRef != ""){
                        val imageRef = storageRef.child(strRef)
                        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                            binding.bgImg1.setImageBitmap(bmp)
                            binding.buttonClothesAdd.visibility = View.INVISIBLE
                        }
                    }
                    return@addOnSuccessListener
                }
            }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_GALLERY) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                selectGallery()
            } else {
                // Permission denied
                Toast.makeText(context, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }



}


