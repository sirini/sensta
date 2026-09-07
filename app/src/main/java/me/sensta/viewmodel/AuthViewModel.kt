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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.data.util.Upload
import me.domain.model.auth.NuboSigninResult
import me.domain.model.auth.NuboSignupStatus
import me.domain.model.auth.NuboUpdateUserInfoParam
import me.domain.model.auth.NuboVerifyCodeParam
import me.domain.model.auth.emptyUser
import me.domain.model.auth.hasCompleteSession
import me.domain.model.common.NuboResponseNothing
import me.domain.repository.NuboResponse
import me.domain.repository.handle
import me.domain.usecase.auth.CheckEmailUseCase
import me.domain.usecase.auth.CheckNameUseCase
import me.domain.usecase.auth.CheckVerificationCodeUseCase
import me.domain.usecase.auth.ClearUserInfoUseCase
import me.domain.usecase.auth.DeleteAccountUseCase
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.auth.GetSignupStatusUseCase
import me.domain.usecase.auth.LogoutUseCase
import me.domain.usecase.auth.RequestPasswordResetUseCase
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
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

private val SESSION_REFRESH_INTERVAL: Duration = Duration.ofHours(1)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val checkEmailUseCase: CheckEmailUseCase,
    private val checkNameUseCase: CheckNameUseCase,
    private val clearUserInfoUseCase: ClearUserInfoUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getSignupStatusUseCase: GetSignupStatusUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val requestPasswordResetUseCase: RequestPasswordResetUseCase,
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

    private val _invite = mutableStateOf("")
    val invite: State<String> get() = _invite

    private val _user = mutableStateOf(emptyUser)
    val user: State<NuboSigninResult> get() = _user

    private val _loginState = mutableStateOf<LoginState>(LoginState.InputEmail)
    val loginState: State<LoginState> get() = _loginState

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _signupState = mutableStateOf<SignupState>(SignupState.InputEmail)
    val signupState: State<SignupState> get() = _signupState

    private val _signupStatus = mutableStateOf<NuboSignupStatus?>(null)
    val signupStatus: State<NuboSignupStatus?> get() = _signupStatus

    private val _isSignupStatusLoading = mutableStateOf(false)
    val isSignupStatusLoading: State<Boolean> get() = _isSignupStatusLoading

    private val _signupStatusError = mutableStateOf<String?>(null)
    val signupStatusError: State<String?> get() = _signupStatusError

    private val _isPasswordResetLoading = mutableStateOf(false)
    val isPasswordResetLoading: State<Boolean> get() = _isPasswordResetLoading

    private val _passwordResetRequested = mutableStateOf(false)
    val passwordResetRequested: State<Boolean> get() = _passwordResetRequested

    private val _passwordResetError = mutableStateOf<String?>(null)
    val passwordResetError: State<String?> get() = _passwordResetError

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

    private val sessionRefreshMutex = Mutex()
    private var sessionRevision = 0L

    // 저장된 세션을 복원한 뒤 서버에서 토큰 갱신을 시도하고, 거부된 세션은 즉시 비운다.
    init {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val restoredUser = getUserInfoUseCase().first()
                if (restoredUser.hasCompleteSession) {
                    replaceUser(restoredUser)
                    updateAccessToken()
                } else {
                    discardSession()
                }
            } finally {
                _isLoading.value = false
            }
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
            if (isSignup && _signupStatus.value?.emailSignupAvailable != true) {
                _uiAuthEvent.emit(AuthUiEvent.SignupUnavailable)
                return@launch
            }
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
                    val signedInUser = resp.result
                    if (signedInUser == null) {
                        _uiLoginEvent.emit(LoginUiEvent.FailedToLogin(resp.error))
                    } else {
                        completeLogin(signedInUser, resp.error)
                    }
                }
            }
            _isLoading.value = false
        }
    }

    // 로그아웃하기
    fun logout() {
        val signedOutUser = _user.value
        _loginState.value = LoginState.InputEmail
        replaceUser(emptyUser)
        viewModelScope.launch {
            // 네트워크 정리가 지연돼도 다음 실행에서 이전 세션을 복원하지 않는다.
            clearUserInfoUseCase()
            googleCredentialClient.clearCredentialState()
            val accessToken = signedOutUser.token
            if (accessToken.isNotBlank()) {
                pushTokenManager.unregister(accessToken)
                logoutUseCase(accessToken).collect { response ->
                    when (response) {
                        is NuboResponse.Error -> AppDiagnostics.report("서버 로그아웃", response)
                        is NuboResponse.Success -> if (!response.data.success) {
                            AppDiagnostics.report("서버 로그아웃", response.data.error)
                        }
                        NuboResponse.Loading -> Unit
                    }
                }
            }
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
                        discardSession()
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
            updateAccessToken()
        }
    }

    // 보호된 쓰기 요청은 토큰 회전과 저장이 끝난 뒤 시작해야 한다.
    suspend fun refreshForProtectedRequest(): Boolean = updateAccessToken()

    // 프로세스가 유지된 채 앱으로 돌아와도 만료 전에 세션을 갱신한다.
    fun refreshIfNeeded() {
        val currentUser = _user.value
        if (!currentUser.needsSessionRefresh(CustomTime.now())) return
        viewModelScope.launch { updateAccessToken() }
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

    fun setInvite(invite: String) {
        _invite.value = invite.trim()
    }

    fun loadSignupStatus() {
        if (_isSignupStatusLoading.value) return
        _isSignupStatusLoading.value = true
        viewModelScope.launch {
            when (val response = getSignupStatusUseCase().first()) {
                is NuboResponse.Success -> {
                    _signupStatus.value = response.data
                    _signupStatusError.value = null
                }
                is NuboResponse.Error -> {
                    _signupStatus.value = null
                    _signupStatusError.value = response.message
                }
                NuboResponse.Loading -> Unit
            }
            _isSignupStatusLoading.value = false
        }
    }

    fun resetPasswordResetFlow() {
        _passwordResetRequested.value = false
        _passwordResetError.value = null
    }

    fun requestPasswordReset(email: String) {
        val normalizedEmail = email.trim()
        if (_isPasswordResetLoading.value) return
        if (!Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()) {
            _passwordResetError.value = "올바른 이메일 주소를 입력해 주세요"
            return
        }

        _isPasswordResetLoading.value = true
        _passwordResetError.value = null
        viewModelScope.launch {
            when (val response = requestPasswordResetUseCase(normalizedEmail).first()) {
                is NuboResponse.Success -> {
                    if (response.data.success) {
                        _passwordResetRequested.value = true
                    } else {
                        _passwordResetError.value = "현재 재설정 메일을 보낼 수 없습니다. 잠시 뒤 다시 시도해 주세요"
                    }
                }
                is NuboResponse.Error -> {
                    _passwordResetError.value =
                        "재설정 메일을 요청하지 못했습니다. 인터넷 연결을 확인해 주세요"
                }
                NuboResponse.Loading -> Unit
            }
            _isPasswordResetLoading.value = false
        }
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
        if (signedInUser == null || !signedInUser.hasCompleteSession) {
            AppDiagnostics.report("NUBO Google 로그인", response.error)
            discardSession()
            _uiLoginEvent.emit(LoginUiEvent.FailedToLogin(response.error))
            return
        }
        completeLogin(signedInUser, response.error)
    }

    // 회원정보 등록 및 필요시 이메일로 전달된 인증 코드 받기
    private fun signUp() {
        viewModelScope.launch {
            if (_id.value.isEmpty() || _pw.value.isEmpty() || _name.value.isEmpty()) {
                _uiAuthEvent.emit(AuthUiEvent.EnterAllInfo)
                return@launch
            }

            if (_signupStatus.value?.requiresInvite == true && _invite.value.isBlank()) {
                _uiAuthEvent.emit(AuthUiEvent.EnterInviteCode)
                return@launch
            }

            signUpUseCase(_id.value, _pw.value, _name.value, _invite.value).collect {
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
                        _invite.value = ""
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

            if (!updateAccessToken()) {
                _uiProfileEvent.emit(ProfileUiEvent.FailedToChangeName("로그인 상태를 확인해 주세요"))
                _isLoading.value = false
                return@launch
            }
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
    private suspend fun updateAccessToken(): Boolean {
        val refreshUser = _user.value
        if (!refreshUser.hasCompleteSession) return false

        return sessionRefreshMutex.withLock {
            // 기다리는 사이 다른 요청이 이미 토큰을 회전했으면 같은 refresh token을 재사용하지 않는다.
            if (
                _user.value.uid != refreshUser.uid ||
                _user.value.refresh != refreshUser.refresh
            ) {
                return@withLock _user.value.hasCompleteSession
            }

            val requestRevision = sessionRevision
            var refreshed = false
            updateAccessTokenUseCase(refreshUser.refresh).collect { response ->
                response.handle(
                    onError = { error -> AppDiagnostics.report("로그인 세션 갱신", error) }
                ) { resp ->
                    // 로그아웃이나 다른 로그인 뒤 늦게 도착한 응답은 이전 세션을 되살리면 안 된다.
                    if (
                        requestRevision != sessionRevision ||
                        _user.value.uid != refreshUser.uid ||
                        _user.value.refresh != refreshUser.refresh
                    ) {
                        return@handle
                    }

                    val tokens = resp.result
                    if (
                        resp.success &&
                        tokens != null &&
                        tokens.token.isNotBlank() &&
                        tokens.refresh.isNotBlank()
                    ) {
                        val refreshedUser = refreshUser.copy(
                            token = tokens.token,
                            refresh = tokens.refresh,
                            signin = CustomTime.now()
                        )
                        replaceUser(refreshedUser)
                        saveUserInfoUseCase(refreshedUser)
                        pushTokenManager.synchronize()
                        _uiAuthEvent.emit(AuthUiEvent.AccessTokenUpdated)
                        refreshed = true
                    } else {
                        discardSession()
                        _uiAuthEvent.emit(AuthUiEvent.ExpiredAccessToken)
                    }
                }
            }
            refreshed
        }
    }

    private suspend fun completeLogin(user: NuboSigninResult, error: String) {
        if (!user.hasCompleteSession) {
            discardSession()
            _uiLoginEvent.emit(LoginUiEvent.FailedToLogin(error.ifBlank { "로그인 응답이 올바르지 않습니다" }))
            return
        }
        replaceUser(user)
        pushTokenManager.synchronize()
        _loginState.value = LoginState.LoginCompleted
    }

    private fun replaceUser(user: NuboSigninResult) {
        sessionRevision += 1
        _user.value = user
    }

    private suspend fun discardSession() {
        replaceUser(emptyUser)
        _loginState.value = LoginState.InputEmail
        clearUserInfoUseCase()
    }

    // 사용자의 프로필 업데이트하기
    fun updateProfileImage(uri: Uri, context: Context) {
        _isLoading.value = true

        viewModelScope.launch {
            if (!updateAccessToken()) {
                _uiProfileEvent.emit(
                    ProfileUiEvent.FailedToUpdateProfileImage("로그인 상태를 확인해 주세요")
                )
                _isLoading.value = false
                return@launch
            }
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

internal fun NuboSigninResult.needsSessionRefresh(now: LocalDateTime): Boolean =
    hasCompleteSession &&
        !signin.isAfter(now) &&
        Duration.between(signin, now) >= SESSION_REFRESH_INTERVAL
