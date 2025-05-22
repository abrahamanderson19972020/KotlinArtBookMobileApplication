package com.artbook.artbookmobileapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.artbook.artbookmobileapp.databinding.ItemArtBinding

class ArtAdapter(private val artList: List<Art>) : RecyclerView.Adapter<ArtAdapter.ArtViewHolder>() {

    inner class ArtViewHolder(val binding: ItemArtBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtViewHolder {
        val binding = ItemArtBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtViewHolder, position: Int) {
        val art = artList[position]
        holder.binding.artNameText.text = art.name
        holder.binding.artistNameText.text = art.artist
        holder.binding.yearText.text = art.year
        holder.binding.imageView.setImageBitmap(art.image)
    }

    override fun getItemCount(): Int = artList.size
}
