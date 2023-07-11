package com.sjk.yoram.model.dto

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class BoardMediaDeserializer: JsonDeserializer<BoardMedia> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BoardMedia {
        val jsonObject = json?.asJsonObject ?: throw NullPointerException("BoardMedia Response is null")


        with(jsonObject["type"].asString) {
            if (this == "NONE")
                return BoardMedia.Empty
            else if (this == "LINK")
                return BoardMedia.Link(jsonObject["url"].asString)
            else if (this == "IMAGE")
                return BoardMedia.Image(jsonObject["url"].asString)
            else if (this == "YOUTUBE")
                return BoardMedia.Youtube(jsonObject["url"].asString, jsonObject["id"].asString, jsonObject["thumbnail"].asString)
            else if (this == "FILE")
                return BoardMedia.File(jsonObject["url"].asString)
            else
                return BoardMedia.Empty
        }
    }
}