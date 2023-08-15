package com.example.food_order

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val orderButton=findViewById<Button>(R.id.ordering)
        orderButton.setOnClickListener{
            val intent = Intent(this, OrderFood::class.java)
            startActivity(intent)
            val urlLocalHost = "http://192.168.56.1//:80"

            val jsonObject = JSONObject()
            jsonObject.put("method", "init")
            val mediaType = "application/json; charset=utf-8"?.toMediaTypeOrNull()
            Thread{

                val requestBody = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(urlLocalHost)
                    .post(requestBody)
                    .build()
                val response: Response
                val client: OkHttpClient = OkHttpClient()
                val call=client.newCall(request)
                try{
                    response = call.execute()
                    if (response.isSuccessful) {
                        val serverResponse: String? = response.body?.string()
                        val jsonObject = JSONObject(serverResponse)
                        val storeSuccess=jsonObject.getString("success").toBoolean()



                        // Update the UI with the total price
                        if(storeSuccess){
                            val cookiesHeader = response.header("Set-Cookie")
                            if (cookiesHeader != null) {
                                val sidCookie = parseSidCookie(cookiesHeader)
                                if(sidCookie!=null&&Profile.sid!=sidCookie){
                                    Profile.sid=sidCookie
                                }

                            }
                        }
                        else{
                            println("Did not store into database")
                        }
                    } else {
                        println("Request failed. Status code: ${response.code}")
                    }
                } catch (e: IOException){
                    e.printStackTrace()
                }
            }
        }

    }
    fun parseSidCookie(cookiesHeader: String): String? {
        val cookies = cookiesHeader.split(";").map { it.trim() }
        for (cookie in cookies) {
            val parts = cookie.split("=")
            if (parts.size == 2 && parts[0] == "sid") {
                return parts[1]
            }
        }
        return null
    }
}