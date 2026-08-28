package com.pinehotel.hospitality.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pinehotel.hospitality.R
import com.pinehotel.hospitality.models.LiveTvChannel

class LiveTvViewModel : ViewModel() {

    private val _channels = MutableLiveData<List<LiveTvChannel>>()
    val channels: LiveData<List<LiveTvChannel>> = _channels

    fun fetchChannels() {
        val dummyChannels = listOf(
            LiveTvChannel(1, "Sports One", "Sports", R.drawable.ic_live_tv, "http://example.com/sports1.m3u8"),
            LiveTvChannel(2, "News Global", "News", R.drawable.ic_live_tv, "http://example.com/news.m3u8"),
            LiveTvChannel(3, "Movie Central", "Entertainment", R.drawable.ic_movies, "http://example.com/movies.m3u8"),
            LiveTvChannel(4, "Music Hit", "Music", R.drawable.ic_live_tv, "http://example.com/music.m3u8"),
            LiveTvChannel(5, "Discovery Plus", "Documentary", R.drawable.ic_hotel_info, "http://example.com/discovery.m3u8"),
            LiveTvChannel(6, "Sports Two", "Sports", R.drawable.ic_live_tv, "http://example.com/sports2.m3u8"),
            LiveTvChannel(7, "Cartoon Network", "Kids", R.drawable.ic_live_tv, "http://example.com/kids.m3u8")
        )
        _channels.value = dummyChannels
    }
}
