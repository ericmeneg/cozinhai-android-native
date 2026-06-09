package com.example.cozinhai;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Cadastro extends AppCompatActivity {

    private static final String BASE_URL = "https://pi-3sem-backend.onrender.com/";
    private AuthApi authApi;

    private EditText nameInput, emailInput, passwordInput, passwordConfirm;
    private TextView reqLength, reqUpper, reqLower, reqNumber, reqSymbol, entrarVisitante;
    private Button cadastrarBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        initializeViews();
        setupRetrofit();
        setupListeners();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        passwordConfirm = findViewById(R.id.passwordConfirm);
        
        reqLength = findViewById(R.id.reqLength);
        reqUpper = findViewById(R.id.reqUpper);
        reqLower = findViewById(R.id.reqLower);
        reqNumber = findViewById(R.id.reqNumber);
        reqSymbol = findViewById(R.id.reqSymbol);
        
        cadastrarBtn = findViewById(R.id.entrarBtn);
        progressBar = findViewById(R.id.loadingProgressBar);
        
        TextView loginRedirect = findViewById(R.id.cadastroText);
        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(Cadastro.this, MainActivity.class));
            finish();
        });
    }

    private void setupRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        authApi = retrofit.create(AuthApi.class);
    }

    private void setupListeners() {
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePasswordRequirements(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        cadastrarBtn.setOnClickListener(v -> attemptSignup());

        entrarVisitante = findViewById(R.id.visitanteOption);
        entrarVisitante.setOnClickListener(v -> {
            Intent intent = new Intent(Cadastro.this, Home.class);
            startActivity(intent);
            finish();
        });
    }

    private void validatePasswordRequirements(String password) {
        // Length 8-64
        updateRequirement(reqLength, password.length() >= 8 && password.length() <= 64);
        // Uppercase
        updateRequirement(reqUpper, password.matches(".*[A-Z].*"));
        // Lowercase
        updateRequirement(reqLower, password.matches(".*[a-z].*"));
        // Number
        updateRequirement(reqNumber, password.matches(".*[0-9].*"));
        // Symbol !@#$%^&*
        updateRequirement(reqSymbol, password.matches(".*[!@#$%^&*].*"));
    }

    private void updateRequirement(TextView textView, boolean fulfilled) {
        if (fulfilled) {
            textView.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            textView.setTextColor(Color.GRAY);
        }
    }

    private void attemptSignup() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirm = passwordConfirm.getText().toString();

        if (name.length() < 3 || name.length() > 30) {
            nameInput.setError("O nome deve ter entre 3 e 30 caracteres");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("E-mail inválido");
            return;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(this, "A senha não atende aos requisitos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            passwordConfirm.setError("As senhas não coincidem");
            return;
        }

        performSignup(new UserRequest(name, email, password));
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8 && password.length() <= 64 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*].*");
    }

    private void performSignup(UserRequest request) {
        progressBar.setVisibility(View.VISIBLE);
        cadastrarBtn.setEnabled(false);

        authApi.signup(request).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Cadastro.this, "Usuário criado com sucesso!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Cadastro.this, MainActivity.class));
                    finish();
                } else if (response.code() == 400) {
                    progressBar.setVisibility(View.GONE);
                    cadastrarBtn.setEnabled(true);
                    Toast.makeText(Cadastro.this, "Email já está em uso", Toast.LENGTH_SHORT).show();
                } else {
                    // Capta erro de spin up do backend no render
                    Log.d("SIGNUP", "Código de erro: " + response.code() + ". Tentando novamente em 5s...");
                    retrySignup(request);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.e("SIGNUP", "Falha na conexão, tentando novamente...", t);
                retrySignup(request);
            }
        });
    }

    private void retrySignup(UserRequest request) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> performSignup(request), 5000);
    }
}
