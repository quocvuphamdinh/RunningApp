package vu.pham.runningappseminar.utils

import vu.pham.runningappseminar.models.Music

object DataStore {
    fun getListMusicLocal(): List<Music>{
        return listOf(
            Music(1, "Music 1", "Author 1", 1, true),
            Music(1, "Music 2", "Author 2", 2, false),
            Music(1, "Music 3", "Author 3", 3, false),
            Music(1, "Music 4", "Author 4", 4, false),
            Music(1, "Music 5", "Author 5", 5, false)
        )
    }
}