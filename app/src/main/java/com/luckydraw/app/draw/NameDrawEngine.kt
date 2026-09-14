package com.luckydraw.app.draw

import kotlin.random.Random

/**
 * Sorteio a partir de uma lista de nomes colada pelo usuário.
 *
 * Lógica pura, sem dependência de Android, para poder ser testada em JVM.
 */
object NameDrawEngine {

    /** Aceita nomes separados por quebra de linha, vírgula ou ponto e vírgula. */
    private val SEPARATORS = Regex("[\\r\\n,;]+")

    /**
     * Quebra o texto bruto em nomes. Espaços nas pontas são removidos e entradas
     * vazias descartadas, de modo que vírgulas sobrando ou linhas em branco não
     * viram "participantes fantasma".
     */
    fun parse(raw: String, dropDuplicates: Boolean = false): List<String> {
        val names = raw.split(SEPARATORS)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        return if (dropDuplicates) names.distinct() else names
    }

    fun draw(
        names: List<String>,
        count: Int,
        allowRepeats: Boolean,
        random: Random = Random.Default,
    ): DrawResult<String> {
        if (names.isEmpty()) return DrawResult.Failure(DrawError.EMPTY_POOL)
        if (count <= 0) return DrawResult.Failure(DrawError.COUNT_INVALID)
        if (count > DrawLimits.MAX_PICK) return DrawResult.Failure(DrawError.COUNT_TOO_LARGE)

        if (allowRepeats) {
            return DrawResult.Success(List(count) { names[random.nextInt(names.size)] })
        }

        if (count > names.size) return DrawResult.Failure(DrawError.COUNT_EXCEEDS_POOL)
        return DrawResult.Success(names.shuffled(random).take(count))
    }
}
