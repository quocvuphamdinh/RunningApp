package vu.pham.runningappseminar.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.EditProfileActivity
import vu.pham.runningappseminar.activity.HistoryRunActivity

class ProfileFragment : Fragment() {
    private lateinit var imgEditProfile:ImageView
    private lateinit var cardViewRunHistory:CardView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        anhXa(view)

        imgEditProfile.setOnClickListener {
            goToEditProfile()
        }

        cardViewRunHistory.setOnClickListener {
            goToHistoryRun()
        }

        return view
    }

    private fun goToHistoryRun() {
        val intent = Intent(context, HistoryRunActivity::class.java)
        startActivity(intent)
    }

    private fun goToEditProfile() {
        val intent = Intent(context, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun anhXa(view: View) {
        imgEditProfile = view.findViewById(R.id.imageViewEditProfile)
        cardViewRunHistory = view.findViewById(R.id.cardViewHistoryRun)
    }
}