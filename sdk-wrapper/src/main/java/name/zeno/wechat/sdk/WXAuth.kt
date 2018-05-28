package name.zeno.wechat.sdk

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelmsg.SendAuth
import io.reactivex.Single

/**
 * 授权登陆
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
@Suppress("unused", "MemberVisibilityCanPrivate")
class WXAuth(
    val scope: String = "snsapi_userinfo",
    val state: String = "wx_sdk_" + System.currentTimeMillis()
) : Req(), Parcelable {
  override fun build(context: Context): Single<BaseReq> {
    val req = SendAuth.Req()
    req.scope = scope
    req.state = state
    return Single.just(req)
  }

  constructor(source: Parcel) : this(
      source.readString(),
      source.readString()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeString(scope)
    writeString(state)
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<WXAuth> = object : Parcelable.Creator<WXAuth> {
      override fun createFromParcel(source: Parcel): WXAuth = WXAuth(source)
      override fun newArray(size: Int): Array<WXAuth?> = arrayOfNulls(size)
    }
  }
}
