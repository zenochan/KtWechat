package name.zeno.wechat.sdk;

import android.support.annotation.IntDef;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 16/6/23
 */
@IntDef({WXScene.SESSION, WXScene.TIMELINE, WXScene.FAVORITE})
@Retention(RetentionPolicy.SOURCE)
public @interface WXScene
{
  int SESSION  = SendMessageToWX.Req.WXSceneSession;//好友
  int TIMELINE = SendMessageToWX.Req.WXSceneTimeline;//朋友圈
  int FAVORITE = SendMessageToWX.Req.WXSceneFavorite;//搜藏
}
