package com.example.smartwaste_admin.domain.di

import com.example.smartwaste_admin.data.repoimpl.residencerepoimpl.ResidentRepositoryImpl
import com.example.smartwaste_admin.data.repoimpl.routerepoimpl.RoutesRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.truckrepoimpl.TruckRepositryImpl
import com.example.smartwaste_admin.domain.repo.residentrepo.ResidentRepository
import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import com.example.smartwaste_admin.domain.repo.truckRepo.TrucksRepositry
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule{

    @Singleton
    @Binds
    abstract fun bindUseCase(RepositryImpl: RoutesRepositryImpl): RoutesRepositry

    @Singleton
    @Binds
    abstract fun bindUseCase2(RepositryImpl: ResidentRepositoryImpl): ResidentRepository

    @Singleton
    @Binds
    abstract fun bindUseCase3(RepositryImpl: TruckRepositryImpl): TrucksRepositry




}