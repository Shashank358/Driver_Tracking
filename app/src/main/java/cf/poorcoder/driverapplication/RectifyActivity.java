package cf.poorcoder.driverapplication;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RectifyActivity extends AppCompatActivity {

    EditText date,month;
    Button rectifyButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser;

    ProgressDialog pd;

    Toolbar toolbar;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectify);

        rectifyButton = (Button) findViewById(R.id.rectifyButton);

        firebaseUser = firebaseAuth.getCurrentUser();

        date = (EditText) findViewById(R.id.choosenDate);
        month = (EditText) findViewById(R.id.choosenMonth);

        rectifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(RectifyActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(true);
                pd.setMessage("Please Wait");
                pd.show();


                if(date.getText().toString() != null && month.getText().toString() != null)
                {
                    String push = db.collection("RectificationRequests").document().getId();

                    HashMap<String,String> hashMap = new HashMap<String, String>();

                    hashMap.put("push",push);
                    hashMap.put("date",date.getText().toString());
                    hashMap.put("month",month.getText().toString());
                    hashMap.put("user_id",firebaseUser.getUid());

                    db.collection("RectificationRequests").document(push).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                        }
                    });
                }
            }
        });
    }

    void setUpToolBar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/font.ttf");
        toolbarTitle.setTypeface(type);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
