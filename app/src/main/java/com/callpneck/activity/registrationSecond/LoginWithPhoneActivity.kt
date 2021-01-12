package com.callpneck.activity.registrationSecond

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.callpneck.R
import com.callpneck.SessionManager
import com.callpneck.Language.ThemeUtils
import com.callpneck.commonutility.AllUrl
import com.callpneck.utils.Variables
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import dmax.dialog.SpotsDialog
import org.json.JSONObject
import java.util.*

class LoginWithPhoneActivity : AppCompatActivity(), SimpleCountDownTimerKotlin.OnCountDownListener{
    var login_with_phoneLayout: RelativeLayout? = null
    var verify_otp_layout: RelativeLayout? = null
    var register_layout_user_detail: RelativeLayout? = null
    var verityTitle: TextView? = null
    var resendotp: TextView? = null
    var phoneNumberEt: EditText? = null
    var timetext: TextView? = null
    var otpEt: EditText? = null
    var nameEt: EditText? = null
    var emailEt: EditText? = null
    var btn_next: Button? = null
    var btn_verify_otp: Button? = null
    var btn_register: Button? = null
    private var progressDialog: AlertDialog? = null
    var sessionManager: SessionManager? = null
    var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var usertoken: String? = null
    var mGoogleApiClient: GoogleApiClient? = null
    var mSmsBroadcastReceiver: SmsBroadcastReceiver? = null
    private val RESOLVE_HINT = 2

    private val senderSms = null //You can change to an number
    private val SMS_CONSENT_REQUEST = 200  // Set to an unused request code


    private var receivedOtp: String? = null

    private var deviceToken: String? = null

    private val countDownTimer =
            SimpleCountDownTimerKotlin(
                    0,
                    30,
                    this
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtils.setLanguage(this)
        setContentView(R.layout.activity_login_with_phone)
        sessionManager = SessionManager(this)
        progressDialog = SpotsDialog(this, R.style.Custom)


        init()
        clicks()
        startSmsUserConsent()
    }

    override fun onCountDownActive(time: String) {

        runOnUiThread {
            resendotp!!.visibility= View.GONE
            timetext!!.visibility= View.VISIBLE

            timetext!!.setText(time);
          /*  Toast.makeText(
                    this,
                    "Seconds = " + countDownTimer.getSecondsTillCountDown() + " Minutes=" + countDownTimer.getMinutesTillCountDown(),
                    Toast.LENGTH_SHORT
            ).show()*/
        }

    }

    override fun onCountDownFinished() {
        runOnUiThread {
            resendotp!!.visibility= View.VISIBLE
            timetext!!.visibility= View.GONE
           // tv.text = "Finished"
          //  startBtn.isEnabled = true
           // resumeBtn.isEnabled = false
        }
    }


    override fun onStop() {
        super.onStop()
      //  autoDetectOTP.stopSmsReciever()
    }

