package com.shafaei.imdbexplorer.ui.main.search

import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.shafaei.imdbexplorer.R
import com.shafaei.imdbexplorer.businessLogic.exception.ExceptionMapper
import com.shafaei.imdbexplorer.businessLogic.exception.InternetException
import com.shafaei.imdbexplorer.ui.adapter.SearchAdapter
import com.shafaei.imdbexplorer.ui.kotlinExt.composeClicks
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextData
import com.shafaei.imdbexplorer.ui.kotlinExt.onErrorResumeNextLceFailure
import com.shafaei.imdbexplorer.ui.main.MainViewModel
import com.shafaei.imdbexplorer.ui.util.EventEnum.EVENT_SEARCH_ITEM_CLICKED
import com.shafaei.imdbexplorer.ui.util.RxBus
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.search_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.TimeUnit.MILLISECONDS

class SearchFragment : Fragment() {
  private val mDisposables = CompositeDisposable()
  private val mViewModel: MainViewModel by sharedViewModel()
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
  }

  private fun bindClicks() {
    // register adapter`s item clicking event
    mDisposables +=
      mAdapter.itemClicks
        .composeClicks()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          { RxBus.publish(EVENT_SEARCH_ITEM_CLICKED, it) },
          { showError(it) }
        )

    mDisposables +=
      search_view.queryTextChanges()
        .skipInitialValue()
        .doOnNext {
          if (it.isBlank()) {
            mAdapter.clear()
            throw NotFoundException()
          }
        }
        .filter { it.isNotBlank() }
        .map { it.toString().trim() }
        .throttleWithTimeout(700, MILLISECONDS)
        .distinctUntilChanged()
        .filter { it.length > 1 }
        .retryUntil { false } // resubscribe to stream
        .onErrorResumeNextData { "" } // prevent breaking the chain of stream
        .subscribe { text ->
          try {
            mViewModel.search(text)
          } catch (e: Exception) {
            mAdapter.clear()
            showError(e)
          }
        }
  }

  private fun bindStates() {
    mDisposables +=
      mViewModel.searchStates
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
              val adapterDataSetChanged = mAdapter.setData(it.searchList)
              if (adapterDataSetChanged) {
                // scroll to previous grid position, that before of User leave this fragment
                mViewModel.mFirstItemInView?.let { pos ->
                  rv_list.postDelayed({ rv_list.smoothScrollToPosition(pos);rv_list.setSelection(pos) }, 500)
                }
              }

              // restore params in ui data,
              if (search_view.query != it.searchParams.movieTitle) {
                search_view.setQuery(it.searchParams.movieTitle, false)
              }
            }

            if (state.loading.not()) {
              prg_loading.hide()
            }
            if (state.isFailure().not()) {
              hideError()
            }

          } catch (e: Exception) {
            showError(e)
          }
        }
  }

  private fun hideError() {
    mSnackBar?.dismiss()
  }

  override fun onStop() {
    // unregister all observers
    mDisposables.clear()
    // save position of the first item in grid, to retrieve that later [onStart]
    mViewModel.mFirstItemInView = rv_list.firstVisiblePosition
    super.onStop()
  }

  override fun onDestroyView() {
    // dismissing and releasing SnackBar to prevent leaking memory
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
      // slide top to bottom the snackBar under the searchView
      val baseException = ExceptionMapper.map(throwable)
      mSnackBar?.let { if (it.isShownOrQueued) it.dismiss() }
      mSnackBar = Snackbar.make(coordinator_of_searchView as View, baseException.getDescription(requireContext()), Snackbar.LENGTH_LONG)
        .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)

      if (baseException is InternetException)
        mSnackBar!!.setAction(R.string.retry) { mViewModel.retryToSearch() }

      mSnackBar!!.view.apply {
        this.layoutParams = (this.layoutParams as CoordinatorLayout.LayoutParams).apply { gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL }
        rotation = 180F
        setBackgroundColor(Color.LTGRAY)
      }
      mSnackBar!!.show()
    } catch (e: Exception) {
      Toast.makeText(requireContext().applicationContext, throwable.message, Toast.LENGTH_LONG).show()
    }
  }
}
