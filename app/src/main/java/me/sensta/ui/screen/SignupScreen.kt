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
import androidx.compose.material3.SnackbarDuration
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
import me.sensta.ui.navigation.common.LocalSnackbar
import me.sensta.ui.screen.signup.SignupCompleted
import me.sensta.ui.screen.signup.SignupInputCode
import me.sensta.ui.screen.signup.SignupInputEmail
import me.sensta.ui.screen.signup.SignupInputName
import me.sensta.ui.screen.signup.SignupInputPassword
import me.sensta.viewmodel.local.LocalAuthViewModel
import me.sensta.viewmodel.state.SignupState
import me.sensta.viewmodel.uievent.AuthUiEvent

@Composable
fun SignupScreen() {
    val context = LocalContext.current
    val imeInsets = WindowInsets.ime
    val density = LocalDensity.current
    val authViewModel = LocalAuthViewModel.current
    val snackbar = LocalSnackbar.current
    val isLoading by authViewModel.isLoading
    val signupState by authViewModel.signupState

    // ViewModel에서 전달된 이벤트에 맞춰 메시지 출력
    LaunchedEffect(Unit) {
        authViewModel.uiAuthEvent.collect { event ->
            when (event) {
                is AuthUiEvent.AccessTokenUpdated -> {
                    Toast.makeText(context, "토큰이 갱신되었습니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.AlreadyUsedID -> {
                    Toast.makeText(context, "이미 사용중인 아이디입니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.AlreadyUsedName -> {
                    Toast.makeText(context, "이미 사용중인 닉네임입니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.AtLeast8Characters -> {
                    Toast.makeText(context, "비밀번호는 8자 이상이어야 합니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.DifferentPassword -> {
                    Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.EnterAllInfo -> {
                    Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.EnterVerificationCode -> {
                    Toast.makeText(context, "인증번호를 입력해주세요", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.ExpiredAccessToken -> {
                    Toast.makeText(context, "토큰이 만료되었습니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.FailedToSignUp -> {
                    Toast.makeText(context, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.InvalidEmailAddress -> {
                    Toast.makeText(context, "올바르지 않은 이메일 형식입니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.InvalidName -> {
                    Toast.makeText(context, "올바르지 않은 닉네임 형식입니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.InvalidTargetUser -> {
                    Toast.makeText(context, "유효하지 않은 사용자입니다", Toast.LENGTH_SHORT).show()
                }

                is AuthUiEvent.RemindPasswordRule -> {
                    snackbar.showSnackbar(
                        "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 적어도 하나씩 포함해야 합니다",
                        "확인",
                        duration = SnackbarDuration.Short
                    )
                }

                is AuthUiEvent.SentVerificationCode -> {
                    snackbar.showSnackbar(
                        "인증번호를 ${event.email}(으)로 전송했습니다. 스팸함도 확인해주세요!",
                        "확인",
                        duration = SnackbarDuration.Short
                    )
                }

                is AuthUiEvent.SignupCompleted -> {
                    snackbar.showSnackbar("회원가입이 완료되었습니다", "확인", duration = SnackbarDuration.Short)
                }

                is AuthUiEvent.WrongVerificationCode -> {
                    Toast.makeText(context, "인증번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
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
                    targetState = signupState,
                    transitionSpec = {
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { width -> width }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { width -> -width }
                        )
                    },
                    label = "ConditionTransition"
                ) { currentSignupState ->
                    when (currentSignupState) {
                        SignupState.InputEmail -> SignupInputEmail()
                        SignupState.InputPassword -> SignupInputPassword()
                        SignupState.InputName -> SignupInputName()
                        SignupState.InputCode -> SignupInputCode()
                        SignupState.SignupCompleted -> SignupCompleted()
                    }
                }
            }
        }
    }
}