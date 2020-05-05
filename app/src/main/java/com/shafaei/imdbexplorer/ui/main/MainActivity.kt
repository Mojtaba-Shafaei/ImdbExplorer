package com.shafaei.imdbexplorer.ui.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.shafaei.imdbexplorer.R
import com.shafaei.imdbexplorer.businessLogic.entity.PlotType.FULL
import com.shafaei.imdbexplorer.businessLogic.entity.Search
import com.shafaei.imdbexplorer.businessLogic.entity.param.MovieParam
import com.shafaei.imdbexplorer.ui.main.movieInfo.MovieInfoFragment
import com.shafaei.imdbexplorer.ui.main.search.SearchFragment
import com.shafaei.imdbexplorer.ui.util.EventEnum.EVENT_MOVIE_INFO_BACK_CLICK
import com.shafaei.imdbexplorer.ui.util.EventEnum.EVENT_SEARCH_ITEM_CLICKED
import com.shafaei.imdbexplorer.ui.util.LifeCyclesEnum.MAIN_ACTIVITY
import com.shafaei.imdbexplorer.ui.util.RxBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
  private val mViewModel: MainViewModel by viewModel()
  private val _tagInfoFragment = "INFO_FRAGMENT"

  //////////////////////////////////////////////////////////////////////////////////////////////////
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)

    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(R.id.container, SearchFragment.newInstance())
        .commitNow()
    }
  }

  private fun isTwoPanAvailable(): Boolean {
    return findViewById<View>(R.id.container_info) != null
  }

  override fun onStart() {
    super.onStart()

    val info = supportFragmentManager.findFragmentByTag(_tagInfoFragment)
    info?.run {
      supportFragmentManager.popBackStackImmediate()
      // inflate fragment movieInfo in right panel for tablets, or replace with searchFragment for mobiles
      supportFragmentManager.beginTransaction()
        .replace(if (isTwoPanAvailable()) R.id.container_info else R.id.container, info, _tagInfoFragment)
        .addToBackStack(null)
        .commit()
    }

    // each time user tap on a searched movie item, the IMDB_ID will pass to [MovieInfoFragment] fragment to refresh the screen
    RxBus.subscribe(EVENT_SEARCH_ITEM_CLICKED, MAIN_ACTIVITY) {
      val search = it as Search
      mViewModel.loadMovieInfo(param = MovieParam(id = it.imdbId, plot = FULL))

      // inflate fragment movieInfo in right panel for tablets, or replace with searchFragment for mobiles
      val infoFragment = supportFragmentManager.findFragmentByTag(_tagInfoFragment)
      if (infoFragment == null) {
        supportFragmentManager.beginTransaction()
          .replace(if (isTwoPanAvailable()) R.id.container_info else R.id.container, MovieInfoFragment.newInstance(isTwoPanAvailable()), _tagInfoFragment)
          .addToBackStack(null)
          .commit()
      }
    }

    RxBus.subscribe(EVENT_MOVIE_INFO_BACK_CLICK, MAIN_ACTIVITY) {
      if (isTwoPanAvailable().not())
        supportFragmentManager.popBackStack()
    }
  }

  override fun onBackPressed() {
    if (supportFragmentManager.backStackEntryCount > 0)
      supportFragmentManager.popBackStack()
    else
      super.onBackPressed()

  }

  override fun onStop() {
    RxBus.unSubscribe(MAIN_ACTIVITY)
    super.onStop()
  }
}
