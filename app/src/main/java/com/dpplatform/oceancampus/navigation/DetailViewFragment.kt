package com.dpplatform.oceancampus.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dpplatform.oceancampus.R
import com.dpplatform.oceancampus.navigation.model.AlarmDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import com.dpplatform.oceancampus.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth

class DetailViewFragment:Fragment() {
    var fireStore: FirebaseFirestore? = null
    var uid : String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)
        fireStore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()

        init {

            fireStore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                if (querySnapshot == null) return@addSnapshotListener

                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged() //값이 새로고침 되도록.
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView

            viewholder.detailviewitem_prifile_textview.text = contentDTOs!![position].userId

            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_imageview_content)

            viewholder.detailviewitem_explain_textview.text = contentDTOs!![position].explain

            viewholder.detailviewitem_favoritecounter_textview.text = "Likes" + contentDTOs!![position].favoriteCount

            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageUrl).into(viewholder.detailviewitem_profile_image)

            viewholder.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }
            if (contentDTOs!![position].favorites.containsKey(uid)) {
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else {
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }

            //다른사람의 프로필 클릭
            viewholder.detailviewitem_profile_image.setOnClickListener {
                var fragment = UserFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[position].uid)
                bundle.putString("userId",contentDTOs[position].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()

            }
            //말풍선 클릭시.
            viewholder.detailviewitem_comment_imageview.setOnClickListener { v->
                var intent = Intent(v.context , CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[position])
                intent.putExtra("destinationUid",contentDTOs[position].uid)
                startActivity(intent)
            }

        }

        override fun getItemCount(): Int {
            return contentDTOs.size


        }

        fun favoriteEvent(position: Int) {
            var tsDoc = fireStore?.collection("images")?.document(contentUidList[position])
            fireStore?.runTransaction { transaction ->

                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)


                } else {
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)

                }
                transaction.set(tsDoc, contentDTO)
            }
        }
        fun favoriteAlarm(destinationUid : String){
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        }




    }
}