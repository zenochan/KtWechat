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
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import io.reactivex.Observable

/**
 * 发送网页消息
 *
 * - [drawable]
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
@Suppress("unused", "MemberVisibilityCanPrivate")
data class WXUrlMsg(
    var webpageUrl: String? = null,
    var title: String? = null,
    var description: String? = null,
    var thumbImageUrl: String? = null,
    var thumbImage: Bitmap? = null,
    @field:WXScene
    @param:WXScene
    var scene: Int = 0
) : Req(), Parcelable {
  override fun build(context: Context): Observable<BaseReq> {
    return Observable.create<Bitmap>(source@ { emitter ->
      // 选择分享主图
      if (thumbImage != null) {
        emitter.onNext(thumbImage!!)
        emitter.onComplete()
        return@source
      }

      if (thumbImageUrl != null) {
        val bitmap = ZBitmap.bitmap(thumbImageUrl)
        if (bitmap != null) {
          emitter.onNext(bitmap)
          emitter.onComplete()
          return@source
        }
      }

      val bitmap = BitmapFactory.decodeResource(context.resources, drawableRes)
      emitter.onNext(bitmap)
      emitter.onComplete()
    }).map { bitmap ->
      // 对大图片处理
      if (bitmap.width <= 256 && bitmap.height <= 256)
        bitmap
      else ZBitmap.zoom(bitmap, 256.0, 256.0)
    }.map { bitmap ->
      // 构建 req
      val webpage = WXWebpageObject()
      webpage.webpageUrl = webpageUrl
      val msg = WXMediaMessage(webpage)
      msg.title = title
      msg.description = description
      msg.setThumbImage(bitmap)

      val req = SendMessageToWX.Req()
      req.transaction = "Zeno" //transaction 字段用于唯一标识一个请求
      req.message = msg
      req.scene = scene
      req
    }
  }

  constructor(source: Parcel) : this(
      source.readString(),
      source.readString(),
      source.readString(),
      source.readString(),
      source.readParcelable<Bitmap>(Bitmap::class.java.classLoader),
      source.readInt()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeString(webpageUrl)
    writeString(title)
    writeString(description)
    writeString(thumbImageUrl)
    writeParcelable(thumbImage, 0)
    writeInt(scene)
  }

  companion object {

    @DrawableRes
    var drawableRes: Int = android.R.drawable.ic_menu_share
    @JvmField
    val CREATOR: Parcelable.Creator<WXUrlMsg> = object : Parcelable.Creator<WXUrlMsg> {
      override fun createFromParcel(source: Parcel): WXUrlMsg = WXUrlMsg(source)
      override fun newArray(size: Int): Array<WXUrlMsg?> = arrayOfNulls(size)
    }
  }
}
