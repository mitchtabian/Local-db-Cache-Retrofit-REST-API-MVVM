package com.codingwithmitch.foodrecipes.di;

import com.codingwithmitch.foodrecipes.ui.recipe.RecipeActivity;
import com.codingwithmitch.foodrecipes.ui.recipelist.RecipeListActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract RecipeListActivity contributeRecipeListActivity();

    @ContributesAndroidInjector
    abstract RecipeActivity contributeRecipeActivity();


}
