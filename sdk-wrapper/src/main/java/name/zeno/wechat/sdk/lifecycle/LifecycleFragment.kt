package name.zeno.wechat.sdk.lifecycle

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN_MR1
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.CallSuper
import android.support.v4.app.FragmentActivity
import android.view.View
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import name.zeno.wechat.sdk.KEY

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/12/4
 */
internal class LifecycleFragment : Fragment(), LifecycleObservable {
  private val listenerList = ArrayList<LifecycleListener>()

  private val CODE = 0xff01
  private var resultHandler: ((ok: Boolean, data: Intent?) -> Unit)? = null
  private val behavior = BehaviorSubject.create<Boolean>()


  override fun registerLifecycleListener(listener: LifecycleListener) {
    if (!listenerList.contains(listener)) {
      listenerList.add(listener)
    }
  }

  override fun unregisterLifecycleListener(listener: LifecycleListener) {
    if (listenerList.contains(listener)) {
      listenerList.remove(listener)
    }
  }


  //<editor-fold desc="life circle">
  @CallSuper
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    listenerList.forEach { it.onCreate() }
  }

  override fun onStart() {
    super.onStart()
    listenerList.forEach { it.onStart() }
  }

  @CallSuper
  override fun onResume() {
    super.onResume()
    listenerList.forEach { it.onResume() }
    behavior.onNext(true)
  }


  @CallSuper
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    listenerList.forEach { it.onActivityResult(requestCode, resultCode, data) }
    if (CODE == requestCode) {
      resultHandler?.invoke(requestCode == Activity.RESULT_OK, data)
      resultHandler = null
    }
  }

  fun navForResult(target: Class<Activity>, data: Parcelable? = null, onResult: (ok: Boolean, data: Intent?) -> Unit) {
    val untilResume = PublishSubject.create<Any>()
    untilResume.subscribe {
      this@LifecycleFragment.resultHandler = onResult
      val intent = Intent(activity, target)
      intent.putExtra(KEY, data)
      startActivityForResult(intent, CODE)
    }

    behavior.filter { it }.subscribe {
      untilResume.onNext(this)
      untilResume.onComplete()
    }

  }


  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    listenerList.forEach { it.onViewCreated() }
  }

  @CallSuper
  override fun onDestroyView() {
    super.onDestroyView()
    listenerList.forEach { it.onDestroyView() }
  }

  @CallSuper
  override fun onStop() {
    super.onStop()
    listenerList.forEach { it.onStop() }
  }

  override fun onPause() {
    super.onPause()
    listenerList.forEach { it.onPause() }
    behavior.onNext(false)
  }

  @CallSuper
  override fun onDestroy() {
    super.onDestroy()
    listenerList.forEach { it.onDestroy() }
  }

  companion object {
    const val TAG = "name.zeno.ktrxpermission.lifecycle.LifecycleFragment"


    fun with(fragment: Fragment): LifecycleFragment {
      val fm = if (SDK_INT >= JELLY_BEAN_MR1) fragment.childFragmentManager else fragment.fragmentManager
      val tag = LifecycleFragment.TAG
      var instance: LifecycleFragment? = fm.findFragmentByTag(tag) as? LifecycleFragment
      if (instance == null) {
        instance = LifecycleFragment()
        fm.beginTransaction().add(instance, tag).commit()
      }

      return instance
    }

    fun with(activity: FragmentActivity): LifecycleFragment {
      val fm = activity.fragmentManager
      val tag = LifecycleFragment.TAG
      var instance: LifecycleFragment? = fm.findFragmentByTag(tag) as? LifecycleFragment
      if (instance == null) {
        instance = LifecycleFragment()
        fm.beginTransaction().add(instance, tag).commit()
      }

      return instance
    }
  }
}
