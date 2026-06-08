package com.example.cozinhai;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.Html;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_URL = "https://pi-3sem-backend.onrender.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_activity);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button entrarBtn = findViewById(R.id.entrarBtn);
        TextView cadastroText = findViewById(R.id.cadastroText);
        TextView entrarVisitante = findViewById(R.id.visitanteOption);

        entrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });

        cadastroText.setText(
                Html.fromHtml(getString(R.string.link_cadastro))
        );
        entrarVisitante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthApi authApi = retrofit.create(AuthApi.class);

        entrarBtn.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() ){
                Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                Log.d("LOGIN", "Campo email ou senha vazio(s)");
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            authApi.login(request).enqueue(new Callback<LoginResponse>(){
                @Override
                public void onResponse(
                        Call<LoginResponse> call,
                        Response<LoginResponse> response
                ) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getAccessTokenData().getToken();
                        Log.d("LOGIN", "Sucesso! Token: " + token);
                        Toast.makeText(MainActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent(MainActivity.this, Home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("LOGIN", "Erro no login. Código: " + response.code());
                        Toast.makeText(MainActivity.this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("LOGIN", "Erro de conexão", t);
                    Toast.makeText(MainActivity.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
                }
            });
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cadastroText.setText(Html.fromHtml(getString(R.string.link_cadastro), Html.FROM_HTML_MODE_LEGACY));
        } else {
            @SuppressWarnings("deprecation")
            android.text.Spanned result = Html.fromHtml(getString(R.string.link_cadastro));
            cadastroText.setText(result);
        }
    }
}
