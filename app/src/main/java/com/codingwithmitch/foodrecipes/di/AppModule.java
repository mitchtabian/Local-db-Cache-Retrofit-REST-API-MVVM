package com.codingwithmitch.foodrecipes.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.persistence.RecipeDao;
import com.codingwithmitch.foodrecipes.persistence.RecipeDatabase;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.requests.RecipeApi;
import com.codingwithmitch.foodrecipes.util.Constants;
import com.codingwithmitch.foodrecipes.util.LiveDataCallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.codingwithmitch.foodrecipes.persistence.RecipeDatabase.DATABASE_NAME;
import static com.codingwithmitch.foodrecipes.util.Constants.CONNECTION_TIMEOUT;
import static com.codingwithmitch.foodrecipes.util.Constants.READ_TIMEOUT;
import static com.codingwithmitch.foodrecipes.util.Constants.WRITE_TIMEOUT;

@Module
public class AppModule {

    @Singleton
    @Provides
    static OkHttpClient provideOkHttpClient(){
        return new OkHttpClient.Builder()

                // establish connection to server
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)

                // time between each byte read from the server
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)

                // time between each byte sent to server
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

                .retryOnConnectionFailure(false)

                .build();
    }

    @Singleton
    @Provides
    static Retrofit provideRetrofitInstance(OkHttpClient client){
        return new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    static RecipeApi provideRecipeApi(Retrofit retrofit){
        return retrofit.create(RecipeApi.class);
    }

    @Singleton
    @Provides
    static RecipeDatabase provideRecipeDatabase(Application application){
        return Room.databaseBuilder(
                application,
                RecipeDatabase.class,
                DATABASE_NAME
        ).build();
    }


    @Singleton
    @Provides
    static RecipeDao provideRecipeDao(RecipeDatabase database){
        return database.getRecipeDao();
    }

    @Singleton
    @Provides
    static AppExecutors provideAppExecutors(){
        return new AppExecutors();
    }

    @Singleton
    @Provides
    static RecipeRepository provideRecipeRepository(RecipeDao recipeDao, RecipeApi recipeApi, AppExecutors appExecutors){
        return new RecipeRepository(recipeDao, recipeApi, appExecutors);
    }


}



























