package com.codingwithmitch.foodrecipes.util;


import com.codingwithmitch.foodrecipes.requests.responses.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.responses.RecipeSearchResponse;

public class CheckRecipeApiKey {

    /**
     * If list of recipes is null the api key is not valid or expired for the day
     * @param response
     * @return
     */
    public static boolean isRecipeApiKeyValid(RecipeSearchResponse response){
        return response.getRecipes() != null;
    }
    public static boolean isRecipeApiKeyValid(RecipeResponse response){
        return response.getRecipe() != null;
    }
}




