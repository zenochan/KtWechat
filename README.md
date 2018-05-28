# KtWechat

对微信sdk 的封装, 主要目的是练习 processor 的开发, 让一些没必要经常重复编写的代码有机会自动化生成

> <font color=red>目前代码不是最终版本，已经达到了练习的目的,就此停止在这了， 务必不要用在生产环境中</font>

#### usage

```groovy
maven { url "http://maven.mjtown.cn/"}
implementation "name.zeno:wechat:0.0.2"
kapt "name.zeno:wechat-compiler:0.0.1"

```

```kotlin
@Wechat("wechat app id","pkgName")
class MyApp{}
```

- Context.isWxAppInstalled:     微信是否安装
- Context.registerAppToWx:      注册应用到微信

- AppCompatActivity.wxPay
- AppcompatActivity.wxAuth
- AppcompatActivity.wxShare

- Fragment.wxPay
- Fragment.wxAuth
- Fragment.wxShare

#### Permissions

- READ_PHONE_STATE
- WRITE_EXTERNAL_STORAGE

> 这两个权限需要运行时申请， 其他的为常规权限

#### proguard
```proguard
-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.tencent.wxop.** { *; }
-keep class com.tencent.mm.sdk.** { *; }
```

#### 项目结构
- annotation: java module， 放注解类
    - 通过注解获取 APP_ID, PKG_NAME
    - 模块中不需要引入 ktl std 依赖，用不上而且会影响后续的引用
- sdk-wrapper: 对微信 SDK 功能封装, 依赖 annotation
    - 封装跳转逻辑，数据结构
    - manifest 权限以及 activity 配置, 自动合并，无需再处理
- compiler: 代码生成的实现模块, 依赖 annotation
    - 继承 AbstractProcessor, 在 process 方法中编写 generate code 逻辑
    - src/main/resources/META-INF/services/ 下 javax.annotation.processing.Processor 声明 processor 类
    - 依赖 android api: `compile 'com.google.android:android:4.1.1.4'`

#### THANKS

- [微信开放平台 | 移动应用 | Android 接入指南][1]
- [Annotation Processing : Don’t Repeat Yourself, Generate Your Code.][2]
- [savepopulation/piri](https://github.com/savepopulation/piri)


[1]:https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&id=1417751808
[2]:https://medium.com/@iammert/annotation-processing-dont-repeat-yourself-generate-your-code-8425e60c6657