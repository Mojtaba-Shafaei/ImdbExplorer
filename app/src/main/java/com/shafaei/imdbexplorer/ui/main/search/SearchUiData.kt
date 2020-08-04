package com.shafaei.imdbexplorer.ui.main.search

import com.shafaei.imdbexplorer.businessLogic.entity.Search
import com.shafaei.imdbexplorer.businessLogic.entity.param.SearchParams

data class SearchUiData(
  val searchParams: SearchParams, // the text that saved in the ui.searchView component
  val searchList: List<Search> // the result of the searching
)