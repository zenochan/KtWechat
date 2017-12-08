package name.zeno.wechat.demo

import android.app.Application
import name.zeno.wechat.annotation.Wechat

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/12/7
 */
@Wechat("12312", BuildConfig.APPLICATION_ID)
class MyApp : Application()
