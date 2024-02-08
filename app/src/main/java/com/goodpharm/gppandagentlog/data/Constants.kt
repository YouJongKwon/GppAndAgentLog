package com.goodpharm.gppandagentlog.data

class Constants {

    companion object {

        /**
         * PROCESSING : 처리내용
         * RECEPTION : 수신내용
         * SEND : 송신내용
         */
        enum class GPP_DATA_TYPE {
            PROCESSING, RECEPTION, SEND
        }

        val gppDataTypeMap = HashMap<String, GPP_DATA_TYPE>().apply {
            put("처리내용", GPP_DATA_TYPE.PROCESSING)
            put("수신내용", GPP_DATA_TYPE.RECEPTION)
            put("송신내용", GPP_DATA_TYPE.SEND)
        }

        /**
         * Agent
         */
        enum class AGENT_DATA_TYPE {
            SEND_FOR_GPP, RECEPTION_BY_GPP, KIOSK_RECEIVE, KIOSK_SEND, API, API_RECEIVE, NORMAL, SOCKET
        }
    }
}