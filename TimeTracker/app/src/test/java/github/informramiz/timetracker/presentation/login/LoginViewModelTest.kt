package github.informramiz.timetracker.presentation.login

import android.content.res.Resources
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import github.informramiz.timetracker.common.MainCoroutineDispatcherRule
import github.informramiz.timetracker.domain.authentication.AuthenticationRepository
import github.informramiz.timetracker.domain.authentication.model.UserRole
import github.informramiz.timetracker.presentation.base.GeneralNavigationDestination
import github.informramiz.timetracker.presentation.base.NotificationState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineDispatcherRule = MainCoroutineDispatcherRule()

    @Mock
    private lateinit var authenticationRepository: AuthenticationRepository

    @Mock
    private lateinit var resources: Resources

    private lateinit var classUnder: LoginViewModel

    @Before
    fun setup() {
        given(resources.getString(any())).willReturn("")
        classUnder = LoginViewModel(authenticationRepository, resources)
    }

    @Test
    fun `Given user is not logged in when onStart is called then updates the state correctly`() =
        runBlockingTest {
            // Given
            val isUserLoggedIn = false
            given(authenticationRepository.isUserLoggedIn()).willReturn(isUserLoggedIn)

            // When
            classUnder.onStart()

            // Then
            assertEquals(isUserLoggedIn, classUnder.viewState.value?.isUserLoggedIn)
        }

    @Test
    fun `Given user is logged in when onStart is called then updates the state correctly`() =
        runBlockingTest {
            // Given
            val isUserLoggedIn = true
            given(authenticationRepository.isUserLoggedIn()).willReturn(isUserLoggedIn)

            // When
            classUnder.onStart()

            // Then
            assertEquals(isUserLoggedIn, classUnder.viewState.value?.isUserLoggedIn)
        }

    @Test
    fun `Given user is not logged in when onBackPressAction called then displays error and finishes activity`() =
        runBlockingTest {
            // Given
            val isUserLoggedIn = false
            given(authenticationRepository.isUserLoggedIn()).willReturn(isUserLoggedIn)

            // When
            classUnder.onStart()
            classUnder.onBackPressAction()

            // Then
            assertEquals(NotificationState.Failure(""), classUnder.notificationState.value)
            assertEquals(
                GeneralNavigationDestination.FinishActivity,
                classUnder.navigationCommands.value
            )
        }


    @Test
    fun `When onLoginButtonClickAction called then emits the right navigation command`() {
        // When
        classUnder.onLoginButtonClickAction()

        // Then
        assertEquals(LoginNavigationDestination.SignIn, classUnder.navigationCommands.value)
    }

    @Test
    fun `When onLoginResultAction is called with null input then emits a failure notification`() {
        // When
        classUnder.onLoginResultAction(null)

        // Then
        assertEquals(NotificationState.Failure(""), classUnder.notificationState.value)
    }

    @Test
    fun `When onLoginResultAction is called with valid input then emits a failure notification`() {
        // When
        classUnder.onLoginResultAction(mock())

        // Then
        assertEquals(GeneralNavigationDestination.Back, classUnder.navigationCommands.value)
    }
}
