package com.major.http

import android.util.Log
import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream

/**
 * TODO
 * https://github.com/NanoHttpd/nanohttpd
 * @author meijie05
 * @since 2021/12/20 10:29 上午
 */
class SimpleServer(a: Int) : NanoHTTPD(a) {

    companion object {
        const val TAG = "ta_ss"
        const val ROOT = "/sdcard/webapps/"
    }

    constructor() : this(8081)

    override fun serve(session: IHTTPSession): Response {
        Log.i(TAG, "serve:请求地址 ${session.remoteIpAddress} ${session.remoteHostName}")

        Log.i(TAG, "serve: uri ${session.uri}, method ${session.method}, header ${session.headers}," +
                " param ${session.parms}")

        var path = session.uri.substring(1)
        Log.i(TAG, "serve: 请求路径 $path")
        if ("" == path || "index.htm" == path) {
            path = "index.html"
        }
        val abspath = ROOT.plus(path)

        if ("test" == path) {

        }

        return if (File(abspath).exists()) {
            newChunkedResponse(
                Response.Status.OK, "text/html",
                FileInputStream(abspath)
            )
        } else {
            newFixedLengthResponse(
                """<!DOCTYPE html><html><body>
                    404 -- Sorry, Not Found [${session.uri}]!
                   </body></html>"""
            )
        }
    }

}