package name.zeno.wechat.sdk

import android.content.Context
import android.os.Parcelable
import com.tencent.mm.opensdk.modelbase.BaseReq
import io.reactivex.Single

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/4/27
 */
abstract class Req : Parcelable {
  abstract fun build(context: Context): Single<BaseReq>
}
