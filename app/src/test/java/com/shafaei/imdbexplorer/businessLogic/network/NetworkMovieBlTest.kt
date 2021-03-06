package com.shafaei.imdbexplorer.businessLogic.network

import com.shafaei.imdbexplorer.businessLogic.entity.PlotType.SHORT
import com.shafaei.imdbexplorer.businessLogic.entity.param.MovieParam
import com.shafaei.imdbexplorer.businessLogic.entity.param.SearchParams
import com.shafaei.imdbexplorer.businessLogic.network.util.RetrofitHelper
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NetworkMovieBlTest {
  lateinit var retrofitHelper: RetrofitHelper

  @Before
  fun init() {
    retrofitHelper = RetrofitHelper()
  }

  //<editor-fold desc="SEARCH MOVIE">
  @Test
  fun search_EmptySearch_ReturnsFailure() {
    val nb = NetworkMovieBl(retrofitHelper)
    val params = SearchParams(movieTitle = "", type = null, year = null)
    val result = nb.search(params).blockingGet()

    assertTrue(result.isFailure)
    assertEquals("Incorrect IMDb ID.", result.exceptionOrNull()!!.message)
  }

  @Test
  fun search_SimpleTitle_ReturnsSuccess() {
    val nb = NetworkMovieBl(retrofitHelper)
    val params = SearchParams(movieTitle = "god", type = null, year = null)
    val result = nb.search(params).blockingGet()
    assertTrue(result.isSuccess)
    assertNotEquals(0, result.getOrNull()!!.size)
  }

  //</editor-fold>

  //<editor-fold desc="LOAD ONE MOVIE INFO">
  @Test
  fun getMovie_EmptyID_ReturnsFailure() {
    val nb = NetworkMovieBl(retrofitHelper)
    val params = MovieParam(id = "", plot = SHORT)
    val result = nb.getMovie(params).blockingGet()

    assertTrue(result.isFailure)
    assertEquals("Incorrect IMDb ID.", result.exceptionOrNull()!!.message)
  }

  @Test
  fun getMovie_CorrectID_ReturnsSuccess() {
    val nb = NetworkMovieBl(retrofitHelper)
    val params = MovieParam(id = "tt3722118")
    val result = nb.getMovie(params).blockingGet()

    assertTrue(result.isSuccess)
    assertEquals("tt3722118", result.getOrNull()!!.imdbID)
  }
  //</editor-fold>
}