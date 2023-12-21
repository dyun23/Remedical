package com.example.remedical;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private EditText mEt_Email, mEt_PW;
    private Button mBtnRegister, mBtnLogin;
    private ImageButton btn_find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase 인증 및 Firestore 인스턴스 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);
        mEt_Email = findViewById(R.id.et_email);
        mEt_PW = findViewById(R.id.et_pw);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnLogin = findViewById(R.id.btn_login);
        btn_find = findViewById(R.id.btn_find);

        // 로그인 버튼 클릭 이벤트 리스너
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(v.VISIBLE);
                String strEmail = mEt_Email.getText().toString();
                String strPW = mEt_PW.getText().toString();
                firebaseUser =  mFirebaseAuth.getCurrentUser();

                DocumentReference productRef = db.collection("User").document(firebaseUser.getUid());
                productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        progressBar.setVisibility(v.GONE);
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                signIn(strEmail, strPW, 0);
                            } else {
                                signIn(strEmail, strPW,1);
                            }
                        }
                    }
                });

            }
        });
        // 회원가입 버튼 클릭 이벤트 리스너
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        // 비밀번호 찾기 버튼 클릭 이벤트 리스너
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
    // 사용자 로그인 처리 메서드
    private void signIn(String email, String pw, int info){
        mFirebaseAuth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    // Firebase 인증을 사용하여 이메일과 비밀번호로 로그인 시도
                    if(mFirebaseAuth.getCurrentUser().isEmailVerified()){
                        Map<String, Object> user = new HashMap<>();
                        user.put("pw", pw);
                        db.collection("User").document(firebaseUser.getUid()).set(user, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (info == 0){
                                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else if(info == 1) {
                                            Toast.makeText(LoginActivity.this, "로그인 성공\n정보 입력 페이지로 넘어갑니다", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, InfoActivity.class);
                                            intent.putExtra("userid",firebaseUser.getUid());
                                            intent.putExtra("email",email);
                                            intent.putExtra("pw",pw);
                                            startActivity(intent);
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "이메일을 확인해 주소를 인증해주세요", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "등록된 회원이 없거나 입력하신 이메일과 비밀번호를 확인해주세요", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}