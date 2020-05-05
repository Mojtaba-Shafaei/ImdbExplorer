package com.shafaei.imdbexplorer.ui.main.movieInfo

import android.graphics.Bitmap
import android.graphics.PorterDuff.Mode.SRC_ATOP
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jakewharton.rxbinding3.view.clicks
import com.mojtaba_shafaei.android.ErrorMessage.State
import com.shafaei.imdbexplorer.R
import com.shafaei.imdbexplorer.ui.kotlinExt.composeClicks
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextLceFailure
import com.shafaei.imdbexplorer.ui.main.MainViewModel
import com.shafaei.imdbexplorer.ui.util.EventEnum.EVENT_MOVIE_INFO_BACK_CLICK
import com.shafaei.imdbexplorer.ui.util.GlideApp
import com.shafaei.imdbexplorer.ui.util.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.layout_title_subtitle_vertically.view.*
import kotlinx.android.synthetic.main.movie_info_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MovieInfoFragment : Fragment() {
  private val mDisposables = CompositeDisposable()
  private var mIsTwoPaneAvailable: Boolean = false
  private val mViewModel: MainViewModel by sharedViewModel() // get Activity's ViewModel and inject required dependencies by Koin

  //////////////////////////////////////////////////////////////////////////////////////////////////
  companion object {

    private const val KEY_TWO_PANE_AVAILABLE = "key_two_pane"
    fun newInstance(isTwoPaneAvailable: Boolean): MovieInfoFragment {
      return MovieInfoFragment().apply {
        arguments = bundleOf(KEY_TWO_PANE_AVAILABLE to isTwoPaneAvailable)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mIsTwoPaneAvailable = requireArguments().getBoolean(KEY_TWO_PANE_AVAILABLE)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.movie_info_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    initUi()
  }

  /**
   * init UI font, colors, drawables and etc...
   */
  private fun initUi() {
    if (mIsTwoPaneAvailable) {
      btn_back2.visibility = View.GONE
      error_message.state = State.message(getString(R.string.message_select_movie))
    } else {
      error_message.state = State.loading()
    }
  }

  override fun onStart() {
    super.onStart()
    bindStates()
    bindClicks()
  }

  private fun bindClicks() {
    mDisposables +=
      btn_back2.clicks()
        .composeClicks()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError { throwable: Throwable -> showError(throwable) }
        .subscribe { RxBus.publish(EVENT_MOVIE_INFO_BACK_CLICK, "N/A") }
  }

  private fun bindStates() {
    mDisposables +=
      mViewModel.infoStates
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNextLceFailure()
        .subscribe { lce ->
          if (lce.firstLoading) {
            error_message.state = State.loading()
          }

          if (lce.isFailure()) {
            showError(lce.error!!)
          }

          lce.data?.let {
            tv_year.text = it.year
            tv_title.text = it.title

            l_ratings.removeAllViews()
            it.ratings.forEach { rating ->
              val view = layoutInflater.inflate(R.layout.layout_title_subtitle_vertically, l_ratings, false)
              view.tv_title.text = rating.value
              view.tv_subtitle.text = rating.source
              l_ratings.addView(view)
            }

            tv_subject.text = it.genre
            tv_duration.text = it.runtime
            tv_stars.text = it.imdbRating

            tv_synopsis.text = it.plot

            tv_director.text = it.director
            tv_writer.text = it.writer
            tv_actors.text = it.actors

            if (it.poster != null) {
              GlideApp.with(l_root)
                .load(it.poster)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeFormat(Bitmap.CompressFormat.JPEG)
                .encodeQuality(50)
                .format(DecodeFormat.PREFER_RGB_565)
                .addListener(object : RequestListener<Drawable> {
                  override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                  }

                  override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    if (isVisible && resource != null) {
                      resource.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(requireActivity(), R.color.colorBackgroundGlassier), SRC_ATOP)
                      requireActivity().runOnUiThread { l_root.background = resource }
                    }

                    return true
                  }

                })
                .submit()
            }
          }

          if (lce.firstLoading.not() && lce.isFailure().not()) {
            error_message.state = State.hidden()
          }
        }
  }

  private fun showError(throwable: Throwable) {
    error_message.state = State.error(message = "Data not received successfully,${throwable.message}")
  }

  override fun onStop() {
    mDisposables.clear()
    super.onStop()
  }

}