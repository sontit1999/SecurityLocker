package com.ls.entertainment.securitylocker.utils

import android.os.Handler
import android.os.Looper
import com.google.gson.internal.Primitives
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import kotlin.reflect.KClass

object RxBus {

    const val NOTIFICATION = "NOTIFICATION"
    const val SHOW_OR_HIDE_ADS = "SHOW_OR_HIDE_ADS"

    private val mHandler = Handler(Looper.getMainLooper())
    private val map = HashMap<String, Disposable>()

    //    val listKClass = mutableListOf<KClass<*>>()
    private val subject: PublishSubject<Any> by lazy {
        val bs = PublishSubject.create<Any>()
        bs.subscribeOn(Schedulers.io())
        bs.observeOn(AndroidSchedulers.mainThread())
        bs
    }

    fun push(data: Any, delay: Long = 0) {
        if (delay > 0) {
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    subject.onNext(data)
                }

            }, delay)
        } else {
            subject.onNext(data)
        }
    }


    // Đăng ký nhiều lần với tên khác nhau
    fun <T : Any> subscribe(name: String, clazz: KClass<T>, callback: ((T) -> Unit)? = null) {
        unregister(name)
        val dispose = subject.ofType(clazz.java).subscribe { onNext ->
            mHandler.post { callback?.invoke(onNext) }
        }
        map[name] = dispose
    }

    fun unregister(name: String) {
        map.remove(name)?.dispose()
    }

    fun <T> cast(classOfT: Class<T>, data: Any): T? {
        return Primitives.wrap(classOfT).cast(data)
    }
}
