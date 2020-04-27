package com.shafaei.imdbexplorer.di

import com.shafaei.imdbexplorer.businessLogic.network.NetworkMovieBl
import com.shafaei.imdbexplorer.ui.main.movieInfo.MovieInfoViewModel
import com.shafaei.imdbexplorer.ui.main.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

  single<NetworkMovieBl> { NetworkMovieBl() }

  viewModel { SearchViewModel(get()) }
  viewModel { MovieInfoViewModel(get()) }
}