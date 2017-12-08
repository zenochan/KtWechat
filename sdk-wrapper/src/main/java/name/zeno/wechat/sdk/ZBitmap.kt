package name.zeno.wechat.sdk

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ZBitmap {
  private val TAG = "ZBitmap"


  // 根据图片url获取图片对象
  fun bitmap(urlPath: String?): Bitmap? {
    val cache = File(Environment.getExternalStorageDirectory(), "cache")
    if (urlPath != null && (cache.exists() || cache.mkdirs())) {
      val name = Encode.md5(urlPath)
      val file = File(cache, name!!)
      if (file.exists()) {
        // 如果图片存在本地缓存目录，则不去服务器下载
        return BitmapFactory.decodeFile(file.absolutePath)
      } else {
        // 从网络上获取图片
        try {
          val url = URL(urlPath)
          val conn = url.openConnection() as HttpURLConnection
          conn.connectTimeout = 5000
          conn.requestMethod = "GET"
          conn.doInput = true
          if (conn.responseCode == 200) {

            val inputStream = conn.inputStream
            val fos = FileOutputStream(file)
            val buffer = ByteArray(1024)

            var len: Int
            do {
              len = inputStream.read(buffer)
              if (len != -1) fos.write(buffer, 0, len)
            } while (len != -1)

            inputStream.close()
            fos.close()
            // 返回一个URI对象
            return BitmapFactory.decodeFile(file.absolutePath)
          }
        } catch (e: Exception) {
          e.printStackTrace()
          Log.e(TAG, "download image failed", e)
        }

      }
      return null
    } else {
      return null
    }
  }

  /***
   * 图片的缩放方法
   *
   * @param bgImage   ：源图片资源
   * @param newWidth  ：缩放后宽度
   * @param newHeight ：缩放后高度
   */
  fun zoom(bgImage: Bitmap, newWidth: Double, newHeight: Double): Bitmap {
    // 获取这个图片的宽和高
    val width = bgImage.width.toFloat()
    val height = bgImage.height.toFloat()
    // 创建操作图片用的matrix对象
    val matrix = Matrix()
    // 计算宽高缩放率
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // 缩放图片动作
    matrix.postScale(scaleWidth, scaleHeight)
    return Bitmap.createBitmap(bgImage, 0, 0, width.toInt(), height.toInt(), matrix, true)
  }
}
