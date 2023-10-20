package com.sjk.yoram.data.entity

data class Board(
    val board_id: Int,
    val category_id: Int,
    val category_name: String,
    val owner_user_id: Int,
    val owner_user_name: String,
    val board_option_script: String,
    val board_option_script_date: String,
    val board_title: String,
    val board_content: String,
    val board_media_list: List<BoardMedia>,
    val board_date: String,
    val board_update: String,
    val board_view_count: Int,
    val board_comment_count: Int,
    val board_like_count: Int
) {

    /**
     * View로 사용하기 위해 게시물 내용과 미디어 리스트에서 태그와 사용하지 않는 요소를 제외하고 순서대로 ArrayDeque<BoardContentsBody> 로 반환합니다.
     *
     * 사용하지 않는 태그 및 요소는 공백 태그(&nbsp;)와 링크([BoardMedia.Link])로 된 이미지 파일(jpg, jpeg, png, bmp)입니다.
     *
     * @see BoardMedia.Link.isImageLink
     *
     * @return ArrayDeque<[BoardContentsBody]>
     */
    fun parseContentsBody(): java.util.ArrayDeque<BoardContentsBody> {
        val queue = java.util.ArrayDeque<BoardContentsBody>()

        val contents = StringBuilder(board_content)
        val blankRegex = "&nbsp;".toRegex()
        var match = blankRegex.find(contents.toString())

        // Remove all blank tag
        while (match != null) {
            contents.replace(match.range.first, match.range.last+1, " ")
            match = blankRegex.find(contents.toString())
        }

        val mediaRegex = "\\\\!%MEDIA%!\\\\".toRegex()
        var index = 0

        // Parse Contents with Media
        match = mediaRegex.find(contents.toString())
        while (match != null) {
            val contentsBeforeMedia = contents.substring(0, match.range.first)
            queue.addLast(BoardContentsBody.Contents(contentsBeforeMedia))
            contents.delete(0, match.range.last+1)

            board_media_list.getOrNull(index)?.let {
                if (it is BoardMedia.Link && it.isImageLink)
                    return@let
                queue.addLast(BoardContentsBody.Media(it))
            }
            index++
            match = mediaRegex.find(contents.toString())
        }

        // Just parsing only has Contents
        if (contents.toString().isNotBlank())
            queue.addLast(BoardContentsBody.Contents(contents.toString()))

        return queue
    }

    override fun equals(other: Any?): Boolean = other is Board
            && other.board_id == this.board_id
            && other.category_id == this.category_id

}

/**
 * 게시물 모든 내용을 담은 클래스입니다.
 *
 * [Contents] : 게시물에서 텍스트로 된 원문 내용을 담은 객체입니다.
 *
 * [Media] : 게시물에서 멀티미디어(사진, 유튜브, 링크 등)을 담은 객체입니다. 자세한 사항은 [BoardMedia]를 참고하세요.
 *
 */
sealed class BoardContentsBody {
    data class Contents(val data: String): BoardContentsBody()
    data class Media(val data: BoardMedia): BoardContentsBody()
}