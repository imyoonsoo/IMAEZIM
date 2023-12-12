package com.example.imaezim

import MyFeedData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.imaezim.databinding.ActivityMyfeedBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imaezim.retrofit.MyPost
import com.example.imaezim.retrofit.PostService
import com.example.imaezim.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MyFeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyfeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myfeed)
        binding = ActivityMyfeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = "아무개"
        //val userName = LoginUser.nickname
        binding.userName.text = userName

        // 리사이클러뷰 초기화
        val recyclerView : RecyclerView = findViewById(R.id.rv_myfeed)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dataList = generateData()
        val  adapter = MyFeedAdapter(this, dataList)
        recyclerView.adapter = adapter

        binding.arButtonMyfeed.setOnClickListener {
            val intent = Intent(this, ARActivity::class.java)
            startActivity(intent)
        }

        binding.homeButtonMyfeed.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        fun calculationTime(dateTime: String): String {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            val parsedDateTime = LocalDateTime.parse(dateTime, formatter)
            val seoulZoneId = ZoneId.of("Asia/Seoul")
            val now = ZonedDateTime.now(seoulZoneId).toLocalDateTime()
            val difference = ChronoUnit.SECONDS.between(parsedDateTime, now)

            return when {
                difference < 60 -> "방금 전"
                difference < 3600 -> "${difference / 60}분 전"
                difference < 86400 -> "${difference / 3600}시간 전"
                else -> parsedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
        }

        val postService = RetrofitClient.getClient()?.create(PostService::class.java)

        fun myFeedPost() {
            lateinit var myPostList: List<MyPost>
            val call = postService!!.getMyPost(LoginUser.id)
            call.enqueue(object : Callback<List<MyPost>> {
                override fun onResponse(call: Call<List<MyPost>>, response: Response<List<MyPost>>) {
                    if (!response.isSuccessful) return
                    myPostList = response.body()!!
                    val myPostDataList = mutableListOf<MyFeedData>()

                    for(myPost in myPostList) {
//                        when (myPost.memoType) {
//                            "A" -> myPostDataList.add(
//                            MyFeedData(
//                                MyFeedData.MemoType.TEXT,
//                                text=myPost.text,
//                                calculationTime(myPost.date),
//                                myPost.latitude,
//                                myPost.longitude,
//                                myPost.location_type,
//                                myPost.detailAddr
//                            )
//                        )
//                            "B" ->
//                            "C" ->
//                            "D" ->
//                        }
                    }
                    //어댑터 연결
                }
                override fun onFailure(call: Call<List<MyPost>>, t: Throwable) {
                    Log.d("Debug", "onFailure 실행$t")
                }
            })
        }
        myFeedPost()
    }

    private fun generateData() : List<MyFeedData> {
        val data1 = MyFeedData(MyFeedData.MemoType.TEXT, text="광화문 첫 방문! 이것저것 구경하고 맛있는 것도...", map = R.drawable.img_map_1, time="3시간 전")
        val data2 = MyFeedData(MyFeedData.MemoType.IMAGE, image = R.drawable.img_myfeed_1, map = R.drawable.img_map_2, time="2023-12-04")
        val data3 = MyFeedData(MyFeedData.MemoType.VIDEO, video = R.raw.home, map = R.drawable.img_map_3, time="2023-11-25")
        val data4 = MyFeedData(MyFeedData.MemoType.AUDIO, audio = R.raw.sample_1, map = R.drawable.img_map_1, time = "2023-11-11")

        return listOf(data1, data2, data3, data4)
    }
}