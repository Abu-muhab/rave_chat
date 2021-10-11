package com.abumuhab.chat.network

import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI

class ChatSocketIO {
    companion object {
        var  socket: Socket?=null

        fun getInstance():Socket{
            if(socket==null){
                socket = IO.socket(URI.create(BASE_URL_TEST+"chat"))
            }
            return  requireNotNull(socket)
        }
    }
}