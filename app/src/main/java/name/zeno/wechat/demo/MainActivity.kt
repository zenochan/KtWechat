package name.zeno.wechat.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import name.zeno.wechat.sdk.WXAuth
import name.zeno.wechat.sdk.wxAuth

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    btnAuth.setOnClickListener {
      wxAuth(WXAuth()) { Log.e("TAG", it.toString()) }
    }
  }
}
