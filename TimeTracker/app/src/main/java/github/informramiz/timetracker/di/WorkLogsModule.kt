package github.informramiz.timetracker.di

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import github.informramiz.timetracker.data.worklogs.AdminWorkLogsDataRepository
import github.informramiz.timetracker.data.worklogs.WorkLogsDataRepository
import github.informramiz.timetracker.domain.worklogs.WorkLogsRepository
import github.informramiz.timetracker.domain.worklogs.model.AdminWorkLogsRepository

@Module
@InstallIn(ActivityComponent::class)
object WorkLogsModule {
    @Reusable
    @Provides
    fun providesWorkLogsRepository(workLogsDataRepository: WorkLogsDataRepository): WorkLogsRepository = workLogsDataRepository

    @Reusable
    @Provides
    fun providesAdminWorkLogsRepository(adminWorkLogsDataRepository: AdminWorkLogsDataRepository): AdminWorkLogsRepository = adminWorkLogsDataRepository
}
