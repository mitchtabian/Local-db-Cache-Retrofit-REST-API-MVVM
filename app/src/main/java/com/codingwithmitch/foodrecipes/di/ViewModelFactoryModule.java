package com.codingwithmitch.foodrecipes.di;


import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.codingwithmitch.foodrecipes.ui.recipe.RecipeViewModel;
import com.codingwithmitch.foodrecipes.ui.recipelist.RecipeListViewModel;
import com.codingwithmitch.foodrecipes.viewmodels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;


@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelFactory);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeListViewModel.class)
    public abstract ViewModel bindRecipeListViewModel(RecipeListViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeViewModel.class)
    public abstract ViewModel bindRecipeViewModel(RecipeViewModel viewModel);
}
