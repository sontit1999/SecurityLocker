package com.entertainment.basemvvmproject.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable


abstract class BaseViewModel : ViewModel() {
    
    protected val disposable = CompositeDisposable()
    var messageError = SingleLiveEvent<Any>()
    var isLoading = MutableLiveData(false)
    var toastMessage = MutableLiveData("")
    
    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}