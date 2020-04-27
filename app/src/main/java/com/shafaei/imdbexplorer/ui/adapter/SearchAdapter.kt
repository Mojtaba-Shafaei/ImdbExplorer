package com.shafaei.imdbexplorer.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shafaei.imdbexplorer.R
import com.shafaei.imdbexplorer.businessLogic.entity.Search
import com.shafaei.imdbexplorer.ui.util.GlideApp
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.li_search.view.*

class SearchAdapter(val inflater: LayoutInflater) : BaseAdapter() {
  private val mData: ArrayList<Search> = ArrayList()

  private val mSubjectClicks: PublishSubject<Search> = PublishSubject.create()
  val itemClicks = mSubjectClicks

  override fun getItem(position: Int): Search? {
    return mData[position]
  }

  override fun getViewTypeCount(): Int {
    return 1
  }

  override fun getCount(): Int {
    return mData.size
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val itemView = convertView ?: inflater.inflate(R.layout.li_search, parent, false)

    mData[position].run {
      itemView.tv_title.text = this.title
      itemView.tv_type.text = this.type
      itemView.tv_year.text = this.year

      itemView.setOnClickListener { mSubjectClicks.onNext(this) }
    }

    GlideApp.with(inflater.context)
      .load(if (mData[position].poster != null) mData[position].poster else R.drawable.ic_image_grey900_48dp)
      .error(R.drawable.ic_error_outline_orange_48dp)
      .skipMemoryCache(true)
      .diskCacheStrategy(DiskCacheStrategy.ALL)
      .encodeFormat(Bitmap.CompressFormat.JPEG)
      .encodeQuality(50)
      .format(DecodeFormat.PREFER_RGB_565)
      .into(itemView.iv_poster)

    return itemView
  }

  override fun getItemViewType(position: Int): Int {
    return 1
  }

  override fun getItemId(position: Int): Long {
    return mData[position].imdbId.hashCode().toLong()
  }

  fun setData(data: List<Search>) {
    mData.clear()
    mData.addAll(data)
    notifyDataSetChanged()
  }

  fun clear() {
    mData.clear()
    notifyDataSetChanged()
  }
}