package com.blackcharm.NotificationHandler

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import java.util.HashMap

class CustomJsonObjectRequestBasicAuth(
    method:Int, url: String,
    jsonObject: JSONObject?,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener
)
    : JsonObjectRequest(method,url, jsonObject, listener, errorListener) {


    private var serverKey: String = "key=" + "AAAAXCtXNGw:APA91bEdR7X34IW-zkDzI87eHB818BRL_-3DnAAD6yxLvlpO-0eNmXgYSw0v0PEr-8Jy7Bnb_6xNzbm75EzUkMnvktFYL1XIoIHd7HZn6i4JxwdRZGH8aK2rez40tRqclLer6lbG6gBk";
    private var contentType: String = "application/json";
    @Throws(AuthFailureError::class)
    override fun getHeaders(): Map<String, String> {
        val headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        //val credentials:String = "username:password"
        val auth = serverKey
        headers["Authorization"] = auth
        return headers
    }
}