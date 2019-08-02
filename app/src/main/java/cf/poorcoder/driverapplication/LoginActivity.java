package cf.poorcoder.driverapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ImageView logoView;
    Animation animation,animationLoginView;
    RelativeLayout loginView;
    Button login;
    TextInputLayout username,pass;
    LinearLayout signUp;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //OnClickLogin
        login = (Button) findViewById(R.id.LoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userid = username.getEditText().getText().toString();
                String passwd = pass.getEditText().getText().toString();

                //Show Progress Bar
                pd = new ProgressDialog(LoginActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(true);
                pd.setTitle("Loading....");
                pd.setMessage("Please Wait");
                pd.show();

                signin(userid,passwd);
                //pd will be dismissed here
            }
        });

        username = (TextInputLayout) findViewById(R.id.input_layout_userId);
        pass = (TextInputLayout) findViewById(R.id.input_layout_pass);

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.LoginButton).requestFocusFromTouch();
            }
        });

        signUp = (LinearLayout) findViewById(R.id.signUpText);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Animation on start

        loginView = (RelativeLayout) findViewById(R.id.loginView);

        logoView = (ImageView) findViewById(R.id.loginLogo);

        animation = AnimationUtils.loadAnimation(this,R.anim.frombottom);
        animationLoginView = AnimationUtils.loadAnimation(this,R.anim.splash_anim);
        logoView.setAnimation(animation);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginView.setVisibility(View.VISIBLE);
            }
        },1500);

    }

    void signin(String userid,String passwd)
    {
        //Sign In with userid and pass
        mAuth.signInWithEmailAndPassword(userid,passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    pd.dismiss();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
