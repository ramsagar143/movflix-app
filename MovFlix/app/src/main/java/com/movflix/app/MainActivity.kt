package com.movflix.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.movflix.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 5
            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> HomeFragment()
                1 -> MediaListFragment.newInstance("tv")
                2 -> MediaListFragment.newInstance("movie")
                3 -> MediaListFragment.newInstance("anime")
                else -> SearchFragment()
            }
        }
        binding.pager.offscreenPageLimit = 4

        val titles = listOf("Home", "Shows", "Movie", "Anime", "Search")
        TabLayoutMediator(binding.tabs, binding.pager) { tab, pos ->
            tab.text = titles[pos]
        }.attach()
    }
}
