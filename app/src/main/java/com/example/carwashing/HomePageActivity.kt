package com.example.carwashing

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class HomePageFragment : Fragment() {

    private val TAG = "HomePageFragment"
    private lateinit var userInfoList: MutableList<UserInfo>
    private lateinit var filteredUserInfoList: MutableList<UserInfo>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserInfoAdapter
    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_homepage, container, false)

        userInfoList = mutableListOf()
        filteredUserInfoList = mutableListOf()
        recyclerView = view.findViewById(R.id.recyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)

        adapter = UserInfoAdapter(filteredUserInfoList) { userInfo ->
            val intent = Intent(context, KurumsalCardActivity::class.java).apply {
                putExtra("companyName", userInfo.companyName)
                putExtra("profileImageUrl", userInfo.profileImageUrl)
                putExtra("sellerId", userInfo.sellerId)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchAllUserData()

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUserInfoList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    private fun fetchAllUserData() {
        val db = FirebaseFirestore.getInstance()
        db.collectionGroup("kurumsal")
            .get()
            .addOnSuccessListener { userInfoDocuments ->
                for (userInfoDocument in userInfoDocuments) {
                    val address = userInfoDocument.getString("Address")
                    val companyName = userInfoDocument.getString("CompanyName")
                    val profileImageUrl = userInfoDocument.getString("profileImageUrl")
                    val sellerId = userInfoDocument.reference.parent.parent?.id

                    val userInfo = UserInfo(address, companyName, profileImageUrl, sellerId)
                    userInfoList.add(userInfo)
                }
                filterUserInfoList(searchEditText.text.toString())
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error getting user info", e)
            }
    }

    private fun filterUserInfoList(query: String) {
        filteredUserInfoList.clear()
        if (query.isEmpty()) {
            filteredUserInfoList.addAll(userInfoList)
        } else {
            val lowerCaseQuery = query.toLowerCase()
            for (userInfo in userInfoList) {
                if (userInfo.companyName?.toLowerCase()?.contains(lowerCaseQuery) == true) {
                    filteredUserInfoList.add(userInfo)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    data class UserInfo(
        val address: String?,
        val companyName: String?,
        val profileImageUrl: String?,
        val sellerId: String? // Satıcı UID'si
    )

    private inner class UserInfoAdapter(
        private val userInfoList: List<UserInfo>,
        private val itemClickListener: (UserInfo) -> Unit
    ) : RecyclerView.Adapter<UserInfoAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val companyNameTextView: TextView = itemView.findViewById(R.id.companyNameTextView)
            val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
            val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
            init {
                itemView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener(userInfoList[position])
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.kurumsal_card_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val userInfo = userInfoList[position]
            holder.companyNameTextView.text = userInfo.companyName
            holder.addressTextView.text = userInfo.address
            userInfo.profileImageUrl?.let { imageUrl ->
                Glide.with(holder.itemView)
                    .load(imageUrl)
                    .into(holder.profileImageView)
            }
        }

        override fun getItemCount(): Int {
            return userInfoList.size
        }
    }
}
