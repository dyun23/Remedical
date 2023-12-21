package com.example.remedical;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    CalendarFragment calendarFragment;
    MapsFragment mapsFragment;
    MypageFragment mypageFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 프래그먼트 초기화
        calendarFragment = new CalendarFragment();
        mapsFragment = new MapsFragment();
        mypageFragment = new MypageFragment();

        // 초기 프래그먼트를 CalendarFragment로 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, calendarFragment).commit();

        // 하단 탐색 바 설정 및 리스너 구현
        NavigationBarView navigationBarView = findViewById(R.id.bottom_nav);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 선택된 아이템에 따라 해당 프래그먼트로 전환
                if (item.getItemId() == R.id.menuCalendar) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, calendarFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.menuMap) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, mapsFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.menuMypage) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypageFragment).commit();
                    return true;
                }
                return false;
            }
        });
    }
}