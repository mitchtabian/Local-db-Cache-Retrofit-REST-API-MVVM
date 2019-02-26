package com.codingwithmitch.foodrecipes.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;


import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

// CacheObject: Type for the Resource data. (database cache)
// RequestObject: Type for the API response. (network request)
public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private MediatorLiveData<Resource<CacheObject>> results = new MediatorLiveData<>();

    // Called to save the result of the API response into the database.
    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestObject item);

    // Called with the data in the database to decide whether to fetch
    // potentially updated data from the network.
    @MainThread
    protected abstract boolean shouldFetch(@Nullable CacheObject data);

    // Called to get the cached data from the database.
    @NonNull @MainThread
    protected abstract LiveData<CacheObject> loadFromDb();

    // Called to create the API call.
    @NonNull @MainThread
    protected abstract LiveData<ApiResponse<RequestObject>> createCall();

    // Returns a LiveData object that represents the resource that's implemented
    // in the base class.
    public final LiveData<Resource<CacheObject>> getAsLiveData(){
        return results;
    };
}




