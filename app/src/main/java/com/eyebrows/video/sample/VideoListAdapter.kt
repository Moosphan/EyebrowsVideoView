package com.eyebrows.video.sample

import android.content.Context
import android.media.MediaPlayer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.eyebrows.video.EyebrowsVideoView

/**
 * the adapter of video list
 * @author moosphon
 */
class VideoListAdapter() : RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>(){

    private var videoList: List<VideoData> = ArrayList()
    private lateinit var context: Context
    var listener: OnVideoSelectListener? = null
    private var mSelectedPosition: Int = -1
    private var checkState: HashSet<Int> = HashSet()

    constructor(data: List<VideoData>):this(){
        this.videoList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_layout, parent, false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val videoView  = holder.itemView.findViewById<EyebrowsVideoView>(R.id.card_item_videoView)
        val progressBar = holder.itemView.findViewById<ProgressBar>(R.id.card_item_progressView)
        val thumbnailImage = holder.itemView.findViewById<ImageView>(R.id.card_item_thumbnail)
        val timeText = holder.itemView.findViewById<TextView>(R.id.card_item_time_text)
        val descText = holder.itemView.findViewById<TextView>(R.id.card_item_desc_text)

        timeText.text = videoList[position].date
        descText.text = videoList[position].description
        thumbnailImage.setImageResource(videoList[position].thumbnail)

        holder.itemView.setOnClickListener {
            listener?.onVideoSelect(videoList[position].description, position)
        }

        if (checkState.contains(position)){
            progressBar.visibility = View.VISIBLE
            videoView.setRawData(videoList[position].videoRes)
            videoView.setLooping(true)
            videoView.prepareAsync(MediaPlayer.OnPreparedListener {
                videoView.start()
                thumbnailImage.visibility = View.INVISIBLE
                progressBar.visibility = View.GONE
            })
        }else {
            if (videoView.isPlaying()) {
                videoView.stop()
                thumbnailImage.visibility = View.VISIBLE
            }

        }
    }

    fun playVideo(position: Int) {
        if (mSelectedPosition != position) {
            //先取消上个item的勾选状态
            checkState.remove(mSelectedPosition)
            notifyItemChanged(mSelectedPosition)
            //设置新Item的勾选状态
            mSelectedPosition = position
            checkState.add(mSelectedPosition)
            notifyItemChanged(mSelectedPosition)
        }

    }

    fun setOnStickerSelectListener(selectListener: OnVideoSelectListener) {
        this.listener = selectListener
    }

    class VideoViewHolder(view: View): RecyclerView.ViewHolder(view)


    interface OnVideoSelectListener{
        fun onVideoSelect(url: String, position:Int)
    }
}