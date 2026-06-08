package com.example.cozinhai;

import java.util.List;

public class SeasonalIngredient {
    private String id;
    private String nome;
    private String icone;
    private String categoria;
    private List<Integer> meses;
    private String slug;

    public SeasonalIngredient(String id, String nome, String icone, String categoria, List<Integer> meses, String slug) {
        this.id = id;
        this.nome = nome;
        this.icone = icone;
        this.categoria = categoria;
        this.meses = meses;
        this.slug = slug;
    }

    public String getNome() { return nome; }
    public List<Integer> getMeses() { return meses; }
    public String getCategoria() { return categoria; }
}
