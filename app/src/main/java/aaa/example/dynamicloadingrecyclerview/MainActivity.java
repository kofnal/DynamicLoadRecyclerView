package aaa.example.dynamicloadingrecyclerview;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    List<Item> items = new ArrayList<>();
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(recyclerView, this, items);
        recyclerView.setAdapter(adapter);
        loadSql(0, 10);
        //Set Load more
        adapter.setLoadMore(new ILoadMore() {
            @Override
            public void onLoadMore() {
                items.add(null);
                adapter.notifyItemInserted(items.size() - 1);
                items.remove(items.size() - 1);
                adapter.notifyItemRemoved(items.size());
                int index = items.size();
                int end = index + 10;
                loadSql(index, end);
            }
        });
    }
    private void loadSql(final int indexStart, final int indexEnd) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ConfigData.SHOW_KATEGORIA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println("new JO response" + response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("tovar");
                    int razmerZaprosa = indexEnd - indexStart;
                    if (array.length() < razmerZaprosa || array.length() == 0) {
                        Toast.makeText(MainActivity.this, "Load data completed !", Toast.LENGTH_SHORT).show();
                        System.out.println("if345dfs3 bolhe net");
                    } else {
                        System.out.println("ehe est 345dfs3");
                    }
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jo = array.getJSONObject(i);
                        System.out.println("new JO Row" + jo);
                        Item newItem = new Item(jo.getString("id"), indexEnd);
                        items.add(newItem);
                    }
                    adapter.notifyDataSetChanged();
                    adapter.setLoaded();
                } catch (JSONException e) {
                    System.out.println("\n ERR SpisokTovarov LoadUrl new JO " + response);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error SQL new JO " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("vetka", "1");
                parameters.put("pokupatel", "59");
                parameters.put("indexstart", Integer.toString(indexStart));
                parameters.put("count", Integer.toString(indexEnd));
                System.out.println("Otpravka na server iz tovar id new JO " + parameters.toString());
                return parameters;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}