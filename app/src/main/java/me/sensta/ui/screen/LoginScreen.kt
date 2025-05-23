package me.sensta.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import me.sensta.ui.screen.login.LoginCompleted
import me.sensta.ui.screen.login.LoginInputEmail
import me.sensta.ui.screen.login.LoginInputPassword
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.state.LoginState
import me.sensta.viewmodel.uievent.LoginUiEvent

@Composable
fun LoginScreen() {
    val imeInsets = WindowInsets.ime
    val context = LocalContext.current
    val density = LocalDensity.current
    val authViewModel = LocalAuthViewModel.current
    val isLoading by authViewModel.isLoading
    val loginState by authViewModel.loginState

    LaunchedEffect(Unit) {
        // AuthViewModel에서 전달된 이벤트들 중 로그인과 연관된 내용들 출력하기
        authViewModel.uiLoginEvent.collect { event ->
            when (event) {
                is LoginUiEvent.IDNotFound -> {
                    Toast.makeText(context, "아이디를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                }

                is LoginUiEvent.FailedToLogin -> {
                    Toast.makeText(context, "로그인에 실패했습니다 (${event.message})", Toast.LENGTH_SHORT)
                        .show()
                }

                is LoginUiEvent.FailedToLoginByGoogle -> {
                    Toast.makeText(
                        context,
                        "구글 계정으로 로그인에 실패했습니다 (${event.message})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // 가상 키보드 높이 절반 계산
    val halfKeyboardPadding by remember {
        derivedStateOf {
            with(density) {
                (imeInsets.getBottom(this) / 2).toDp()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = halfKeyboardPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Column {
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                    )
                }

                AnimatedContent(
                    targetState = loginState,
                    transitionSpec = {
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { width -> width }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { width -> -width }
                        )
                    },
                    label = "LoginTransition"
                ) { currentLoginState ->
                    when (currentLoginState) {
                        LoginState.InputEmail -> LoginInputEmail()
                        LoginState.InputPassword -> LoginInputPassword()
                        LoginState.LoginCompleted -> LoginCompleted()
                    }
                }
            }
        }
    }
}