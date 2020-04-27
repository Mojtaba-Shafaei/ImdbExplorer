package com.shafaei.imdbexplorer.ui.kotlinExt

import android.content.Context
import android.graphics.Bitmap
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import io.reactivex.Single
import java.io.File

private val shadeColors: IntArray by lazy {
  intArrayOf(
    0xFF86804e.toInt(),
    0xFF979058.toInt(),
    0xFFa8a162.toInt(),
    0xFFb0aa71.toInt()
  )
}

fun Context.createProgressDrawable(): CircularProgressDrawable {
  return CircularProgressDrawable(this).apply {
    setColorSchemeColors(*shadeColors)
    setStyle(CircularProgressDrawable.DEFAULT)
    start()
  }
}

fun Context.downloadImageAndGetFile(url: String): Single<File> {
  return Single.fromFuture(
    Glide.with(this)
      .downloadOnly()
      .load(url)
      .encodeFormat(Bitmap.CompressFormat.JPEG)
      .encodeQuality(100)
      .submit(SIZE_ORIGINAL, SIZE_ORIGINAL)
  )
}