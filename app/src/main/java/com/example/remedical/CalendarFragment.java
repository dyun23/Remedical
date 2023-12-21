package com.example.remedical;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment implements CalendarView.OnDateChangeListener, View.OnClickListener {
    private String selectDay=null;
    private CalendarView calendarView;
    private ImageButton save_Btn, update_Btn, cha_Btn,del_Btn;
    private ImageView ill1, ill2, ill3;
    private TextView memoTextView,textView2;
    private EditText contextEditText;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    private DatabaseReference mDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // fragment_calendar 레이아웃을 인플레이트하여 이 프래그먼트의 뷰 생성
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Log.d("CalendarFragment", "onCreateView called");

        calendarView=view.findViewById(R.id.calendarView);
        memoTextView=view.findViewById(R.id.memoTextView);
        save_Btn=view.findViewById(R.id.save_Btn);
        del_Btn=view.findViewById(R.id.del_Btn);
        cha_Btn=view.findViewById(R.id.cha_Btn);
        update_Btn=view.findViewById(R.id.update_Btn);
        textView2=view.findViewById(R.id.textView2);
        contextEditText=view.findViewById(R.id.contextEditText);
        ill1=view.findViewById(R.id.img_ill1);
        ill2=view.findViewById(R.id.img_ill2);
        ill3=view.findViewById(R.id.img_ill3);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        // 현재 날짜를 가져와서 메모 텍스트뷰에 설정
        Date dateNow = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy / MM / dd", Locale.getDefault());
        memoTextView.setText(format.format(dateNow));
        format.applyPattern("yyyyMMdd");
        selectDay = format.format(dateNow);
        contextEditText.setText("");
        checkDay();
        checkIll(ill1);
        checkIll(ill2);
        checkIll(ill3);

        // 이벤트 리스너 설정
        calendarView.setOnDateChangeListener(this);
        save_Btn.setOnClickListener(this);
        cha_Btn.setOnClickListener(this);
        del_Btn.setOnClickListener(this);
        update_Btn.setOnClickListener(this);

        return view;
    }

    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        // 달력에서 날짜가 선택되었을 때 호출되는 메서드
        try {
            Log.d("CalendarFragment", "onSelectedDayChange called");
            memoTextView.setText(String.format("%d / %d / %d",year,month+1,dayOfMonth));
            selectDay = String.format("%04d%02d%02d", year, month+1, dayOfMonth);
            contextEditText.setText("");
            checkDay();
            checkIll(ill1);
            checkIll(ill2);
            checkIll(ill3);
        } catch (Exception e) {
            Log.e("CalendarFragment", "setOnDateChangeListener 오류: " + e.getMessage());
        }
    }
    public void onClick(View v){
        if (v.getId() == R.id.save_Btn) {
            // 버튼이 클릭되었을 때 실행할 코드
            uploadMemo();
            textView2.setText(contextEditText.getText());
            save_Btn.setVisibility(View.INVISIBLE);
            cha_Btn.setVisibility(View.VISIBLE);
            del_Btn.setVisibility(View.VISIBLE);
            contextEditText.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.cha_Btn) {
            contextEditText.setText(textView2.getText());
            contextEditText.setVisibility(View.VISIBLE);
            textView2.setVisibility(View.INVISIBLE);
            update_Btn.setVisibility(View.VISIBLE);
            cha_Btn.setVisibility(View.INVISIBLE);
            del_Btn.setVisibility(View.INVISIBLE);
        } else if (v.getId() == R.id.del_Btn) {
            deleteMemo();
            textView2.setVisibility(View.INVISIBLE);
            contextEditText.setText("");
            contextEditText.setVisibility(View.VISIBLE);
            save_Btn.setVisibility(View.VISIBLE);
            cha_Btn.setVisibility(View.INVISIBLE);
            del_Btn.setVisibility(View.INVISIBLE);
        } else if (v.getId() == R.id.update_Btn) {
            updateMemo();
            textView2.setText(contextEditText.getText());
            update_Btn.setVisibility(View.INVISIBLE);
            cha_Btn.setVisibility(View.VISIBLE);
            del_Btn.setVisibility(View.VISIBLE);
            contextEditText.setVisibility(View.INVISIBLE);
            textView2.setVisibility(View.VISIBLE);
        }
    }
    public void checkDay(){
        // 특정 날짜에 대한 메모가 있는지 확인하는 메서드
        DocumentReference productRef = db.collection("Memo").document(firebaseUser.getUid());
        productRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    String context = document.getString(selectDay);
                    if(context==null){
                        textView2.setVisibility(View.INVISIBLE);
                        memoTextView.setVisibility(View.VISIBLE);
                        save_Btn.setVisibility(View.VISIBLE);
                        cha_Btn.setVisibility(View.INVISIBLE);
                        del_Btn.setVisibility(View.INVISIBLE);
                        update_Btn.setVisibility(View.INVISIBLE);
                        contextEditText.setVisibility(View.VISIBLE);
                    } else {
                        textView2.setText(context);
                        contextEditText.setVisibility(View.INVISIBLE);
                        textView2.setVisibility(View.VISIBLE);
                        save_Btn.setVisibility(View.INVISIBLE);
                        cha_Btn.setVisibility(View.VISIBLE);
                        del_Btn.setVisibility(View.VISIBLE);
                        update_Btn.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }
    public void uploadMemo(){
        // 메모를 업로드하는 메서드
        String text = contextEditText.getText().toString();
        Map<String, Object> memo = new HashMap<>();
        memo.put(selectDay, text);
        db.collection("Memo").document(firebaseUser.getUid()).set(memo, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(), "메모가 저장됐습니다", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "메모 저장에 실패했습니다",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void updateMemo(){
        // 메모를 업데이트하는 메서드
        String text = contextEditText.getText().toString();
        DocumentReference updateRef = db.collection("Memo").document(firebaseUser.getUid());
        updateRef.update(selectDay,text).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getActivity(), "메모가 수정됐습니다", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "메모 수정에 실패했습니다", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void deleteMemo(){
        // 메모를 삭제하는 메서드
        DocumentReference docRef = db.collection("Memo").document(firebaseUser.getUid());
        Map<String, Object> delete = new HashMap<>();
        delete.put(selectDay, FieldValue.delete());
        docRef.update(delete).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(), "메모가 삭제됐습니다", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "메모 삭제에 실패했습니다", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void checkIll(ImageView view) {
        // 약 복용 여부를 확인하는 메서드
        String time, timeKr;
        if (view.getId() == R.id.img_ill1) {
            time = "morning";
            timeKr = "아침";
        } else if (view.getId() == R.id.img_ill2) {
            time = "lunch";
            timeKr = "점심";
        } else if (view.getId() == R.id.img_ill3) {
            time = "evening";
            timeKr = "저녁";
        } else {
            time = "";
            timeKr = "";
        }
        mDatabase.child("time_to_app").child(time).child(selectDay).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("CalendarFragment", "Data loaded from Firebase for " + timeKr);
                String value = snapshot.getValue(String.class);
                if(value!=null) {
                    view.setImageResource(R.drawable.yes);
                } else {
                    view.setImageResource(R.drawable.no);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "데이터를 불러오는데 실패하였습니다", Toast.LENGTH_SHORT).show();
                Log.e("CalendarFragment", "Error loading data: " + error.getMessage());
            }
        });
    }
}