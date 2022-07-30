package vu.pham.runningappseminar.utils

import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.models.Music

object DataStore {
    fun getListMusicLocal(): List<Music>{
        return listOf(
            Music(1, "Action Teaser", "Stringer Bell", R.raw.action_teaser, false),
            Music(2, "All I Want", "EvgenyBardyuzha", R.raw.all_i_want, false),
            Music(3, "Epic and Dramatic", "Coma-Media", R.raw.epic_and_dramatic, false),
            Music(4, "Happy Travel Pop", "Stock Studio", R.raw.happy_travel_pop, false),
            Music(5, "Modern Fashion Promo Rock", "Coma-Media", R.raw.modern_fashion_promo_rock, false),
            Music(6, "Order", "ComaStudio", R.raw.order, false),
            Music(7, "Bright Energetic Electronica", "penguinmusic", R.raw.penguinmusic_bright_energetic_electronica, false),
            Music(8, "Vlog Groovy Hip-Hop", "Anton Vlasov", R.raw.vlog_groovy_hip_hop, false)
        )
    }
}