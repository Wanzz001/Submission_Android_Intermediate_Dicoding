package org.d3if0080.mystoryapp

import org.d3if0080.mystoryapp.database.Story

object DataDummy {

    fun generateDummyListStory(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                id = "story-DaB3492TI5Fln9pN",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1720671936009_ef53bbe2a2c3689c9136.jpg",
                createdAt = "2024-07-11T04:25:36.011Z",
                name = "student89",
                description = "tes",
                lon = 107.09153305739164,
                lat = -6.281467419903524
            )

            items.add(story)
        }

        return items
    }


    fun generateDummyToken() : String {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXNHamQzZWx0Wk1zckl1M3IiLCJpYXQiOjE2NTcyMTc2NjV9.ZlZaTNeZX3Db4KYwTkIaiUTBy5oX-3DkSmlSnpSuZws"
    }
}