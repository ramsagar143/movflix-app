package com.movflix.app

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val trending = mutableListOf<Media>()
    private val movies = mutableListOf<Media>()
    private val shows = mutableListOf<Media>()
    private val anime = mutableListOf<Media>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val banner: ViewPager2 = view.findViewById(R.id.banner)
        val progress: ProgressBar = view.findViewById(R.id.progress)

        fun hRow(rvId: Int, list: MutableList<Media>): MediaAdapter {
            val rv = view.findViewById<RecyclerView>(rvId)
            val a = MediaAdapter(list)
            rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rv.adapter = a
            return a
        }

        val aTrending = hRow(R.id.rowTrending, trending)
        val aMovies = hRow(R.id.rowMovies, movies)
        val aShows = hRow(R.id.rowShows, shows)
        val aAnime = hRow(R.id.rowAnime, anime)

        load(progress, banner, Api.service.trending(), trending, aTrending)
        load(progress, null, Api.service.movies(), movies, aMovies)
        load(progress, null, Api.service.shows(), shows, aShows)
        load(progress, null, Api.service.anime(), anime, aAnime)
    }

    private fun load(
        progress: ProgressBar,
        banner: ViewPager2?,
        call: Call<MediaResponse>,
        list: MutableList<Media>,
        adapter: MediaAdapter
    ) {
        call.enqueue(object : Callback<MediaResponse> {
            override fun onResponse(call: Call<MediaResponse>, response: Response<MediaResponse>) {
                progress.visibility = View.GONE
                list.clear()
                list.addAll(response.body()?.results ?: emptyList())
                adapter.notifyDataSetChanged()
                banner?.adapter = BannerAdapter(list.toList())
            }

            override fun onFailure(call: Call<MediaResponse>, t: Throwable) {
                progress.visibility = View.GONE
            }
        })
    }
}
