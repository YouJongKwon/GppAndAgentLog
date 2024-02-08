package com.goodpharm.gppandagentlog.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

object DLog {

    /**
     * application에서 isDebug값 셋팅 필수
     */
    var isDebug = true

    fun init(context: Context) {
        isDebug = isDebug(context)
    }

    /**
     *  태그 자동생성
     *  호출된 클래스명, 메소드명 추가
     */
    private fun getTAG(): String {
        var TAG = "####LOG"
        try {
            // todo - lifecycle 에서 stack trace 추적 시 메모리 릭 발생함 -> 수정 필요
            val stack = Throwable().fillInStackTrace()
            val trace = stack.stackTrace
            val className = trace[2].fileName
            val methodName = trace[2].methodName
            TAG = "[###$className] $methodName"
//            TAG = "### [$className]"
        } catch (e: Exception) {
            Log.e("###DLog", "getTag Error : ${e.localizedMessage}")
            Log.e("###DLog", "getTag()", e)
        }
        return TAG
    }

    /**
     * 디버그 유무 확인
     */
    private fun isDebug(context: Context): Boolean {
        var result = false
        val pm = context.packageManager
        try {
            val appinfo = pm.getApplicationInfo(context.packageName, 0)
            result = (0 != appinfo.flags and ApplicationInfo.FLAG_DEBUGGABLE)
        } catch (e: PackageManager.NameNotFoundException) {
            /* debuggable variable will remain false */
        }
        return result;
    }

    fun d(msg: String) {
        if (isDebug) Log.d(getTAG(), msg)
    }

    fun e(msg: String) {
        if (isDebug) Log.e(getTAG(), msg)
    }

    fun e(e: Exception) {
        if (isDebug) Log.e(getTAG(), "error", e)
    }


}