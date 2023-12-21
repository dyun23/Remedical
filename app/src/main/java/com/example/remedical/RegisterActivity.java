package com.example.remedical;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private ProgressBar progressBar;
    private EditText mEt_Email, mEt_PW, et_checkPw;
    private ImageButton mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase 및 뷰 초기화
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        progressBar = findViewById(R.id.progressBar);
        mEt_Email = findViewById(R.id.et_email);
        mEt_PW =  findViewById(R.id.et_pw);
        et_checkPw = findViewById(R.id.et_checkPw);
        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이메일, 비밀번호, 비밀번호 확인 입력값 가져오기
                String strEmail = mEt_Email.getText().toString();
                String strPW = mEt_PW.getText().toString();
                String strCheck = et_checkPw.getText().toString();

                // 입력값 누락이나 형식 불일치 등의 문제가 있을 경우 경고 메시지 표시
                if (strEmail.length()==0 || strPW.length()==0 || strCheck.length()==0) {
                    Toast.makeText(getApplicationContext(),"값을 모두 입력해주세요",Toast.LENGTH_LONG).show();
                } else if (strEmail.contains("@") == false || strEmail.contains(".") == false) {
                    Toast.makeText(getApplicationContext(),"이메일 형식에 맞춰 입력해주세요",Toast.LENGTH_LONG).show();
                } else if (strPW.length()<=7){
                    Toast.makeText(getApplicationContext(),"비밀번호를 8자리 이상 입력해주세요",Toast.LENGTH_LONG).show();
                } else if (!strPW.equals(strCheck)) {
                    Toast.makeText(getApplicationContext(),"비밀번호가 일치하지 않습니다",Toast.LENGTH_LONG).show();
                } else {
                    // 모든 조건 충족 시 회원가입 진행
                    progressBar.setVisibility(v.VISIBLE);
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail,strPW).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // 회원가입 성공 시 이메일 인증 메일 발송 및 로그인 화면으로 이동
                                mFirebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        if(task.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다\n이메일을 확인해 주소를 인증해주세요", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                            startActivity(intent);//다음 액티비티 화면에 출력
                                        } else {
                                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                // 회원가입 실패 시 오류 메시지 표시
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}