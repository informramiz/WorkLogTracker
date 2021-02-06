package github.informramiz.timetracker.di

import android.content.Context
import android.content.res.Resources
import android.os.Environment
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import github.informramiz.timetracker.di.qualifiers.ExternalFilesDirectoryPath

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Reusable
    @Provides
    fun providesResources(@ApplicationContext context: Context): Resources = context.resources

    @Reusable
    @Provides
    @ExternalFilesDirectoryPath
    fun providesExternalFilesDirectory(@ApplicationContext context: Context): String = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.absolutePath
}
