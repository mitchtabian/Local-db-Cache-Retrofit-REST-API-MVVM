package com.codingwithmitch.foodrecipes.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private AppExecutors appExecutors;
    private MediatorLiveData<Resource<CacheObject>> result = new MediatorLiveData<>();

    public NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        init();
    }

    private void init(){
        // update LiveData for loading status
        result.setValue((Resource<CacheObject>) Resource.loading(null));

        // Observe LiveData source from local db
        final LiveData<CacheObject> dbSource = loadFromDb();

        result.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject CacheObject) {

                // Remove observer from local db. Need to decide if read local db or network
                result.removeSource(dbSource);

                // get data from network if conditions in shouldFetch(boolean) are true
                if(shouldFetch(CacheObject)){
                    // get data from network
                }
                else{ // Otherwise read data from local db
                    result.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject CacheObject) {
                            // Null and empty is handled in ApiResponse class
                            setValue(Resource.success(CacheObject));

                        }
                    });
                }
            }
        });
    }

    /**
     * Setting new value to LiveData
     * Must be done on MainThread
     * @param newValue
     */
    private void setValue(Resource<CacheObject> newValue) {
        if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }

    // Called to save the result of the API response into the database.
    @WorkerThread
    public abstract void saveCallResult(@NonNull RequestObject item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    public abstract boolean shouldFetch(@Nullable CacheObject data);

    // Called to get the cached data from the database.
    @NonNull @MainThread
    public abstract LiveData<CacheObject> loadFromDb();

    // Called to create the API call.
    @NonNull
    @MainThread
    public abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return result;
    };


}












