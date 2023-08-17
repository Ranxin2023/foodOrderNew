package com.example.food_order

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.time.LocalTime

class ViewRecipe:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_recipe)
        val viewButton=findViewById<Button>(R.id.viewButton)
        val orderIdView=findViewById<EditText>(R.id.orderIdEditText)
        val frenchFriesAmount=findViewById<TextView>(R.id.frenchFriesQuantity)
        val bigMacAmount=findViewById<TextView>(R.id.bigmacQuantity)
        val frenchFriesCost=findViewById<TextView>(R.id.frenchFriesCost)
        val bigMacCost=findViewById<TextView>(R.id.bigmacCost)
        val totalCost=findViewById<TextView>(R.id.totalCost)
        viewButton.setOnClickListener{
            val orderIdText=orderIdView.text.toString().toInt()
//            val urlLocalHost = "http://192.168.56.1//:80"  // Server-side endpoint
            val jsonArr=JSONArray(arrayOf(orderIdText))
            val jsonObject=JSONObject()
            jsonObject.put("method", "view order")
            jsonObject.put("args", jsonArr)
            val mediaType = "application/json; charset=utf-8"?.toMediaTypeOrNull()
            Thread{
                val requestBody=jsonObject.toString().toRequestBody(mediaType)
                val request= Request.Builder()
                    .url(Profile.baseUrl)
                    .post(requestBody)
                    .build()
                val response:Response
                val client= OkHttpClient()
                val call=client.newCall(request)
                try{
                    response=call.execute()
                    if(response.isSuccessful){
                        val serverResponse: String? = response.body?.string()
                        val jsonObject = JSONObject(serverResponse)
                        val success = jsonObject.getString("commit").toBoolean()
                        val responseData=jsonObject.getJSONObject("response data")
                        if (success){
                            runOnUiThread {
                                frenchFriesAmount.text=responseData.getString("french fries quantity")
                                bigMacAmount.text=responseData.getString("big mac quantity")
                                frenchFriesCost.text=responseData.getString("french fries cost")
                                bigMacCost.text=responseData.getString("big mac cost")
                                totalCost.text=responseData.getString("total cost")
                            }
                        }
                    }
                    else{
                        println("Request failed. Status code: ${response.code}")
                    }
                }catch (e:IOException){
                    e.printStackTrace()
                }
            }.start()
        }
        val exitButton=findViewById<Button>(R.id.exitButton)
        exitButton.setOnClickListener{
            //store logout time in database
            val jsonArr=JSONArray(arrayOf(LocalTime.now()))
            val jsonObject = JSONObject()
            jsonObject.put("method", "exit")
            jsonObject.put("args", jsonArr)
            val mediaType = "application/json; charset=utf-8"?.toMediaTypeOrNull()
            Thread{

                val requestBody = jsonObject.toString().toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(Profile.baseUrl)
                    .addHeader("Cookie", "sid=${Profile.sid}")
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
                        val exitSuccess=jsonObject.getString("success").toBoolean()

                        // Update the UI with the total price
                        if(exitSuccess){
                            Profile.sid=""
                            val intent=Intent(this, MainActivity::class.java)
                            startActivity(intent)
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
            }.start()
            //set profile id to be empty
        }
    }
//    fun parseSidCookie(cookiesHeader: String): String? {
//        val cookies = cookiesHeader.split(";").map { it.trim() }
//        for (cookie in cookies) {
//            val parts = cookie.split("=")
//            if (parts.size == 2 && parts[0] == "sid") {
//                return parts[1]
//            }
//        }
//        return null
//    }
}