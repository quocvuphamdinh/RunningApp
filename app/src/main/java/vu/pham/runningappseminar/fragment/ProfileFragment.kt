package vu.pham.runningappseminar.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.EditProfileActivity

class ProfileFragment : Fragment() {
    private lateinit var imgEditProfile:ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        anhXa(view)
        goToEditProfile()
        return view
    }

    private fun goToEditProfile() {
        imgEditProfile.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun anhXa(view: View) {
        imgEditProfile = view.findViewById(R.id.imageViewEditProfile)
    }
}