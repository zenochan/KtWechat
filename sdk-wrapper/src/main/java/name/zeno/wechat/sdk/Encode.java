package name.zeno.wechat.sdk;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * # 编码解码工具类
 * - {@link #md5(String)}
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 16/7/11
 */
class Encode
{
  private static final String TAG           = "Encode";
  private static final String ALGORITHM_MD5 = "MD5";

  /**
   * 将字符串转成MD5值
   */
  public static String md5(String string)
  {
    String result = null;

    if (string != null) {
      byte[] hash;
      try {
        hash = MessageDigest.getInstance(ALGORITHM_MD5).digest(string.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
          if ((b & 0xFF) < 0x10)
            hex.append("0");
          hex.append(Integer.toHexString(b & 0xFF));
        }
        result = hex.toString();
      } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
        Log.e(TAG, "error", e);
      }
    }

    return result;
  }
}
