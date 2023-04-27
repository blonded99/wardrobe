package com.example.wardrobe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.example.wardrobe.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File

class MainActivity : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    lateinit var binding: ActivityMainBinding


    private var list = ArrayList<String>() // post image 넘어오는 array

    private val REQUEST_CODE = 1001
    companion object{
        const val PHOTO_MAX_LENGTH = 10
        const val REQ_GALLERY = 1
        const val PARAM_KEY_IMAGE = "image"
    }

    private val imageResultSingle = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if(result.resultCode == Activity.RESULT_OK){
            result?.data?.let { it ->
                if (it.clipData != null) {   // 사진 여러장 선택
                    val count = it.clipData!!.itemCount
                    if (count > 1) {
                        // 아래 toast를 다른 표시 방법으로 변경할 것
//                        Toast.makeText(context, "사진은 1장만 선택 가능합니다.", Toast.LENGTH_SHORT)
//                            .show()
                        return@registerForActivityResult
                    }

                    for (i in 0 until count) {
                        val imageUri = getRealPathFromURI(it.clipData!!.getItemAt(i).uri)
//                        refreshBackgroundImage(imageUri) // 배경이미지 새로고침
                    }
                } else {    // 사진 1장 선택
                    val imageUri = getRealPathFromURI(it.data!!)
//                    refreshBackgroundImage(imageUri) // 배경이미지 새로고침
                }

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storage = Firebase.storage
        val storageRef = storage.reference
        var imageRef: StorageReference
        val itemsString = mutableListOf<String>()

        binding.button.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val path = "/data/data/$packageName/cloth.png"
                    removeBackground(path)
                    binding.imageView.visibility = View.INVISIBLE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        binding.button2.setOnClickListener {
//                listImageDialog(itemsString)
//                itemsString.forEach {
//                    Log.e("", it)
//                    imageRef = storageRef.child(it)
//                    displayImageRef(imageRef)
//                    return@setOnClickListener
//                }

            val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if(readPermission == PackageManager.PERMISSION_DENIED || writePermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                    REQ_GALLERY
                )
            }else{
                // 사진 1장 선택
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE)
            }

//            val intent = Intent(Intent.ACTION_PICK)
//            intent.type = "image/*"
//            startActivityForResult(intent, REQUEST_CODE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data

            // Do something with the selected image URI, such as displaying it in an ImageView
            if(selectedImageUri != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val path = getFilePathFromUri(selectedImageUri)
                        removeBackground(path)
                        binding.imageView.visibility = View.INVISIBLE
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }


        }
    }

    suspend fun removeBackground(path: String){
        storage = Firebase.storage
        val storageRef = storage.reference
        var imageRef: StorageReference
        val itemsString = mutableListOf<String>()

        val file = File(path)

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
        Snackbar.make(binding.root,"Completed",Snackbar.LENGTH_SHORT).show()


        withContext(Dispatchers.Main) {
            // handle the response on the main thread
        }
    }

    private fun listImageDialog(itemsString: MutableList<String>){
        storage.reference.listAll()
            .addOnSuccessListener {
                for(i in it.items){
                    itemsString.add(i.name)
                }
            }
    }

    private fun displayImageRef(imageRef: StorageReference?){
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
            binding.imageView.setImageBitmap(bmp)
            binding.imageView.visibility = View.VISIBLE
        }
    }

    fun getRealPathFromURI(uri: Uri): String{

        val buildName = Build.MANUFACTURER
        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = MainActivity().contentResolver?.query(uri, proj, null, null, null)
        if(cursor!!.moveToFirst()){
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }

    fun selectGallery(){
        list.clear()

        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if(readPermission == PackageManager.PERMISSION_DENIED || writePermission == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
                REQ_GALLERY
            )
        }else{
            // 사진 1장 선택
            var intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            imageResultSingle.launch(intent)
        }
    }

    private fun getFilePathFromUri(uri: Uri): String {
        var filePath = ""
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            filePath = it.getString(columnIndex)
            it.close()
        }
        return filePath
    }
}