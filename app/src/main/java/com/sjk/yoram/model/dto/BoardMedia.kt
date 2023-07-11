package com.sjk.yoram.model.dto

sealed class BoardMedia(val type: BoardMediaType) {
    object Empty: BoardMedia(BoardMediaType.NONE)
    data class Link(val url: String): BoardMedia(BoardMediaType.LINK)
    data class Image(val url: String): BoardMedia(BoardMediaType.IMAGE)
    data class Youtube(val url: String, val id: String, val thumbnail: String): BoardMedia(BoardMediaType.YOUTUBE)
    data class File(val url: String): BoardMedia(BoardMediaType.FILE)
}