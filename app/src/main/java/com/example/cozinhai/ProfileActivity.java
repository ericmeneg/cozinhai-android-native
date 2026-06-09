package com.example.cozinhai;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CozinhaAiPrefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";

    private TextView txtUserName, txtUserEmail;
    private CardView cardChangePassword, cardViewComments, cardViewSavedRecipes, cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
        cardChangePassword.setOnClickListener(v -> 
            Toast.makeText(ProfileActivity.this, "Recurso em desenvolvimento", Toast.LENGTH_SHORT).show()
        );

        cardLogout.setOnClickListener(v -> logout());

        setupBottomNavigation();
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
