package cf.poorcoder.driverapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText userName,userEmail,userPassword;
    Button registerButton;
    TextView loginText;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String,String> hashDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        loginText = (TextView) findViewById(R.id.loginText);
        userName = (EditText) findViewById(R.id.registerName);
        userEmail = (EditText) findViewById(R.id.registerEmail);
        userPassword = (EditText) findViewById(R.id.registerPassword);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;

                if(TextUtils.isEmpty(userEmail.getText()))
                {
                    userEmail.setError("This field can't be empty");
                    userEmail.requestFocus();
                    flag = 0;
                }
                if(TextUtils.isEmpty(userName.getText()))
                {
                    userName.setError("This field can't be empty");
                    userName.requestFocus();
                    flag = 0;

                }
                if(userPassword.getText().toString().length() < 6)
                {
                    userPassword.setError("Password must be atleast 6 letters long");
                    userPassword.requestFocus();
                    flag = 0;
                }
                if(flag == 1) {
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setMessage("Registering");
                    pd.show();
                    //register user
                    registerUser(userEmail.getText().toString(),userPassword.getText().toString());
                }
            }
        });
    }

    private void registerUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    //Getting UID of user
                    String uid = current_user.getUid();

                    hashDetails = new HashMap<String, String>();
                    hashDetails.put("user_name",userName.getText().toString());
                    hashDetails.put("user_email",email);
                    hashDetails.put("user_pass",password);
                    hashDetails.put("push_id",uid);
                    hashDetails.put("image","default");
                    hashDetails.put("profile_image","default");
                    hashDetails.put("search",userName.getText().toString().toLowerCase());

                    db.collection("Drivers").document(uid).set(hashDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
}
