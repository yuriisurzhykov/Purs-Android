package com.github.yuriisurzhykov.purs

import android.content.Context
import androidx.room.Room
import com.github.yuriisurzhykov.purs.core.MergeStrategy
import com.github.yuriisurzhykov.purs.core.RequestResponseMergeStrategy
import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.data.cache.GetLocationId
import com.github.yuriisurzhykov.purs.data.cache.LocationCacheDataSource
import com.github.yuriisurzhykov.purs.data.cache.LocationDao
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import com.github.yuriisurzhykov.purs.data.cloud.LocationCloudDataSource
import com.github.yuriisurzhykov.purs.data.cloud.LocationService
import com.github.yuriisurzhykov.purs.data.repository.LocationCloudMapper
import com.github.yuriisurzhykov.purs.data.repository.LocationCloudToCacheMapper
import com.github.yuriisurzhykov.purs.data.repository.LocationRepository
import com.github.yuriisurzhykov.purs.data.repository.LocationWorkingHoursCloudMapper
import com.github.yuriisurzhykov.purs.domain.chain.AddMissingDaysUseCase
import com.github.yuriisurzhykov.purs.domain.chain.MergeCrossDayTimeSlotsUseCase
import com.github.yuriisurzhykov.purs.domain.chain.MergeTimeSlotsUseCase
import com.github.yuriisurzhykov.purs.domain.chain.SortMissingDaysUseCase
import com.github.yuriisurzhykov.purs.domain.chain.WorkingHourChainProcessor
import com.github.yuriisurzhykov.purs.domain.mapper.LocationCacheToDomainMapper
import com.github.yuriisurzhykov.purs.domain.mapper.StringToLocalTimeMapper
import com.github.yuriisurzhykov.purs.domain.mapper.WorkingHourCacheToDomainMapper
import com.github.yuriisurzhykov.purs.domain.usecase.BuildWorkingHoursListUseCase
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PursAppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): PursDatabase = Room.databaseBuilder(context, PursDatabase::class.java, "purs.db").build()

    @Provides
    @Singleton
    fun provideLocationsDao(database: PursDatabase): LocationDao = database.locationDao()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .baseUrl("https://purs-demo-bucket-test.s3.us-west-2.amazonaws.com")
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory =
        Json.asConverterFactory("application/json; charset=UTF8".toMediaType())


    @Provides
    @Singleton
    fun provideBuildWorkingHoursListUseCase(
        repository: LocationRepository,
        mapper: LocationCacheToDomainMapper,
        mergeTimeSlotsUseCase: MergeTimeSlotsUseCase,
        processor: WorkingHourChainProcessor
    ): BuildWorkingHoursListUseCase = BuildWorkingHoursListUseCase.Base(
        repository,
        mapper,
        mergeTimeSlotsUseCase,
        processor
    )

    @Provides
    @Singleton
    fun provideWorkingHourChainProcessor(
        mergeCrossDayTimeSlots: MergeCrossDayTimeSlotsUseCase,
        addMissingDaysUseCase: AddMissingDaysUseCase,
        sortMissingDaysUseCase: SortMissingDaysUseCase
    ): WorkingHourChainProcessor = WorkingHourChainProcessor.Base(
        mergeCrossDayTimeSlots,
        addMissingDaysUseCase,
        sortMissingDaysUseCase
    )

    @Provides
    @Singleton
    fun provideMergeTimeSlotsUseCase(): MergeTimeSlotsUseCase = MergeTimeSlotsUseCase.Base()

    @Provides
    @Singleton
    fun provideAddMissingDaysUseCase(): AddMissingDaysUseCase = AddMissingDaysUseCase.Base()

    @Provides
    @Singleton
    fun provideMergeCrossDayTimeSlotsUseCase(sortMissingDaysUseCase: SortMissingDaysUseCase): MergeCrossDayTimeSlotsUseCase =
        MergeCrossDayTimeSlotsUseCase.Base(sortMissingDaysUseCase)

    @Provides
    @Singleton
    fun provideSortMissingDaysUseCase(): SortMissingDaysUseCase = SortMissingDaysUseCase.Base()

    @Provides
    @Singleton
    fun provideLocationCacheToDomainMapper(mapper: WorkingHourCacheToDomainMapper): LocationCacheToDomainMapper =
        LocationCacheToDomainMapper.Base(mapper)

    @Provides
    @Singleton
    fun provideWorkingHourCacheToDomainMapper(mapper: StringToLocalTimeMapper): WorkingHourCacheToDomainMapper =
        WorkingHourCacheToDomainMapper.Base(mapper)

    @Provides
    @Singleton
    fun provideStringToLocalTimeMapper(): StringToLocalTimeMapper = StringToLocalTimeMapper.Base()

    @Provides
    @Singleton
    fun provideLocationRepository(
        cloudDataSource: LocationCloudDataSource,
        cacheDataSource: LocationCacheDataSource,
        sourceMergeStrategy: MergeStrategy<RequestResult<LocationWithWorkingHours>>,
        mapper: LocationCloudToCacheMapper
    ): LocationRepository = LocationRepository.Base(
        cloudDataSource,
        cacheDataSource,
        sourceMergeStrategy,
        mapper
    )

    @Provides
    @Singleton
    fun provideLocationCloudToCacheMapper(
        cloudMapper: LocationCloudMapper,
        workingHoursCloudMapper: LocationWorkingHoursCloudMapper
    ): LocationCloudToCacheMapper =
        LocationCloudToCacheMapper.Base(cloudMapper, workingHoursCloudMapper)

    @Provides
    @Singleton
    fun provideLocationCloudDataSource(locationService: LocationService): LocationCloudDataSource =
        LocationCloudDataSource.Base(locationService)

    @Provides
    @Singleton
    fun provideLocationCacheDataSource(
        locationDao: LocationDao,
        getLocationId: GetLocationId
    ): LocationCacheDataSource = LocationCacheDataSource.Base(locationDao, getLocationId)

    @Provides
    @Singleton
    fun provideGetLocationId(): GetLocationId = GetLocationId.Const(1)

    @Provides
    @Singleton
    fun provideMergeStrategy(): MergeStrategy<RequestResult<LocationWithWorkingHours>> =
        RequestResponseMergeStrategy()

    @Provides
    @Singleton
    fun provideLocationService(retrofit: Retrofit): LocationService =
        retrofit.create(LocationService::class.java)
}