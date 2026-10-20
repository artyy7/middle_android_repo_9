package ru.yandex.loginapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Заменяем основной диспатчер корутин на тестовый
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel()
    }

    @After
    fun tearDown() {
        // Восстанавливаем основной диспатчер после тестов
        Dispatchers.resetMain()
    }

    @Test
    fun `login with empty fields sets EmptyFieldsError`() = runTest {
        viewModel.login(email = "", password = "")

        val currentState = viewModel.state.value
        assertEquals(LoginScreenState.EmptyFieldsError, currentState)
    }

    @Test
    fun `login with invalid email sets EmailValidationError`() = runTest {
        viewModel.login(email = "wrongEmailru", password = "123")

        val currentState = viewModel.state.value
        assertEquals(LoginScreenState.EmailValidationError, currentState)
    }

    @Test
    fun `login with valid data sets Loading`() = runTest {
        viewModel.login(email = "coolEmail@yandex.ru", password = "1234")
        testDispatcher.scheduler.runCurrent()

        val currentState = viewModel.state.value
        assertEquals(LoginScreenState.Loading, currentState)
    }

    @Test
    fun `login with valid data sets Loading then Success`() = runTest {
        viewModel.login(email = "coolEmail@yandex.ru", password = "1234")
        testDispatcher.scheduler.runCurrent()
        assertEquals(LoginScreenState.Loading, viewModel.state.value)

        testDispatcher.scheduler.advanceTimeBy(3000)
        testDispatcher.scheduler.runCurrent()

        assertEquals(LoginScreenState.Success, viewModel.state.value)
    }
}