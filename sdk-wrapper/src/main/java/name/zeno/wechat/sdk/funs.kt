package name.zeno.wechat.sdk

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import name.zeno.wechat.sdk.lifecycle.LifecycleFragment

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/12/8
 */
fun AppCompatActivity.wxPay(data: WXPay, next: (Resp) -> Unit) {
  val clazz = Class.forName("$packageName.wxapi.WXPayEntryActivity") as Class<Activity>
  LifecycleFragment.with(this).navForResult(clazz, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}

fun AppCompatActivity.wxAuth(data: WXAuth, next: (Resp) -> Unit) = wxSend(data, next)
fun AppCompatActivity.wxShare(data: WXTextMsg, next: (Resp) -> Unit) = wxSend(data, next)
fun AppCompatActivity.wxShare(data: WXUrlMsg, next: (Resp) -> Unit) = wxSend(data, next)
internal fun AppCompatActivity.wxSend(data: Req, next: (Resp) -> Unit) {
  val clazz = Class.forName("$packageName.wxapi.WXEntryActivity") as Class<Activity>
  LifecycleFragment.with(this).navForResult(clazz, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}


fun Fragment.wxPay(data: WXPay, next: (Resp) -> Unit) {
  val clazz = Class.forName("${activity.packageName}.wxapi.WXPayEntryActivity") as Class<Activity>
  LifecycleFragment.with(this).navForResult(clazz, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}

fun Fragment.wxAuth(data: WXAuth, next: (Resp) -> Unit) = wxSend(data, next)
fun Fragment.wxShare(data: WXTextMsg, next: (Resp) -> Unit) = wxSend(data, next)
fun Fragment.wxShare(data: WXUrlMsg, next: (Resp) -> Unit) = wxSend(data, next)
internal fun Fragment.wxSend(data: Req, next: (Resp) -> Unit) {
  val clazz = Class.forName("${activity.packageName}.wxapi.WXEntryActivity") as Class<Activity>
  LifecycleFragment.with(this).navForResult(clazz, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}


internal const val KEY = "extra_data"
internal fun <T : Parcelable> Activity.dataOrNull(): T? = intent.getParcelableExtra(KEY)
internal fun Activity.okAndFinish(data: Parcelable?) {
  if (data != null) {
    val intentData = Intent()
    intentData.putExtra(KEY, data)
    setResult(Activity.RESULT_OK, intentData)
  } else {
    setResult(Activity.RESULT_OK)
  }
  finish()
}
