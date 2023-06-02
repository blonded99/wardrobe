package com.example.wardrobe

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.example.wardrobe.DTO.TopBottomDTO
import com.example.wardrobe.databinding.FragmentAddclothesBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AddclothesFragment : Fragment() {
    private lateinit var binding: FragmentAddclothesBinding
    private lateinit var storage: FirebaseStorage

    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    var clothesInfo = TopBottomDTO()
    var isTop : Boolean = true
    var isClothesEntered : Boolean = false

    val db = Firebase.firestore
    // Top(상의) Collection Ref
    val topColRef = db.collection("top")
    // Bottom(하의) Collection Ref
    val bottomColRef = db.collection("bottom")

    companion object{
        const val REQ_GALLERY = 1
        const val REQUEST_CAMERA = 2
    }

    private var isCamera : Boolean = false
    private var currentPhotoPath: String = ""

    private var list = ArrayList<String>() // post image 넘어오는 array

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

                        doSomething(imageUri, isTop)

                    }
                } else {    // 사진 1장 선택
                    val imageUri = getRealPathFromURI(it.data!!)

                    doSomething(imageUri, isTop)


                }

            }
        }
    }

    private val takePictureResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (currentPhotoPath.isNotBlank())
                doSomething(currentPhotoPath, isTop)
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddclothesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivGallery.setOnClickListener {
            val dialog = context?.let { it1 -> Dialog(it1) }

            // dialog 적용할 layout
            dialog?.setContentView(R.layout.custom_dialog_gallery)

            // dialog 모서리 둥글게
            dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_rounding)

            // dialog size 설정
            dialog?.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

            // dialog 화면 밖 터치 액션
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)

            val topButton = dialog.findViewById<ImageButton>(R.id.button_top)
            val bottomButton = dialog.findViewById<ImageButton>(R.id.button_bottom)

            topButton.setOnClickListener {
                isTop = true
//                selectGallery()
                AlertDialog.Builder(context)
                    .setTitle("사진 업로드")
                    .setPositiveButton("카메라") { _, _ ->
                        isCamera = true
                        checkCameraPermission()
                    }
                    .setNegativeButton("갤러리") { _, _ ->
                        isCamera = false
                        selectGallery()
                    }
                    .show()
                dialog.dismiss()
            }

            bottomButton.setOnClickListener {
                isTop = false
//                selectGallery()
                AlertDialog.Builder(context)
                    .setTitle("사진 업로드")
                    .setPositiveButton("카메라") { _, _ ->
                        isCamera = true
                        checkCameraPermission()
                    }
                    .setNegativeButton("갤러리") { _, _ ->
                        isCamera = false
                        selectGallery()
                    }
                    .show()
                dialog.dismiss()
            }

            dialog.show()
        }


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
            if(binding.radioGroupThickness.checkedRadioButtonId == -1 || binding.radioGroupSeason.checkedRadioButtonId == -1
                || binding.radioGroupLength.checkedRadioButtonId == -1 || binding.radioGroupSize.checkedRadioButtonId == -1
                || !isClothesEntered){
                Snackbar.make(binding.root,"사진을 추가하고 모든 버튼을 클릭해주세요.", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            clothesInfo.userID = currentUID
            clothesInfo.brand = binding.editTextBrandName.text.toString()
            clothesInfo.memo = binding.editTextMemo.text.toString()
            if(binding.editTextHashtag.text.startsWith("#")){
                val tempList = binding.editTextHashtag.text.split("#"," ")
                clothesInfo.hashtag = tempList.filter{
                    !(it.equals("") || it.equals(" "))
                }
            }

            if(isTop) {
                topColRef.add(clothesInfo).addOnSuccessListener {
                    Snackbar.make(binding.root,"옷 추가 성공", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addclothesFragment_to_wardrobeFragment)
                }
                    .addOnFailureListener {
                        Snackbar.make(binding.root,"옷 추가 실패", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_addclothesFragment_to_wardrobeFragment)
                    }
            }
            else{
                bottomColRef.add(clothesInfo).addOnSuccessListener {
                    Snackbar.make(binding.root,"옷 추가 성공", Snackbar.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addclothesFragment_to_wardrobeFragment)
                }
                    .addOnFailureListener {
                        Snackbar.make(binding.root,"옷 추가 실패", Snackbar.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_addclothesFragment_to_wardrobeFragment)
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



    private fun getRealPathFromURI(uri: Uri): String{

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


    private fun selectGallery(){
        list.clear()

        var readPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)


        if(readPermission == PackageManager.PERMISSION_DENIED){
            Log.e("","readPerm = ${readPermission}")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
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


    private fun removeBackground(path: String, isTop: Boolean): String?{ // isTop == true -> 상의
        storage = Firebase.storage

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
//        val tempPath = "/data/data/com.example.wardrobe/test23052903.jpg"
        val file = File(path)

        val client = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("image/*".toMediaType())
            )
            .addFormDataPart("uid", currentUID)
            .addFormDataPart("smooth_edges", "true")
            .build()

        var baseUrl = BuildConfig.BASE_URL

        if (isTop) {
            baseUrl += "?include=0"
        } else {
            baseUrl += "?include=1"
        }

        val request: Request = Request.Builder()
            .url(baseUrl.toHttpUrl())
            .method("POST", requestBody)
            .addHeader("Content-Type", "multipart/form-data")
            .build()

        val response: Response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        Log.e("","response= ${responseBody}")
//        response.close()
//        Snackbar.make(binding.root,"Completed", Snackbar.LENGTH_SHORT).show()
        Log.e("","Storage Upload Completed")


        // RESPONSE 성공시
        if (response.isSuccessful) {
            try {
                val jsonArray = JSONArray(responseBody)
                if (jsonArray.length() > 0) {
                    val jsonObject: Any
                    jsonObject = if(isTop)
                        jsonArray.getJSONObject(0)
                    else
                        jsonArray.getJSONObject(1)
                    val path = jsonObject.optString("path", "") // storage에 들어간 사진의 path
                    Log.e("", "path= $path")

                    clothesInfo.imageRef = path
//                    listImageDialog(path)
                    return path

                } else {
//                    Snackbar.make(binding.root,"ARRAY IS EMPTY", Snackbar.LENGTH_SHORT).show()
                    Log.e("","ARRAY IS EMPTY")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
//                Snackbar.make(binding.root,"JSON PARSING ERROR", Snackbar.LENGTH_SHORT).show()
                Log.e("","JSON PARSING ERROR")
            }
        } else {
//            Snackbar.make(binding.root,"RESPONSE FAILED", Snackbar.LENGTH_SHORT).show()
            Log.e("","RESPONSE FAILED")
        }

        return null
    }

    private fun doSomething(imageUri: String, isTop: Boolean){
        GlobalScope.launch(Dispatchers.IO) {
            var storageImageRef: String? = ""
            try {
                if(isCamera) {
                    val originalFile = File(imageUri)
                    val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
                    val rotatedBitmap =
                        rotateImageIfRequired(requireActivity(), bitmap, Uri.fromFile(originalFile))

                    val newFile = File(context?.cacheDir, originalFile.name)
                    val out = FileOutputStream(newFile)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    out.flush()
                    out.close()

                    val path = newFile.absolutePath
                    storageImageRef = removeBackground(path, isTop)
                }
                else {
                    storageImageRef = removeBackground(imageUri, isTop)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            withContext(Dispatchers.Main) {
                if(!storageImageRef.isNullOrBlank())
                    listItemDialog(storageImageRef)
                else{
                    Snackbar.make(binding.root, "사진을 다시 등록해주세요", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }



    // path를 넘겨받고 storage에서 이미지 받아와서 imageView에 세팅
    private fun listItemDialog(path: String?){
        val imageRef = path?.let { Firebase.storage.reference.child(it) }
        Glide.with(binding.root.context)
            .asBitmap()
            .load(imageRef)
            .into(BitmapImageViewTarget(binding.ivGallery))
        isClothesEntered = true
    }


    private fun takePictureFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // 파일 생성에서 문제 생기면
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireActivity(),
                    "${BuildConfig.APPLICATION_ID}.provider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureResultLauncher.launch(takePictureIntent)
            }
        }
    }


    private fun createImageFile(): File {
        // 파일 이름
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA)
        } else {
            takePictureFromCamera()
        }
    }

    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei: ExifInterface = ExifInterface(input!!)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        input.close()
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)

            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)

            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)

            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> flipImage(img, horizontal = true, vertical = false)

            ExifInterface.ORIENTATION_FLIP_VERTICAL -> flipImage(img, horizontal = false, vertical = true)

            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    private fun flipImage(img: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }


}


