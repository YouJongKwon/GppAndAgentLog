package com.goodpharm.gppandagentlog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.goodpharm.gppandagentlog.adapter.AgentAdapter
import com.goodpharm.gppandagentlog.adapter.GppAdapter
import com.goodpharm.gppandagentlog.base.BaseActivity
import com.goodpharm.gppandagentlog.data.Constants
import com.goodpharm.gppandagentlog.databinding.ActivityMainBinding
import com.goodpharm.gppandagentlog.dialog.DialogSearch
import com.goodpharm.gppandagentlog.dialog.DialogTypeFilter
import com.goodpharm.gppandagentlog.presenter.MainPresenter
import com.goodpharm.gppandagentlog.util.FileUtils
import com.goodpharm.gppandagentlog.util.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private var presenter = MainPresenter()

    private var adapterGpp = GppAdapter()
    private val adapterAgent = AgentAdapter()

    private var isScrollGpp = true

    private val takeFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    presenter.isLoading.value = true
                    CoroutineScope(Dispatchers.IO).launch {
                        val txt = FileUtils.readFile(this@MainActivity, data) ?: return@launch
                        if (txt.length > 500) {
                            Log.d("####", "txt : ${txt.substring(0, 500)}")
                        } else {
                            Log.d("####", "txt : ${txt}")
                        }

                        MainScope().launch {
                            val fileName = FileUtils.getFileName(this@MainActivity, data)
                            if (fileName.contains("agent")) {
                                presenter.initAgentData(txt)
                                adapterAgent.setList(presenter.agentDataList)
                            } else if (fileName.contains("GPP")) {
                                presenter.initGppData(txt)
                                adapterGpp.setList(presenter.gppDataList)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "agent 혹은 GPP가 들어간 파일을 선택해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            presenter.isLoading.value = false
                        }
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding {

        }
    }

    override fun observeData() {

        presenter.isLoading.observe(this@MainActivity) { loading ->
            MainScope().launch {
                mBinding.loadingLayer.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }

        presenter.gppSync.observe(this@MainActivity){
            if(it){
                adapterGpp.setList(presenter.gppDataList)
                presenter.gppSync.value = false

                if(presenter.gppSearchResults.isEmpty()){
                    mBinding.gppArrowLayer.visibility = View.GONE
                }else {
                    mBinding.gppArrowLayer.visibility = View.VISIBLE
                    gppSearchUp()
                }
            }
        }

        presenter.agentSync.observe(this@MainActivity){
            if(it){
                adapterAgent.setList(presenter.agentDataList)
                presenter.agentSync.value = false

                if(presenter.agentSearchResults.isEmpty()){
                    mBinding.agentArrowLayer.visibility = View.GONE
                }else {
                    mBinding.agentArrowLayer.visibility = View.VISIBLE
                }
            }
        }

        mBinding.recyclerViewGpp.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isScrollGpp = true
            }
        })

        mBinding.recyclerViewAgent.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                isScrollGpp = false
            }
        })
    }

    override fun setEvent() {

        mBinding.recyclerViewGpp.adapter = adapterGpp
        mBinding.recyclerViewAgent.adapter = adapterAgent

        // 파일 추가
        mBinding.btnAddFile.setOnClickListener {
            takeFile.launch(FileUtils.openFileChooser())
        }

        // 싱크
        mBinding.btnSync.setOnClickListener {
            sync()
        }

        // 타입 필터
        mBinding.btnTypeFilter.setOnClickListener {
            DialogTypeFilter(object : DialogTypeFilter.TypeCallback {
                override fun onConfirm(filterList: ArrayList<Constants.Companion.AGENT_DATA_TYPE>) {
                    presenter.setFilter(filterList)
                }

                override fun onCustomFilter() {
                    presenter.setFilter(true)
                }
            }).show(supportFragmentManager, "filter")
        }

        // 검색
        mBinding.btnSearch.setOnClickListener {
            DialogSearch{ word ->
                search(word)
            }.show(supportFragmentManager, "search")
        }

        mBinding.btnGppUp.setOnClickListener {
            gppSearchUp()
        }

        mBinding.btnGppDown.setOnClickListener {
            gppSearchDown()
        }

    }

    private fun sync(){

        if(adapterAgent.list.isEmpty() || adapterGpp.list.isEmpty()){
            return
        }

        if(isScrollGpp){
            // agent 스크롤 싱크
            val position = (mBinding.recyclerViewGpp.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val dateTimes = adapterGpp.list[position].time

            adapterAgent.list.firstOrNull { it.time == dateTimes }.also {
                if(it != null){
                    mBinding.recyclerViewAgent.scrollToPosition(adapterAgent.list.indexOf(it))
                }else {
                    // 해당 시간보다 좀 더 전 시간으로 검색
                    val currentDate = TimeUtils.dateFormat.parse(dateTimes)
                    currentDate.time = currentDate.time - 1000
                    val findTime = TimeUtils.dateFormat.format(currentDate)

                    adapterAgent.list.firstOrNull { it.time == findTime }.also {secondData ->
                        if(secondData != null){
                            mBinding.recyclerViewAgent.scrollToPosition(adapterAgent.list.indexOf(secondData))
                        }
                    }
                }
            }

        }else {

            // gpp 스크롤 싱크
            val position = (mBinding.recyclerViewAgent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val dateTimes = adapterAgent.list[position].time

            adapterGpp.list.firstOrNull { it.time == dateTimes }.also {
                if(it != null){
                    mBinding.recyclerViewGpp.scrollToPosition(adapterGpp.list.indexOf(it))
                }else {
                    // 해당 시간보다 좀 더 전 시간으로 검색
                    val currentDate = TimeUtils.dateFormat.parse(dateTimes)
                    currentDate.time = currentDate.time - 1000
                    val findTime = TimeUtils.dateFormat.format(currentDate)

                    adapterGpp.list.firstOrNull { it.time == findTime }.also {secondData ->
                        if(secondData != null){
                            mBinding.recyclerViewGpp.scrollToPosition(adapterGpp.list.indexOf(secondData))
                        }
                    }
                }
            }
        }

    }

    private fun search(word : String){
        presenter.search(word)
    }

    private fun gppSearchUp(){
        if(presenter.gppSearchResults.isEmpty()){
            mBinding.btnGppUp.setColorFilter(Color.parseColor("#aaaaaa"))
            mBinding.btnGppDown.setColorFilter(Color.parseColor("#aaaaaa"))
            return
        }

        presenter.gppSearchIndex--

        if(presenter.gppSearchIndex <= 0){
            presenter.gppSearchIndex = 0
            mBinding.btnGppUp.setColorFilter(Color.parseColor("#aaaaaa"))
        }else {
            mBinding.btnGppUp.setColorFilter(Color.parseColor("#777777"))
        }

        if(presenter.gppSearchIndex >= presenter.gppSearchResults.size - 1){
            mBinding.btnGppDown.setColorFilter(Color.parseColor("#aaaaaa"))
        }else {
            mBinding.btnGppDown.setColorFilter(Color.parseColor("#777777"))
        }

        val position = presenter.gppDataList.indexOf(presenter.gppSearchResults[presenter.gppSearchIndex])
        (mBinding.recyclerViewGpp.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
//        mBinding.recyclerViewGpp.smoothScrollToPosition(position)
//        mBinding.recyclerViewGpp.scrollToPosition(position)
    }

    private fun gppSearchDown(){

        presenter.gppSearchIndex++
        if(presenter.gppSearchIndex >= presenter.gppSearchResults.size - 1){
            presenter.gppSearchIndex = presenter.gppSearchResults.size - 1
            mBinding.btnGppDown.setColorFilter(Color.parseColor("#aaaaaa"))
        }else {
            mBinding.btnGppDown.setColorFilter(Color.parseColor("#777777"))
        }

        if(presenter.gppSearchIndex == 0){
            mBinding.btnGppUp.setColorFilter(Color.parseColor("#aaaaaa"))
        }else {
            mBinding.btnGppUp.setColorFilter(Color.parseColor("#777777"))
        }

        val position = presenter.gppDataList.indexOf(presenter.gppSearchResults[presenter.gppSearchIndex])
        (mBinding.recyclerViewGpp.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, 0)
//        mBinding.recyclerViewGpp.smoothScrollToPosition(position)
//        mBinding.recyclerViewGpp.scrollToPosition(position)
    }
}