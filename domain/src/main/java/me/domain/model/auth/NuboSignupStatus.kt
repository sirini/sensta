package me.domain.model.auth

data class NuboSignupStatus(
    val mode: String,
    val mailConfigured: Boolean,
    val oauthRegistrationAllowed: Boolean
) {
    val emailSignupAvailable: Boolean
        get() = mode == MODE_INVITE_ONLY || (mode == MODE_VERIFIED_EMAIL && mailConfigured)

    val requiresInvite: Boolean
        get() = mode == MODE_INVITE_ONLY

    companion object {
        const val MODE_VERIFIED_EMAIL = "verified_email"
        const val MODE_INVITE_ONLY = "invite_only"
        const val MODE_DISABLED = "disabled"
    }
}
