package com.movflix.app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load

fun openDetail(v: View, m: Media) {
    val i = Intent(v.context, DetailActivity::class.java)
    // Intent keys updated to match DetailActivity requirements
    i.putExtra("MEDIA_ID", m.id)
    i.putExtra("MEDIA_TYPE", m.type ?: "movie")
    i.putExtra("title", m.displayTitle)
    v.context.startActivity(i)
}

class MediaAdapter(private val items: List<Media>) :
    RecyclerView.Adapter<MediaAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val poster: ImageView = v.findViewById(R.id.poster)
        val title: TextView = v.findViewById(R.id.cardTitle)
        val rating: TextView = v.findViewById(R.id.cardRating)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_media, p, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val m = items[pos]
        h.poster.load(Api.img(m.posterPath)) {
            placeholder(R.color.card_bg)
            error(R.color.card_bg)
        }
        h.title.text = m.displayTitle
        h.rating.text = "★ %.1f".format(m.voteAverage ?: 0.0)
        h.itemView.setOnClickListener { openDetail(it, m) }
    }
}

class BannerAdapter(private val items: List<Media>) :
    RecyclerView.Adapter<BannerAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.bannerImg)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_banner, p, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(h: VH, pos: Int) {
        val m = items[pos]
        h.img.load(Api.img(m.backdropPath ?: m.posterPath, "w780")) {
            placeholder(R.color.card_bg)
            error(R.color.card_bg)
        }
        h.itemView.setOnClickListener { openDetail(it, m) }
    }
}
