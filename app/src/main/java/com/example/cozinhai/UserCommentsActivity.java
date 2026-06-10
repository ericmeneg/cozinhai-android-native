package com.example.cozinhai;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCommentsActivity extends AppCompatActivity {

    private RecyclerView rvUserComments;
    private UserCommentsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Meus Comentários");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvUserComments = findViewById(R.id.rvUserComments);
        rvUserComments.setLayoutManager(new LinearLayoutManager(this));

        fetchUserComments();
    }

    private void fetchUserComments() {
        android.content.SharedPreferences prefs = getSharedPreferences("CozinhaAiPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", "");
        String token = prefs.getString("access_token", "");

        if (userId.isEmpty() || token.isEmpty()) {
            Toast.makeText(this, "Faça login para ver seus comentários", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AuthApi authApi = NetworkClient.getAuthApi();
        authApi.getUserRatings(userId, "Bearer " + token).enqueue(new Callback<List<UserComment>>() {
            @Override
            public void onResponse(Call<List<UserComment>> call, Response<List<UserComment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new UserCommentsAdapter(response.body());
                    rvUserComments.setAdapter(adapter);
                } else {
                    Log.e("USER_COMMENTS", "Erro ao carregar: " + response.code());
                    Toast.makeText(UserCommentsActivity.this, "Erro ao carregar comentários", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserComment>> call, Throwable t) {
                Log.e("USER_COMMENTS", "Falha na conexão", t);
                Toast.makeText(UserCommentsActivity.this, "Falha na conexão", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
