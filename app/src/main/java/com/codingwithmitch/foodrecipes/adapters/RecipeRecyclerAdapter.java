package com.codingwithmitch.foodrecipes.adapters;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.codingwithmitch.foodrecipes.util.Constants.PAGINATION_NUMBER;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ListPreloader.PreloadModelProvider<String>
{

    private static final String TAG = "RecipeRecyclerAdapter";

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    private List<Recipe> mRecipes;
    private OnRecipeListener mOnRecipeListener;
    private ViewPreloadSizeProvider<String> mPreloadSizeProvider;
    private RequestManager mRequestManager;

    public RecipeRecyclerAdapter(OnRecipeListener mOnRecipeListener,
                                 ViewPreloadSizeProvider<String> viewPreloadSizeProvider,
                                 RequestManager requestManager) {
        this.mOnRecipeListener = mOnRecipeListener;
        this.mPreloadSizeProvider = viewPreloadSizeProvider;
        this.mRequestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = null;
        switch (i){

            case RECIPE_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, mOnRecipeListener);
            }

            case LOADING_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_loading_list_item, viewGroup, false);
                return new LoadingViewHolder(view);
            }

            case EXHAUSTED_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_search_exhausted, viewGroup, false);
                return new SearchExhaustedViewHolder(view);
            }

            case CATEGORY_TYPE:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_category_list_item, viewGroup, false);
                return new CategoryViewHolder(view, mOnRecipeListener);
            }

            default:{
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_recipe_list_item, viewGroup, false);
                return new RecipeViewHolder(view, mOnRecipeListener);
            }
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int itemViewType = getItemViewType(i);
        if(itemViewType == RECIPE_TYPE){

            mRequestManager
                    .load(mRecipes.get(i).getImage_url())
                    .into(((RecipeViewHolder)viewHolder).image);

            ((RecipeViewHolder)viewHolder).title.setText(mRecipes.get(i).getTitle());
            ((RecipeViewHolder)viewHolder).publisher.setText(mRecipes.get(i).getPublisher());
            ((RecipeViewHolder)viewHolder).socialScore.setText(String.valueOf(Math.round(mRecipes.get(i).getSocial_rank())));
//            ((RecipeViewHolder)viewHolder).socialScore.setText(String.valueOf(i)); // Test the pagination

            mPreloadSizeProvider.setView(((RecipeViewHolder)viewHolder).image);
        }
        else if(itemViewType == CATEGORY_TYPE){

            Uri path = Uri.parse("android.resource://com.codingwithmitch.foodrecipes/drawable/" + mRecipes.get(i).getImage_url());
            mRequestManager
                    .load(path)
                    .into(((CategoryViewHolder)viewHolder).categoryImage);

            ((CategoryViewHolder)viewHolder).categoryTitle.setText(mRecipes.get(i).getTitle());

            mPreloadSizeProvider.setView(((CategoryViewHolder)viewHolder).categoryImage);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(mRecipes.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else if(mRecipes.get(position).getTitle().equals("EXHAUSTED...")) {
            return EXHAUSTED_TYPE;
        }
        else{
            return RECIPE_TYPE;
        }
    }

    public void displayOnlyLoading(){
        clearRecipesList();
        Recipe recipe = new Recipe();
        recipe.setTitle("LOADING...");
        mRecipes.add(recipe);
        notifyDataSetChanged();
    }

    public void displayLoading(){
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        if(!isLoading()){
            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING...");
            mRecipes.add(recipe); // loading at bottom of screen
            notifyDataSetChanged();
        }
    }

    public void setQueryExhausted(){
        hideLoading();
        Recipe exhaustedRecipe = new Recipe();
        exhaustedRecipe.setTitle("EXHAUSTED...");
        mRecipes.add(exhaustedRecipe);
        notifyDataSetChanged();
    }

    public void hideLoading(){
        if(isLoading()) {
            if (mRecipes.get(0).getTitle().equals("LOADING...")) {
                mRecipes.remove(mRecipes.size() - 1);
            }
        }
        if(isLoading()){
            if(mRecipes.get(mRecipes.size() - 1).getTitle().equals("LOADING...")){
                mRecipes.remove(mRecipes.size() - 1);
            }
        }
        notifyDataSetChanged();
    }

    private boolean isLoading(){
        if(mRecipes != null){
            if(mRecipes.size() > 0){
                return mRecipes.get(mRecipes.size() - 1).getTitle().equals("LOADING...")
                        || mRecipes.get(0).getTitle().equals("LOADING...");
            }
        }
        return false;
    }


    public void displaySearchCategories(){
        clearRecipesList();
        List<Recipe> categories = new ArrayList<>();
        for(int i = 0; i< Constants.DEFAULT_SEARCH_CATEGORIES.length; i++){
            Recipe recipe = new Recipe();
            recipe.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            recipe.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            recipe.setSocial_rank(-1);
            categories.add(recipe);
        }
        mRecipes = categories;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(mRecipes != null){
            return mRecipes.size();
        }
        return 0;
    }

    public void setRecipes(List<Recipe> recipes){
        clearRecipesList();
        mRecipes.addAll(recipes);
        notifyDataSetChanged();
    }


    public Recipe getSelectedRecipe(int position){
        if(mRecipes != null){
            if(mRecipes.size() > 0){
                return mRecipes.get(position);
            }
        }
        return null;
    }

    private void clearRecipesList(){
        if(mRecipes == null){
            mRecipes = new ArrayList<>();
        }
        else {
            mRecipes.clear();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
//        Log.d(TAG, "getPreloadItems: called: " + position );
        String url = mRecipes.get(position).getImage_url();
        if (TextUtils.isEmpty(url)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
//        Log.d(TAG, "getPreloadRequestBuilder: called: " + item);
        return mRequestManager.load(item);
    }

}















