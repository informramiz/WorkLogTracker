package github.informramiz.timetracker.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import github.informramiz.timetracker.data.authentication.respository.AuthenticationDataRepository
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository

@Module
@InstallIn(ActivityComponent::class)
object AuthenticationModule {
    @Reusable
    @Provides
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Reusable
    @Provides
    fun providesAuthenticationRepository(authenticationDataRepository: AuthenticationDataRepository): AuthenticationRepository =
        authenticationDataRepository
}
