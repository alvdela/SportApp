package com.alvdela.sportapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.properties.Delegates


class LoginActivity : AppCompatActivity() {

    companion object{
        lateinit var userEmail: String
        lateinit var providerSession: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()

    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var llRepeatPassword: LinearLayout
    private lateinit var inputPassword2: EditText
    private lateinit var termsView: LinearLayout
    private lateinit var termsButton: CheckBox
    private lateinit var loginButton: TextView

    private lateinit var loginAuth: FirebaseAuth

    private var RESULT_CODE_GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        termsButton = findViewById(R.id.terminos_check)

        termsView = findViewById(R.id.terminos)
        termsView.visibility = View.INVISIBLE
        inputPassword2 = findViewById(R.id.password_repeat)
        llRepeatPassword = findViewById(R.id.ll_repeat_password)
        llRepeatPassword.visibility = View.INVISIBLE

        initShowButtons()

        inputEmail = findViewById(R.id.email_input)
        inputPassword = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)

        loginAuth = FirebaseAuth.getInstance()
        manageButtonLogin()
        inputEmail.doOnTextChanged { text, start, before, count -> manageButtonLogin() }
        inputPassword.doOnTextChanged { text, start, before, count -> manageButtonLogin() }

    }

    private fun initShowButtons() {
        val passwordButton = findViewById<CheckBox>(R.id.show_password)
        val repeatButton = findViewById<CheckBox>(R.id.show_repeat)

        passwordButton.setOnCheckedChangeListener{ _, isChecked ->
            inputPassword.transformationMethod = if (isChecked) null else PasswordTransformationMethod.getInstance()
        }

        repeatButton.setOnCheckedChangeListener{ _, isChecked ->
            inputPassword.transformationMethod = if (isChecked) null else PasswordTransformationMethod.getInstance()
        }
    }

    private fun manageButtonLogin() {
        email = inputEmail.text.toString()
        password = inputPassword.text.toString()
        if(TextUtils.isEmpty(password) || !ValidateEmail.isEmail(email)){
            loginButton.setBackgroundColor(ContextCompat.getColor(this,R.color.gray))
            loginButton.isEnabled = false
        }else{
            loginButton.setBackgroundColor(ContextCompat.getColor(this,R.color.green))
            loginButton.isEnabled = true
        }
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null){
            goMain(email,"email")
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    fun login(view: View) {
        loginUser()
    }

    private fun loginUser() {
        email = inputEmail.text.toString()
        password = inputPassword.text.toString()

        loginAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful){
                goMain(email, "email")
            }else{
                if(termsView.visibility == View.INVISIBLE){
                    termsView.visibility = View.VISIBLE
                    llRepeatPassword.visibility = View.VISIBLE
                }else{
                    if (termsButton.isChecked){
                        register()
                    }
                }
            }
        }
    }

    private fun register() {
        if (inputPassword.text.toString() == inputPassword2.text.toString()){
            loginAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (it.isSuccessful){

                        var dateRegistrer = SimpleDateFormat("dd/MM/yyyy").format(Date())
                        var dbRegistrer = FirebaseFirestore.getInstance()

                        dbRegistrer.collection("users").document().set(hashMapOf(
                            "user" to email,
                            "dateRegistrer" to dateRegistrer
                        ))

                        goMain(email,"email")

                    }else{
                        Toast.makeText(this,"Error algo ha salido mal", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goMain(email: String, provider: String) {

        userEmail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }

    fun forgotPassword(view: View) {
        var e = inputEmail.text.toString()
        if (!TextUtils.isEmpty(e)){
            loginAuth.sendPasswordResetEmail(e)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"Email enviado", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"No se encontró al usuario con este correo", Toast.LENGTH_SHORT).show()

                    }
                }
        }else{
            Toast.makeText(this,"Indica un email", Toast.LENGTH_SHORT).show()
        }
    }
    fun goTerms(view: View) {
        val intent = Intent(this, TermsActivity::class.java)
        startActivity(intent)
    }

    fun callSignInGoogle(view: View) {
        signInGoogle()
    }

    private fun signInGoogle(){
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        startActivityForResult(googleSignInClient.signInIntent, RESULT_CODE_GOOGLE_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RESULT_CODE_GOOGLE_SIGN_IN) {

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!

                if (account != null) {
                    email = account.email!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    loginAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) goMain(email, "Google")
                        else Toast.makeText(
                            this,
                            "Error en la conexión con Google",
                            Toast.LENGTH_SHORT
                        )

                    }
                }


            } catch (e: ApiException) {
                Toast.makeText(this, "Error en la conexión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }
}