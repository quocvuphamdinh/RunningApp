package vu.pham.runningappseminar.utils

import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.models.Music

object DataStore {
    fun getListMusicLocal(): List<Music>{
        return listOf(
            Music(1, "Kataware Doki", "Radwimps", R.raw.your_name_lofi, false),
            Music(2, "Call of Silence", "Sawano Hiroyuki", R.raw.call_of_silence_lofi, false),
            Music(3, "Tanjirō no Uta", "Shiina Go", R.raw.demon_slayer_lofi, false),
            Music(4, "Hikaru Nara", "Goose house", R.raw.hikaru_nara_lofi, false),
            Music(5, "Secret Base", "Zone", R.raw.secret_base_lofi, false),
            Music(6, "Sincerely", "TRUE", R.raw.sincerely_lofi, false),
            Music(7, "Red Swan", "Yoshiki", R.raw.red_swan, false),
            Music(8, "Name of Love", "Cinema Staff", R.raw.name_of_love_lofi, false)
        )
    }
}