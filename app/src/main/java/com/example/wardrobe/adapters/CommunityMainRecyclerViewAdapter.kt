package com.example.wardrobe.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.wardrobe.R
import com.example.wardrobe.viewmodel.CommunityItem
import com.example.wardrobe.viewmodel.CommunityViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class CommunityMainRecyclerViewAdapter(private val viewModel: CommunityViewModel, val context: Context?, val fragment: Fragment):
    RecyclerView.Adapter<CommunityMainRecyclerViewAdapter.RecyclerViewViewHolder>() {

    private lateinit var storage: FirebaseStorage

    // 회원가입 구현 시 이부분 firebase auth에서 받아올 것
    val currentUID = "3t6Dt8DleiZXrzzf696dgF15gJl2"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.itemview_community,
            parent, false)
        storage = Firebase.storage

        return RecyclerViewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewViewHolder, position: Int) {
        holder.setContents(position)
    }

    override fun getItemCount(): Int {
        // 커뮤니티 메인 아이템 개수
        return viewModel.communityMainItems.size
    }

    inner class RecyclerViewViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val db = Firebase.firestore
        // Set(코디) Collection Ref
        val setColRef = db.collection("set")

        private val clothesImage: ImageView = itemView.findViewById(R.id.iv_clothes)
        private val buttonLike: ImageButton = itemView.findViewById(R.id.button_like)

        fun setContents(pos: Int){
            with(viewModel.communityMainItems[pos]){
                val imageRef = storage.reference.child(clothesImageUrl)
                imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener {
                    val bmp = BitmapFactory.decodeByteArray(it,0,it.size)
                    clothesImage.setImageBitmap(bmp)
                }
                    .addOnFailureListener {
                        Log.e("","firebase storage called failed")
                    }

                if(isLiked)
                    buttonLike.setBackgroundResource(R.drawable.icon_heart)
                else
                    buttonLike.setBackgroundResource(R.drawable.icon_heart_empty)
            }

            buttonLike.setOnClickListener {
                setColRef.whereEqualTo("imageRef",viewModel.communityMainItems[pos].clothesImageUrl).get()
                    .addOnSuccessListener {
                        for(doc in it){
                            val tempList = doc["likedUser"] as MutableList<String> ?: mutableListOf()
                            if (tempList.contains(currentUID)) {
                                tempList.remove(currentUID)
                                setColRef.document(doc.id).update("likedUser",tempList)
                                buttonLike.setBackgroundResource(R.drawable.icon_heart_empty)
                                return@addOnSuccessListener
                            }
                            else {
                                tempList.add(currentUID)
                                setColRef.document(doc.id).update("likedUser",tempList)
                                buttonLike.setBackgroundResource(R.drawable.icon_heart)
                                return@addOnSuccessListener
                            }
                        }
                    }
            }

        }

    }

}