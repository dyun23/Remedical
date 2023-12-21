package com.example.remedical;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MypageFragment extends Fragment implements View.OnClickListener {
    private String checkPw = null;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;
    private TextView tv_email, tv_pw, tv_name, tv_birth, tv_illname, tv_illtime1, tv_illtime2, tv_illtime3;
    private EditText et_pw, et_illname, et_illtime1, et_illtime2, et_illtime3;
    private ImageButton btnCha, btnUpdate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        Log.d("MypageFragment", "onCreateView called");

        // Firebase 및 뷰 초기화
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        progressBar = view.findViewById(R.id.progressBar);
        tv_email = view.findViewById(R.id.tv_email);
        tv_name = view.findViewById(R.id.tv_name);
        tv_birth = view.findViewById(R.id.tv_birth);
        tv_pw = view.findViewById(R.id.tv_pw);
        tv_illname = view.findViewById(R.id.tv_illname);
        tv_illtime1 = view.findViewById(R.id.tv_illTime1);
        tv_illtime2 = view.findViewById(R.id.tv_illTime2);
        tv_illtime3 = view.findViewById(R.id.tv_illTime3);
        et_pw = view.findViewById(R.id.et_pw);
        et_illname = view.findViewById(R.id.et_illname);
        et_illtime1 = view.findViewById(R.id.et_illTime1);
        et_illtime2 = view.findViewById(R.id.et_illTime2);
        et_illtime3 = view.findViewById(R.id.et_illTime3);
        btnCha = view.findViewById(R.id.BtnCha);
        btnUpdate = view.findViewById(R.id.BtnUpdate);

        setText();

        btnCha.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);

        return view;
    }
    public void onClick(View v){
        // 클릭 이벤트를 처리하는 메서드. 버튼 클릭에 따라 UI 변경 또는 정보 업데이트
        if(v.getId() == R.id.BtnCha){
            setUI(0);
        } else if (v.getId() == R.id.BtnUpdate) {
            updateUser();
        }
    }
    // Firestore에서 사용자 정보를 가져와 화면에 표시하는 메서드
    public void setText(){
        DocumentReference productRef = db.collection("User").document(firebaseUser.getUid());
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    // Firestore에서 정보를 성공적으로 가져온 경우 UI에 표시
                    DocumentSnapshot document = task.getResult();
                    // 각 TextView에 정보 설정
                    tv_email.setText(document.getString("email"));
                    tv_name.setText(document.getString("name"));
                    tv_birth.setText(document.getString("birth"));
                    tv_illname.setText(document.getString("illname"));
                    tv_illtime1.setText(document.getString("illtime1"));
                    tv_illtime2.setText(document.getString("illtime2"));
                    tv_illtime3.setText(document.getString("illtime3"));
                    checkPw = document.getString("pw");
                }
            }
        });
    }
    public void setUI(int i) {
        // UI 상태를 설정하는 메서드
        switch (i) {
            case 0:
                // 정보 변경 모드 활성화: TextView를 숨기고 EditText를 표시
                tv_illname.setVisibility(View.INVISIBLE);
                tv_illtime1.setVisibility(View.INVISIBLE);
                tv_illtime2.setVisibility(View.INVISIBLE);
                tv_illtime3.setVisibility(View.INVISIBLE);
                btnCha.setVisibility(View.INVISIBLE);
                et_illname.setText(tv_illname.getText());
                et_illtime1.setText(tv_illtime1.getText());
                et_illtime2.setText(tv_illtime2.getText());
                et_illtime3.setText(tv_illtime3.getText());
                tv_pw.setVisibility(View.VISIBLE);
                et_pw.setVisibility(View.VISIBLE);
                et_illname.setVisibility(View.VISIBLE);
                et_illtime1.setVisibility(View.VISIBLE);
                et_illtime2.setVisibility(View.VISIBLE);
                et_illtime3.setVisibility(View.VISIBLE);
                btnUpdate.setVisibility(View.VISIBLE);
                break;
            case 1:
                // 일반 모드 활성화: EditText를 숨기고 TextView를 표시
                tv_pw.setVisibility(View.INVISIBLE);
                et_pw.setVisibility(View.INVISIBLE);
                et_pw.setText("");
                et_illname.setVisibility(View.INVISIBLE);
                et_illtime1.setVisibility(View.INVISIBLE);
                et_illtime2.setVisibility(View.INVISIBLE);
                et_illtime3.setVisibility(View.INVISIBLE);
                btnUpdate.setVisibility(View.INVISIBLE);
                tv_illname.setText(et_illname.getText());
                tv_illtime1.setText(et_illtime1.getText());
                tv_illtime2.setText(et_illtime2.getText());
                tv_illtime3.setText(et_illtime3.getText());
                tv_illname.setVisibility(View.VISIBLE);
                tv_illtime1.setVisibility(View.VISIBLE);
                tv_illtime2.setVisibility(View.VISIBLE);
                tv_illtime3.setVisibility(View.VISIBLE);
                btnCha.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void updateUser(){
        // 사용자 정보를 업데이트하는 메서드
        // 입력된 정보를 가져와 유효성 검사 후 Firestore에 업데이트
        String pw = et_pw.getText().toString();
        String illname = et_illname.getText().toString();
        String illtime1 = et_illtime1.getText().toString();
        String illtime2 = et_illtime2.getText().toString();
        String illtime3 = et_illtime3.getText().toString();
        if (pw.length()==0 || illname.length()==0 || illtime1.length()==0 ||
                illtime2.length()==0 || illtime3.length()==0) {
            Toast.makeText(getActivity(),"값을 모두 입력해주세요",Toast.LENGTH_SHORT).show();
        } else if (illtime1.length()!=4 || illtime2.length()!=4 || illtime3.length()!=4) {
            Toast.makeText(getActivity(),"복용 시간을 4자리로 입력해주세요",Toast.LENGTH_SHORT).show();
        } else if (!pw.equals(checkPw)){
            Toast.makeText(getActivity(),"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            //FireStore
            Map<String, Object> user = new HashMap<>();
            user.put("illname", illname);
            user.put("illtime1", illtime1);
            user.put("illtime2", illtime2);
            user.put("illtime3", illtime3);

            db.collection("User").document(firebaseUser.getUid()).set(user, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "회원 정보 수정에 성공하였습니다", Toast.LENGTH_SHORT).show();
                            setUI(1); //UI 설정
                            //FireBase
                            Map<String, Object> time =new HashMap<>();
                            time.put("illtime1", illtime1);
                            time.put("illtime2", illtime2);
                            time.put("illtime3", illtime3);
                            mDatabase.child("time").setValue(time);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "회원 정보 수정에 실패하였습니다", Toast.LENGTH_SHORT).show();
                            Log.d("MypageDBUpdate", e.toString());
                        }
                    });
        }

    }
}