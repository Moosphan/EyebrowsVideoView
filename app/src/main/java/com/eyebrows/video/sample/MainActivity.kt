package com.eyebrows.video.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
    }

    private fun initialize() {

        videoUploadBtn.setOnClickListener {
            Toast.makeText(this, "Upload video", Toast.LENGTH_SHORT).show()
        }

        val videos = arrayListOf(
            VideoData(R.raw.video1, R.drawable.thumbnail1, "just now", "但愿人长久，千里共婵娟🌛"),
            VideoData(R.raw.video2, R.drawable.thumbnail2, "an hour ago", "Long time no see so happy moment in my life, it reminds me of my childhood."),
            VideoData(R.raw.video3, R.drawable.thumbnail3, "10 hours ago", "The AR is so cool! I am glad to play this type games."),
            VideoData(R.raw.video4, R.drawable.thumbnail4, "2 days ago", "There is something just like this... "),
            VideoData(R.raw.video5, R.drawable.thumbnail5, "September 9, 2019", "Tomorrow is always fresh, with no mistakes in it yet.")

        )

        val mLayoutManager = LinearLayoutManager(this)
        val videoListAdapter = VideoListAdapter(videos)
        videoRecyclerView.layoutManager = mLayoutManager
        videoRecyclerView.adapter = videoListAdapter
        videoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem = mLayoutManager . findFirstVisibleItemPosition ()
                val firstView = mLayoutManager.findViewByPosition (firstVisibleItem)
                var playPosition = 0
                if (null != firstView) {
                    if (dy > 0) {
                        if (firstView.height + firstView.top <= firstView.height / 3) {
                            if (playPosition == firstVisibleItem + 1) {
                                return
                            }
                            playPosition = firstVisibleItem + 1
                            videoListAdapter.playVideo(playPosition)
                        } else {
                            if (playPosition == firstVisibleItem) {
                                return
                            }
                            playPosition = firstVisibleItem
                            videoListAdapter.playVideo(playPosition)
                        }

                    }else if (dy < 0) {
                        if (firstView.height + firstView.top >= firstView.height * 2 / 3) {
                            //video stop or play second
                            if (playPosition == firstVisibleItem) {
                                return
                            }
                            playPosition = firstVisibleItem
                            videoListAdapter.playVideo(playPosition)
                        } else {
                            if (playPosition == firstVisibleItem + 1) {
                                return
                            }
                            playPosition = firstVisibleItem + 1
                            videoListAdapter.playVideo(playPosition)
                        }
                    }else {
                        playPosition = 0
                        videoListAdapter.playVideo(playPosition)
                    }
                }
            }

        })

    }
}
