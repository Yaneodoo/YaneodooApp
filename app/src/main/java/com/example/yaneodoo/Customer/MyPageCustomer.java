package com.example.yaneodoo.Customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.yaneodoo.Info.Order;
import com.example.yaneodoo.Info.User;
import com.example.yaneodoo.InfoEdit;
import com.example.yaneodoo.ListView.OrderListViewAdapter;
import com.example.yaneodoo.Login;
import com.example.yaneodoo.R;
import com.example.yaneodoo.RetrofitService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyPageCustomer extends AppCompatActivity {
    private Intent intent;
    private String token, id, name, orderInfo;
    private Retrofit mRetrofit;
    private RetrofitService service;
    private String baseUrl = "https://api.bistroad.kr/v1/";

    private User user = new User();

    private OrderListViewAdapter adapter = new OrderListViewAdapter();
    private ListView listview;

    private List<Order> orderList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_customer);
        final SharedPreferences tk = getSharedPreferences("sFile", MODE_PRIVATE);
        listview = (ListView) findViewById(R.id.order_list_view_customer);

        token = getSharedPreferences("sFile", MODE_PRIVATE).getString("bistrotk", "");
        name = getSharedPreferences("sFile", MODE_PRIVATE).getString("fullName", "");
        id = getSharedPreferences("sFile", MODE_PRIVATE).getString("id", "");
        final TextView nameText = (TextView)findViewById(R.id.cutomer_name_textView);
        nameText.setText(name+" 고객님");

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = mRetrofit.create(RetrofitService.class);

        intent = getIntent();
        user = (User) intent.getSerializableExtra("userInfo");

        intent = getIntent();
        token = tk.getString("bistrotk", "");

        getOrderList(token, id);//자신의 주문내역 불러오기

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // 주문 아이템 하나 선택해도 일단은 아무것도 안함.
            }
        });

        // 홈 버튼 클릭 리스너
        TextView btnHome = (TextView) findViewById(R.id.homebtn);
        btnHome.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageCustomer.this, ShowCustomerBistroList.class);
                MyPageCustomer.this.finish();
                startActivity(intent);
            }
        });

        // 정보수정 버튼 클릭 리스너
        Button btnInfoEdit = (Button) findViewById(R.id.mypage_info_edit);
        btnInfoEdit.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageCustomer.this, InfoEdit.class);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼 클릭 리스너
        Button btnCustomer = (Button) findViewById(R.id.mypage_logout_button);
        btnCustomer.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = tk.edit();
                editor.putString("bId", ""); //
                editor.putString("bistrotk","");
                editor.commit();
                Intent intent = new Intent(MyPageCustomer.this, Login.class);
                startActivity(intent);
                MyPageCustomer.this.finish();
            }
        });
    }

    private void getOrderList(final String token, String userId) {
        service.getUserOrders("Bearer " + token, userId).enqueue(new Callback<List<Order>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    List<Order> body = response.body();
                    int statusCode  = response.code();
                    Log.d("MyOrders CODE",Integer.toString(statusCode));
                    Log.d("MyOrderToken", token);
                    Log.d("MyOrderId", id);
                    if (body != null) {
                        Log.d("MyOrders: ",body.toString());
                        for (int i = 0; i < body.size(); i++) {
                            Order order = new Order();
                            order.setHasReview(body.get(i).getHasReview());
                            order.setId(body.get(i).getId());
                            order.setProgress(body.get(i).getProgress());
                            order.setTableNum(body.get(i).getTableNum());
                            order.setTimestamp(body.get(i).getTimestamp());
                            order.setUserId(body.get(i).getUserId());
                            order.setRequest(body.get(i).getRequests());
                            orderList.add(order);

                            String requests = "";
                            Integer amount;
                            String menu = "";
                            // Todo: price 문제 해결되면 수정
                            Integer price = 0; // price 고쳐지면 변경
                            for( int j = 0 ; j < order.getRequests().size() ; j++ ){
                                amount = order.getRequests().get(j).getAmount();
                                menu = String.valueOf(order.getRequests().get(j).getMenu().getName());
                                // price = order.getRequests().get(j).getPrice() * amount;
                                requests += menu + " x " + amount.toString() + " = " + price.toString() + "\n";
                                Log.d("requests", requests);
                            }
                            requests = requests.substring(0,requests.length()-1);

                            if(order.getProgress().equals("REQUESTED"))
                                adapter.addItem(ContextCompat.getDrawable(MyPageCustomer.this, R.drawable.requested),
                                        order.getTimestamp().toString(), name, requests, "접수중",order.getId(),order.getTableNum());
                            else
                                adapter.addItem(ContextCompat.getDrawable(MyPageCustomer.this, R.drawable.accepted),
                                        order.getTimestamp().toString(), name, requests, "접수 완료",order.getId(),order.getTableNum());
                            Log.d("menu data", "--------------------------------------");
                        }
                        Log.d("getmyOrderList end", "======================================");
                        listview.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                t.printStackTrace();
                Log.d("fail", "======================================");
            }
        });

        // 홈 버튼 클릭 리스너
        TextView btnHome = (TextView) findViewById(R.id.homebtn);
        btnHome.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyPageCustomer.this, ShowCustomerBistroList.class);
                MyPageCustomer.this.finish();
                startActivity(intent);
            }
        });
    }

    // 주문접수 토글 버튼 클릭 리스너
    public void progressToggle(View v) {
        //do nothing
    }
}