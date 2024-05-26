package com.github.yuriisurzhykov.purs.data.cloud

import com.github.yuriisurzhykov.purs.data.cloud.model.LocationCloud
import retrofit2.Response
import retrofit2.http.GET

interface LocationService {

    // https://purs-demo-bucket-test.s3.us-west-2.amazonaws.com/location.json
    @GET("/location.json")
    suspend fun fetchLocation(): Response<LocationCloud.Base>
}