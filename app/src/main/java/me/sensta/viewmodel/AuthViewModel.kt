package me.sensta.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.data.util.Upload
import me.domain.model.auth.NuboSigninResult
import me.domain.model.auth.NuboUpdateUserInfoParam
import me.domain.model.auth.NuboVerifyCodeParam
import me.domain.model.auth.emptyUser
import me.domain.model.common.NuboResponseNothing
import me.domain.repository.NuboResponse
import me.domain.repository.handle
import me.domain.usecase.auth.CheckEmailUseCase
import me.domain.usecase.auth.CheckNameUseCase
import me.domain.usecase.auth.CheckVerificationCodeUseCase
import me.domain.usecase.auth.ClearUserInfoUseCase
import me.domain.usecase.auth.DeleteAccountUseCase
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.auth.SaveUserInfoUseCase
import me.domain.usecase.auth.SignInUseCase
import me.domain.usecase.auth.SignInWithGoogleUseCase
import me.domain.usecase.auth.SignUpUseCase
import me.domain.usecase.auth.UpdateAccessTokenUseCase
import me.domain.usecase.auth.UpdateUserInfoUseCase
import me.sensta.auth.GoogleCredentialClient
import me.sensta.auth.GoogleCredentialResult
import me.sensta.diagnostics.AppDiagnostics
import me.sensta.push.PushTokenManager
import me.sensta.policy.CommunityPolicyManager
import me.sensta.util.CustomTime
import me.sensta.util.now
import me.sensta.viewmodel.state.LoginState
import me.sensta.viewmodel.state.SignupState
import me.sensta.viewmodel.uievent.AuthUiEvent
import me.sensta.viewmodel.uievent.LoginUiEvent
import me.sensta.viewmodel.uievent.ProfileUiEvent
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkEmailUseCase: CheckEmailUseCase,
    private val checkNameUseCase: CheckNameUseCase,
    private val clearUserInfoUseCase: ClearUserInfoUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val saveUserInfoUseCase: SaveUserInfoUseCase,
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val updateAccessTokenUseCase: UpdateAccessTokenUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val verifyCodeUseCase: CheckVerificationCodeUseCase,
    private val pushTokenManager: PushTokenManager,
    private val communityPolicyManager: CommunityPolicyManager,
    private val googleCredentialClient: GoogleCredentialClient
) : ViewModel() {
    private val _id = mutableStateOf("")
    val id: State<String> get() = _id

    private val _isEmailValid = mutableStateOf(true)
    val isEmailValid: State<Boolean> get() = _isEmailValid

    private val _pw = mutableStateOf("")
    val pw: State<String> get() = _pw

    private val _name = mutableStateOf("")
    val name: State<String> get() = _name

    private val _user = mutableStateOf(emptyUser)
    val user: State<NuboSigninResult> get() = _user

    private val _loginState = mutableStateOf<LoginState>(LoginState.InputEmail)
    val loginState: State<LoginState> get() = _loginState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _signupState = mutableStateOf<SignupState>(SignupState.InputEmail)
    val signupState: State<SignupState> get() = _signupState

    private val _isCommunityPolicyAccepted = mutableStateOf(communityPolicyManager.isAccepted())
    val isCommunityPolicyAccepted: State<Boolean> get() = _isCommunityPolicyAccepted

    private val _targetUserUid = mutableIntStateOf(0)
    val targetUserUid: State<Int> get() = _targetUserUid

    private val _otp = mutableStateOf("")
    val otp: State<String> get() = _otp

    private val _uiAuthEvent = MutableSharedFlow<AuthUiEvent>()
    val uiAuthEvent = _uiAuthEvent.asSharedFlow()

    private val _uiLoginEvent = MutableSharedFlow<LoginUiEvent>()
    val uiLoginEvent = _uiLoginEvent.asSharedFlow()

    private val _uiProfileEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiProfileEvent = _uiProfileEvent.asSharedFlow()

    // 생성 시점에 기존에 로그인했던 정보가 있다면 가져오기
    init {
        _isLoading.value = true
        viewModelScope.launch {
            _user.value = getUserInfoUseCase().first()
            if (_user.value.token.isNotBlank()) {
                pushTokenManager.synchronize()
            }
            _loginState.value = LoginState.InputEmail
            _isLoading.value = false
        }
    }

    // 비밀번호 입력 화면에서 아이디 입력 화면으로 (뒤로)
    fun backToID() {
        _loginState.value = LoginState.InputEmail
    }

    // 회원가입 시 아이디 확인
    private fun checkIDForSignup(checkEmailData: NuboResponseNothing) {
        viewModelScope.launch {
            when {
                !checkEmailData.success -> _uiAuthEvent.emit(AuthUiEvent.InvalidEmailAddress)
                checkEmailData.result == "true" -> _uiAuthEvent.emit(AuthUiEvent.AlreadyUsedID)
                else -> _signupState.value = SignupState.InputPassword
            }
        }
    }

    // 로그인 시 아이디 확인
    private fun checkIDForLogin(checkEmailData: NuboResponseNothing) {
        viewModelScope.launch {
            when {
                !checkEmailData.success -> _uiAuthEvent.emit(AuthUiEvent.InvalidEmailAddress)
                checkEmailData.result == "true" -> _loginState.value = LoginState.InputPassword
                else -> _uiLoginEvent.emit(LoginUiEvent.IDNotFound("등록되지 않은 이메일입니다"))
            }
        }
    }

    // 아이디(이메일)가 존재하는지 확인 후 비밀번호 입력란으로 이동
    fun checkValidID(isSignup: Boolean = false) {
        viewModelScope.launch {
            if (_id.value.isEmpty() || !_isEmailValid.value) {
                _uiAuthEvent.emit(AuthUiEvent.InvalidEmailAddress)
                return@launch
            }

            _isLoading.value = true
            checkEmailUseCase(_id.value).collect {
                it.handle { resp ->
                    when (isSignup) {
                        true -> checkIDForSignup(resp)
                        false -> checkIDForLogin(resp)
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 회원 가입시 유효한 이름인지 확인하고, 확인되면 인증 코드 입력으로 이동 혹은 가입 완료
    fun checkValidName() {
        viewModelScope.launch {
            if (!_isCommunityPolicyAccepted.value) {
                _uiAuthEvent.emit(AuthUiEvent.CommunityPolicyRequired)
                return@launch
            }
            if (_name.value.isEmpty() || _name.value.length < 2) {
                _uiAuthEvent.emit(AuthUiEvent.InvalidName)
                return@launch
            }

            _isLoading.value = true
            checkNameUseCase(_name.value).collect {
                it.handle { resp ->
                    when {
                        !resp.success -> _uiAuthEvent.emit(AuthUiEvent.InvalidName)
                        resp.result == "true" -> _uiAuthEvent.emit(AuthUiEvent.AlreadyUsedName)
                        else -> signUp() // 회원가입 진행
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 회원 가입시 제대로된 비밀번호를 입력했는지 확인 후 (유효할 시) 닉네임 작성으로 이동
    fun checkValidPW(pwAgain: String) {
        viewModelScope.launch {
            if (pw.value != pwAgain) {
                _uiAuthEvent.emit(AuthUiEvent.DifferentPassword)
                return@launch
            }
            if (pw.value.length < 8) {
                _uiAuthEvent.emit(AuthUiEvent.AtLeast8Characters)
                return@launch
            }
            val regex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).+$")
            if (!regex.matches(pw.value)) {
                _uiAuthEvent.emit(AuthUiEvent.RemindPasswordRule)
                return@launch
            }
            _signupState.value = SignupState.InputName
        }
    }

    // 회원 가입 시 인증 코드 입력이 필요한 경우 인증 코드 유효성 확인하고, 확인되면 가입 절차 완료
    fun checkVerificationCode() {
        viewModelScope.launch {
            if (_targetUserUid.intValue == 0) {
                _uiAuthEvent.emit(AuthUiEvent.InvalidTargetUser)
                return@launch
            }
            if (otp.value.length != 6) {
                _uiAuthEvent.emit(AuthUiEvent.EnterVerificationCode)
                return@launch
            }

            _isLoading.value = true
            verifyCodeUseCase(
                NuboVerifyCodeParam(
                    target = _targetUserUid.intValue,
                    code = _otp.value,
                    email = _id.value,
                    password = _pw.value,
                    name = _name.value
                )
            ).collect {
                it.handle { resp ->
                    if (!resp.success || resp.result != "true") {
                        _uiAuthEvent.emit(AuthUiEvent.WrongVerificationCode)
                    } else {
                        _signupState.value = SignupState.SignupCompleted
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 입력된 아이디와 비밀번호가 유효한지 확인 후 (유효할 시) 홈 화면으로 이동
    fun login() {
        _isLoading.value = true

        viewModelScope.launch {
            signInUseCase(_id.value, _pw.value).collect {
                it.handle { resp ->
                    if (null == resp.result) {
                        _uiLoginEvent.emit(LoginUiEvent.FailedToLogin(resp.error))
                    } else {
                        _user.value = resp.result!!
                        pushTokenManager.synchronize()
                        _loginState.value = LoginState.LoginCompleted
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 로그아웃하기
    fun logout() {
        _loginState.value = LoginState.InputEmail
        viewModelScope.launch {
            val accessToken = _user.value.token
            if (accessToken.isNotBlank()) {
                pushTokenManager.unregister(accessToken)
            }
            googleCredentialClient.clearCredentialState()
            _user.value = emptyUser
            clearUserInfoUseCase()
        }
    }

    // 서버 데이터를 먼저 삭제한 뒤 기기의 로그인 정보도 제거한다.
    fun deleteAccount() {
        val accessToken = _user.value.token
        if (accessToken.isBlank() || _isLoading.value) return

        _isLoading.value = true
        viewModelScope.launch {
            deleteAccountUseCase(accessToken).collect { response ->
                response.handle { result ->
                    if (result.success) {
                        // 서버에서 기기 등록도 함께 삭제하므로 별도의 해제 호출은 하지 않는다.
                        googleCredentialClient.clearCredentialState()
                        _user.value = emptyUser
                        clearUserInfoUseCase()
                        _loginState.value = LoginState.InputEmail
                        _uiProfileEvent.emit(ProfileUiEvent.AccountDeleted)
                    } else {
                        _uiProfileEvent.emit(ProfileUiEvent.FailedToDeleteAccount(result.error))
                    }
                }
                if (response is me.domain.repository.NuboResponse.Error) {
                    _uiProfileEvent.emit(ProfileUiEvent.FailedToDeleteAccount(response.message))
                }
            }
            _isLoading.value = false
        }
    }

    // 로그인 세션 갱신
    fun refresh() {
        viewModelScope.launch {
            if (_user.value.token.isNotEmpty()) {
                updateAccessToken()
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

    // 회원가입 전에 이용약관과 커뮤니티 운영 원칙에 동의한 상태를 보관한다.
    fun acceptCommunityPolicy(accepted: Boolean) {
        _isCommunityPolicyAccepted.value = accepted
        if (accepted) communityPolicyManager.accept()
    }

    // 인증코드 6자리 입력 받기
    fun setOTP(otp: String) {
        _otp.value = otp.trim()
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
        _isLoading.value = true
        viewModelScope.launch {
            try {
                when (val credential = googleCredentialClient.requestIdToken(context)) {
                    is GoogleCredentialResult.Success -> authenticateWithGoogle(credential.idToken)
                    is GoogleCredentialResult.Failure -> _uiLoginEvent.emit(
                        LoginUiEvent.FailedToLoginByGoogle(credential.message)
                    )
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun authenticateWithGoogle(idToken: String) {
        signInWithGoogleUseCase(idToken).collect { response ->
            when (response) {
                NuboResponse.Loading -> Unit
                is NuboResponse.Error -> {
                    AppDiagnostics.report("NUBO Google 로그인", response)
                    _uiLoginEvent.emit(LoginUiEvent.FailedToLoginByGoogle(response.message))
                }
                is NuboResponse.Success -> completeGoogleLogin(response.data)
            }
        }
    }

    private suspend fun completeGoogleLogin(response: me.domain.model.auth.NuboSignin) {
        val signedInUser = response.result
        if (signedInUser == null) {
            AppDiagnostics.report("NUBO Google 로그인", response.error)
            _uiLoginEvent.emit(LoginUiEvent.FailedToLogin(response.error))
            return
        }
        _user.value = signedInUser
        pushTokenManager.synchronize()
        _loginState.value = LoginState.LoginCompleted
    }

    // 회원정보 등록 및 필요시 이메일로 전달된 인증 코드 받기
    private fun signUp() {
        viewModelScope.launch {
            if (_id.value.isEmpty() || _pw.value.isEmpty() || _name.value.isEmpty()) {
                _uiAuthEvent.emit(AuthUiEvent.EnterAllInfo)
                return@launch
            }

            signUpUseCase(_id.value, _pw.value, _name.value).collect {
                it.handle { resp ->
                    if (!resp.success) {
                        _uiAuthEvent.emit(AuthUiEvent.FailedToSignUp)
                        return@handle
                    }
                    if (resp.result.requiresVerification) {
                        _targetUserUid.intValue = resp.result.target
                        _signupState.value = SignupState.InputCode
                        _uiAuthEvent.emit(AuthUiEvent.SentVerificationCode(_id.value))
                    } else if (resp.result.completed) {
                        _signupState.value = SignupState.SignupCompleted
                        _uiAuthEvent.emit(AuthUiEvent.SignupCompleted)
                    }
                }
            }
        }
    }

    // 사용자의 이름 업데이트하기
    fun updateName(name: String) {
        viewModelScope.launch {
            if (name.length < 2) {
                _uiAuthEvent.emit(AuthUiEvent.InvalidName)
                return@launch
            }
            _isLoading.value = true

            updateAccessToken()
            val param = NuboUpdateUserInfoParam(
                authorization = _user.value.token,
                name = name,
                signature = _user.value.signature,
                password = "",
                profile = null
            )

            updateUserInfoUseCase(param).collect {
                it.handle { resp ->
                    if (resp.success) {
                        _user.value = _user.value.copy(name = name)
                        saveUserInfoUseCase(_user.value)
                        _uiProfileEvent.emit(ProfileUiEvent.ChangedName)
                    } else {
                        _uiProfileEvent.emit(ProfileUiEvent.FailedToChangeName(resp.error))
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 사용자의 서명 업데이트하기
    fun updateSignature(signature: String) {
        _isLoading.value = true

        viewModelScope.launch {
            val param = NuboUpdateUserInfoParam(
                authorization = _user.value.token,
                name = _user.value.name,
                signature = signature,
                password = "",
                profile = null
            )

            updateUserInfoUseCase(param).collect {
                it.handle { resp ->
                    if (resp.success) {
                        _user.value = _user.value.copy(signature = signature)
                        saveUserInfoUseCase(_user.value)
                        _uiProfileEvent.emit(ProfileUiEvent.ChangedSignature)
                    } else {
                        _uiProfileEvent.emit(ProfileUiEvent.FailedToChangeSignature(resp.error))
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 사용자의 리프레시 토큰으로 새 액세스 토큰 발급받기
    private suspend fun updateAccessToken() {
        if (_user.value.uid < 1) return

        updateAccessTokenUseCase(_user.value.refresh).collect {
            it.handle { resp ->
                val tokens = resp.result
                if (resp.success && tokens != null) {
                    _user.value = _user.value.copy(
                        token = tokens.token,
                        refresh = tokens.refresh,
                        signin = CustomTime.now()
                    )
                    saveUserInfoUseCase(_user.value)
                    pushTokenManager.synchronize()
                    _uiAuthEvent.emit(AuthUiEvent.AccessTokenUpdated)
                } else {
                    _user.value = emptyUser
                    clearUserInfoUseCase()
                    _uiAuthEvent.emit(AuthUiEvent.ExpiredAccessToken)
                }
            }
        }
    }

    // 사용자의 프로필 업데이트하기
    fun updateProfileImage(uri: Uri, context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            updateAccessToken()
            val preparedProfile = withContext(Dispatchers.IO) {
                Upload.prepareImage(context, uri, "profile")
            }
            if (preparedProfile == null) {
                _uiProfileEvent.emit(
                    ProfileUiEvent.FailedToUpdateProfileImage("사진을 읽지 못했습니다")
                )
                _isLoading.value = false
                return@launch
            }
            val param = NuboUpdateUserInfoParam(
                authorization = _user.value.token,
                name = _user.value.name,
                signature = _user.value.signature,
                password = "",
                profile = preparedProfile.part
            )

            try {
                updateUserInfoUseCase(param).collect {
                    it.handle { resp ->
                        if (resp.success) {
                            val userInfo = getUserInfoUseCase().first()
                            _user.value = userInfo
                            saveUserInfoUseCase(userInfo)
                            _uiProfileEvent.emit(ProfileUiEvent.ProfileImageUpdated)
                        } else {
                            _uiProfileEvent.emit(ProfileUiEvent.FailedToUpdateProfileImage(resp.error))
                        }
                    }
                }
            } finally {
                withContext(Dispatchers.IO) { preparedProfile.cleanUp() }
                _isLoading.value = false
            }
        }
    }
}
