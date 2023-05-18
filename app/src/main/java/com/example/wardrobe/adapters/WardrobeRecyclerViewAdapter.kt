package com.example.wardrobe.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.wardrobe.R
import com.example.wardrobe.viewmodel.WardrobeViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class WardrobeRecyclerViewAdapter(private val viewModel: WardrobeViewModel, val context: Context?, val fragment: Fragment):
    RecyclerView.Adapter<WardrobeRecyclerViewAdapter.RecyclerViewViewHolder>() {

    private lateinit var storage: FirebaseStorage


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.itemview_wardrobe,
            parent, false)
        storage = Firebase.storage
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemViewBinding.inflate(inflater,parent,false)
        return RecyclerViewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        // 옷 개수
        return viewModel.topItems.size
    }

    inner class RecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val db = Firebase.firestore
        // Top(상의) Collection Ref
        val topColRef = db.collection("top")

        private val clothesImage: ImageView = itemView.findViewById(R.id.iv_clothes)

        fun setContents(pos: Int){
            with(viewModel.topItems[pos]){
                val imageRef = storage.reference.child(clothesImageUrl)
                imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                    val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                    clothesImage.setImageBitmap(bmp)
                }
                    .addOnFailureListener {
                        Log.e("","firebase storage called failed")
                    }

            }


            clothesImage.setOnClickListener {
                topColRef.whereEqualTo("imageRef",viewModel.topItems[pos].clothesImageUrl).get()
                    .addOnSuccessListener {
                        for(doc in it){
                            val bundle = Bundle()
                            bundle.putString("imageRef",doc["imageRef"].toString())
                            fragment.findNavController().navigate(R.id.action_wardrobeFragment_to_detailClothesFragment,bundle)
                        }
                    }
            }
        }

    }

}