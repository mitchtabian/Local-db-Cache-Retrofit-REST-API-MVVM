package com.codingwithmitch.foodrecipes.util;

import android.arch.lifecycle.LiveData;

import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    private Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(final Call<R> call) {
        return new LiveData<ApiResponse<R>>(){
            @Override
            protected void onActive() {
                super.onActive();
                final ApiResponse apiResponse = new ApiResponse();
                if(!call.isExecuted()){
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(Call<R> call, Response<R> response) {
                            postValue(apiResponse.create(response));
                        }

                        @Override
                        public void onFailure(Call<R> call, Throwable t) {
                            postValue(apiResponse.create(t));
                        }
                    });
                }

            }
        };
    }

}







