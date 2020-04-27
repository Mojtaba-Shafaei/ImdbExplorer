package com.shafaei.imdbexplorer.ui.main.search

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.shafaei.imdbexplorer.R
import com.shafaei.imdbexplorer.ui.adapter.SearchAdapter
import com.shafaei.imdbexplorer.ui.kotlinExt.composeClicks
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextData
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextLceFailure
import com.shafaei.imdbexplorer.ui.util.EventEnum.MOVIE_ITEM_CLICK
import com.shafaei.imdbexplorer.ui.util.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit.MILLISECONDS

class SearchFragment : Fragment() {
  private val mViewModel: SearchViewModel by sharedViewModel()
  private val mDisposables = CompositeDisposable()
  private lateinit var mAdapter: SearchAdapter

  private var mSnackBar: Snackbar? = null

  //////////////////////////////////////////////////////////////////////////////////////////////////
  companion object {

    fun newInstance() = SearchFragment()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.search_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    initUi()
  }

  override fun onStart() {
    super.onStart()
    bindStates()
    bindClicks()

    mDisposables +=
      search_view.queryTextChanges()
        .skipInitialValue()
        .filter { it.isNotEmpty() }
        .map { it.toString().trim() }
        .throttleWithTimeout(700, MILLISECONDS)
        .distinctUntilChanged()
        .doOnNext { if (it.isEmpty()) mAdapter.clear() }
        .filter { it.length > 1 }
        .onErrorResumeNextData { "" }
        .subscribe { text ->
          try {
            mViewModel.search(text.toString())
          } catch (e: Exception) {
            mAdapter.clear()
            showError(e)
          }
        }


  }

  private fun bindClicks() {
    mDisposables +=
      mAdapter.itemClicks
        .composeClicks()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError { t: Throwable -> showError(t) }
        .subscribe {
          RxBus.publish(MOVIE_ITEM_CLICK, it)
        }
  }

  private fun bindStates() {
    mDisposables +=
      mViewModel.states
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorResumeNextLceFailure()
        .subscribe { state ->
          try {
            if (state.loading) {
              prg_loading.show()
            }

            if (state.isFailure()) {
              showError(state.error!!)
            }

            state.data?.let {
              mAdapter.setData(it)

              // scroll to previous grid position before User leave this fragment
              mViewModel.mFirstItemInView?.let { pos ->
                rv_list.postDelayed({ rv_list.smoothScrollToPosition(pos);rv_list.setSelection(pos) }, 500)
              }

            }

            if (state.loading.not()) {
              prg_loading.hide()
            }

          } catch (e: Exception) {
            showError(e) // FIXME: handle exception and show a useful error to user
          }
        }
  }

  override fun onStop() {
    mDisposables.clear()
    mViewModel.mFirstItemInView = rv_list.firstVisiblePosition
    super.onStop()
  }

  override fun onDestroyView() {
    mSnackBar?.let { if (it.isShownOrQueued) it.dismiss();mSnackBar = null }
    super.onDestroyView()
  }

  /**
   * init UI font, colors, drawables and etc...
   */
  private fun initUi() {
    mAdapter = SearchAdapter(inflater = layoutInflater)
    rv_list.adapter = mAdapter
    search_view.findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setHintTextColor(Color.GRAY)
  }

  private fun showError(throwable: Throwable) {
    try {
      mSnackBar?.let { if (it.isShownOrQueued) it.dismiss() }
      mSnackBar = Snackbar.make(l_root as View, "${throwable.message}", Snackbar.LENGTH_INDEFINITE)
        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        .setAction("Retry") { mViewModel.retryToSearch() }
      mSnackBar?.show()

    } catch (e: Exception) {
      Toast.makeText(requireContext().applicationContext, throwable.message, Toast.LENGTH_LONG).show()
    }
  }
}
