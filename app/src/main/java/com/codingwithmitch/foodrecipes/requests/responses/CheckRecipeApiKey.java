package com.codingwithmitch.foodrecipes.requests.responses;


public class CheckRecipeApiKey {

    /**
     * If list of recipes is null the api key is not valid or expired for the day
     * @param response
     * @return
     */
    protected static boolean isRecipeApiKeyValid(RecipeSearchResponse response){
        return response.getError() == null;
    }
    protected static boolean isRecipeApiKeyValid(RecipeResponse response){
        return response.getError() == null;
    }
}




