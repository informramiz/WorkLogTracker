package github.informramiz.timetracker.di

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import github.informramiz.timetracker.data.user.UserManagementDataRepository
import github.informramiz.timetracker.domain.user.UserManagementRepository

@Module
@InstallIn(ActivityComponent::class)
object UserDomainModule {
    @Reusable
    @Provides
    fun providesUserRepository(userDataRepository: UserManagementDataRepository): UserManagementRepository = userDataRepository
}
