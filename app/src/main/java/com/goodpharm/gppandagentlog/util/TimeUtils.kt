package com.goodpharm.gppandagentlog.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

class TimeUtils {

    companion object {

        val dateFormat = SimpleDateFormat("HH:mm:ss")

        /**
         * HH:mm:ss.SSS
         * ex ) 08:36:58.852 [1] INFO Program - [Option] IsConnTablet - 0
         */
        fun getAgentTime(line : String): String{
            var result = ""

            try {

                if(line.isNullOrEmpty()) return result

                val list = line.split(" ")
                if(list.isNotEmpty() && list[0].isNotEmpty() && list[0].length == 12){

                    val date = SimpleDateFormat("HH:mm:ss.SSS", Locale.KOREA).parse(list[0])
                    result = date?.let { dateFormat.format(it) } ?: ""

                }

            } catch ( e : Exception ) {
                Log.d("####", e.localizedMessage)
            }

            return result
        }


        /**
         * ex ) 11:20:07 처리내용 - EDBBarcodeData Load : Success
         */
        fun getGppTime(line : String): String{
            var result = ""

            try {

                val list = line.split(" ")
                if(list.isNotEmpty() && list[0].isNotEmpty() && list[0].length == 8){
                    result = list[0]
                }

            } catch ( e : Exception ) {
                Log.d("####", e.localizedMessage)
            }

            return result
        }
    }
}