package com.sjk.yoram.model.dto

/**
 * 게시물에서 사용하는 HTML 태그를 멀티미디어로 표현한 클래스입니다.
 *
 * 게시물 본문 내용에서 \\!%MEDIA%!\\ 로 된 부분에 해당 객체와 짝을 이룹니다.
 *
 * [Empty] : 게시물을 구현하는데 사용하지 않는 태그를 표현한 객체입니다.
 *
 * [Link] : 링크, 하이퍼텍스트로 된 태그를 표현합니다.
 *
 * [Image] : 이미지 태그를 표현합니다.
 *
 * [Youtube] : 유튜브 태그를 표현합니다.
 *
 * [File] : 파일 태그를 표현합니다.
 */
sealed class BoardMedia(val type: BoardMediaType) {
    object Empty: BoardMedia(BoardMediaType.NONE)
    data class Link(val url: String): BoardMedia(BoardMediaType.LINK) {
        val isImageLink: Boolean
            get() = url.endsWith("jpg", true)
                    || url.endsWith("jpeg", true)
                    || url.endsWith("png", true)
                    || url.endsWith("bmp", true)
    }
    data class Image(val url: String): BoardMedia(BoardMediaType.IMAGE)
    data class Youtube(val url: String, val id: String, val thumbnail: String): BoardMedia(BoardMediaType.YOUTUBE)
    data class File(val url: String): BoardMedia(BoardMediaType.FILE)
}