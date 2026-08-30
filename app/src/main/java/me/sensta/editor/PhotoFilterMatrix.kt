package me.sensta.editor

object PhotoFilterMatrix {
    private val identity = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    fun values(filter: PhotoFilter, intensity: Float): FloatArray {
        val amount = intensity.coerceIn(0f, 1f)
        val target = when (filter) {
            PhotoFilter.ORIGINAL -> identity
            PhotoFilter.VIVID -> vivid
            PhotoFilter.WARM -> warm
            PhotoFilter.COOL -> cool
            PhotoFilter.FILM -> film
            PhotoFilter.MONO -> mono
        }
        return FloatArray(identity.size) { index ->
            identity[index] + (target[index] - identity[index]) * amount
        }
    }

    private val vivid = floatArrayOf(
        1.20f, -0.05f, -0.05f, 0f, -5f,
        -0.05f, 1.15f, -0.05f, 0f, -3f,
        -0.05f, -0.05f, 1.20f, 0f, -5f,
        0f, 0f, 0f, 1f, 0f
    )
    private val warm = floatArrayOf(
        1.08f, 0.03f, 0f, 0f, 3f,
        0.01f, 1.02f, 0f, 0f, 1f,
        0f, 0f, 0.90f, 0f, -2f,
        0f, 0f, 0f, 1f, 0f
    )
    private val cool = floatArrayOf(
        0.92f, 0f, 0f, 0f, -2f,
        0f, 1.01f, 0.02f, 0f, 0f,
        0f, 0.03f, 1.10f, 0f, 3f,
        0f, 0f, 0f, 1f, 0f
    )
    private val film = floatArrayOf(
        0.92f, 0.08f, 0.03f, 0f, 5f,
        0.04f, 0.94f, 0.07f, 0f, 2f,
        0.02f, 0.10f, 0.84f, 0f, -3f,
        0f, 0f, 0f, 1f, 0f
    )
    private val mono = floatArrayOf(
        0.213f, 0.715f, 0.072f, 0f, 0f,
        0.213f, 0.715f, 0.072f, 0f, 0f,
        0.213f, 0.715f, 0.072f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
}
