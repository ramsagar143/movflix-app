package com.movflix.app

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val items = mutableListOf<Media>()
    private lateinit var adapter: MediaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input = view.findViewById<EditText>(R.id.searchInput)
        val btn = view.findViewById<Button>(R.id.searchBtn)
        val recycler = view.findViewById<RecyclerView>(R.id.recycler)
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val emptyText = view.findViewById<TextView>(R.id.emptyText)

        adapter = MediaAdapter(items)
        recycler.layoutManager = GridLayoutManager(context, 3)
        recycler.adapter = adapter

        fun doSearch() {
            val q = input.text.toString().trim()
            if (q.length < 2) {
                Toast.makeText(context, "Kam se kam 2 characters likho", Toast.LENGTH_SHORT).show()
                return
            }
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(input.windowToken, 0)
            progress.visibility = View.VISIBLE
            emptyText.visibility = View.GONE

            Api.service.search(q).enqueue(object : Callback<MediaResponse> {
                override fun onResponse(call: Call<MediaResponse>, response: Response<MediaResponse>) {
                    progress.visibility = View.GONE
                    items.clear()
                    items.addAll(response.body()?.results ?: emptyList())
                    adapter.notifyDataSetChanged()
                    if (items.isEmpty()) {
                        emptyText.text = "\"$q\" ke liye kuch nahi mila"
                        emptyText.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<MediaResponse>, t: Throwable) {
                    progress.visibility = View.GONE
                    Toast.makeText(context, "Search failed: ${'$'}{t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        btn.setOnClickListener { doSearch() }
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { doSearch(); true } else false
        }
    }
}
