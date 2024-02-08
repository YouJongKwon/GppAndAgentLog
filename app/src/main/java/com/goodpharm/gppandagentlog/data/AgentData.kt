package com.goodpharm.gppandagentlog.data

import com.goodpharm.gppandagentlog.util.DLog
import com.goodpharm.gppandagentlog.util.TimeUtils

data class AgentData(
    val time: String,
    val dataType: Constants.Companion.AGENT_DATA_TYPE,
    val contents: String
)

fun getAgentData(line: String): AgentData {

    try {

        val list = line.split(" ")

        val time = TimeUtils.getAgentTime(line)
        var type = Constants.Companion.AGENT_DATA_TYPE.NORMAL
        var contents = ""

        // api
        if (line.contains("Web Api")) {
            if(line.contains("[1]")){
                type = Constants.Companion.AGENT_DATA_TYPE.API
            }else {
                type = Constants.Companion.AGENT_DATA_TYPE.API_RECEIVE
            }
        } else if (line.contains("팜플러스 메세지 보냄")) {
            type = Constants.Companion.AGENT_DATA_TYPE.SEND_FOR_GPP
        } else if (line.contains("팜플러스 메세지 받음")) {
            type = Constants.Companion.AGENT_DATA_TYPE.RECEPTION_BY_GPP
        } else if(line.contains("[41] Receive")) {
            type = Constants.Companion.AGENT_DATA_TYPE.KIOSK_RECEIVE
        } else if(line.contains("[41] Send")){
            type = Constants.Companion.AGENT_DATA_TYPE.KIOSK_SEND
        } else if(line.contains("Socket")) {
            type = Constants.Companion.AGENT_DATA_TYPE.SOCKET
        }

        if(line.contains("INFO Program - ") && line.split("INFO Program - ").size > 1){
            contents = line.split("INFO Program - ")[1]
        }else if(line.contains("ERROR Program - ") && line.split("ERROR Program - ").size > 1){
            contents = line.split("ERROR Program - ")[1]
        }

        if(contents.isNotEmpty()){

            if(type == Constants.Companion.AGENT_DATA_TYPE.API){
                contents = contents.replace("Web Api - ", "[전송] ")
            }else if(type == Constants.Companion.AGENT_DATA_TYPE.API_RECEIVE){
                contents = contents.replace("Web Api - ", "[수신] ")
            }else if(type == Constants.Companion.AGENT_DATA_TYPE.KIOSK_RECEIVE){
                contents = contents.replace("[41] Receive - ", "")
                contents = contents.replace("[41] Receive Raw Packet - ", "")
            }else if(type == Constants.Companion.AGENT_DATA_TYPE.KIOSK_SEND){
                contents = contents.replace("[41] Send - ", "")
            }else if(type == Constants.Companion.AGENT_DATA_TYPE.SEND_FOR_GPP) {
                contents = contents.replace("팜플러스 메세지 보냄 - ", "[GPP] ")
            }else if(type == Constants.Companion.AGENT_DATA_TYPE.RECEPTION_BY_GPP) {
                contents = contents.replace("팜플러스 메세지 받음 - ", "[GPP] ")
            }else if(type == Constants.Companion.AGENT_DATA_TYPE.SOCKET){
                contents = contents.replace("[41] Socket connected to ", "소켓 연결 완료 : ")
            }

        }

        return AgentData(time, type, contents)
    } catch (e: Exception) {
        DLog.e(e)
    }

    return AgentData("", Constants.Companion.AGENT_DATA_TYPE.NORMAL, "")
}