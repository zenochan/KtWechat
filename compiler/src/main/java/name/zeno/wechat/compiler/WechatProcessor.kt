package name.zeno.wechat.compiler

import android.support.annotation.NonNull
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import name.zeno.wechat.annotation.Wechat
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes("name.zeno.wechat.annotation.Wechat")
class WechatProcessor : AbstractProcessor() {
  override fun process(mutableSet: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
    val elements = roundEnv.getElementsAnnotatedWith(Wechat::class.java)
    val wechat = elements.map { it.getAnnotation(Wechat::class.java) }.firstOrNull() ?: return true

    val packageName = wechat.packageName + ".wxapi"
    var type: TypeSpec


    val clazz = Class.forName("name.zeno.wechat.sdk.AbsWxEntryActivity")

    //@NotNull @Override protected String getAppId()
    //{
    //  return appId;
    //}
    val method = MethodSpec.methodBuilder("getAppId")
        .addAnnotation(NonNull::class.java)
        .addAnnotation(Override::class.java)
        .returns(String::class.java)
        .addModifiers(Modifier.PROTECTED)
        .addStatement("""return "${wechat.appId}"""")
        .build()


    type = TypeSpec.classBuilder("WXEntryActivity")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .superclass(clazz)
        .addMethod(method)
        .build()
    JavaFile.builder(packageName, type).build().writeTo(processingEnv.filer)

    type = TypeSpec.classBuilder("WXPayEntryActivity")
        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
        .superclass(clazz)
        .addMethod(method)
        .build()
    JavaFile.builder(packageName, type).build().writeTo(processingEnv.filer)

    return false
  }

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
}
