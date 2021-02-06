package github.informramiz.timetracker.di

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import github.informramiz.timetracker.data.cloudfunctions.CloudFunctions
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object CloudFunctionsModule {
    @Reusable
    @Provides
    fun providesFirebaseFunctions() = FirebaseFunctions.getInstance()

    @Singleton
    @Provides
    fun providesCloudFunctions(firebaseFunctions: FirebaseFunctions) = CloudFunctions(firebaseFunctions)
}
