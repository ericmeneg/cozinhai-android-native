package com.example.cozinhai;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

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

    }
}