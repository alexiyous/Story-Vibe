package com.alexius.storyvibe

import com.alexius.storyvibe.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                id = "id_$i",
                name = "Story $i",
                description = "Description $i",
                photoUrl = "https://example.com/photo_$i.jpg",
                createdAt = "2023-10-01T00:00:00Z",
                lon = 0.0,
                lat = 0.0,
            )
            items.add(quote)
        }
        return items
    }
}