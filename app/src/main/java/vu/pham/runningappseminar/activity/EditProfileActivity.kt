package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import vu.pham.runningappseminar.R

class EditProfileActivity : AppCompatActivity() {
    private lateinit var txtCancel:TextView
    private lateinit var txtSave:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        anhXa()

        onClickCancel()
    }

    private fun onClickCancel() {
        txtCancel.setOnClickListener {
            finish()
        }
    }

    private fun anhXa() {
        txtCancel = findViewById(R.id.textViewCancelEditProfile)
        txtSave = findViewById(R.id.textViewSaveEditProfile)
    }
}