package com.example.cozinhai;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Search extends AppCompatActivity {

    private Button btnByIngredient, btnByTitle, btnSearchAction;
    private ImageButton btnFilterIcon;
    private EditText editSearch;
    private boolean isSearchingByIngredient = true;

    private final String[] filterOptions = {"Sem Lactose", "Sem Gluten", "Light", "Vegetariano", "Vegano"};
    private boolean[] selectedFilters = {false, false, false, false, false};
    
    // Substitua pela sua chave da API Spoonacular
    private static final String API_KEY = "SUA_API_KEY_AQUI"; 
    private SpoonacularApi spoonacularApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        // Inicializa o Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        spoonacularApi = retrofit.create(SpoonacularApi.class);

        btnByIngredient = findViewById(R.id.btnByIngredient);
        btnByTitle = findViewById(R.id.btnByTitle);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        btnFilterIcon = findViewById(R.id.btnFilterIcon);
        editSearch = findViewById(R.id.editSearch);

        btnByIngredient.setOnClickListener(v -> updateSearchMode(true));
        btnByTitle.setOnClickListener(v -> updateSearchMode(false));

        btnFilterIcon.setOnClickListener(v -> showFilterDialog());

        btnSearchAction.setOnClickListener(v -> {
            String query = editSearch.getText().toString().trim();
            if (!query.isEmpty()) {
                performSearch(query);
            } else {
                Toast.makeText(this, "Digite algo para pesquisar", Toast.LENGTH_SHORT).show();
            }
        });

        updateSearchMode(true);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione os Filtros");
        builder.setMultiChoiceItems(filterOptions, selectedFilters, (dialog, which, isChecked) -> {
            selectedFilters[which] = isChecked;
        });
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Filtros aplicados
            StringBuilder activeFilters = new StringBuilder();
            for (int i = 0; i < filterOptions.length; i++) {
                if (selectedFilters[i]) {
                    if (activeFilters.length() > 0) activeFilters.append(", ");
                    activeFilters.append(filterOptions[i]);
                }
            }
            if (activeFilters.length() > 0) {
                Toast.makeText(this, "Filtros: " + activeFilters.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void updateSearchMode(boolean byIngredient) {
        isSearchingByIngredient = byIngredient;
        int colorActive = ContextCompat.getColor(this, R.color.mainDarkBlue);
        int colorInactive = Color.TRANSPARENT; 
        int textActive = Color.WHITE;
        int textInactive = ContextCompat.getColor(this, R.color.gray100);

        if (byIngredient) {
            btnByIngredient.setBackgroundTintList(ColorStateList.valueOf(colorActive));
            btnByIngredient.setTextColor(textActive);
            btnByTitle.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnByTitle.setTextColor(textInactive);
            editSearch.setHint("Insira os ingredientes");
        } else {
            btnByTitle.setBackgroundTintList(ColorStateList.valueOf(colorActive));
            btnByTitle.setTextColor(textActive);
            btnByIngredient.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnByIngredient.setTextColor(textInactive);
            editSearch.setHint("Pesquisar por título");
        }
    }

    private void performSearch(String query) {
        String diet = "";
        StringBuilder intolerances = new StringBuilder();

        if (selectedFilters[0]) { // Sem Lactose
            if (intolerances.length() > 0) intolerances.append(",");
            intolerances.append("dairy");
        }
        if (selectedFilters[1]) { // Sem Gluten
            if (intolerances.length() > 0) intolerances.append(",");
            intolerances.append("gluten");
        }
        if (selectedFilters[3]) { // Vegetariano
            diet = "vegetarian";
        }
        if (selectedFilters[4]) { // Vegano
            diet = "vegan";
        }
        // "Light" (index 2) não possui mapeamento direto simples no complexSearch sem parâmetros extras de calorias.

        if (isSearchingByIngredient) {
            // Busca por ingredientes (Note: findByIngredients não suporta diet/intolerances diretamente)
            // Para suportar filtros, o ideal seria usar complexSearch com includeIngredients
            spoonacularApi.findByIngredients(query, 10, API_KEY).enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("SearchAPI", "Ingredientes encontrados: " + response.body().size());
                    }
                }

                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    Toast.makeText(Search.this, "Erro na busca", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Busca por título com filtros
            spoonacularApi.searchByTitle(query, diet, intolerances.toString(), 10, API_KEY).enqueue(new Callback<RecipeSearchResponse>() {
                @Override
                public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("SearchAPI", "Títulos encontrados: " + response.body().getResults().size());
                    }
                }

                @Override
                public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
                    Toast.makeText(Search.this, "Erro na busca", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
