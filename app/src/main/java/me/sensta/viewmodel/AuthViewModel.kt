package me.sensta.viewmodel

import android.content.Context
import android.credentials.GetCredentialException
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
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
import me.domain.model.auth.TsboardSigninResult
import me.domain.model.auth.TsboardUpdateUserInfoParam
import me.domain.model.auth.TsboardVerifyCodeParam
import me.domain.model.auth.emptyUser
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.handle
import me.domain.usecase.auth.CheckEmailUseCase
import me.domain.usecase.auth.CheckNameUseCase
import me.domain.usecase.auth.CheckVerificationCodeUseCase
import me.domain.usecase.auth.ClearUserInfoUseCase
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.auth.SaveUserInfoUseCase
import me.domain.usecase.auth.SignInUseCase
import me.domain.usecase.auth.SignInWithGoogleUseCase
import me.domain.usecase.auth.SignUpUseCase
import me.domain.usecase.auth.UpdateAccessTokenUseCase
import me.domain.usecase.auth.UpdateUserInfoUseCase
import me.sensta.R
import me.sensta.util.CustomTime
import me.sensta.util.now
import me.sensta.viewmodel.state.ID_INVALID
import me.sensta.viewmodel.state.ID_REGISTERED
import me.sensta.viewmodel.state.LoginState
import me.sensta.viewmodel.state.SignupState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkEmailUseCase: CheckEmailUseCase,
    private val checkNameUseCase: CheckNameUseCase,
    private val clearUserInfoUseCase: ClearUserInfoUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val updateAccessTokenUseCase: UpdateAccessTokenUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val verifyCodeUseCase: CheckVerificationCodeUseCase
) : ViewModel() {
    private val _id = mutableStateOf("")
    val id: State<String> get() = _id

    private val _isEmailValid = mutableStateOf(true)
    val isEmailValid: State<Boolean> get() = _isEmailValid

    private val _pw = mutableStateOf("")
    val pw: State<String> get() = _pw

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _user = MutableStateFlow(emptyUser)
    val user: StateFlow<TsboardSigninResult> = _user.asStateFlow()

    private val _loginState = mutableStateOf<LoginState>(LoginState.InputEmail)
    val loginState: State<LoginState> get() = _loginState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _signupState = mutableStateOf<SignupState>(SignupState.InputEmail)
    val signupState: State<SignupState> get() = _signupState

    private val _targetUserUid = mutableIntStateOf(0)
    val targetUserUid: State<Int> get() = _targetUserUid

    private val _otp = mutableStateOf("")
    val otp: State<String> get() = _otp

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

    // 회원가입 시 아이디 확인
    private fun checkIDForSignup(context: Context, checkEmailData: TsboardResponseNothing) {
        when (checkEmailData.code) {
            ID_REGISTERED -> {
                Toast.makeText(context, "이미 존재하는 아이디입니다", Toast.LENGTH_SHORT).show()
            }

            ID_INVALID -> {
                Toast.makeText(context, "유효하지 않은 메일 주소입니다", Toast.LENGTH_SHORT).show()
            }

            else -> {
                _signupState.value = SignupState.InputPassword
            }
        }
    }

    // 로그인 시 아이디 확인
    private fun checkIDForLogin(context: Context, checkEmailData: TsboardResponseNothing) {
        when (checkEmailData.code) {
            ID_INVALID -> {
                Toast.makeText(context, "유효하지 않은 메일 주소입니다", Toast.LENGTH_SHORT).show()
            }

            ID_REGISTERED -> {
                _loginState.value = LoginState.InputPassword
            }

            else -> {
                Toast.makeText(context, "존재하지 않는 아이디입니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 아이디(이메일)가 존재하는지 확인 후 비밀번호 입력란으로 이동
    fun checkValidID(context: Context, isSignup: Boolean = false) {
        if (_id.value.isEmpty() || !_isEmailValid.value) {
            Toast.makeText(context, "올바른 이메일 주소를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        _isLoading.value = true

        viewModelScope.launch {
            checkEmailUseCase(_id.value).collect {
                it.handle(context) { resp ->
                    when (isSignup) {
                        true -> checkIDForSignup(context, resp)
                        false -> checkIDForLogin(context, resp)
                    }
                }
                _isLoading.value = false
            }
        }
    }

    // 회원 가입시 유효한 이름인지 확인하고, 확인되면 인증 코드 입력으로 이동 혹은 가입 완료
    fun checkValidName(context: Context) {
        if (_name.value.isEmpty() || _name.value.length < 2) {
            Toast.makeText(context, "올바른 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        _isLoading.value = true

        viewModelScope.launch {
            checkNameUseCase(_name.value).collect {
                it.handle(context) { resp ->
                    if (resp.code == ID_REGISTERED) {
                        Toast.makeText(context, "이미 존재하는 이름입니다", Toast.LENGTH_SHORT).show()
                    } else {
                        signUp(context) // 회원가입 진행
                    }
                }
                _isLoading.value = false
            }
        }
    }

    // 회원 가입시 제대로된 비밀번호를 입력했는지 확인 후 (유효할 시) 닉네임 작성으로 이동
    fun checkValidPW(context: Context, pwAgain: String) {
        if (pw.value != pwAgain) {
            Toast.makeText(context, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            return
        }
        if (pw.value.length < 8) {
            Toast.makeText(context, "비밀번호는 8자 이상이어야 합니다", Toast.LENGTH_SHORT).show()
            return
        }
        val regex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$")
        if (!regex.matches(pw.value)) {
            Toast.makeText(
                context, "비밀번호는 알파벳 대소문자, 숫자, 특수문자를 모두 포함해야 합니다", Toast.LENGTH_SHORT
            ).show()
            return
        }
        _signupState.value = SignupState.InputName
    }

    // 회원 가입 시 인증 코드 입력이 필요한 경우 인증 코드 유효성 확인하고, 확인되면 가입 절차 완료
    fun checkVerificationCode(context: Context) {
        if (_targetUserUid.intValue == 0) {
            Toast.makeText(context, "죄송합니다. 인증 대상 번호를 확인할 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }
        if (otp.value.length != 6) {
            Toast.makeText(context, "인증 코드를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        _isLoading.value = true

        viewModelScope.launch {
            verifyCodeUseCase(
                TsboardVerifyCodeParam(
                    target = _targetUserUid.intValue,
                    code = _otp.value,
                    email = _id.value,
                    password = _pw.value,
                    name = _name.value
                )
            ).collect {
                it.handle(context) { resp ->
                    if (!resp.success) {
                        Toast.makeText(context, "인증 코드가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        _signupState.value = SignupState.SignupCompleted
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 입력된 아이디와 비밀번호가 유효한지 확인 후 (유효할 시) 홈 화면으로 이동
    fun login(context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            signInUseCase(_id.value, _pw.value).collect {
                it.handle(context) { resp ->
                    if (null == resp.result) {
                        Toast.makeText(context, "로그인에 실패했습니다", Toast.LENGTH_LONG).show()
                    } else {
                        _user.value = resp.result!!
                        _loginState.value = LoginState.LoginCompleted
                    }
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
            if (_user.value.token.isNotEmpty()) {
                updateAccessToken(context)
            }
        }
    }

    // 아이디(이메일 주소) 입력 받기
    fun setID(id: String) {
        _id.value = id.trim()
        _isEmailValid.value = Patterns.EMAIL_ADDRESS.matcher(id).matches()
    }

    // 회원가입 시 이름 입력 받기
    fun setName(name: String) {
        _name.value = name.trim()
    }

    // 인증코드 6자리 입력 받기
    fun setOTP(otp: String) {
        _otp.value = otp.trim()
        //
    }

    // 비밀번호 입력 받기
    fun setPW(pw: String) {
        _pw.value = pw.trim()
    }

    // 회원가입 단계 변경하기
    fun setSignupState(state: SignupState) {
        _signupState.value = state
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
                                it.handle(context) { resp ->
                                    if (null == resp.result) {
                                        Toast.makeText(context, "로그인에 실패했습니다", Toast.LENGTH_LONG)
                                            .show()
                                    } else {
                                        _user.value = resp.result!!
                                        _loginState.value = LoginState.LoginCompleted
                                    }
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

    // 회원정보 등록 및 필요시 이메일로 전달된 인증 코드 받기
    private fun signUp(context: Context) {
        if (_id.value.isEmpty() || _pw.value.isEmpty() || _name.value.isEmpty()) {
            Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            signUpUseCase(_id.value, _pw.value, _name.value).collect {
                it.handle(context) { resp ->
                    if (!resp.success) {
                        Toast.makeText(context, "회원가입에 실패했습니다 (${resp.error})", Toast.LENGTH_LONG)
                            .show()
                        return@handle
                    }
                    if (resp.result.sendmail) {
                        _targetUserUid.intValue = resp.result.target
                        _signupState.value = SignupState.InputCode

                        Toast.makeText(
                            context, "인증 코드 6자리를 ${_id.value}로 발송했습니다.", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        _signupState.value = SignupState.SignupCompleted
                        Toast.makeText(context, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    }
                }
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
                it.handle(context) { resp ->
                    if (resp.success) {
                        _user.value = _user.value.copy(name = name)
                        saveUserInfoUseCase(_user.value)
                        Toast.makeText(context, "이름이 변경되었습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context, "이름 변경에 실패했습니다 (${resp.error})", Toast.LENGTH_SHORT
                        ).show()
                    }
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
                it.handle(context) { resp ->
                    if (resp.success) {
                        _user.value = _user.value.copy(signature = signature)
                        saveUserInfoUseCase(_user.value)
                        Toast.makeText(context, "서명이 변경되었습니다", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "서명 변경에 실패했습니다 (${resp.error})", Toast.LENGTH_LONG)
                            .show()
                    }
                }
                _isLoading.value = false
            }
        }
    }

    // 사용자의 리프레시 토큰으로 새 액세스 토큰 발급받기
    private suspend fun updateAccessToken(context: Context? = null) {
        if (_user.value.uid < 1) return

        updateAccessTokenUseCase(_user.value.uid, _user.value.refresh).collect {
            it.handle(context) { resp ->
                if (null != resp.result) {
                    _user.emit(
                        _user.value.copy(token = resp.result!!, signin = CustomTime.now())
                    )
                    saveUserInfoUseCase(_user.value)
                    context?.let {
                        Toast.makeText(context, "로그인 세션이 갱신되었습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    _user.emit(emptyUser)
                    clearUserInfoUseCase()
                    context?.let {
                        Toast.makeText(
                            context,
                            "로그인 세션이 만료되었습니다. 다시 로그인을 해주세요.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
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
                it.handle(context) { resp ->
                    if (resp.success) {
                        getUserInfoUseCase().collect { user ->
                            _user.value = user
                            saveUserInfoUseCase(user)

                            Toast.makeText(
                                context, "프로필 이미지를 업데이트 하였습니다", Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context, "프로필 변경에 실패했습니다 (${resp.error})", Toast.LENGTH_LONG
                        ).show()
                    }
                }
                _isLoading.value = false
            }
        }
    }
}