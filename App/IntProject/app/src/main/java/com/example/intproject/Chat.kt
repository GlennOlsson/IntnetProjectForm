package com.example.intproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_rooms.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class Chat : AppCompatActivity() {

    var userCache: HashMap<String, Bitmap> = HashMap<String, Bitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var id : String = ""

        try {
            id = intent.getStringExtra("id")
            val name: String = intent.getStringExtra("name")
            val count: String = intent.getStringExtra("count")

            txtName.text = name
            txtCount.text = "Online: " + count

        } catch (e: Exception) {
            txtName.text = "Err: " + e.toString()
        }

        val socketSingleton = SocketSingleton.getInstance(this.applicationContext)
        val tag = socketSingleton.tag
        val socket = socketSingleton.socket

        val jsonEmit = JSONObject()
        jsonEmit.put("chatid", id)
        socket.emit("join", jsonEmit)


        var url = "https://glennolsson.se/intnet/room/" + id
        val queue = RequestSingleton.getInstance(this.applicationContext).requestQueue

        val req = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                try {
                    val messages: JSONArray = response.getJSONArray("messages")

                    for (i in 0..messages.length()-1) {
                        val message = messages.getJSONObject(i)

                        val msg = message.getString("message")
                        val sentBy = message.getString("sentby")
                        //val sent = room.getInt("sent")



                        //usernameCache.add(sentBy)

                        val msgView: MessageView = MessageView(this, sentBy, msg)

                        val u = userCache.get(sentBy)
                        if (u != null) {
                            msgView.setImage(u)
                        } else {
                            // ugly

                            val url = "https://glennolsson.se/intnet/profile/" + sentBy

                            val req = JsonObjectRequest(
                                Request.Method.GET, url, null,
                                Response.Listener<JSONObject> { response ->
                                    val imgB64: String = response.getString("picture")
                                    val decodedString: ByteArray = Base64.decode(imgB64, Base64.DEFAULT)
                                    val decodedByte: Bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size-1)
                                    msgView.setImage(decodedByte)
                                    userCache.put(sentBy, decodedByte)
                                },
                                Response.ErrorListener { error ->
                                    txtDebug.text = error.message
                                })
                            queue.add(req)
                        }

                        msgView.setOnClickListener {
                            val username: String = sentBy

                            val intent = Intent(this, Profile::class.java)
                            intent.putExtra("username", username)
                            startActivity(intent)
                        }

                        linMessages.addView(msgView)
                    }
                } catch (e : Exception) {
                    txtDebug.text = "OnCreate: " + e.toString()
                }
            },
            Response.ErrorListener { error ->
                txtDebug.text = error.message
            })

        queue.add(req)
    }
}
