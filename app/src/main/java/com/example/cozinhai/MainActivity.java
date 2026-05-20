package com.example.cozinhai;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

        ImageView mainLogo = findViewById(R.id.mainLogo);
        TextView bemVindoText = findViewById(R.id.bemVindoText);
        TextView entreText = findViewById(R.id.entreText);
        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button entrarBtn = findViewById(R.id.entrarBtn);
        TextView semContaText = findViewById(R.id.semContaText);
        TextView cadastroText = findViewById(R.id.cadastroText);
        TextView visitanteOption = findViewById(R.id.visitanteOption);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthApi authApi = retrofit.create(AuthApi.class);

        entrarBtn.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() ){
                Log.d("LOGIN", "Campo email ou senha vazio(s)");
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            authApi.login(request).enqueue(new Callback<Object>(){
                @Override
                public void onResponse(
                        Call<Object> call,
                        Response<Object> response
                ) {
                    if (response.isSuccessful()) {
                        Log.d("LOGIN", "Sucesso: " + response.body());
                    } else {
                        Log.d("LOGIN", "Código de erro: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e("LOGIN", "Request falhou", t);
                }
            });
        });

        cadastroText.setText(
                Html.fromHtml(getString(R.string.link_cadastro))
        );

    }
}