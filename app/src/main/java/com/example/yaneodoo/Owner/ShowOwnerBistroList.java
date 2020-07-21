package com.example.yaneodoo.Owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.yaneodoo.Common.ShowMenuInfo;
import com.example.yaneodoo.ListView.BistroListViewAdapter;
import com.example.yaneodoo.ListView.BistroListViewItem;
import com.example.yaneodoo.R;

public class ShowOwnerBistroList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bistro_list_owner);

        // TODO : owerId로 GET /stores하여 얻은 정보 아이템으로 추가

        // Adapter 생성
        BistroListViewAdapter adapter = new BistroListViewAdapter();

        // 리스트뷰 참조 및 Adapter달기
        ListView listview = (ListView) findViewById(R.id.bistro_list_view_owner);
        listview.setAdapter(adapter);

        // 아이템 추가 예시1
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.tteokbokki), "레드 175", "서울시 동작구", "#짜장 #짬뽕");
        // 아이템 추가 예시2
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.mypage), "얄리얄리얄랴셩", "사랑시 고백구", "#맛집 #또먹");

        //가게 선택 리스너
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // get item
                BistroListViewItem item = (BistroListViewItem) parent.getItemAtPosition(position);
                String titleStr = item.getTitle();

                Intent intent = new Intent(ShowOwnerBistroList.this, ShowOwnerMenuList.class);
                intent.putExtra("selectedBistro", titleStr);
                startActivity(intent);
            }
        });

        // 추가 버튼 클릭 리스너
        Button addbtn = (Button) findViewById(R.id.btn_add) ;
        addbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowOwnerBistroList.this, RegisterBistro.class);
                startActivity(intent);
            }
        });

        // 삭제 버튼 클릭 리스너
        final Button delbtn = (Button) findViewById(R.id.btn_delete) ;
        delbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : 여러 뷰 선택 활성화
                // onchoice 이용?
                if(delbtn.getText()=="삭제"){
                    delbtn.setText("확인");
                    delbtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,0f));
                }
                else{
                    // TODO : (삭제)확인 버튼 클릭 리스너
                    // DELETE /stores/{storeId}로 선택한 매장들 삭제
                    delbtn.setText("삭제");
                    delbtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
                }
            }
        });

        // 홈 버튼 클릭 리스너
        Button btnHome = (Button) findViewById(R.id.homebtn) ;
        btnHome.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowOwnerBistroList.this, ShowOwnerBistroList.class);
                startActivity(intent);
            }
        }) ;

        // TODO : mypagebtn 클릭 리스너
    }
}