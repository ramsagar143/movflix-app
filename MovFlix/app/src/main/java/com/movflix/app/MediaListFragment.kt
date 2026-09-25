package com.movflix.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MediaListFragment : Fragment(R.layout.fragment_list) {

    private val items = mutableListOf<Media>()
    private lateinit var adapter: MediaAdapter

    companion object {
        fun newInstance(mode: String) = MediaListFragment().apply {
            arguments = Bundle().apply { putString("mode", mode) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val error = view.findViewById<TextView>(R.id.errorText)
        val retry = view.findViewById<Button>(R.id.retryBtn)

        adapter = MediaAdapter(items)
        recycler.layoutManager = GridLayoutManager(context, 3)
        recycler.adapter = adapter

        fun load() {
            progress.visibility = View.VISIBLE
            error.visibility = View.GONE
            retry.visibility = View.GONE
            val mode = arguments?.getString("mode") ?: "movie"
            val call: Call<MediaResponse> = when (mode) {
                "movie" -> Api.service.movies()
                "tv" -> Api.service.shows()
                "anime" -> Api.service.anime()
                else -> Api.service.trending()
            }
            call.enqueue(object : Callback<MediaResponse> {
                override fun onResponse(call: Call<MediaResponse>, response: Response<MediaResponse>) {
                    progress.visibility = View.GONE
                    items.clear()
                    items.addAll(response.body()?.results ?: emptyList())
                    adapter.notifyDataSetChanged()
                    if (items.isEmpty()) {
                        error.text = "Kuch nahi mila."
                        error.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<MediaResponse>, t: Throwable) {
                    progress.visibility = View.GONE
                    error.text = "Network error: ${'$'}{t.message}"
                    error.visibility = View.VISIBLE
                    retry.visibility = View.VISIBLE
                }
            })
        }

        retry.setOnClickListener { load() }
        load()
    }
}
