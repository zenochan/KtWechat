package name.zeno.wechat.sdk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import name.zeno.wechat.sdk.AbsWxEntryActivity.Companion.isWxAppInstalled
import name.zeno.wechat.sdk.AbsWxEntryActivity.Companion.register

/**
 * * [register] 注册 App 到微信
 * * [isWxAppInstalled] 是否安装微信
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
abstract class AbsWxEntryActivity : AppCompatActivity(), IWXAPIEventHandler {

  private lateinit var api: IWXAPI
  private var disposable: Disposable? = null

  // 处理分享到微信但是没有立马返回 App 的场景
  private var finishIfNullRes = false
  private var resp: BaseResp? = null
//  private var dialog: MaterialDialog? = null

  /**
   * @return 微信 AppId
   */
  protected abstract val appId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    api = WXAPIFactory.createWXAPI(this, appId, true)
    api.registerApp(appId)
    val req: Req? = dataOrNull()

    if (req == null) {
      finish()
      return
    }

//    dialog = MaterialDialog.Builder(this)
//        .progress(true, 5000, true)
//        .content("正在打开微信...")
//        .cancelListener { finish() }
//        .show()

    req.build(this)
        .observeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe({
          api.sendReq(it)
          finishIfNullRes = true
        }, {
          finish()
        }, { /*on completed*/ }, {
          disposable = it
        })
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    api.handleIntent(intent, this)
  }

  override fun onResume() {
    super.onResume()
    val resp = this.resp
    if (resp != null) {

      val type = resp.type
      val errCode = resp.errCode
      val respWrapper = Resp(
          type = type,
          errCode = errCode
      )
      respWrapper.desc = when (type) {

        ConstantsAPI.COMMAND_PAY_BY_WX -> when (errCode) {                //微信支付
          BaseResp.ErrCode.ERR_OK -> "支付成功"
          BaseResp.ErrCode.ERR_USER_CANCEL -> "取消支付"
          else -> "支付未完成"
        }

        ConstantsAPI.COMMAND_SENDAUTH -> when (errCode) {               //微信登录
          BaseResp.ErrCode.ERR_OK -> {
            respWrapper.code = (resp as SendAuth.Resp).code
            "登录成功"
          }
          BaseResp.ErrCode.ERR_USER_CANCEL -> "取消登录"
          BaseResp.ErrCode.ERR_AUTH_DENIED -> "拒绝登录"
          else -> null
        }

        ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX -> when (errCode) {      //发送消息到微信（如 分享）
          BaseResp.ErrCode.ERR_OK -> "分享成功"
          BaseResp.ErrCode.ERR_USER_CANCEL -> "取消分享"
          else -> "分享失败"
        }

        else -> null
      }
      okAndFinish(respWrapper)
    } else if (finishIfNullRes) {
      finish()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    // 切断管道
    disposable?.dispose()
    disposable = null

//    if (dialog != null && dialog!!.isShowing) {
//      dialog!!.dismiss()
//      dialog = null
//    }
  }

  override fun onReq(baseReq: BaseReq) = finish()

  override fun onResp(baseResp: BaseResp) {
    this.resp = baseResp
  }

  companion object {
    /** 微信是否已安装  */
    fun isWxAppInstalled(context: Context): Boolean {
      return WXAPIFactory.createWXAPI(context, null).isWXAppInstalled
    }

    /** 注册 App 到微信  */
    fun register(context: Context, appId: String): Boolean {
      val api = WXAPIFactory.createWXAPI(context, appId, true)
      return api.isWXAppInstalled && api.registerApp(appId)
    }
  }

}
