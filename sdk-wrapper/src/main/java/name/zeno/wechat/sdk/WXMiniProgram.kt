package name.zeno.wechat.sdk

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import io.reactivex.Single


/**
 * 拉起小程序
 * @param userName        小程序原始 id
 * @param path            小程序页面的可带参数路径， 不填默认拉去小程序首页
 * @param type            可选打开 开发版，体验版和正式版
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
@Suppress("unused")
class WXMiniProgram(
    var userName: String,
    var path: String? = null,
    @field:MiniProgramType
    @param:MiniProgramType
    var type: Int = MiniProgramType.RELEASE
) : Req(), Parcelable {
  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readString(),
      parcel.readInt()) {
  }

  override fun build(context: Context): Single<BaseReq> {
    val req = WXLaunchMiniProgram.Req()
    req.userName = userName
    req.path = path
    req.miniprogramType = type

    return Single.just(req)
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<WXMiniProgram> = object : Parcelable.Creator<WXMiniProgram> {
      override fun createFromParcel(source: Parcel): WXMiniProgram = WXMiniProgram(source)
      override fun newArray(size: Int): Array<WXMiniProgram?> = arrayOfNulls(size)
    }
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(userName)
    parcel.writeString(path)
    parcel.writeInt(type)
  }

  override fun describeContents(): Int {
    return 0
  }

}
