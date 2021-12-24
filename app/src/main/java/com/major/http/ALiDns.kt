package com.major.http


import android.util.Log
import com.aliyun.alidns20150109.Client
import com.aliyun.alidns20150109.models.AddDomainRecordRequest
import com.aliyun.alidns20150109.models.AddDomainRecordResponse
import com.aliyun.alidns20150109.models.DescribeDomainGroupsRequest
import com.aliyun.alidns20150109.models.DescribeDomainRecordsRequest
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponseBody
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest
import com.aliyun.alidns20150109.models.UpdateDomainRecordResponse
import com.aliyun.tea.TeaUnretryableException
import com.aliyun.teaopenapi.models.Config
import com.google.gson.Gson

/**
 * 云解析DNS/域名解析
 *     implementation 'com.aliyun:alidns20150109:2.0.1'
 * https://dns.console.aliyun.com/?spm=5176.100251.111252.1.72014f15TKJ2O4&accounttraceid=4545b823578847cbae6f40c86575555eqybw#/dns/domainList
 *
 * @author meijie05
 * @since 2021/8/12 9:41 上午
 */
class ALiDns {

    companion object {
        const val TAG = "ALiDns"
    }

    // 根据传入参数获取指定主域名的所有解析记录列表。
    fun query() :List<DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord> {
        try {
            val client = createClient()
            val request1 = DescribeDomainRecordsRequest()
            request1.setDomainName("yun1991.top")
            val response: DescribeDomainRecordsResponse = client.describeDomainRecords(request1)
            val body: DescribeDomainRecordsResponseBody = response.getBody()
            val domainRecords: DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecords =
                body.getDomainRecords()

            Log.i(TAG, "body ${Gson().toJson(body)}")
            for (record in domainRecords.getRecord()) {
                Log.w(TAG, "rer ${record.RR}, domainName ${record.domainName} --> " +
                        "${record.value}, recordId ${record.recordId}")
            }
            return domainRecords.getRecord()
        } catch (e: TeaUnretryableException) {
            Log.e(TAG, "ex ", e)
        }
        return arrayListOf()
    }

    // 根据传入参数修改解析记录 https://next.api.aliyun.com/api/Alidns/2015-01-09/UpdateDomainRecord?params={}
    fun update(record:DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord,
               ip:String) {
        try {
            val client: Client = createClient()
            val request = UpdateDomainRecordRequest()
            request.recordId = record.recordId
            request.RR = record.RR
            request.type = record.type
            request.value = ip
            val response: UpdateDomainRecordResponse = client.updateDomainRecord(request)
            Log.i(TAG, "body " + Gson().toJson(response.body))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 增加指定 domain 的解析配置
    fun add() {
        try {
            val client: Client = createClient()
            val request = AddDomainRecordRequest()
            request.RR = "www"
            request.type = "A"
            request.setDomainName("yun1991.top")
            request.value = "192.168.1.100"
            val response: AddDomainRecordResponse = client.addDomainRecord(request)
            Log.i(TAG, "body " + Gson().toJson(response))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun queryDomains():String {
        try {
            val client: Client = createClient()
            val request = DescribeDomainGroupsRequest()
            val domains = client.describeDomainGroups(request)
            Log.w(TAG, "queryDomains: ${Gson().toJson(domains.body)}")
            return Gson().toJson(domains.body)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @Throws(Exception::class)
    private fun createClient(): Client {
        return Client(Config()
            // 需要修改为自己的 accesskey
            .setAccessKeyId("aaa")
            // 修改修改为自己的 secret
            .setAccessKeySecret("bbb"))
    }
}