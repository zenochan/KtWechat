@file:Suppress("unused")

package name.zeno.wechat.sdk

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import name.zeno.wechat.sdk.lifecycle.LifecycleFragment

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/12/8
 */

/** 微信是否安装 */
fun Context.isWxAppInstalled(): Boolean = WXAPIFactory.createWXAPI(this, null).isWXAppInstalled

/** 注册应用到微信 */
fun Context.registerAppToWx(appId: String): Boolean {
  val api = WXAPIFactory.createWXAPI(this, appId, true)
  return api.isWXAppInstalled && api.registerApp(appId)
}

//<editor-fold desc="微信支付">
fun AppCompatActivity.wxPay(data: WXPay, next: (Resp) -> Unit) {
  LifecycleFragment.with(this).navForResult(wxPayEntry, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}

fun Fragment.wxPay(data: WXPay, next: (Resp) -> Unit) {
  LifecycleFragment.with(this).navForResult(activity.wxPayEntry, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}
//</editor-fold>

fun AppCompatActivity.wxAuth(data: WXAuth, next: (Resp) -> Unit) = wxSend(data, next)
fun AppCompatActivity.wxShare(data: WXTextMsg, next: (Resp) -> Unit) = wxSend(data, next)
fun AppCompatActivity.wxShare(data: WXUrlMsg, next: (Resp) -> Unit) = wxSend(data, next)
internal fun AppCompatActivity.wxSend(data: Req, next: (Resp) -> Unit) {
  LifecycleFragment.with(this).navForResult(wxEntry, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}

/** 微信授权 */
fun Fragment.wxAuth(data: WXAuth, next: (Resp) -> Unit) = wxSend(data, next)

/** 微信分享 */
fun Fragment.wxShare(data: WXTextMsg, next: (Resp) -> Unit) = wxSend(data, next)

/** 微信分享 */
fun Fragment.wxShare(data: WXUrlMsg, next: (Resp) -> Unit) = wxSend(data, next)

/** 微信分享小程序 */
fun Fragment.wxShare(data: WXMiniProgramMsg, next: (Resp) -> Unit) = wxSend(data, next)

/** 微信分享图片 */
fun Fragment.wxShare(data: WXImgMsg, next: (Resp) -> Unit) = wxSend(data, next)

/** 打开小程序 */
fun Fragment.launchMiniProgram(data: WXMiniProgram, next: (Resp) -> Unit) = wxSend(data, next)

/** 微信消息 */
internal fun Fragment.wxSend(data: Req, next: (Resp) -> Unit) {
  LifecycleFragment.with(this).navForResult(activity.wxEntry, data) { _, intentData ->
    if (intentData != null) next(intentData.getParcelableExtra(KEY))
  }
}

internal const val KEY = "extra_data"
internal fun <T : Parcelable> Activity.dataOrNull(): T? = intent.getParcelableExtra(KEY)
internal fun Activity.okAndFinish(data: Parcelable) {
  val intentData = Intent()
  intentData.putExtra(KEY, data)
  setResult(Activity.RESULT_OK, intentData)
  finish()
}

@Suppress("UNCHECKED_CAST")
private val Context.wxEntry: Class<Activity>
  get() = Class.forName("${this.packageName}.wxapi.WXEntryActivity") as Class<Activity>
@Suppress("UNCHECKED_CAST")
private val Context.wxPayEntry: Class<Activity>
  get() = Class.forName("${this.packageName}.wxapi.WXPayEntryActivity") as Class<Activity>
