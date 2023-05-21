package com.example.wardrobe

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.databinding.FragmentDoCodiBinding
import com.example.wardrobe.viewmodel.WardrobeViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class DoCodiFragment : Fragment() {
    private lateinit var binding: FragmentDoCodiBinding
    private lateinit var storage: FirebaseStorage

//    private val viewModel by viewModels<WardrobeViewModel>()

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var scaleGestureDetector2: ScaleGestureDetector

    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    var topRef = ""
    var bottomRef = ""

    lateinit var topBitmap : Bitmap
    lateinit var bottomBitmap : Bitmap

    val locationTop = IntArray(2)
    val locationBottom = IntArray(2)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            topRef = bundle.getString("topRef","")
            bottomRef = bundle.getString("bottomRef","")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDoCodiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTop(topRef)
        setBottom(bottomRef)


        scaleGestureDetector = ScaleGestureDetector(requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (scaleFactor > 1) {
                    // 축소
                    binding.ivTop.scaleX /= scaleFactor
                    binding.ivTop.scaleY /= scaleFactor
                } else {
                    // 확대
                    binding.ivTop.scaleX *= scaleFactor
                    binding.ivTop.scaleY *= scaleFactor
                }
                return true
            }
        })

        scaleGestureDetector2 = ScaleGestureDetector(requireContext(), object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
                if (scaleFactor > 1) {
                    // 축소
                    binding.ivBottom.scaleX /= scaleFactor
                    binding.ivBottom.scaleY /= scaleFactor
                } else {
                    // 확대
                    binding.ivBottom.scaleX *= scaleFactor
                    binding.ivBottom.scaleY *= scaleFactor
                }
                return true
            }
        })


        binding.ivTop.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                scaleGestureDetector.onTouchEvent(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                    }
                    else -> return false
                }
                return true
            }
        })

        binding.ivBottom.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                scaleGestureDetector2.onTouchEvent(event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        view.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                    }
                    else -> return false
                }
                return true
            }
        })

        binding.buttonCombine.setOnClickListener {
            binding.ivTop.getLocationOnScreen(locationTop)
            binding.ivBottom.getLocationOnScreen(locationBottom)

            val position1 = floatArrayOf(locationTop[0].toFloat(), locationTop[1].toFloat())
            val position2 = floatArrayOf(locationBottom[0].toFloat(), locationBottom[1].toFloat())


            val adjustedClothes1 = adjustBitmap(topBitmap, binding.ivTop.scaleX, binding.ivTop.scaleY)
            val adjustedClothes2 = adjustBitmap(bottomBitmap, binding.ivBottom.scaleX, binding.ivBottom.scaleY)

            val combineBitmap = combineImages(adjustedClothes1,position1,adjustedClothes2,position2)

            val uniqueId = generateRandomString(20)
            val path = "${currentUID}/codi/${uniqueId}/codi.webp"
            val imageRef = storage.reference.child(path)

            val baos = ByteArrayOutputStream()
            combineBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, baos)
            val data = baos.toByteArray()

            imageRef.putBytes(data).addOnCompleteListener{
                if(it.isSuccessful) {
                    Snackbar.make(binding.root, "Storage upload completed", Snackbar.LENGTH_SHORT)
                        .show()

                    val bundle = Bundle()
                    bundle.putString("imageRef",path)
                    bundle.putString("topRef",topRef)
                    bundle.putString("bottomRef",bottomRef)
                    findNavController().navigate(R.id.action_doCodiFragment_to_addCodiFragment,bundle)
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


    private fun setTop(path: String){
        storage = Firebase.storage

        if(path != ""){ // path is always not null
            val imageRef = storage.reference.child(path)
            imageRef.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                topBitmap = bmp
                binding.ivTop.setImageBitmap(bmp)
            }
        }

    }

    private fun setBottom(path: String){
        storage = Firebase.storage

        if(path != ""){ // path is always not null
            val imageRef = storage.reference.child(path)
            imageRef.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
                val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                bottomBitmap = bmp
                binding.ivBottom.setImageBitmap(bmp)
            }
        }

    }


    private fun adjustBitmap(original: Bitmap, scaleX: Float, scaleY: Float): Bitmap {
        val width = (original.width * scaleX).toInt()
        val height = (original.height * scaleY).toInt()
        return Bitmap.createScaledBitmap(original, width, height, false)
    }


    private fun combineImages(clothes1: Bitmap, position1: FloatArray, clothes2: Bitmap, position2: FloatArray): Bitmap {
        val minX = Math.min(position1[0], position2[0])
        val minY = Math.min(position1[1], position2[1])
        val maxX = Math.max(position1[0] + clothes1.width, position2[0] + clothes2.width)
        val maxY = Math.max(position1[1] + clothes1.height, position2[1] + clothes2.height)

        val width = (maxX - minX).toInt()
        val height = (maxY - minY).toInt()

        val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(combined)
        canvas.drawBitmap(clothes1, position1[0] - minX, position1[1] - minY, null)
        canvas.drawBitmap(clothes2, position2[0] - minX, position2[1] - minY, null)

        return combined
    }

    private fun generateRandomString(length: Int): String {
        val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }



}


