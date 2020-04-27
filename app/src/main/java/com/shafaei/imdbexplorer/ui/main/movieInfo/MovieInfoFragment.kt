package com.shafaei.imdbexplorer.ui.main.movieInfo

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import com.shafaei.imdbexplorer.R
import com.shafaei.imdbexplorer.businessLogic.entity.PlotType.FULL
import com.shafaei.imdbexplorer.businessLogic.entity.param.MovieParam
import com.shafaei.imdbexplorer.ui.kotlinExt.composeClicks
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextLceFailure
import com.shafaei.imdbexplorer.ui.util.EventEnum.MOVIE_INFO_BACK_CLICK
import com.shafaei.imdbexplorer.ui.util.EventEnum.SHOW_MOVIE_INFO
import com.shafaei.imdbexplorer.ui.util.GlideApp
import com.shafaei.imdbexplorer.ui.util.LifeCyclesEnum.FRAGMENT_MOVIE_INFO
import com.shafaei.imdbexplorer.ui.util.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.layout_title_subtitle_vertically.view.*
import kotlinx.android.synthetic.main.movie_info_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieInfoFragment : Fragment() {
  private val mDisposables = CompositeDisposable()
  private val mViewModel: MovieInfoViewModel by viewModel() // get ViewModel and inject required dependencies by koin
  private lateinit var mIMdbID: String

  private var mSnackBar: Snackbar? = null

  //////////////////////////////////////////////////////////////////////////////////////////////////
  companion object {

    private const val KEY_IMDB_ID = "imdb_id"
    fun newInstance(imdbID: String): MovieInfoFragment {
      val f = MovieInfoFragment()
      f.arguments = bundleOf(KEY_IMDB_ID to imdbID)
      return f
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mIMdbID = requireArguments().getString(KEY_IMDB_ID)!!
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.movie_info_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    initUi()
    loadMovie()
  }

  private fun loadMovie() {
    mViewModel.loadMovieInfo(MovieParam(id = mIMdbID, plot = FULL))
  }

  /**
   * init UI font, colors, drawables and etc...
   */
  private fun initUi() {
    val isTablet = resources.getBoolean(R.bool.is_tablet)
    if (isTablet) {
      btn_back2.visibility = View.GONE
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
        .subscribe { RxBus.publish(MOVIE_INFO_BACK_CLICK, "N/A") }

    RxBus.subscribe(SHOW_MOVIE_INFO, FRAGMENT_MOVIE_INFO) {
      // this action will call if and if the device is tablet, otherwise fragment works from onCreate() and get imdbID through that
      mIMdbID = it as String
      loadMovie()
    }
  }

  private fun bindStates() {
    mDisposables +=
      mViewModel.states
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNextLceFailure()
        .subscribe { lce ->
          if (lce.firstLoading) {
            showLoading()
          }

          if (lce.isFailure()) {
            showError(lce.error!!)
          }

          lce.data?.let {
            tv_year.text = it.year.toString()
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
                    activity!!.runOnUiThread { l_root.background = resource }
                    return true
                  }

                })
                .submit()

            }
          }

          if (lce.firstLoading.not() && lce.isFailure().not()) {
            l_loading.visibility = View.GONE
          }
        }
  }

  private fun showError(throwable: Throwable) {
    mSnackBar = Snackbar.make(l_root, "Data not received successfully,${throwable.message}", Snackbar.LENGTH_INDEFINITE)
      .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
      .setAction("Retry") { loadMovie() }
    mSnackBar?.show()
  }

  private fun showLoading() {
    l_loading.visibility = View.VISIBLE
  }

  override fun onStop() {
    mDisposables.clear()
    RxBus.unSubscribe(FRAGMENT_MOVIE_INFO)
    super.onStop()
  }

  override fun onDestroyView() {
    mSnackBar?.let {
      if (it.isShownOrQueued) it.dismiss()
    }
    super.onDestroyView()
  }
}