package com.example.intproject

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketSingleton constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: SocketSingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SocketSingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    val tag = "SocketSingleton"
    val socket: Socket by lazy {
        IO.socket("http://glennolsson.se:8082")
    }

    init {
        //instance = this

        socket.on(Socket.EVENT_CONNECT) {
            Log.i(tag, "Socket connected!")
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            Log.i(tag, "Socket disconnected!")
        }
        socket.on(Socket.EVENT_ERROR) { error ->
            Log.i(tag, "Event Error: " + error[0].toString())
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) { error ->
            Log.i(tag, "Event Connect Error: " + error[0].toString())
        }
        socket.on("notification") {
            // do something
        }
        socket.connect()
    }
}

/*
class SocketSingleton : Application() {

    val tag = "SocketSingleton"
    val socket: Socket by lazy {
        IO.socket("https://glennolsson.se/intnet/socket.io").connect()
    }
    val socket: Socket = IO.socket("http://glennolsson.se:8082")
    //val socket: Socket = IO.socket("http://130.229.129.92:8082/socket.io")


    init {
        instance = this

        socket.on(Socket.EVENT_CONNECT) {
            Log.i(tag, "Socket connected!")
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            Log.i(tag, "Socket disconnected!")
        }
        socket.on(Socket.EVENT_ERROR) { error ->
            Log.i(tag, "Event Error: " + error[0].toString())
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) { error ->
            Log.i("ConErr", "Event Connect Error: " + error[0].toString())
        }
        socket.on("notification") {
            // do something
        }
        socket.connect()
    }


    companion object {
        private var instance: SocketSingleton? = null

        fun applicationContext() : SocketSingleton {
            return instance as SocketSingleton
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        socket.on(Socket.EVENT_CONNECT) {
            Log.i(tag, "Socket connected!")
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            Log.i(tag, "Socket disconnected!")
        }
        socket.on(Socket.EVENT_ERROR) { error ->
            Log.i(tag, "Event Error: " + error[0].toString())
        }
        socket.on(Socket.EVENT_CONNECT_ERROR) { error ->
            Log.i("ConErr", "Event Connect Error: " + error[0].toString())
        }
        socket.on("notification") {
            // do something
        }
        socket.connect()
    }

    companion object {
        lateinit var instance: SocketSingleton
    }

    companion object {
        @Volatile
        private var INSTANCE: SocketSingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SocketSingleton(context).also {
                    INSTANCE = it
                }
            }
    }

    fun logSocket() {
        Log.i(tag, socket.connect().toString())
    }
}
*/