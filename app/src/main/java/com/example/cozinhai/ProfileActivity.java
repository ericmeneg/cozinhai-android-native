package com.example.cozinhai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CozinhaAiPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ACCESS_TOKEN = "access_token";

    private TextView txtUserName, txtUserEmail;
    private CardView cardChangePassword, cardViewComments, cardViewSavedRecipes, cardLogout;
    private AuthApi authApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupRetrofit();

        // 1. Vincular componentes do XML
        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);

        cardChangePassword = findViewById(R.id.cardChangePassword);
        cardViewComments = findViewById(R.id.cardViewComments);
        cardViewSavedRecipes = findViewById(R.id.cardViewSavedRecipes);
        cardLogout = findViewById(R.id.cardLogout);

        // 2. Carregar dados salvos
        loadUserData();

        // 3. Configurar os cliques dos Cards
        cardChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        cardViewComments.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, UserCommentsActivity.class));
        });

        cardViewSavedRecipes.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SavedRecipesActivity.class));
        });

        cardLogout.setOnClickListener(v -> logout());

        setupBottomNavigation();
    }

    private void setupRetrofit() {
        authApi = NetworkClient.getAuthApi();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alterar Senha");

        // Layout para o Diálogo
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputOld = new EditText(this);
        inputOld.setHint("Senha Atual");
        inputOld.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputOld);

        final EditText inputNew = new EditText(this);
        inputNew.setHint("Nova Senha");
        inputNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputNew);

        builder.setView(layout);

        builder.setPositiveButton("Alterar", (dialog, which) -> {
            String oldPass = inputOld.getText().toString().trim();
            String newPass = inputNew.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(this, "Preencha ambos os campos", Toast.LENGTH_SHORT).show();
            } else {
                performChangePassword(oldPass, newPass);
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void performChangePassword(String oldPass, String newPass) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, "");
        String token = prefs.getString(KEY_ACCESS_TOKEN, "");

        // Log para ajudar a identificar o que está faltando no console do Android Studio
        Log.d("PROFILE_DEBUG", "UserID: '" + userId + "'");
        Log.d("PROFILE_DEBUG", "Token presente: " + (token != null && !token.isEmpty()));

        if (userId.isEmpty() || token.isEmpty()) {
            String motivo = userId.isEmpty() ? "ID do usuário não encontrado" : "Sessão expirada";
            Toast.makeText(this, "Erro: " + motivo + ". Por favor, saia e entre novamente.", Toast.LENGTH_LONG).show();
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(oldPass, newPass);
        
        // Garante que o Bearer token esteja formatado corretamente
        String authHeader = token.startsWith("Bearer ") ? token : "Bearer " + token;

        authApi.changePassword(userId, authHeader, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("AUTH_API", "Erro ao alterar senha. Código: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            Log.e("AUTH_API", "Mensagem de erro: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ProfileActivity.this, "Erro ao alterar senha. Verifique a senha atual.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Erro de conexão com o servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnNavHome).setOnClickListener(v -> {
            startActivity(new Intent(this, Home.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.btnNavSearch).setOnClickListener(v -> {
            startActivity(new Intent(this, Search.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString(KEY_USER_NAME, "Usuário");
        String email = prefs.getString(KEY_USER_EMAIL, "usuario@email.com");

        txtUserName.setText(name);
        txtUserEmail.setText(email);
    }

    private void logout() {
        // Limpa as preferências (Remove o estado de login e dados do usuário)
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Volta para a MainActivity (Login) limpando o histórico de telas
        Intent logoutIntent = new Intent(ProfileActivity.this, MainActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logoutIntent);
        finish();
    }
}