    val hintPhoneNumber: Unit
        get() {
            val hintRequest = HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build()
            val mIntent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient, hintRequest)
            try {
                startIntentSenderForResult(mIntent.intentSender, RESOLVE_HINT, null, 0, 0, 0)
            } catch (e: SendIntentException) {
                e.printStackTrace()
            }
        }

    private fun clicks() {
        btn_next!!.setOnClickListener {
            if (phoneNumberEt!!.text.toString().isEmpty()) {
                Toast.makeText(this@LoginWithPhoneActivity, "Mobile no is required", Toast.LENGTH_SHORT).show()
            } else {
                sendOtpApi(phoneNumberEt!!.text.toString())
            }
        }
        btn_verify_otp!!.setOnClickListener {
            if (otpEt!!.text.toString().isEmpty()) {
                Toast.makeText(this@LoginWithPhoneActivity, "Otp no is required", Toast.LENGTH_SHORT).show()
            } else {
                verifyotpApi(phoneNumberEt!!.text.toString(), otpEt!!.text.toString())
            }
        }
        btn_register!!.setOnClickListener {
            login_with_phoneLayout!!.visibility = View.GONE
            verify_otp_layout!!.visibility = View.GONE
            register_layout_user_detail!!.visibility = View.VISIBLE
            if (nameEt!!.text.toString() == "") {
                Toast.makeText(this@LoginWithPhoneActivity, "Name is required", Toast.LENGTH_SHORT).show()
            } else if (emailEt!!.text.toString() == "") {
                Toast.makeText(this@LoginWithPhoneActivity, "Email is required", Toast.LENGTH_SHORT).show()
            } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(emailEt!!.text.toString()).matches()) {
                Toast.makeText(this@LoginWithPhoneActivity, "Enter valid email", Toast.LENGTH_SHORT).show()
            } else {
                registerApi(nameEt!!.text.toString(), emailEt!!.text.toString())
            }
        }
        resendotp!!.setOnClickListener { sendOtpApi(phoneNumberEt!!.text.toString()) }
    }

    private fun init() {
        login_with_phoneLayout = findViewById(R.id.login_with_phoneLayout)
        verify_otp_layout = findViewById(R.id.verify_otp_layout)
        register_layout_user_detail = findViewById(R.id.register_layout_user_detail)
        verityTitle = findViewById(R.id.verityTitle)
        timetext = findViewById(R.id.time)
        phoneNumberEt = findViewById(R.id.phoneNumberEt)
        otpEt = findViewById(R.id.otpEt)
        nameEt = findViewById(R.id.nameEt)
        emailEt = findViewById(R.id.emailEt)
        resendotp = findViewById(R.id.resendotp)
        btn_next = findViewById(R.id.btn_next)
        btn_verify_otp = findViewById(R.id.btn_verify_otp)
        btn_register = findViewById(R.id.btn_register)
    }



    fun sendOtpApi(mobileno: String) {
        val params: MutableMap<String?, String?> = HashMap()
        if (sessionManager!!.loginType.toString() == "1")
        {
            params["mobile"] = mobileno
            params["type"] = "1"
        }
        else{
            params["mobile"] = mobileno
            params["type"] = "2"
        }
        Log.e("CHECKDATA", "ondata is called....." + params.toString())

        //        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        progressDialog!!.show()
        //Utility.showProgressDialog(this);
        val requestQueue = Volley.newRequestQueue(this@LoginWithPhoneActivity)
        val stringRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, AllUrl.userOtpSend, JSONObject(params), com.android.volley.Response.Listener { jsonObject ->
            // Utility.dismissProgressDialog();
            Log.e("CHECKDATA", "ondata response is called....." + jsonObject.toString())

            progressDialog!!.dismiss()
            try {
                val jsonObject1 = jsonObject.getJSONObject("response")
                val pass = jsonObject1.getBoolean("success")
                val msg = jsonObject1.getString("message")
                val resp_status = true
                if (pass == resp_status) {
                    val jsonObject2 = jsonObject1.getJSONObject("data")
                    val otp = jsonObject2.getString("create_otp")
                    Log.d("Seraj", otp);
                    login_with_phoneLayout!!.visibility = View.GONE
                    verify_otp_layout!!.visibility = View.VISIBLE
                    verityTitle!!.setText("Please wait We will send you the otp to +91 " + mobileno.toString())
                    countDownTimer.start()

                } else {
                    Toast.makeText(this@LoginWithPhoneActivity, msg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, com.android.volley.Response.ErrorListener { error ->
            Log.e("CHECKDATA", "ondata is called....." + error.toString())

            progressDialog!!.dismiss()
            VolleyLog.d("Error", "Error: " + error.message)
            if (error is TimeoutError || error is NoConnectionError) {
            } else if (error is AuthFailureError) {
            } else if (error is ServerError) {
            } else if (error is NetworkError) {
            } else if (error is ParseError) {
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        requestQueue.add(stringRequest)
    }

    fun verifyotpApi(mobileno: String, otp: String) {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@LoginWithPhoneActivity,
                OnSuccessListener<InstanceIdResult> { instanceIdResult ->
                    val newToken = instanceIdResult.token
                    deviceToken = newToken
                    Log.e("newToken", deviceToken)
                    val params: MutableMap<String?, String?> = HashMap()

                    if (sessionManager!!.loginType.toString() == "1")
                    {
                        params["mobile"] = mobileno
                        params["otp"] = otp
                        params["device_token"] = deviceToken
                    }
                    else
                    {
                        params["mobile"] = mobileno
                        params["otp"] = otp
                        params["user_id"] = sessionManager!!.userid
                        params["device_token"] = deviceToken
                    }

                    //        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);

                    progressDialog!!.show()
                    //Utility.showProgressDialog(this);
                    val requestQueue = Volley.newRequestQueue(this@LoginWithPhoneActivity)
                    val stringRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, AllUrl.userMobileVerifyOtpSend, JSONObject(params), com.android.volley.Response.Listener { jsonObject ->
                        //    Log.d("booking_pending_status", jsonObject.toString())

                        // Utility.dismissProgressDialog();
                        progressDialog!!.dismiss()
                        try {
                            val jsonObject1 = jsonObject.getJSONObject("response")
                            val pass = jsonObject1.getBoolean("success")
                            //Log.d("pass",pass);
                            val msg = jsonObject1.getString("message")
                            val resp_status = true
                            if (pass == resp_status) {
                                val jsonObject2 = jsonObject1.getJSONObject("data")
                                val token = jsonObject2.getString("ep_token")
                                val isactive = jsonObject2.getInt("is_active")
                                if (sessionManager!!.loginType.toString() == "1") {
                                    if (isactive.toString() == "1") {
                                        val userFirstName = jsonObject2.getString("first_name")
                                        val userId = jsonObject2.getString("user_id")
                                        val userTokenNo = jsonObject2.getString("ep_token")
                                        val userLastName = jsonObject2.getString("last_name")
                                        val userEmail = jsonObject2.getString("email")
                                        val userMobile = jsonObject2.getString("mobile")
                                        sessionManager!!.createSession(userFirstName, userMobile, userId,
                                                userLastName, userTokenNo, userEmail, jsonObject2.getString("image"))
                                        // intent.putExtra("mobile",mobile);
                                        /* Intent intent = new Intent(LoginWithPhoneActivity.this, SplashActivity.class);
                                        startActivity(intent);
                                        finish();*/
                                        sessionManager!!.setSuccess(true);
                                        val intent = Intent(this@LoginWithPhoneActivity, MainScreenActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        val userid = jsonObject2.getInt("user_id")
                                        usertoken = jsonObject2.getString("ep_token")
                                        login_with_phoneLayout!!.visibility = View.GONE
                                        verify_otp_layout!!.visibility = View.GONE
                                        register_layout_user_detail!!.visibility = View.VISIBLE
                                        sessionManager!!.userid = userid.toString()
                                    }
                                } else {
                                    val userFirstName = jsonObject2.getString("first_name")
                                    val userId = jsonObject2.getString("user_id")
                                    val userTokenNo = jsonObject2.getString("ep_token")
                                    val userLastName = jsonObject2.getString("last_name")
                                    val userEmail = jsonObject2.getString("email")
                                    val userMobile = jsonObject2.getString("mobile")
                                    sessionManager!!.setSuccess(true);

                                    sessionManager!!.createSession(userFirstName, userMobile, userId,
                                            userLastName, userTokenNo, userEmail, jsonObject2.getString("image"))
                                    // intent.putExtra("mobile",mobile);
                                    /* Intent intent = new Intent(LoginWithPhoneActivity.this, SplashActivity.class);
                                        startActivity(intent);
                                        finish();*/
                                    val intent = Intent(this@LoginWithPhoneActivity, MainScreenActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                Toast.makeText(this@LoginWithPhoneActivity, msg, Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, com.android.volley.Response.ErrorListener { error ->
                        progressDialog!!.dismiss()
                        VolleyLog.d("Error", "Error: " + error.message)
                        if (error is TimeoutError || error is NoConnectionError) {
                        } else if (error is AuthFailureError) {
                        } else if (error is ServerError) {
                        } else if (error is NetworkError) {
                        } else if (error is ParseError) {
                        }
                    }) {
                        @Throws(AuthFailureError::class)
                        override fun getHeaders(): Map<String, String> {
                            val headers: MutableMap<String, String> = HashMap()
                            headers["Content-Type"] = "application/json"
                            return headers
                        }
                    }
                    requestQueue.add(stringRequest)
                })


    }


    fun registerApi(name: String, email: String) {
        val params: MutableMap<String?, String?> = HashMap()
        params["user_id"] = sessionManager!!.userid
        params["name"] = name
        params["email"] = email
        //        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);

        progressDialog!!.show()
        //Utility.showProgressDialog(this);
        val requestQueue = Volley.newRequestQueue(this@LoginWithPhoneActivity)
        val stringRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, AllUrl.userRegister, JSONObject(params), com.android.volley.Response.Listener { jsonObject ->

            // Utility.dismissProgressDialog();
            progressDialog!!.dismiss()
            try {
                val jsonObject1 = jsonObject.getJSONObject("response")
                val pass = jsonObject1.getBoolean("success")
                //Log.d("pass",pass);
                val msg = jsonObject1.getString("message")
                val resp_status = true
                if (pass == resp_status) {
                    /* Toast.makeText(LoginWithPhoneActivity.this, msg.toString(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginWithPhoneActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();*/
                    val userId = sessionManager!!.userid
                    val userMobile = phoneNumberEt!!.text.toString()
                    sessionManager!!.createSession(name, userMobile, userId,
                            name, usertoken.toString(), email, "")

                    // intent.putExtra("mobile",mobile);
                    /*Intent intent = new Intent(LoginWithPhoneActivity.this, SplashActivity.class);
                        startActivity(intent);PneckMapLocation
                        finish();*/

                    sessionManager!!.setSuccess(true);
                    val intent = Intent(this@LoginWithPhoneActivity, MainScreenActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginWithPhoneActivity, msg.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, com.android.volley.Response.ErrorListener { error ->
            progressDialog!!.dismiss()
            VolleyLog.d("Error", "Error: " + error.message)
            if (error is TimeoutError || error is NoConnectionError) {
            } else if (error is AuthFailureError) {
            } else if (error is ServerError) {
            } else if (error is NetworkError) {
            } else if (error is ParseError) {
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        requestQueue.add(stringRequest)
    }







    private fun startSmsUserConsent() {
        SmsRetriever.getClient(this).also {
            //We can add sender phone number or leave it blank
            it.startSmsUserConsent(senderSms)
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener {
                    }
        }
    }

    override fun onStart() {
        super.onStart()
    }



    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_CONSENT_REQUEST -> {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {

                    Log.d("MainActivity", "onActivityResult LISTENING_FAILURE")

                    //That gives all message to us. We need to get the code from inside with regex
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    val code = message?.let { fetchVerificationCode(it) }
                    /*The message should be for exmaple: Your code verification is : 123456
                    * Because you need to write letters and numbers */
                    otpEt!!.setText(code)
                    verifyotpApi(phoneNumberEt!!.text.toString(), otpEt!!.text.toString())
                }
            }
        }
    }

    private fun fetchVerificationCode(message: String): String {
        return Regex("(\\d{6})").find(message)?.value ?: ""
    }
}