package com.example.acefc

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.Serializable
import java.util.concurrent.TimeUnit

object DataProvider {
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Connection timeout
        .readTimeout(60, TimeUnit.SECONDS)    // Read timeout
        .writeTimeout(60, TimeUnit.SECONDS)   // Write timeout
        .build()

    val service = Retrofit.Builder()
        .baseUrl("https://acefc-api.fly.dev/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(LiveFCService::class.java)

    fun getLiveFC(onResponse: (liveFCs: List<LiveFC>) -> Unit) {
        service.getLiveFC().enqueue(object : Callback<List<LiveFC>> {

            override fun onFailure(call: Call<List<LiveFC>>, t: Throwable) {
                Log.d("DataProvider::getLiveFC", "An error happened!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<List<LiveFC>>, response: Response<List<LiveFC>>) {
                Log.d("DataProvider::getLiveFC", response.body().toString())
                if (response.isSuccessful) {
                    onResponse(response.body()!!)
                }
            }
        })
    }

    fun getLiveFCStreams(id: String, onResponse: (liveFCStreams: List<LiveFCStream>) -> Unit) {
        service.getLiveFCStreams(id).enqueue(object : Callback<List<LiveFCStream>> {

            override fun onFailure(call: Call<List<LiveFCStream>>, t: Throwable) {
                Log.d("TAG_", "An error happened!")
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<List<LiveFCStream>>,
                response: Response<List<LiveFCStream>>
            ) {
                Log.d("TAG_", response.body().toString())
                if (response.isSuccessful) {
                    onResponse(response.body()!!)
                }
            }
        })
    }

}

data class LiveFC(val id: String, val title: String, val time: String): Serializable {
    override fun toString(): String {
        return "LiveFC{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", time='" + time + '\'' +
                '}'
    }

    companion object {
        internal const val serialVersionUID = 727566175075960653L
    }
}

data class LiveFCStream(val id: String, val quality: String, val language: String)

interface LiveFCService {
    @GET("/livefc")
    fun getLiveFC(): Call<List<LiveFC>>

    @GET("/livefcstreams/{id}")
    fun getLiveFCStreams(@Path("id") id: String): Call<List<LiveFCStream>>
}
