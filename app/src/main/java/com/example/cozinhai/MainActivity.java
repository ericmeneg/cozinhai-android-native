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

    private static final String PREFS_NAME = "CozinhaAiPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verifica se o usuário já está logado ANTES de carregar o layout
        if (isUserLoggedIn()) {
            goToHome();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.main_activity);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        EditText emailInput = findViewById(R.id.emailInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        Button entrarBtn = findViewById(R.id.entrarBtn);
        TextView cadastroText = findViewById(R.id.cadastroText);
        TextView entrarVisitante = findViewById(R.id.visitanteOption);

        // Configuração do link de cadastro
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cadastroText.setText(Html.fromHtml(getString(R.string.link_cadastro), Html.FROM_HTML_MODE_LEGACY));
        } else {
            cadastroText.setText(Html.fromHtml(getString(R.string.link_cadastro)));
        }

        // Opção de entrar como visitante
        entrarVisitante.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
            finish();
        });

<<<<<<< HEAD
        AuthApi authApi = NetworkClient.getAuthApi();
=======
        cadastroText.setOnClickListener(v->{
            startActivity(new Intent(MainActivity.this, Cadastro.class));
            finish();
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        AuthApi authApi = retrofit.create(AuthApi.class);
>>>>>>> 2b51aef84b2b6d3227dda0a11e71cf09cd5a6e0e

        // Botão entrar com validação de login via API
        entrarBtn.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() ){
                Toast.makeText(MainActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            LoginRequest request = new LoginRequest(email, password);

            authApi.login(request).enqueue(new Callback<LoginResponse>(){
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("LOGIN", "Sucesso ao receber resposta");
                        
                        LoginResponse loginResponse = response.body();
                        String token = loginResponse.getAccessToken();
                        String userName = "";
                        String userEmail = "";
                        String userId = "";

                        if (loginResponse.getUser() != null) {
                            userName = loginResponse.getUser().getName();
                            userEmail = loginResponse.getUser().getEmail();
                            userId = loginResponse.getUser().getId();
                            
                            // Log para depuração se o ID vier vazio
                            if (userId == null || userId.isEmpty()) {
                                Log.e("LOGIN", "AVISO: userId veio vazio do objeto user!");
                            }
                        }

                        if (token == null || token.isEmpty()) {
                            Log.e("LOGIN", "Token veio vazio do servidor!");
                            Toast.makeText(MainActivity.this, "Erro interno: Token não recebido", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Salva o estado de login e dados do usuário
                        saveLoginState(true, userName, userEmail, userId, token);
                        
                        Toast.makeText(MainActivity.this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        goToHome();
                    } else {
                        Log.e("LOGIN", "Erro no login. Código: " + response.code());
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
    }

    private boolean isUserLoggedIn() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void saveLoginState(boolean loggedIn, String name, String email, String id, String token) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_IS_LOGGED_IN, loggedIn)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_ID, id)
                .putString(KEY_ACCESS_TOKEN, token)
                .apply();
    }

    private void goToHome() {
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
        finish();
    }
}
