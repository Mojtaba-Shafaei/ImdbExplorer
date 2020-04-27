package com.shafaei.imdbexplorer.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shafaei.imdbexplorer.R
import com.shafaei.imdbexplorer.R.id
import com.shafaei.imdbexplorer.R.layout
import com.shafaei.imdbexplorer.businessLogic.entity.Search
import com.shafaei.imdbexplorer.ui.main.movieInfo.MovieInfoFragment
import com.shafaei.imdbexplorer.ui.main.search.SearchFragment
import com.shafaei.imdbexplorer.ui.main.search.SearchViewModel
import com.shafaei.imdbexplorer.ui.util.EventEnum.*
import com.shafaei.imdbexplorer.ui.util.LifeCyclesEnum.MAIN_ACTIVITY
import com.shafaei.imdbexplorer.ui.util.RxBus
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
  private val mViewModel: SearchViewModel by viewModel()
  private var isTablet: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.main_activity)

    isTablet = resources.getBoolean(R.bool.is_tablet)

    if (savedInstanceState == null) {
      supportFragmentManager.beginTransaction()
        .replace(id.container, SearchFragment.newInstance())
        .commitNow()

      if (isTablet) {
        // inflate movie info fragment, too

      }
    }
  }

  override fun onStart() {
    super.onStart()

    RxBus.subscribe(MOVIE_ITEM_CLICK, MAIN_ACTIVITY) {
      val search = it as Search

      if (isTablet) {
        RxBus.publish(SHOW_MOVIE_INFO, search.imdbId)
      } else {
        supportFragmentManager.beginTransaction()
          .replace(id.container, MovieInfoFragment.newInstance(search.imdbId), "info")
          .addToBackStack(null)
          .commit()
      }
    }

    RxBus.subscribe(MOVIE_INFO_BACK_CLICK, MAIN_ACTIVITY) {
      if (isTablet.not()) // Actually, this condition always is TRUE, because in movieInfo fragment, the button is always hidden,if the device is tablet
        supportFragmentManager.popBackStack()
    }
  }

  override fun onStop() {
    super.onStop()
    RxBus.unSubscribe(MAIN_ACTIVITY)
  }
}
