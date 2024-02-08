package com.goodpharm.gppandagentlog.adapter

import android.graphics.Color
import android.util.Log
import android.view.View
import com.goodpharm.gppandagentlog.R
import com.goodpharm.gppandagentlog.base.BaseRecyclerViewAdapter
import com.goodpharm.gppandagentlog.data.Constants
import com.goodpharm.gppandagentlog.data.GppData
import com.goodpharm.gppandagentlog.data.getGppData
import com.goodpharm.gppandagentlog.databinding.ItemGppBinding
import com.goodpharm.gppandagentlog.util.DLog
import com.goodpharm.gppandagentlog.util.TextUtils.setCustomText
import com.goodpharm.gppandagentlog.util.TimeUtils
import com.goodpharm.gppandagentlog.util.TimeUtils.Companion.dateFormat
import java.lang.StringBuilder

class GppAdapter : BaseRecyclerViewAdapter<GppData, ItemGppBinding>(
    R.layout.item_gpp,
    onBind = { adapter, item, binding ->
        // binding

//    DLog.d("item : ${item.toString()}")

        binding.tvDateTimes.text = item.time
        binding.tvContents.text = item.contents
        binding.imgSendLeft.visibility =
            if (item.dataType == Constants.Companion.GPP_DATA_TYPE.PROCESSING || item.dataType == Constants.Companion.GPP_DATA_TYPE.RECEPTION) View.GONE else View.VISIBLE
        binding.imgSendRight.visibility =
            if (item.dataType == Constants.Companion.GPP_DATA_TYPE.PROCESSING || item.dataType == Constants.Companion.GPP_DATA_TYPE.SEND) View.GONE else View.VISIBLE

        /**
         * time
         */
        try {

            if(item.time.isEmpty()){
                binding.tvDateTimes.visibility = View.GONE
            }else {

                val index = adapter.list.indexOf(item)

                if (index == 0) {
                    binding.tvDateTimes.visibility = View.VISIBLE
                } else {
                    if (item.time == adapter.list[index - 1].time) {
                        binding.tvDateTimes.visibility = View.GONE
                    } else {
                        binding.tvDateTimes.visibility = View.VISIBLE
                    }
                }
            }

        } catch ( e : Exception ) {
            Log.e("####", e.localizedMessage)
            binding.tvDateTimes.visibility = View.VISIBLE
        }

        /**
         * contents
         */
        if(item.contents.contains("수납저장 :")) {
            val startIndex = item.contents.indexOf(":")
            binding.tvContents.setCustomText(item.contents, startIndex + 1, item.contents.length, color = Color.BLUE, isBold = true)
        }else if(item.contents.contains("수납파싱처리")) {
            val startIndex = item.contents.indexOfLast { it.toString() == "‡" }
            val endIndex = item.contents.indexOfLast { it.toString() == "|" }
            binding.tvContents.setCustomText(item.contents, startIndex + 1, item.contents.length, color = Color.BLUE, isBold = true)
        } else if(item.contents.contains("PANAME:")) {
            val startIndex = item.contents.indexOfLast { it.toString() == ":" }
            binding.tvContents.setCustomText(item.contents, startIndex + 1, item.contents.length, color = Color.BLUE, isBold = true)
        } else if(item.contents.contains("SALE_MAIN SAVE")){
            binding.tvContents.setCustomText(item.contents, 0, item.contents.length, color = Color.BLUE, isBold = true)
        } else if(item.contents.contains("조제정보조회 실패")){ // 2024.02.08 - 처방프로그램에 정보 저장 실패
            val startIndex = item.contents.indexOf("조제")
            binding.tvContents.setCustomText(item.contents, startIndex, item.contents.length, color = Color.RED, isBold = true)
        } else if(item.contents.contains("조제정보조회")){
            val startIndex = item.contents.indexOf("조제")
            binding.tvContents.setCustomText(item.contents, startIndex, item.contents.length, color = Color.BLUE, isBold = true)
        }

        binding.cardView.setOnClickListener {
            DLog.d("${item.contents}")
        }

    }) {

}