package com.eyebrows.video

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.support.annotation.RawRes
import android.util.AttributeSet
import android.view.Surface
import android.view.TextureView
import java.io.FileDescriptor
import java.io.IOException

/**
 * A stretch component to display video files.
 * It can display videos in any way but automatically adjust render texture range as you want.
 * Just like using [android.widget.ImageView.ScaleType] in imageViews.
 */
class EyebrowsVideoView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : TextureView(context, attrs, defStyleAttr),
        TextureView.SurfaceTextureListener, MediaPlayer.OnVideoSizeChangedListener {

    private var mScaleType: ScaleType
    private var mMediaPlayer: MediaPlayer? = null
    private lateinit var mDisplayedRect: Rect
    private var mVideoSizeChangeListener: OnVideoSizeChangedListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)


    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EyebrowsVideoView)
        val typeIndex = typedArray.getInt(R.styleable.EyebrowsVideoView_scaleType, ScaleType.FIT_CENTER.ordinal)
        typedArray.recycle()
        mScaleType = ScaleType.values()[typeIndex]
    }


    private fun initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer()
            mMediaPlayer?.setOnVideoSizeChangedListener(this)
            surfaceTextureListener = this
        } else {
            reset()
        }
    }

    @Throws(IOException::class)
    fun setRawData(@RawRes id: Int) {
        val afd = resources.openRawResourceFd(id)
        setDataSource(afd)
    }

    @Throws(IOException::class)
    fun setAssetData(assetName: String) {
        val manager = context.assets
        val afd = manager.openFd(assetName)
        setDataSource(afd)
    }

    @Throws(IOException::class)
    private fun setDataSource(afd: AssetFileDescriptor) {
        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()
    }

    @Throws(IOException::class)
    fun setDataSource(path: String) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(path)
    }

    @Throws(IOException::class)
    fun setDataSource(context: Context, url: String) {
        val uri = Uri.parse(url)
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(context, uri)
    }

    @Throws(IOException::class)
    fun setDataSource(context: Context, uri: Uri,
                      headers: Map<String, String>) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(context, uri, headers)
    }

    @Throws(IOException::class)
    fun setDataSource(context: Context, uri: Uri) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(context, uri)
    }

    @Throws(IOException::class)
    fun setDataSource(fd: FileDescriptor, offset: Long, length: Long) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(fd, offset, length)
    }

    @Throws(IOException::class)
    fun setDataSource(fd: FileDescriptor) {
        initializeMediaPlayer()
        mMediaPlayer?.setDataSource(fd)
    }

    @Throws(IOException::class, IllegalStateException::class)
    fun prepare(listener: MediaPlayer.OnPreparedListener?) {
        mMediaPlayer?.setOnPreparedListener(listener)
        mMediaPlayer?.prepare()
    }

    @Throws(IllegalStateException::class)
    fun prepareAsync(listener: MediaPlayer.OnPreparedListener?) {
        mMediaPlayer?.setOnPreparedListener(listener)
        mMediaPlayer?.prepareAsync()
    }

    @Throws(IOException::class, IllegalStateException::class)
    fun prepare() {
        prepare(null)
    }

    @Throws(IllegalStateException::class)
    fun prepareAsync() {
        prepareAsync(null)
    }

    fun setOnErrorListener(listener: MediaPlayer.OnErrorListener) {
        mMediaPlayer?.setOnErrorListener(listener)
    }

    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mMediaPlayer?.setOnCompletionListener(listener)
    }

    fun setOnInfoListener(listener: MediaPlayer.OnInfoListener) {
        mMediaPlayer?.setOnInfoListener(listener)
    }

    fun setOnVideoSizeChangedListener(listener: OnVideoSizeChangedListener) {
        this.mVideoSizeChangeListener = listener
    }

    fun getCurrentPosition(): Int {
        return mMediaPlayer?.currentPosition ?: 0
    }

    fun getDuration(): Int {
        return mMediaPlayer?.duration ?: -1
    }

    fun getVideoHeight(): Int {
        return mMediaPlayer?.videoHeight ?: -1
    }

    fun getVideoWidth(): Int {
        return mMediaPlayer?.videoWidth ?: -1
    }

    /**
     * Set scale type for displaying video resource as you want.
     */
    fun setVideoScaleType(scaleType: ScaleType) {
        this.mScaleType = scaleType
        adjustVideoSize(getVideoWidth(), getVideoHeight())
    }

    /**
     * get displayed render range rect of video.
     */
    fun getRenderRect(): Rect {
        return mDisplayedRect
    }

    fun isLooping(): Boolean {
        return mMediaPlayer?.isLooping ?: false
    }

    fun isPlaying(): Boolean {
        return mMediaPlayer?.isPlaying ?: false
    }

    fun pause() {
        if (isPlaying())
            mMediaPlayer?.pause()
    }

    fun seekTo(msec: Int) {
        mMediaPlayer?.seekTo(msec)
    }

    fun setLooping(looping: Boolean) {
        mMediaPlayer?.isLooping = looping
    }

    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mMediaPlayer?.setVolume(leftVolume, rightVolume)
    }

    fun start() {
        //Logger.e("StretchVideoView视频开始播放")
        mMediaPlayer?.start()
    }

    fun stop() {
        mMediaPlayer?.stop()
    }


    fun release() {
        reset()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    fun reset() {
        mMediaPlayer?.reset()
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        // deal with rendering size
        if (mVideoSizeChangeListener != null) {
            mVideoSizeChangeListener?.onVideoSizeChanged(width, height)
        }
        adjustVideoSize(width, height)
        //scaleVideoSize(width, height)
    }


    private fun adjustVideoSize(width: Int, height: Int) {
        if (width == 0 || height == 0)
            return
        setTransform(getAdaptedSize(Size(width.toFloat(), height.toFloat())))
    }

    /**
     * Adjust video render size based on [ScaleType]
     */
    private fun getAdaptedSize(videoSize: Size): Matrix {
        val targetSize = Size(width.toFloat(), height.toFloat())
        val (ratioWidth, ratioHeight) = targetSize / videoSize
        val matrix = Matrix()
        var sx = 0f     // the ratio to scale in direction x
        var sy = 0f     // the ratio to scale in direction y
        var px = 0f     // the x value of pivot point for scaling
        var py = 0f     // the y value of pivot point for scaling

        when(mScaleType) {
            ScaleType.FIT_CENTER  -> {
                val minRatio = Math.min(ratioWidth, ratioHeight)
                sx = (minRatio / ratioWidth)
                sy = (minRatio / ratioHeight)
                px = targetSize.width / 2f
                py = targetSize.height / 2f
            }

            ScaleType.CENTER_CROP -> {
                val maxRatio = Math.max(ratioWidth, ratioHeight)
                sx = (maxRatio / ratioWidth)
                sy = (maxRatio / ratioHeight)
                px = targetSize.width / 2f
                py = targetSize.height / 2f
            }

            ScaleType.LEFT_CROP   -> {
                val maxRatio = Math.max(ratioWidth, ratioHeight)
                sx = (maxRatio / ratioWidth)
                sy = (maxRatio / ratioHeight)
                px = 0f
                py = targetSize.height / 2f
            }

            ScaleType.TOP_CROP    -> {
                val maxRatio = Math.max(ratioWidth, ratioHeight)
                sx = (maxRatio / ratioWidth)
                sy = (maxRatio / ratioHeight)
                px = targetSize.width / 2f
                py = 0f
            }

            ScaleType.RIGHT_CROP  -> {
                val maxRatio = Math.max(ratioWidth, ratioHeight)
                sx = (maxRatio / ratioWidth)
                sy = (maxRatio / ratioHeight)
                px = targetSize.width
                py = targetSize.height / 2f
            }

            ScaleType.BOTTOM_CROP -> {
                val maxRatio = Math.max(ratioWidth, ratioHeight)
                sx = (maxRatio / ratioWidth)
                sy = (maxRatio / ratioHeight)
                px = targetSize.width / 2f
                py = targetSize.height
            }
        }

        mDisplayedRect = calculateRenderRect(mScaleType,
            (sx * videoSize.width).toInt(),
            (sy * videoSize.height).toInt())

        matrix.setScale(sx, sy, px, py)
        return matrix
    }

    // calculate video displayed rect.
    // provide touch event to get real-time rect of video, just for cutting videos.
    private fun calculateRenderRect(type: ScaleType, transformedWidth: Int, transformedHeight: Int): Rect {
        val px: Int = Math.abs((width - transformedWidth) / 2)
        val py: Int = Math.abs((height - transformedHeight) / 2)
        when(type) {
            ScaleType.FIT_CENTER -> {
                // left   The X coordinate of the left side of the rectangle
                // top    The Y coordinate of the top of the rectangle
                // right  The X coordinate of the right side of the rectangle
                // bottom The Y coordinate of the bottom of the rectangle
                return Rect(px, py, px + transformedWidth, py + transformedHeight)
            }

            ScaleType.CENTER_CROP -> {

                return Rect(px, py, px + width, py + height)
            }

            ScaleType.LEFT_CROP  -> {
                return Rect(0, py, width, py + height)
            }

            ScaleType.TOP_CROP   -> {
                return Rect(px, 0, px + width, height)
            }

            ScaleType.RIGHT_CROP -> {
                return Rect(2 * px, py, 2 * px + width, py + height)
            }

            ScaleType.BOTTOM_CROP -> {
                return Rect(px, 2 * py, px + width, 2 * py + height)
            }
        }
    }


    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        val renderSurface = Surface(surface)
        if (mMediaPlayer != null) {
            mMediaPlayer?.setSurface(renderSurface)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mMediaPlayer == null) {
            return
        }

        if (isPlaying()) {
            stop()
        }
        release()
    }

    interface OnVideoSizeChangedListener {
        fun onVideoSizeChanged(videoWidth: Int, videoHeight: Int)
    }

    /**
     * The video display type, like [android.widget.ImageView.ScaleType].
     * Provide six frequently-used styles.
     * If you want any other styles, contract me by issues.
     */
    enum class ScaleType {
        FIT_CENTER,
        CENTER_CROP,
        LEFT_CROP,
        TOP_CROP,
        RIGHT_CROP,
        BOTTOM_CROP
    }

}