package com.example.wardrobe

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wardrobe.databinding.FragmentThirdBinding

class ThirdFragment : Fragment() {
    private lateinit var binding: FragmentThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCamera.setOnClickListener {
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
                dialog.dismiss()
            }

            bottomButton.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()

    }



}


