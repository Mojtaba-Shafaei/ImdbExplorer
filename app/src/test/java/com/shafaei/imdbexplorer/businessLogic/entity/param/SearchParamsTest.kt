package com.shafaei.imdbexplorer.businessLogic.entity.param

import com.shafaei.imdbexplorer.businessLogic.entity.MovieType
import com.shafaei.imdbexplorer.businessLogic.entity.param.SearchParams.Companion.SEARCH_KEY
import com.shafaei.imdbexplorer.businessLogic.entity.param.SearchParams.Companion.TYPE_KEY
import com.shafaei.imdbexplorer.businessLogic.entity.param.SearchParams.Companion.YEAR_KEY
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchParamsTest {

  @Test
  fun toMap_JustTitle_ReturnsTrue() {
    val lvTitle = "Test..TITLE"
    val p = SearchParams(movieTitle = lvTitle)
    val map = p.toMap()
    assertEquals(2, map.size)
    assertEquals(lvTitle, map[SEARCH_KEY])
  }

  @Test
  fun toMap_TitleType_ReturnsTrue() {
    val lvTitle = "Test..TITLE"
    val lvType = MovieType.MOVIE
    val p = SearchParams(movieTitle = lvTitle, type = lvType)
    val map = p.toMap()

    assertEquals(3, map.size)
    assertEquals(lvTitle, map[SEARCH_KEY])
    assertEquals(lvType.toString(), map[TYPE_KEY])
  }

  @Test
  fun toMap_TitleTypeYear_ReturnsTrue() {
    val lvTitle = "Test..TITLE"
    val lvType = MovieType.MOVIE
    val lvYear = "1995"

    val p = SearchParams(movieTitle = lvTitle, type = lvType, year = lvYear)
    val map = p.toMap()

    assertEquals(4, map.size)
    assertEquals(lvTitle, map[SEARCH_KEY])
    assertEquals(lvType.toString(), map[TYPE_KEY])
    assertEquals(lvYear.toString(), map[YEAR_KEY])
  }
}