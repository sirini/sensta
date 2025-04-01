package me.sensta.viewmodel

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.domain.model.auth.TsboardCheckEmail
import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult
import me.domain.model.auth.emptyUser
import me.domain.repository.TsboardResponse
import me.domain.usecase.auth.CheckEmailUseCase
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.auth.SigninUseCase
import javax.inject.Inject

// 로그인 단계 정의
sealed class LoginState {
    object InputEmail : LoginState()
    object InputPassword : LoginState()
    object LoginCompleted : LoginState()
}

// 아이디 체크 코드 확인
const val ID_INVALID = 3
const val ID_REGISTERED = 5

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkEmailUseCase: CheckEmailUseCase,
    private val signinUseCase: SigninUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private var _id by mutableStateOf<String>("")
    val id: String get() = _id

    private var _isEmailValid by mutableStateOf(true)
    val isEmailValid: Boolean get() = _isEmailValid

    private var _pw by mutableStateOf<String>("")
    val pw: String get() = _pw

    private var _user = MutableStateFlow<TsboardSigninResult>(emptyUser)
    val user: StateFlow<TsboardSigninResult> get() = _user

    private var _loginState by mutableStateOf<LoginState>(LoginState.InputEmail)
    val loginState: LoginState get() = _loginState

    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading


    // 생성 시점에 기존에 로그인했던 정보가 있다면 가져오기
    init {
        _isLoading = true
        viewModelScope.launch {
            getUserInfoUseCase().collect { user ->
                if (user.uid > 0) {
                    _user.value = user
                    _loginState = LoginState.LoginCompleted
                }
                _isLoading = false
            }
        }
    }

    // 아이디(이메일 주소) 입력 받기
    fun setID(id: String) {
        _id = id
        _isEmailValid = Patterns.EMAIL_ADDRESS.matcher(id).matches()
    }

    // 비밀번호 입력 받기
    fun setPW(pw: String) {
        _pw = pw
    }

    // 아이디(이메일)가 존재하는지 확인 후 비밀번호 입력란으로 이동
    fun checkValidID(context: Context) {
        if (_id.isEmpty() || !_isEmailValid) {
            Toast.makeText(context, "올바른 이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        _isLoading = true

        viewModelScope.launch {
            checkEmailUseCase(_id).collect {
                val checkEmailData = (it as TsboardResponse.Success<TsboardCheckEmail>).data

                if (checkEmailData.code == ID_REGISTERED) {
                    _loginState = LoginState.InputPassword

                } else if (checkEmailData.code == ID_INVALID) {
                    Toast.makeText(context, "유효하지 않은 메일 주소입니다", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "등록된 이메일 주소가 아닙니다", Toast.LENGTH_LONG).show()
                }
                _isLoading = false
            }
        }
    }

    // 비밀번호 입력 화면에서 아이디 입력 화면으로 (뒤로)
    fun backToID() {
        _loginState = LoginState.InputEmail
    }

    // 입력된 아이디와 비밀번호가 유효한지 확인 후 (유효할 시) 홈 화면으로 이동
    fun login(context: Context) {
        _isLoading = true

        viewModelScope.launch {
            signinUseCase(_id, _pw).collect {
                val signinData = (it as TsboardResponse.Success<TsboardSignin>).data

                if (null == signinData.result) {
                    Toast.makeText(context, "로그인에 실패했습니다", Toast.LENGTH_LONG).show()

                } else {
                    _user.value = signinData.result!!
                    _loginState = LoginState.LoginCompleted
                }
                _isLoading = false
            }
        }
    }

    // 사용자의 이름 업데이트하기
    fun updateName(name: String) {
        // do something
    }

    // 사용자의 서명 업데이트하기
    fun updateSignature(signature: String) {
        // do something
    }
}