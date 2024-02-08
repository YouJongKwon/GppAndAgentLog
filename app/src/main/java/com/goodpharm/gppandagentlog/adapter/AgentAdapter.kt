package com.goodpharm.gppandagentlog.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.RotateDrawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.goodpharm.gppandagentlog.R
import com.goodpharm.gppandagentlog.base.BaseRecyclerViewAdapter
import com.goodpharm.gppandagentlog.data.AgentData
import com.goodpharm.gppandagentlog.data.Constants
import com.goodpharm.gppandagentlog.data.GppData
import com.goodpharm.gppandagentlog.data.getGppData
import com.goodpharm.gppandagentlog.databinding.ItemAgentBinding
import com.goodpharm.gppandagentlog.databinding.ItemGppBinding
import com.goodpharm.gppandagentlog.util.DLog
import com.goodpharm.gppandagentlog.util.TimeUtils
import com.goodpharm.gppandagentlog.util.TimeUtils.Companion.dateFormat
import java.lang.StringBuilder

class AgentAdapter : BaseRecyclerViewAdapter<AgentData, ItemAgentBinding>(
    R.layout.item_agent,
    onBind = { adapter, item, binding ->

//    DLog.d("item : ${item.toString()}")

        binding.tvDateTimes.text = item.time
        binding.cardView.setBackgroundColor(Color.parseColor("#ffffff"))

        binding.imgSendRight.visibility =
            if (item.dataType == Constants.Companion.AGENT_DATA_TYPE.SEND_FOR_GPP) View.VISIBLE else View.GONE

        if(item.dataType != Constants.Companion.AGENT_DATA_TYPE.SEND_FOR_GPP) {
//            binding.cardView.
            binding.imgTypeInfo.visibility = View.VISIBLE
            when(item.dataType){
                Constants.Companion.AGENT_DATA_TYPE.RECEPTION_BY_GPP -> {
                    binding.imgTypeInfo.setImageResource(R.drawable.ic_send_gpp)
                }
                Constants.Companion.AGENT_DATA_TYPE.KIOSK_SEND -> {
                    binding.imgTypeInfo.setImageResource(R.drawable.ic_send_kiosk)
                }
                Constants.Companion.AGENT_DATA_TYPE.KIOSK_RECEIVE -> {
                    binding.imgTypeInfo.setImageResource(R.drawable.ic_receive_kiosk)
                }
                Constants.Companion.AGENT_DATA_TYPE.API -> {
                    binding.imgTypeInfo.setImageResource(R.drawable.ic_http)
                }
                Constants.Companion.AGENT_DATA_TYPE.API_RECEIVE -> {
                    binding.imgTypeInfo.setImageResource(R.drawable.ic_http)
                }
                else -> {
                    binding.imgTypeInfo.visibility = View.GONE
                }
            }

        }else {
            binding.imgTypeInfo.visibility = View.GONE
        }

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
         * Contents
         */
        try {

            var contents = item.contents

            if(item.dataType == Constants.Companion.AGENT_DATA_TYPE.API || item.dataType == Constants.Companion.AGENT_DATA_TYPE.API_RECEIVE){
                binding.tvContents.setTextColor(Color.parseColor("#999999"))
                binding.tvContents.setTypeface(binding.tvContents.typeface, Typeface.NORMAL)
            } else if(item.dataType == Constants.Companion.AGENT_DATA_TYPE.KIOSK_RECEIVE){
                binding.tvContents.setTextColor(Color.parseColor("#666666"))
                binding.tvContents.setTypeface(binding.tvContents.typeface, Typeface.NORMAL)
            } else if(item.dataType == Constants.Companion.AGENT_DATA_TYPE.NORMAL){
                binding.tvContents.setTextColor(Color.BLACK)
                binding.tvContents.setTypeface(binding.tvContents.typeface, Typeface.NORMAL)
            }else if(item.dataType == Constants.Companion.AGENT_DATA_TYPE.RECEPTION_BY_GPP) {
                if(contents.contains("GPBEGIN;1;02200;E000;")){ // 처방전 복호화 데이터
                    binding.tvContents.setTextColor(Color.BLACK)
                    binding.tvContents.setTypeface(binding.tvContents.typeface, Typeface.BOLD)
                    binding.cardView.setBackgroundColor(Color.parseColor("#eaeaea"))
                }else {
                    binding.tvContents.setTextColor(Color.BLACK)
                    binding.tvContents.setTypeface(binding.tvContents.typeface, Typeface.NORMAL)
                }
            }else if(item.dataType == Constants.Companion.AGENT_DATA_TYPE.SEND_FOR_GPP && contents.contains("Command=")){
                binding.tvContents.setTextColor(Color.RED)
            }else {
                binding.tvContents.setTextColor(Color.BLACK)
                binding.tvContents.setTypeface(binding.tvContents.typeface, Typeface.NORMAL)
            }

        } catch ( e : Exception ) {
            DLog.e(e)
        }

        binding.tvContents.text = item.contents

    }) {

}