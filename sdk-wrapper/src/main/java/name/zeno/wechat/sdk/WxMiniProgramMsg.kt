package name.zeno.wechat.sdk

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject
import io.reactivex.Single
import name.zeno.wechat.sdk.WxMiniProgramMsg.Companion.drawableRes

/**
 * 发送小程序消息
 *
 * - [drawableRes] 默认封面图片
 *
 * @param webpageUrl 兼容低版本的网页链接
 * @param miniprogramType
 * @param userName 小程序原始 id
 * @param path 小程序页面路径
 *
 * @param thumbImageUrl 小程序封面图片
 * @param thumbImage 小程序封面图片
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
@Suppress("unused", "MemberVisibilityCanPrivate")
data class WxMiniProgramMsg(
    var webpageUrl: String? = null,
    @field:MiniProgramType
    @param:MiniProgramType
    var miniprogramType: Int = MiniProgramType.RELEASE,

    var userName: String,
    var path: String,

    var title: String? = null,
    var description: String? = null,

    var thumbImageUrl: String? = null,
    var thumbImage: Bitmap? = null
) : Req(), Parcelable {

  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readInt(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readParcelable(Bitmap::class.java.classLoader)) {
  }

  override fun build(context: Context): Single<BaseReq> {
    return Single.create<Bitmap>(source@{ emitter ->
      // 选择分享主图
      if (thumbImage != null) {
        emitter.onSuccess(thumbImage!!)
        return@source
      }

      if (thumbImageUrl != null) {
        val bitmap = ZBitmap.bitmap(thumbImageUrl)
        if (bitmap != null) {
          emitter.onSuccess(bitmap)
          return@source
        }
      }

      val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
      emitter.onSuccess(bitmap)
    }).map { bitmap ->
      // 对大图片处理, 需要小于 128 k
      if (bitmap.byteCount < 128000)
        bitmap
      else {
        val rate = 128000.0 / bitmap.byteCount
        val result = ZBitmap.zoom(bitmap, bitmap.width * rate, bitmap.height * rate)
        bitmap.recycle()
        result
      }
    }.map { bitmap ->
      // 构建 req
      val msgObj = WXMiniProgramObject()
      msgObj.webpageUrl = webpageUrl
      msgObj.miniprogramType = miniprogramType
      msgObj.userName = userName
      msgObj.path = path

      val msg = WXMediaMessage(msgObj)
      msg.title = title
      msg.description = description
      msg.setThumbImage(bitmap)

      val req = SendMessageToWX.Req()
      req.transaction = "Zeno" //transaction 字段用于唯一标识一个请求
      req.message = msg
      req.scene = WXScene.SESSION
      req
    }
  }

  companion object {
    @DrawableRes
    var drawableRes: Int = android.R.drawable.ic_menu_share

    @JvmField
    val CREATOR: Parcelable.Creator<WxMiniProgramMsg> = object : Parcelable.Creator<WxMiniProgramMsg> {
      override fun createFromParcel(source: Parcel): WxMiniProgramMsg = WxMiniProgramMsg(source)
      override fun newArray(size: Int): Array<WxMiniProgramMsg?> = arrayOfNulls(size)
    }
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(webpageUrl)
    parcel.writeInt(miniprogramType)
    parcel.writeString(userName)
    parcel.writeString(path)
    parcel.writeString(title)
    parcel.writeString(description)
    parcel.writeString(thumbImageUrl)
    parcel.writeParcelable(thumbImage, flags)
  }

  override fun describeContents(): Int = 0
}
