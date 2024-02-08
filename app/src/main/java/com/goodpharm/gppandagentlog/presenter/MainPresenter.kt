package com.goodpharm.gppandagentlog.presenter

import androidx.lifecycle.MutableLiveData
import com.goodpharm.gppandagentlog.data.AgentData
import com.goodpharm.gppandagentlog.data.Constants
import com.goodpharm.gppandagentlog.data.GppData
import com.goodpharm.gppandagentlog.data.getAgentData
import com.goodpharm.gppandagentlog.data.getGppData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainPresenter {

    val isLoading = MutableLiveData(false)

    // file 원본
    var gppList = arrayListOf<String>()
    var agentList = arrayListOf<String>()

    // parsing list
    var gppDataList = arrayListOf<GppData>()
    var agentDataList = arrayListOf<AgentData>()

    var gppSync = MutableLiveData(false)
    var agentSync = MutableLiveData(false)

    // search list
    var gppSearchResults = arrayListOf<GppData>()
    var agentSearchResults = arrayListOf<AgentData>()
    var gppSearchIndex = 0
    var agentSearchIndex = 0

    fun initAgentData(txt: String) {
        agentList.clear()
        agentList.addAll(txt.split("\n"))
        agentDataList =
            ArrayList(agentList.map { getAgentData(it) }.filter { it.contents.isNotEmpty() })
    }

    fun initGppData(txt: String) {
        gppList.clear()
        gppList.addAll(txt.split("\n"))
        gppDataList = ArrayList(gppList.map { getGppData(it) }.filter { it.contents.isNotEmpty() })
    }

    fun setFilter(filterList: ArrayList<Constants.Companion.AGENT_DATA_TYPE>) {
        isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            agentDataList = ArrayList(
                agentList.map { getAgentData(it) }.filter {
                    it.contents.isNotEmpty() &&
                            filterList.firstOrNull { filter ->
                                filter == it.dataType
                            } != null
                }
            )

            MainScope().launch {
                agentSync.value = true
                isLoading.value = false
            }
        }


    }

    /**
     * 맞춤 필터
     * Agent :
     *
     *
     *
     * GPP :
     * 특정 단어 포함 ( 처방전, 조제, )
     * "LAW_DATA 1 - " : 수신 데이터 ( QR data, 화면이동 등 )
     *
     */
    fun setFilter(isCustomFilter: Boolean) {
        if (isCustomFilter) {
            isLoading.value = true

            CoroutineScope(Dispatchers.IO).launch {
                gppDataList =
                    ArrayList(gppList.map { getGppData(it) }.filter { checkGppCustomFilter(it) })

                MainScope().launch {
                    gppSync.value = true
                    isLoading.value = false
                }
            }
        }
    }

    private fun checkGppCustomFilter(data: GppData): Boolean {
        val contents = data.contents
        return if (
            contents.contains("LAW_DATA 1 : ") ||
            contents.contains("LAW_DATA 2 : ") ||
            contents.contains("ClientSocket - ")
        ) {
            false
        } else
            contents.contains("처방전") ||
                    contents.contains("실패") ||
                    contents.contains("취소") ||
                    contents.contains("수납파싱처리") ||
                    contents.contains("수납저장") ||
                    contents.contains("조제정보조회") ||
                    contents.contains("BufTxt Create") ||
                    contents.contains("#A") ||
                    contents.contains("#B") ||
                    contents.contains("Clesock1 -") ||
                    contents.contains("ClientSocket ATC") ||
                    contents.contains("Success") ||
                    contents.contains("CUSCODE") ||
                    contents.contains("SALE_MAIN SAVE")

    }

    private fun checkAgentCustomFilter(data: GppData): Boolean {
        val contents = data.contents
        return if (contents.contains("LAW_DATA 1 : ")) {
            false
        } else if (
            contents.contains("처방전")
        ) {
            true
        } else if (contents.contains("ClientSocket - ")) {
            if (contents.contains("http://")) {
                false
            } else if (contents.contains("GPBEGIN")) {
                false
            } else if (contents.equals("ClientSocket -")) {
                false
            } else {
                true
            }
        } else {
            false
        }

    }

    fun search(word: String) {

        gppSearchIndex = 0
        agentSearchIndex = 0

        isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {

            // gpp
            gppSearchResults = ArrayList(gppDataList.filter { it.contents.contains(word) })

            // agent
            agentSearchResults = ArrayList(agentDataList.filter { it.contents.contains(word) })

            MainScope().launch {
                agentSync.value = true
                gppSync.value = true
                isLoading.value = false
            }
        }

    }
}