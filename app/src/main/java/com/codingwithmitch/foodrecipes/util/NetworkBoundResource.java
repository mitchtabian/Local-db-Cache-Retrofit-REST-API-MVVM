package com.codingwithmitch.foodrecipes.util;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.requests.responses.ApiResponse;

public abstract class NetworkBoundResource<CacheObject, RequestObject> {

    private static final String TAG = "NetworkBoundResource";

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
                    fetchFromNetwork(dbSource);
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
     * 1) observe local db
     * 2) if <condition/> query the network
     * 3) stop observing the local db
     * 4) insert new data into local db
     * 5) begin observing local db again to see refreshed network data
     * @param dbSource
     */
    private void fetchFromNetwork(final LiveData<CacheObject> dbSource){
        Log.d(TAG, "fetchFromNetwork: called.");

        // Update LiveData for loading status
        result.addSource(dbSource, new Observer<CacheObject>() {
            @Override
            public void onChanged(@Nullable CacheObject CacheObject) {
                setValue(Resource.loading(CacheObject));
            }
        });

        final LiveData<ApiResponse<RequestObject>> apiResponse = createCall();

        result.addSource(apiResponse, new Observer<ApiResponse<RequestObject>>() {
            @Override
            public void onChanged(@Nullable final ApiResponse<RequestObject> RequestObjectApiResponse) {
                result.removeSource(dbSource);
                result.removeSource(apiResponse);

                Log.d(TAG, "run: attempting to refresh data from network...");

                /*
                    3 Cases:
                        1) ApiSuccessResponse
                        2) ApiErrorResponse
                        3) ApiEmptyResponse
                */

                if(RequestObjectApiResponse instanceof ApiResponse.ApiSuccessResponse){
                    Log.d(TAG, "run: ApiSuccessResponse");
                    appExecutors.diskIO().execute(new Runnable() {
                        @Override
                        public void run() {

                            // save response to local db
                            saveCallResult((RequestObject) processResponse((ApiResponse.ApiSuccessResponse)RequestObjectApiResponse));


                            // observe local db again since new result from network will have been saved
                            appExecutors.mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    // we specially request a new live data,
                                    // otherwise we will get immediately last cached value,
                                    // which may not be updated with latest results received from network.
                                    // as opposed to use the @dbSource variable passed as input
                                    result.addSource(loadFromDb(), new Observer<CacheObject>() {
                                        @Override
                                        public void onChanged(@Nullable CacheObject CacheObject) {
                                            setValue(Resource.success(CacheObject));
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else if(RequestObjectApiResponse instanceof ApiResponse.ApiEmptyResponse){ // empty result
                    Log.d(TAG, "run: ApiEmptyResponse");
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            result.addSource(loadFromDb(), new Observer<CacheObject>() {
                                @Override
                                public void onChanged(@Nullable CacheObject CacheObject) {
                                    setValue(Resource.success(CacheObject));
                                }
                            });
                        }
                    });
                }
                else if(RequestObjectApiResponse instanceof ApiResponse.ApiErrorResponse){ // error result
                    Log.d(TAG, "run: ApiErrorResponse");
                    result.addSource(dbSource, new Observer<CacheObject>() {
                        @Override
                        public void onChanged(@Nullable CacheObject CacheObject) {
                            setValue(
                                    Resource.error(
                                            ((ApiResponse.ApiErrorResponse)RequestObjectApiResponse).getErrorMessage(),
                                            CacheObject
                                    )
                            );
                        }
                    });
                }
            }
        });
    }


    private CacheObject processResponse(ApiResponse.ApiSuccessResponse response){
        return (CacheObject) response.getBody();
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












