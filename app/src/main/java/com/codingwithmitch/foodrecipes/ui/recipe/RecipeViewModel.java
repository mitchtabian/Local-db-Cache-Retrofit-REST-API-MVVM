package com.codingwithmitch.foodrecipes.ui.recipe;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

import javax.inject.Inject;


public class RecipeViewModel extends ViewModel {

    private final RecipeRepository recipeRepository;

    @Inject
    public RecipeViewModel(RecipeRepository repository) {
        this.recipeRepository = repository;
    }

    public LiveData<Resource<Recipe>> searchRecipeApi(String recipeId){
        return recipeRepository.searchRecipesApi(recipeId);
    }
}





















