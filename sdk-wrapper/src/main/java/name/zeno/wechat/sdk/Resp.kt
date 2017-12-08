package name.zeno.wechat.sdk

import android.os.Parcel
import android.os.Parcelable
import com.tencent.mm.opensdk.constants.ConstantsAPI.*
import com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode.*

/**
 * # 微信响应结果封装
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since  16/6/9
 */
@Suppress("unused")
class Resp(
    /**
     * # 消息类型
     * - [COMMAND_SENDAUTH] 微信授权登录
     * - [COMMAND_SENDMESSAGE_TO_WX] 发送消息到微信（分享）
     * - [COMMAND_PAY_BY_WX] 微信支付
     */
    var type: Int = 0,

    /**
     * # 错误码
     * - [ERR_OK] 0 成功
     * - [ERR_COMM] -1
     * - [ERR_USER_CANCEL] -2 用户取消
     * - [ERR_SENT_FAILED] -3 发送失败
     * - [ERR_UNSUPPORT] -5 不支持此功能
     * - [ERR_BAN]
     */
    var errCode: Int = 0,
    var code: String? = null,//授权登录获取的code
    var desc: String? = null//描述
) : Parcelable {

  fun success(): Boolean = errCode == ERR_OK

  constructor(source: Parcel) : this(
      source.readInt(),
      source.readInt(),
      source.readString(),
      source.readString()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeInt(type)
    writeInt(errCode)
    writeString(code)
    writeString(desc)
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<Resp> = object : Parcelable.Creator<Resp> {
      override fun createFromParcel(source: Parcel): Resp = Resp(source)
      override fun newArray(size: Int): Array<Resp?> = arrayOfNulls(size)
    }
  }
}
