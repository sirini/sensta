package me.sensta.viewmodel

import android.content.Context
import android.credentials.GetCredentialException
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.data.util.Upload
import me.domain.model.auth.TsboardCheckEmail
import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult
import me.domain.model.auth.TsboardUpdateAccessToken
import me.domain.model.auth.TsboardUpdateUserInfo
import me.domain.model.auth.TsboardUpdateUserInfoParam
import me.domain.model.auth.emptyUser
import me.domain.repository.TsboardResponse
import me.domain.usecase.auth.CheckEmailUseCase
import me.domain.usecase.auth.ClearUserInfoUseCase
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.auth.SaveUserInfoUseCase
import me.domain.usecase.auth.SignInUseCase
import me.domain.usecase.auth.SignInWithGoogleUseCase
import me.domain.usecase.auth.UpdateAccessTokenUseCase
import me.domain.usecase.auth.UpdateUserInfoUseCase
import me.sensta.R
import me.sensta.util.CustomTime
import me.sensta.util.now
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
    private val clearUserInfoUseCase: ClearUserInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val updateAccessTokenUseCase: UpdateAccessTokenUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase
) : ViewModel() {
    private val _id = mutableStateOf("")
    val id: State<String> get() = _id

    private val _isEmailValid = mutableStateOf(true)
    val isEmailValid: State<Boolean> get() = _isEmailValid

    private val _pw = mutableStateOf("")
    val pw: State<String> get() = _pw

    private val _user = MutableStateFlow(emptyUser)
    val user: StateFlow<TsboardSigninResult> = _user.asStateFlow()

    private val _loginState = mutableStateOf<LoginState>(LoginState.InputEmail)
    val loginState: State<LoginState> get() = _loginState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    // 생성 시점에 기존에 로그인했던 정보가 있다면 가져오기
    init {
        _isLoading.value = true
        viewModelScope.launch {
            getUserInfoUseCase().collect { user ->
                if (user.uid > 0) {
                    _user.value = user
                }
                _loginState.value = LoginState.InputEmail
                _isLoading.value = false
            }
        }
    }

    // 비밀번호 입력 화면에서 아이디 입력 화면으로 (뒤로)
    fun backToID() {
        _loginState.value = LoginState.InputEmail
    }

    // 아이디(이메일)가 존재하는지 확인 후 비밀번호 입력란으로 이동
    fun checkValidID(context: Context) {
        if (_id.value.isEmpty() || !_isEmailValid.value) {
            Toast.makeText(context, "올바른 이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        _isLoading.value = true

        viewModelScope.launch {
            checkEmailUseCase(_id.value).collect {
                val checkEmailData = (it as TsboardResponse.Success<TsboardCheckEmail>).data

                if (checkEmailData.code == ID_REGISTERED) {
                    _loginState.value = LoginState.InputPassword
                } else if (checkEmailData.code == ID_INVALID) {
                    Toast.makeText(context, "유효하지 않은 메일 주소입니다", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "등록된 이메일 주소가 아닙니다", Toast.LENGTH_LONG).show()
                }
                _isLoading.value = false
            }
        }
    }

    // 입력된 아이디와 비밀번호가 유효한지 확인 후 (유효할 시) 홈 화면으로 이동
    fun login(context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            signInUseCase(_id.value, _pw.value).collect {
                val signInData = (it as TsboardResponse.Success<TsboardSignin>).data

                if (null == signInData.result) {
                    Toast.makeText(context, "로그인에 실패했습니다", Toast.LENGTH_LONG).show()
                } else {
                    _user.value = signInData.result!!
                    _loginState.value = LoginState.LoginCompleted
                }
                _isLoading.value = false
            }
        }
    }

    // 로그아웃하기
    fun logout() {
        _loginState.value = LoginState.InputEmail
        _user.value = emptyUser
        viewModelScope.launch {
            clearUserInfoUseCase()
        }
    }

    // 로그인 세션 갱신
    fun refresh(context: Context? = null) {
        viewModelScope.launch {
            updateAccessToken(context)
        }
    }

    // 아이디(이메일 주소) 입력 받기
    fun setID(id: String) {
        _id.value = id
        _isEmailValid.value = Patterns.EMAIL_ADDRESS.matcher(id).matches()
    }

    // 비밀번호 입력 받기
    fun setPW(pw: String) {
        _pw.value = pw
    }

    // 구글 계정으로 로그인하기
    fun signInWithGoogle(context: Context) {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.google_web_client_id))
            .setAutoSelectEnabled(true)
            .build()
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                when (val credential = result.credential) {
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken

                            signInWithGoogleUseCase(idToken).collect {
                                val signInData = (it as TsboardResponse.Success<TsboardSignin>).data

                                if (null == signInData.result) {
                                    Toast.makeText(context, "로그인에 실패했습니다", Toast.LENGTH_LONG).show()
                                } else {
                                    _user.value = signInData.result!!
                                    _loginState.value = LoginState.LoginCompleted
                                }

                            }
                        }
                    }
                }
                _isLoading.value = false

            } catch (e: GetCredentialException) {
                Toast.makeText(
                    context, "구글 계정으로 로그인을 하지 못했습니다: ${e.message}", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // 사용자의 이름 업데이트하기
    fun updateName(name: String, context: Context) {
        if (name.length < 2) {
            Toast.makeText(context, "이름은 2자 이상이어야 합니다", Toast.LENGTH_SHORT).show()
            return
        }
        _isLoading.value = true

        viewModelScope.launch {
            updateAccessToken(context)
            val param = TsboardUpdateUserInfoParam(
                authorization = _user.value.token,
                name = name,
                signature = _user.value.signature,
                password = "",
                profile = null
            )

            updateUserInfoUseCase(param).collect {
                val data = (it as TsboardResponse.Success<TsboardUpdateUserInfo>).data
                if (data.success) {
                    _user.value = _user.value.copy(name = name)
                    saveUserInfoUseCase(_user.value)
                    Toast.makeText(context, "이름이 변경되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "이름 변경에 실패했습니다 (${data.error})", Toast.LENGTH_SHORT)
                        .show()
                }
                _isLoading.value = false
            }
        }
    }

    // 사용자의 서명 업데이트하기
    fun updateSignature(signature: String, context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            updateAccessToken(context)
            val param = TsboardUpdateUserInfoParam(
                authorization = _user.value.token,
                name = _user.value.name,
                signature = signature,
                password = "",
                profile = null
            )

            updateUserInfoUseCase(param).collect {
                val data = (it as TsboardResponse.Success<TsboardUpdateUserInfo>).data
                if (data.success) {
                    _user.value = _user.value.copy(signature = signature)
                    saveUserInfoUseCase(_user.value)
                    Toast.makeText(context, "서명이 변경되었습니다", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "서명 변경에 실패했습니다 (${data.error})", Toast.LENGTH_LONG)
                        .show()
                }
                _isLoading.value = false
            }
        }
    }

    // 사용자의 리프레시 토큰으로 새 액세스 토큰 발급받기
    private suspend fun updateAccessToken(context: Context? = null) {
        if (_user.value.uid < 1) return

        updateAccessTokenUseCase(_user.value.uid, _user.value.refresh).collect {
            val token = (it as TsboardResponse.Success<TsboardUpdateAccessToken>).data
            if (null != token.result) {
                _user.emit(
                    _user.value.copy(token = token.result!!, signin = CustomTime.now())
                )
                saveUserInfoUseCase(_user.value)
                context?.let {
                    Toast.makeText(context, "로그인 세션이 갱신되었습니다", Toast.LENGTH_SHORT).show()
                }
            } else {
                _user.emit(emptyUser)
                clearUserInfoUseCase()
                context?.let {
                    Toast.makeText(context, "로그인 세션이 만료되었습니다. 다시 로그인을 해주세요.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    // 사용자의 프로필 업데이트하기
    fun updateProfileImage(uri: Uri, context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            updateAccessToken(context)
            val param = TsboardUpdateUserInfoParam(
                authorization = _user.value.token,
                name = _user.value.name,
                signature = _user.value.signature,
                password = "",
                profile = Upload.uriToMultipart(context, uri)
            )

            updateUserInfoUseCase(param).collect {
                val data = (it as TsboardResponse.Success<TsboardUpdateUserInfo>).data
                if (data.success) {
                    getUserInfoUseCase().collect { user ->
                        _user.value = user
                        saveUserInfoUseCase(user)

                        Toast.makeText(
                            context, "프로필 이미지를 업데이트 하였습니다", Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context, "프로필 변경에 실패했습니다 (${data.error})", Toast.LENGTH_LONG
                    ).show()
                }
                _isLoading.value = false
            }
        }
    }
}