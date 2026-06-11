package com.example.cozinhai;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ImageButton btnAddIcon;
    private LinearLayout layoutIngredientChips;
    private View scrollIngredients;
    private TextView txtStatus;
    private RecyclerView recyclerSearchResults;
    private RecipeAdapter adapter;
    private boolean isSearchingByIngredient = true;
    private List<String> ingredientList = new ArrayList<>();

    private final String[] filterOptions = {"Sem Lactose", "Sem Gluten", "Light", "Vegetariano", "Vegano"};
    private boolean[] selectedFilters = {false, false, false, false, false};
    
    // Substitua pela sua chave da API Spoonacular
    private static final String API_KEY = "3b8b03322d804d3496d6eee0aef27ead";
    private SpoonacularApi spoonacularApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        // Inicializa o Retrofit via NetworkClient
        spoonacularApi = NetworkClient.getSpoonacularApi();

        btnByIngredient = findViewById(R.id.btnByIngredient);
        btnByTitle = findViewById(R.id.btnByTitle);
        btnSearchAction = findViewById(R.id.btnSearchAction);
        btnFilterIcon = findViewById(R.id.btnFilterIcon);
        btnAddIcon = findViewById(R.id.btnAddIcon);
        layoutIngredientChips = findViewById(R.id.layoutIngredientChips);
        scrollIngredients = findViewById(R.id.scrollIngredients);
        editSearch = findViewById(R.id.editSearch);
        txtStatus = findViewById(R.id.txtStatus);
        recyclerSearchResults = findViewById(R.id.recyclerSearchResults);

        setupRecyclerView();

        btnByIngredient.setOnClickListener(v -> {
            updateSearchMode(true);
            clearIngredients();
        });
        btnByTitle.setOnClickListener(v -> {
            updateSearchMode(false);
            clearIngredients();
        });

        btnFilterIcon.setOnClickListener(v -> showFilterDialog());

        btnAddIcon.setOnClickListener(v -> {
            String ingredient = editSearch.getText().toString().trim();
            if (!ingredient.isEmpty()) {
                addIngredientChip(ingredient);
                editSearch.setText("");
            }
        });

        btnSearchAction.setOnClickListener(v -> {
            String query = editSearch.getText().toString().trim();
            if (isSearchingByIngredient) {
                if (!query.isEmpty() && !ingredientList.contains(query)) {
                    addIngredientChip(query);
                }
                if (ingredientList.isEmpty()) {
                    Toast.makeText(this, "Adicione pelo menos um ingrediente", Toast.LENGTH_SHORT).show();
                    return;
                }
                performSearch(""); 
            } else {
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    Toast.makeText(this, "Digite o título da receita", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateSearchMode(true);
        setupBottomNavigation();
    }

    private void addIngredientChip(String ingredient) {
        if (ingredientList.contains(ingredient)) return;

        ingredientList.add(ingredient);
        scrollIngredients.setVisibility(View.VISIBLE);

        View chipView = getLayoutInflater().inflate(R.layout.item_ingredient_chip, layoutIngredientChips, false);
        TextView txtName = chipView.findViewById(R.id.txtIngredientName);
        ImageButton btnRemove = chipView.findViewById(R.id.btnRemoveIngredient);

        txtName.setText(ingredient);
        btnRemove.setOnClickListener(v -> {
            layoutIngredientChips.removeView(chipView);
            ingredientList.remove(ingredient);
            if (ingredientList.isEmpty()) {
                scrollIngredients.setVisibility(View.GONE);
            }
        });

        layoutIngredientChips.addView(chipView);
    }

    private void clearIngredients() {
        ingredientList.clear();
        layoutIngredientChips.removeAllViews();
        scrollIngredients.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new RecipeAdapter(recipe -> {
            Intent intent = new Intent(Search.this, RecipeDetailActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            startActivity(intent);
        });
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerSearchResults.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        findViewById(R.id.btnNavHome).setOnClickListener(v -> {
            startActivity(new Intent(this, Home.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.btnNavMap).setOnClickListener(v -> {
            startActivity(new Intent(this, MapActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
        findViewById(R.id.btnNavProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
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
            btnAddIcon.setVisibility(View.VISIBLE);
        } else {
            btnByTitle.setBackgroundTintList(ColorStateList.valueOf(colorActive));
            btnByTitle.setTextColor(textActive);
            btnByIngredient.setBackgroundTintList(ColorStateList.valueOf(colorInactive));
            btnByIngredient.setTextColor(textInactive);
            editSearch.setHint("Pesquisar por título");
            btnAddIcon.setVisibility(View.GONE);
        }
    }

    private void performSearch(String query) {
        String diet = "";
        StringBuilder intolerances = new StringBuilder();
        String includeIngredients = "";

        // Mapeamento dos filtros selecionados
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

        if (isSearchingByIngredient) {
            // Concatena a lista de ingredientes para a API
            includeIngredients = String.join(",", ingredientList);
            query = ""; // No modo ingrediente, o complexSearch foca nos ingredientes incluídos
        }

        spoonacularApi.complexSearch(query, includeIngredients, diet, intolerances.toString(), 10, API_KEY)
                .enqueue(new Callback<RecipeSearchResponse>() {
            @Override
            public void onResponse(Call<RecipeSearchResponse> call, Response<RecipeSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body().getResults());
                } else {
                    updateUI(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<RecipeSearchResponse> call, Throwable t) {
                Log.e("SEARCH", "Falha na busca", t);
                Toast.makeText(Search.this, "Erro na conexão", Toast.LENGTH_SHORT).show();
                updateUI(new ArrayList<>());
            }
        });
    }

    private void updateUI(List<Recipe> recipes) {
        if (recipes.isEmpty()) {
            txtStatus.setVisibility(View.VISIBLE);
            recyclerSearchResults.setVisibility(View.GONE);
        } else {
            txtStatus.setVisibility(View.GONE);
            recyclerSearchResults.setVisibility(View.VISIBLE);
            adapter.setRecipes(recipes);
        }
    }
}
