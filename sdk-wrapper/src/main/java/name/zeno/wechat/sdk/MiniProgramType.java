package name.zeno.wechat.sdk;

import android.support.annotation.IntDef;

import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 16/6/23
 */
@IntDef({MiniProgramType.RELEASE, MiniProgramType.TEST, MiniProgramType.PREVIEW})
@Retention(RetentionPolicy.SOURCE)
public @interface MiniProgramType
{
  int RELEASE = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;    //正式版
  int TEST    = WXMiniProgramObject.MINIPROGRAM_TYPE_TEST;       //测试版
  int PREVIEW = WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW;    //体验版
}
