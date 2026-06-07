package com.example.cozinhai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class ProfileActivity extends AppCompatActivity {

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

        // 2. Resgatar dados enviados pela tela de Login
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_NAME")) {
            String name = intent.getStringExtra("USER_NAME");
            String email = intent.getStringExtra("USER_EMAIL");

            txtUserName.setText(name);
            txtUserEmail.setText(email);
        }

        // 3. Configurar os cliques dos Cards
        cardChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Abrir Alterar Senha", Toast.LENGTH_SHORT).show();
            }
        });

        cardViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para tela de comentários
            }
        });

        cardViewSavedRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar para tela de receitas salvas
            }
        });

        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica de Logout: Volta para a MainActivity (Login) limpando o histórico
                Intent logoutIntent = new Intent(ProfileActivity.this, MainActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
            }
        });
    }
}