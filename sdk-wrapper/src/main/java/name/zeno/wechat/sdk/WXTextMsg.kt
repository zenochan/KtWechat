package name.zeno.wechat.sdk

import android.content.Context
import android.os.Parcel
import android.os.Parcelable

import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject

import io.reactivex.Observable

/**
 * 发送文本消息
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
data class WXTextMsg(
    var text: String? = null,
    @param:WXScene
    @field:WXScene
    var scene: Int = WXScene.SESSION
) : Req(), Parcelable {
  override fun build(context: Context): Observable<BaseReq> {
    val textObject = WXTextObject(text)
    val msg = WXMediaMessage(textObject)
    msg.description = text

    val req = SendMessageToWX.Req()
    req.transaction = "Zeno" //transaction 字段用于唯一标识一个请求
    req.message = msg
    req.scene = scene

    return Observable.just(req)
  }

  constructor(source: Parcel) : this(
      source.readString(),
      source.readInt()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeString(text)
    writeInt(scene)
  }

  companion object {
    @JvmField
    val CREATOR: Parcelable.Creator<WXTextMsg> = object : Parcelable.Creator<WXTextMsg> {
      override fun createFromParcel(source: Parcel): WXTextMsg = WXTextMsg(source)
      override fun newArray(size: Int): Array<WXTextMsg?> = arrayOfNulls(size)
    }
  }
}
