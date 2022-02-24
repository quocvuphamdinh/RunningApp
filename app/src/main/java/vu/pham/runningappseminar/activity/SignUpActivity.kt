package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.model.Sex

import android.view.ViewGroup

import android.graphics.Color
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import org.w3c.dom.Text


class SignUpActivity : AppCompatActivity() {
    private lateinit var txtGoBackWelComeScreen:TextView
    private lateinit var spinnerGioiTinh:Spinner
    private lateinit var listGioiTinh:ArrayList<Sex>
    private lateinit var adapterSpinner:ArrayAdapter<Sex>
    private lateinit var txtPassword:TextView
    private lateinit var txtPassword2:TextView
    private lateinit var editTextPassword:EditText
    private lateinit var editTextPassword2:EditText
    private var showPass = false
    private var showPass2 = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        anhXa()
        initSpinner()

        txtGoBackWelComeScreen.setOnClickListener {
            finish()
        }
        txtPassword.setOnClickListener {
            onClickShowPass(1)
        }
        txtPassword2.setOnClickListener {
            onClickShowPass(2)
        }
    }

    private fun initSpinner(){
        listGioiTinh = ArrayList()
        listGioiTinh.add(Sex(-1, "Gender"))
        listGioiTinh.add(Sex(1, "Nam"))
        listGioiTinh.add(Sex(2, "Nữ"))
        listGioiTinh.add(Sex(3, "Khác"))
        adapterSpinner = object: ArrayAdapter<Sex>(this@SignUpActivity, android.R.layout.simple_list_item_1, listGioiTinh){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if(position==0){
                    view.setTextColor(resources.getColor(R.color.grey_100))
                }else{
                    view.setTextColor(resources.getColor(R.color.black))
                }
                return view
            }
        }

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGioiTinh.adapter = adapterSpinner

        spinnerGioiTinh.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItemText = parent.getItemAtPosition(position).toString()
                if (position > 0) {
                    Toast.makeText(this@SignUpActivity, "Selected : $selectedItemText", Toast.LENGTH_SHORT).show()
                }else if(position==0){
                    (view as TextView).setTextColor(resources.getColor(R.color.grey_100))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun anhXa() {
        spinnerGioiTinh = findViewById(R.id.spinnerGioiTinh)
        txtGoBackWelComeScreen = findViewById(R.id.textViewGoBackWelcomeScreen)
        txtPassword = findViewById(R.id.textViewShowHidePasswordSignUp)
        txtPassword2 = findViewById(R.id.textViewShowHidePasswordSignUp2)
        editTextPassword = findViewById(R.id.editTextPasswordSignUp)
        editTextPassword2 = findViewById(R.id.editTextPasswordSignUp2)
    }
    private fun onClickShowPass(option:Int) {
        if(option==1){
            if(showPass){
                showPass=false
                editTextPassword.transformationMethod= PasswordTransformationMethod.getInstance()
                txtPassword.text = "SHOW"
            }else{
                showPass=true
                editTextPassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
                txtPassword.text = "HIDE"
            }
        }else if(option==2){
            if(showPass2){
                showPass2=false
                editTextPassword2.transformationMethod= PasswordTransformationMethod.getInstance()
                txtPassword2.text = "SHOW"
            }else{
                showPass2=true
                editTextPassword2.transformationMethod= HideReturnsTransformationMethod.getInstance()
                txtPassword2.text = "HIDE"
            }
        }

    }
}