package com.example.smartwaste_admin.domain.di

import com.example.smartwaste_admin.data.models.ExtraServiceModel
import com.example.smartwaste_admin.data.repoimpl.arearepositryimpl.AreaRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.extraservicerepoimpl.ExtraServiceRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.holidayrepositryimpl.HolidayRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.reportrepositryimpl.ReportRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.residencerepoimpl.ResidentRepositoryImpl
import com.example.smartwaste_admin.data.repoimpl.routeprogressrepoimpl.RouteProgressRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.routerepoimpl.RoutesRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.truckrepoimpl.TruckRepositryImpl
import com.example.smartwaste_admin.data.repoimpl.workerrepoimpl.WorkersRepositryImpl
import com.example.smartwaste_admin.domain.repo.arearepo.AreaRepositry
import com.example.smartwaste_admin.domain.repo.extraservicerepo.ExtraServiceRepositry
import com.example.smartwaste_admin.domain.repo.holidayrepo.HolidayRepositry
import com.example.smartwaste_admin.domain.repo.reportrepo.ReportRepositry
import com.example.smartwaste_admin.domain.repo.residentrepo.ResidentRepository
import com.example.smartwaste_admin.domain.repo.routeprogressrepositry.RouteProgressRepositry
import com.example.smartwaste_admin.domain.repo.routesrepo.RoutesRepositry
import com.example.smartwaste_admin.domain.repo.truckRepo.TrucksRepositry
import com.example.smartwaste_admin.domain.repo.workerrepo.WorkersRepositry
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

    @Singleton
    @Binds
    abstract fun bindUseCase4(RepositryImpl: WorkersRepositryImpl): WorkersRepositry

    @Singleton
    @Binds
    abstract fun bindUseCase5(RepositryImpl: AreaRepositryImpl): AreaRepositry

    @Singleton
    @Binds
    abstract fun bindsUseCase5(repositryimpl : HolidayRepositryImpl) : HolidayRepositry


    @Singleton
    @Binds
    abstract fun bindUseCase6(RepositryImpl: ReportRepositryImpl): ReportRepositry

    @Singleton
    @Binds
    abstract fun bindUseCase7(RepositryImpl: RouteProgressRepositryImpl): RouteProgressRepositry

    @Singleton
    @Binds
    abstract fun bindUseCase8(RepositryImpl: ExtraServiceRepositryImpl): ExtraServiceRepositry




}