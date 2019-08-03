# EyebrowsVideoView
> A stretch VideoView to display video files. Just like scaleType in ImageViews.

## Why use it

Let's see the difference between **`VideoView`** and **`EyebrowsVideoView`**. First, we usually face the problem by using `VideoView`：

![videoview](https://github.com/Moosphan/EyebrowsVideoView/blob/56cccfdc7ad0651ea95a5df434c0f46507c487ca/art/normal_preview.png)

It is obvious that the video playback area is not completely paved. You can deal this problem by using `EyebrowsVideoView`：

![eyebrows-video](https://github.com/Moosphan/EyebrowsVideoView/blob/82203a29d8faea3809145f6775d05d4613b48b53/art/preview.gif)

## Usage

1. in xml：

   ```kotlin
   <com.eyebrows.video.EyebrowsVideoView
               android:id="@+id/card_item_videoView"
               android:layout_width="match_parent"
               android:layout_height="match_parent"
               app:scaleType="center_crop"/>
   ```

2. Play video in following ways：

   ```kotlin
   eyebrowsVideoView.setRawData(videoList[position].videoRes)
               eyebrowsVideoView.setLooping(true)
               eyebrowsVideoView.prepareAsync(MediaPlayer.OnPreparedListener {
                   eyebrowsVideoView.start()
               })
   ```

   Also, we support other ways to play video resource：

   - [x] Video from network
   - [x] Video from local file
   - [x] video from assets
   - [x] video from raw directory
   - [x] Video from uri

3. supported **ScaleTypes**：

   ```kotlin
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
   ```

## License

```
MIT License

Copyright (c) 2019 AFeng

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```



