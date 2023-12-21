package com.example.remedical;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class FindPasswordActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private EditText et_email;
    private ImageButton btn_find;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password); // 레이아웃 설정
        // 뷰 초기화
        progressBar = findViewById(R.id.progressBar);
        et_email = findViewById(R.id.et_email);
        btn_find = findViewById(R.id.btn_find);

        firebaseAuth = FirebaseAuth.getInstance();
        // 비밀번호 찾기 버튼 클릭 이벤트 리스너 설정
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(v.VISIBLE);
                // Firebase를 통해 비밀번호 재설정 이메일 보내기
                firebaseAuth.sendPasswordResetEmail(et_email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(v.GONE);
                                if(task.isSuccessful()) {
                                    // 비밀번호 재설정 메일 전송 성공시
                                    Toast.makeText(FindPasswordActivity.this, "이메일로 비밀번호 재설정 메일을 보냈습니다", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(FindPasswordActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(FindPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            Log.d("FindPW", e.toString());
                        }
                        });
            }
        });
    }
}