package com.example.remedical;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;
    private TextView tv_email;
    private EditText et_name, et_birth, et_illname, et_illtime1, et_illtime2, et_illtime3;
    private ImageButton BtnCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info); // 레이아웃 설정

        // Firebase 및 뷰 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        tv_email = findViewById(R.id.tv_email);
        et_name = findViewById(R.id.et_name);
        et_birth = findViewById(R.id.et_birth);
        et_illname = findViewById(R.id.et_illname);
        et_illtime1 = findViewById(R.id.et_illtime1);
        et_illtime2 = findViewById(R.id.et_illtime2);
        et_illtime3 = findViewById(R.id.et_illtime3);
        BtnCheck = findViewById(R.id.BtnCheck);
        Intent getIntent = getIntent();
        String pw = getIntent.getStringExtra("pw");
        tv_email.setText(firebaseUser.getEmail());

        // 확인 버튼 클릭 이벤트 리스너 설정
        BtnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 정보 가져오기
                String name = et_name.getText().toString();
                String birth = et_birth.getText().toString();
                String illname = et_illname.getText().toString();
                String illtime1 = et_illtime1.getText().toString();
                String illtime2 = et_illtime2.getText().toString();
                String illtime3 = et_illtime3.getText().toString();

                // 유효성 검사 후 Firestore에 사용자 정보 저장
                if (name.length()==0 || birth.length()==0 || illname.length()==0
                        || illtime1.length()==0 || illtime2.length()==0 || illtime3.length()==0) {
                    Toast.makeText(getApplicationContext(),"값을 모두 입력해주세요",Toast.LENGTH_LONG).show();
                } else if (birth.length()!=8) {
                    Toast.makeText(getApplicationContext(),"생년월일을 8자리로 입력해주세요",Toast.LENGTH_LONG).show();
                } else if (illtime1.length()!=4 || illtime2.length()!=4 || illtime3.length()!=4) {
                    Toast.makeText(getApplicationContext(),"복용 시간을 4자리로 입력해주세요",Toast.LENGTH_LONG).show();
                }
                else {
                    // Firestore에 사용자 정보 저장
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", firebaseUser.getEmail());
                    user.put("pw", pw);
                    user.put("name", name);
                    user.put("birth", birth);
                    user.put("illname", illname);
                    user.put("illtime1", illtime1);
                    user.put("illtime2", illtime2);
                    user.put("illtime3", illtime3);
                    // ... 사용자 정보 맵에 추가
                    db.collection("User").document(firebaseUser.getUid()).set(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    // 저장 성공 시
                                    Toast.makeText(InfoActivity.this, "회원 정보 저장에 성공하였습니다", Toast.LENGTH_SHORT).show();
                                    // Firebase Realtime Database에 시간 정보 저장
                                    Map<String, Object> time =new HashMap<>();
                                    time.put("illtime1", illtime1);
                                    time.put("illtime2", illtime2);
                                    time.put("illtime3", illtime3);
                                    mDatabase.child("time").setValue(time);

                                    // 메인 활동으로 인텐트 전환
                                    Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(InfoActivity.this, "회원 정보 저장에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                    Log.d("InfoActivity", e.toString());
                                }
                            });
                }
            }
        });
    }
}
// 오승윤 접신한날 2023-10-06~07