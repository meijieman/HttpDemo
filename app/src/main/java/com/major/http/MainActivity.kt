package com.major.http

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponseBody
import com.google.gson.Gson
import com.major.http.databinding.ActivityMainBinding
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.SimpleWebServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "ta_ma"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i(TAG, "onCreate: ")

        requestPerms()

        val server = SimpleServer()

        binding.btnStart.setOnClickListener {
            if (!server.wasStarted()) {
                server.start()
            }
            Log.i(TAG, "The server started.")
        }

        binding.btnStop.setOnClickListener {
            server.stop()
            Log.i(TAG, "The server stopped.")
        }

        // 需要把html 放到 /sdcard/webapps/下，然后浏览器访问 localhost:8083/index.html
        val root = File(Environment.getExternalStorageDirectory(), "webapps")
        val ss = SimpleWebServer("", 8082, root, false, "*")

        binding.btnStart2.setOnClickListener {
            /*
            Android 抖音爆红的口红挑战爬坑总结
            https://juejin.cn/post/6844903757042417671
             */

            try {
                ss.start(NanoHTTPD.SOCKET_READ_TIMEOUT, true)
                Log.i(TAG, "The server started2.")
            } catch (e: Exception) {
                Log.e(TAG, "onCreate: ", e)
            }
        }

        binding.btnStop2.setOnClickListener {
            ss.stop()
            Log.i(TAG, "The server2 stopped.")

        }

        val dns = ALiDns()
        var query: List<DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord> =
            arrayListOf()
        binding.btnQuery.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                Log.i(TAG, "modify dns ${Thread.currentThread()}")
                query = dns.query()
                withContext(Dispatchers.Main) {
                    Log.i(TAG, "modify dns2 ${Thread.currentThread()}")
                    dialog(Gson().toJson(query))
                }
                Log.i(TAG, "modify dns3 ${Thread.currentThread()}")
            }
        }
        binding.btnUpdate.setOnClickListener {
            // 修改 dns
            CoroutineScope(Dispatchers.IO).launch {
                Log.i(TAG, "update dns")
                val currentIp = binding.etDomain.text.toString()
                query.firstOrNull { it.RR == "www" && it.value != currentIp }?.run {
                    Log.i(TAG, "record ${Gson().toJson(this)}")
                    dns.update(this, currentIp)
                }
            }
        }
        binding.btnQueryDomains.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Log.i(TAG, "query ${Thread.currentThread()}")
                val domains = dns.queryDomains()
                withContext(Dispatchers.Main) {
                    Log.i(TAG, "query2 ${Thread.currentThread()}")
                    dialog(domains)
                }
            }
        }

        //        binding.btnStart.performClick()
        //        binding.btnStart2.performClick()

    }

    private fun dialog(msg: String) {
        AlertDialog.Builder(this)
            .setMessage(msg)
            .setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun requestPerms(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        val list: MutableList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                list.add(permission)
            }
        }

        return if (list.isEmpty()) {
            true
        } else {
            ActivityCompat.requestPermissions(this, list.toTypedArray(), 100)
            false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请先给予必要的权限", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tvInfo.text = NetWorkUtils.getLocalIpStr(this)
        if (binding.etDomain.text.isEmpty()) {
            binding.etDomain.setText(NetWorkUtils.getLocalIpStr(this))
        }

        //        if (NetWorkUtils.checkEnable(this)) {
        //            binding.tvInfo.text = NetWorkUtils.getLocalIpAddress(this)
        //        } else {
        //            binding.tvInfo.text = "disable"
        //        }
        //        if (binding.etDomain.text.isEmpty()) {
        //            binding.etDomain.setText(NetWorkUtils.getLocalIpAddress(this))
        //        }

    }
}