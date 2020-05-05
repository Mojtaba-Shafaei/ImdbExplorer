package com.shafaei.imdbexplorer.di

import com.shafaei.imdbexplorer.businessLogic.network.NetworkMovieBl
import com.shafaei.imdbexplorer.businessLogic.network.util.RetrofitHelper
import com.shafaei.imdbexplorer.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single<RetrofitHelper> { RetrofitHelper() }
  single<NetworkMovieBl> { NetworkMovieBl(get()) }
  viewModel { MainViewModel(get()) }
}