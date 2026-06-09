package com.example.cozinhai;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpoonacularApi {
    // Busca detalhes da receita por ID
    @GET("recipes/{id}/information")
    Call<RecipeDetailResponse> getRecipeInformation(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );

    // Busca por ingredientes
    @GET("recipes/findByIngredients")
    Call<List<Recipe>> findByIngredients(
            @Query("ingredients") String ingredients,
            @Query("number") int number,
            @Query("apiKey") String apiKey
    );

    // Busca por título (Complex Search) com filtros
    @GET("recipes/complexSearch")
    Call<RecipeSearchResponse> searchByTitle(
            @Query("query") String query,
            @Query("diet") String diet,
            @Query("intolerances") String intolerances,
            @Query("number") int number,
            @Query("apiKey") String apiKey
    );

    // Busca receitas aleatórias
    @GET("recipes/random")
    Call<RandomRecipesResponse> getRandomRecipes(
            @Query("number") int number,
            @Query("apiKey") String apiKey
    );
}
