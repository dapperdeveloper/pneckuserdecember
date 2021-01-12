package com.callpneck.activity.registrationSecond;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.SplashActivity;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.adapter.SliderAdapterExample;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.model.ModelSliderMain;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class LoginScreenMain extends AppCompatActivity {

    Button btn_loginWithPone;
    CardView btn_gmail, btn_facebook;

    private static final String TAG = "FB";
    CallbackManager callbackManager;
    FirebaseAuth mAuth;

    private static final int RC_SIGN_IN =100 ;
    GoogleSignInClient mGoogleSignInClient;

    //sliderLayout
    private SliderView sliderView;
    List<ModelSliderMain>  modelSliderMainList;
    SessionManager sessionManager;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_login_second);
        sessionManager = new SessionManager(this);
        progressDialog = new SpotsDialog(LoginScreenMain.this, R.style.Custom);
        printKeyHash(this);
        init();
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginScreenMain.this,gso);
        sliderView.setScrollTimeInSec(3); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        loadScreen();
        clicks();
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                // String key = new String(Base64.encodeBytes(md.digest()));
            }
        } catch (PackageManager.NameNotFoundException e1) {
        }
        catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }

        return key;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String id=account.getId();
                String fname=""+account.getGivenName();
                String lname=""+account.getFamilyName();
                String email = ""+account.getEmail();
                String pic_url;
                if(account.getPhotoUrl()!=null) {
                    pic_url = account.getPhotoUrl().toString();
                }else {
                    pic_url="null";
                }
                registerApi(fname, lname,email,id,"Google");

               // firebaseAuthWithGoogle(account.getIdToken(),email,fname,lname);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        else if(callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void loadScreen() {
        modelSliderMainList = new ArrayList<>();
        modelSliderMainList.add(new ModelSliderMain(R.drawable.sliderone));
        modelSliderMainList.add(new ModelSliderMain(R.drawable.slidertwo));
        modelSliderMainList.add(new ModelSliderMain(R.drawable.sliderthree));
        modelSliderMainList.add(new ModelSliderMain(R.drawable.sliderfour));
        SliderAdapterExample adapter = new SliderAdapterExample(LoginScreenMain.this,modelSliderMainList );
        sliderView.setSliderAdapter(adapter);

    }

    private void clicks() {

        btn_loginWithPone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setLoginType("1");
                startActivity(new Intent(LoginScreenMain.this,LoginWithPhoneActivity.class));
            }
        });

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(LoginScreenMain.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(
                        callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {

                                // App code
                                handleFacebookAccessToken(loginResult.getAccessToken());
                            }
                            @Override
                            public void onCancel() {
                                // App code

                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code

                            }
                        }
                );
            }
        });

        btn_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginScreenMain.this);
                if (account != null) {

                    String id=account.getId();
                    String fname=""+account.getGivenName();
                    String lname=""+account.getFamilyName();
                    String email=""+account.getEmail();

                    registerApi(fname,lname,email,id,"Google");

                    String pic_url;

                    if(account.getPhotoUrl()!=null) {
                        pic_url = account.getPhotoUrl().toString();
                    }else {
                        pic_url="null";
                    }

                    if(fname.equals("") || fname.equals("null"))
                        fname=getResources().getString(R.string.app_name);

                    if(lname.equals("") || lname.equals("null"))
                        lname="User";

                    //Todo For database access api
                    // Call_Api_For_Signup(id,fname,lname,pic_url,"gmail");

                }
                else {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }

            }
        });



    }

    private void init() {
        btn_loginWithPone =findViewById(R.id.btn_loginWithPone);
        btn_gmail =findViewById(R.id.btn_gmail);
        btn_facebook =findViewById(R.id.btn_facebook);
        sliderView = findViewById(R.id.sliderLayout);
        btn_facebook = findViewById(R.id.btn_facebook);
       // btn_gmail = findViewById(R.id.btn_gmail);
    }


    // Todo Login With Gmail Handle
    private void firebaseAuthWithGoogle(String idToken,String email,String fname, String lname) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginScreenMain.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            FirebaseInstanceId.getInstance()
                                    .getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                                            Common.updateToken(getBaseContext(),task.getResult().getToken());

                                            //  Log.d("DAPPERTOKEN",task.getResult().getToken());
                                            Toast.makeText(LoginScreenMain.this, "Successfully Login"+email+fname+lname, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(LoginScreenMain.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginScreenMain.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    // Todo Login With Facebook Handle
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser userFirebase = mAuth.getCurrentUser();
                            String firebaseId = task.getResult().getUser().getUid();
                            String lastname = task.getResult().getUser().getDisplayName();
                            String facebookName = task.getResult().getUser().getDisplayName();
                            String facebookEmail = task.getResult().getUser().getEmail();
                            String facebookPhoto = task.getResult().getUser().getPhotoUrl().toString();
                            registerApi(facebookName,lastname,facebookEmail,firebaseId.toString(),"facebook");

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginScreenMain.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }



    public void registerApi(String fname, String lname,final String email,final String socialid,final String logintype) {
        mAuth.getInstance().signOut();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Map<String, String> params = new HashMap<>();

        params.put("first_name", fname);
        params.put("last_name", lname);
//        params.put("name", fname.toString()+lname.toString());
        params.put("email", email.toString());
        params.put("device_token", "ddggfgffggfgffgfgfg");
        params.put("social_id", socialid.toString());
        params.put("login_type", logintype.toString());
//        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        if(logintype.equals("facebook"))
        {
           // LoginManager.getInstance().logOut();
        }
        else {
            mAuth.signOut();
            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }

        progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(LoginScreenMain.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userRegisterBySocial, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                // Utility.dismissProgressDialog();
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {
                        sessionManager.setLoginType("2");
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        Integer isactive = jsonObject2.getInt("is_active");
                        Integer userid = jsonObject2.getInt("user_id");
                        String name = jsonObject2.getString("name");
                        sessionManager.setUserid(userid.toString());
                        if(isactive.toString().equals("1"))
                        {
                            String mobile = jsonObject2.getString("mobile");
                            String token = jsonObject2.getString("ep_token");
                            sessionManager.setSuccess(true);
                           sessionManager.createSession(name.toString(),mobile.toString(),userid.toString(),
                                    "",token.toString(),email,"");
                            Intent intent=new Intent(LoginScreenMain.this, MainSplashScreen.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            startActivity(new Intent(LoginScreenMain.this,LoginWithPhoneActivity.class));
                        }
                    } else {
                        Toast.makeText(LoginScreenMain.this, msg.toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                VolleyLog.d("Error", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d("error ocurred", "TimeoutError");
                } else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred", "AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.d("error ocurred", "ServerError");
                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred", "NetworkError");
                } else if (error instanceof ParseError) {
                    Log.d("error ocurred", "ParseError");
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
}