package com.goodpharm.gppandagentlog.data

import android.util.Log
import com.goodpharm.gppandagentlog.util.DLog
import com.goodpharm.gppandagentlog.util.TimeUtils
import kotlin.text.StringBuilder

data class GppData (val time: String, val dataType: Constants.Companion.GPP_DATA_TYPE, val contents: String)

/**
 * gpp data parsing
 * 11:25:50 송신내용 - GPBEGIN;1;02800;E000;;;GPEND
 */
fun getGppData(line: String) : GppData {

    try {

        val list = line.split(" ")

        if(list.isNotEmpty() && list.size > 3){

            val time = TimeUtils.getGppTime(line)

            var dataType = Constants.Companion.GPP_DATA_TYPE.PROCESSING
            if(Constants.gppDataTypeMap.containsKey(list[1])){
                dataType = Constants.gppDataTypeMap[list[1]]!!
            }

            val sb = StringBuilder()
            for(i in 3..< list.size){
                sb.append(list[i] + " ")
            }
            var contents = sb.toString()

            /**
             * 줄맞춤
             */
            if(contents.contains("#B")){
                val newSb = StringBuilder()
                contents.split("#B").forEach {
                    newSb.append("#B$it\n")
                }
                contents = newSb.toString()
            }

            return GppData(time, dataType, contents)
        }

    } catch ( e : Exception ) {
        DLog.e(e)
    }

    return GppData("", Constants.Companion.GPP_DATA_TYPE.PROCESSING, "-")
}