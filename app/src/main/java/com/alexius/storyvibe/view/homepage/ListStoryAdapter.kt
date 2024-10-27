package com.alexius.storyvibe.view.homepage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alexius.storyvibe.R
import com.alexius.storyvibe.data.remote.response.ListStoryItem
import com.alexius.storyvibe.data.remote.response.StoryResponse
import com.alexius.storyvibe.databinding.ItemStoryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ListStoryAdapter : PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListStoryViewHolder>(
    DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListStoryViewHolder, position: Int) {
        val storyItem = getItem(position)
        if (storyItem != null) {
            holder.bind(storyItem)
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(storyItem, holder.binding)
            }
        }
    }

    class ListStoryViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(storyItem: ListStoryItem) {
            binding.tvItemName.text = storyItem.name
            binding.tvItemDesc.text = storyItem.description
            Glide.with(itemView.context)
                .load(storyItem.photoUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.imgPoster)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryItem, view: ItemStoryBinding)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<ListStoryItem> =
            object : DiffUtil.ItemCallback<ListStoryItem>() {
                override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem == newItem
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}