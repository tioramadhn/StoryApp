package com.dicoding.storyapp.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.model.UserModel
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding
import com.dicoding.storyapp.ui.DetailActivity
import com.dicoding.storyapp.utils.withDateFormat

class StoryListAdapter :
    PagingDataAdapter<ListStoryItem, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            Log.d("PAGING : adapter :", data.toString())
            holder.bind(data)
        }
    }

    class MyViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(binding.imgUserPhoto.context)
                .load(R.drawable.profile)
                .circleCrop()
                .into(binding.imgUserPhoto)
            binding.tvItemName.text = data.name
            Glide.with(binding.imgPhoto.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgPhoto)
            binding.tvItemDate.text =
                binding.tvItemDate.context.getString(R.string.date, data.createdAt.withDateFormat())
            itemView.setOnClickListener {
                val person = UserModel(
                    name = data.name,
                    photo = data.photoUrl,
                    description = data.description,
                    createdAt = data.createdAt
                )
                val moveWithObjectIntent = Intent(itemView.context, DetailActivity::class.java)
                moveWithObjectIntent.putExtra(DetailActivity.EXTRA_PERSON, person)
                itemView.context.startActivity(moveWithObjectIntent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}