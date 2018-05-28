package name.zeno.wechat.sdk

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.DrawableRes
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXImageObject
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import io.reactivex.Single

/**
 * 发送网页消息
 *
 * - [drawable]
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
@Suppress("unused", "MemberVisibilityCanPrivate")
data class WXImgMsg(
    var thumbImageUrl: String? = null,
    var thumbImage: Bitmap? = null,
    @field:WXScene
    @param:WXScene
    var scene: Int = WXScene.SESSION
) : Req(), Parcelable {
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
      // 构建 req
      val webpage = WXImageObject(bitmap)
      val msg = WXMediaMessage(webpage)
      msg.setThumbImage(bitmap)

      // 设置缩略图
      if (bitmap.byteCount < 128000) {
        msg.setThumbImage(bitmap)
      } else {
        val rate = 128000.0 / bitmap.byteCount
        val result = ZBitmap.zoom(bitmap, bitmap.width * rate, bitmap.height * rate)
        bitmap.recycle()
        msg.setThumbImage(result)
      }

      val req = SendMessageToWX.Req()
      req.transaction = "Zeno" //transaction 字段用于唯一标识一个请求
      req.message = msg
      req.scene = scene
      req
    }
  }

  constructor(source: Parcel) : this(
      source.readString(),
      source.readParcelable<Bitmap>(Bitmap::class.java.classLoader),
      source.readInt()
  )

  override fun describeContents() = 0

  override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    writeString(thumbImageUrl)
    writeParcelable(thumbImage, 0)
    writeInt(scene)
  }

  companion object {

    @DrawableRes
    var drawableRes: Int = android.R.drawable.ic_menu_share
    @JvmField
    val CREATOR: Parcelable.Creator<WXImgMsg> = object : Parcelable.Creator<WXImgMsg> {
      override fun createFromParcel(source: Parcel): WXImgMsg = WXImgMsg(source)
      override fun newArray(size: Int): Array<WXImgMsg?> = arrayOfNulls(size)
    }
  }
}
