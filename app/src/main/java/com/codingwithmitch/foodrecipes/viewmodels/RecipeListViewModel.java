package com.codingwithmitch.foodrecipes.viewmodels;


import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;


public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public enum ViewState {CATEGORIES, RECIPES}

    private RecipeRepository mRecipeRepository;
    private MutableLiveData<ViewState> mViewState;
    private MediatorLiveData<Resource<List<Recipe>>> mRecipes = new MediatorLiveData<>();
    private MutableLiveData<Boolean> mIsQueryExhausted = new MutableLiveData<>();
    private boolean mCancelRequest;
    private String mQuery;
    private int mPageNumber;
    private boolean mIsPerformingQuery;



    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRecipeRepository = RecipeRepository.getInstance(application);

        init();
    }

    private void init(){
        if(mViewState == null){
            mViewState = new MutableLiveData<>();
            mViewState.setValue(ViewState.CATEGORIES);
        }
    }

    public LiveData<Resource<List<Recipe>>> getRecipes(){
        return mRecipes;
    }

    public LiveData<ViewState> getViewState(){
        return mViewState;
    }

    public LiveData<Boolean> isQueryExhausted(){
        return mIsQueryExhausted;
    }

    public void setViewCategories(){
        mViewState.setValue(ViewState.CATEGORIES);
    }

    public int getPageNumber(){
        return mPageNumber;
    }

    public void searchRecipesApi(String query, int pageNumber){
        if(!mIsPerformingQuery){
            if(pageNumber == 0){
                pageNumber = 1;
            }
            mPageNumber = pageNumber;
            mQuery = query;
            mIsQueryExhausted.setValue(false);
            executeSearch();
        }
    }


    public void searchNextPage(){
        if(!mIsQueryExhausted.getValue() && !mIsPerformingQuery){
            mPageNumber++;
            executeSearch();
        }
    }

    private void executeSearch(){
        mCancelRequest = false;
        mIsPerformingQuery = true;
        mViewState.setValue(ViewState.RECIPES);
        final LiveData<Resource<List<Recipe>>> repositorySource = mRecipeRepository.searchRecipesApi(mQuery, mPageNumber);
        mRecipes.addSource(repositorySource, new Observer<Resource<List<Recipe>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<Recipe>> listResource) {
                if(!mCancelRequest){
                    if(listResource != null){
                        mRecipes.setValue(listResource);
                        if(listResource.status == Resource.Status.SUCCESS ){
                            mIsPerformingQuery = false;
                            if(listResource.data != null) {
                                if (listResource.data.size() == 0) {
                                    Log.d(TAG, "onChanged: query is EXHAUSTED...");
                                    mIsQueryExhausted.setValue(true);
                                }
                            }
                            // must remove or it will keep listening to repository
                            mRecipes.removeSource(repositorySource);
                        }
                        else if(listResource.status == Resource.Status.ERROR ){
                            mIsPerformingQuery = false;
                            mRecipes.removeSource(repositorySource);
                        }
                    }
                }
                else{
                    mRecipes.removeSource(repositorySource);
                }
            }
        });
    }

    public void cancelSearchRequest(){
        if(mIsPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: canceling the search request.");
            mCancelRequest = true;
            mIsPerformingQuery = false;
            mPageNumber = 1;
        }
    }

}















