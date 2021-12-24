package com.major.http

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException


/**
 * TODO
 *
 * @author meijie05
 * @since 2021/12/20 6:50 下午
 */
object NetWorkUtils {

    /**
     * 检查网络是否可用
     *
     * @param paramContext
     * @return
     */
    fun checkEnable(paramContext: Context): Boolean {
        val manager = paramContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val localNetworkInfo = manager.activeNetworkInfo
        return localNetworkInfo != null && localNetworkInfo.isAvailable
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    fun int2ip(ipInt: Int): String {
        val sb = StringBuilder()
        sb.append(ipInt and 0xFF).append(".")
        sb.append(ipInt shr 8 and 0xFF).append(".")
        sb.append(ipInt shr 16 and 0xFF).append(".")
        sb.append(ipInt shr 24 and 0xFF)
        return sb.toString()
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    fun getLocalIpAddress(context: Context): String {
        return try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val i = wifiInfo.ipAddress
            int2ip(i)
        } catch (ex: Exception) {
            """ 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!${ex.message}"""
        }
        // return null;
    }

    fun getLocalIpStr(context: Context): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val enumIpAddr = en.nextElement().inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.getHostAddress()
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return ""
    }
}