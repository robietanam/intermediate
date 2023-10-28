package com.example.androidintermedieatesubmission

import com.example.androidintermedieatesubmission.data.response.StoryResponse
import com.example.androidintermedieatesubmission.data.response.StoryResponseItem

object DataDummy {

    fun generateDummyStoryResponse(): StoryResponse {
        val items: MutableList<StoryResponseItem> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryResponseItem(
                i.toString(),
                "author + $i",
                "quote $i",
            )
            items.add(quote)
        }
        return StoryResponse(error = false, message = "Testing", listStory = ArrayList(items))
    }
}