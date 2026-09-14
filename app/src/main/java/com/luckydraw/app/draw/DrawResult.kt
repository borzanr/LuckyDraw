package com.luckydraw.app.draw

/** Motivos pelos quais um sorteio não pode ser realizado. A UI traduz cada um. */
enum class DrawError {
    /** Mínimo maior que o máximo. */
    RANGE_INVERTED,

    /** Quantidade ausente, zero ou negativa. */
    COUNT_INVALID,

    /** Quantidade acima do teto suportado ([DrawLimits.MAX_PICK]). */
    COUNT_TOO_LARGE,

    /** Sem repetição, mas há menos candidatos do que a quantidade pedida. */
    COUNT_EXCEEDS_POOL,

    /** Campo numérico vazio ou não numérico. */
    INVALID_NUMBER,

    /** Nenhum nome válido foi informado. */
    EMPTY_POOL,
}

sealed interface DrawResult<out T> {
    data class Success<T>(val items: List<T>) : DrawResult<T>
    data class Failure(val error: DrawError) : DrawResult<Nothing>
}

object DrawLimits {
    /** Teto de itens sorteados de uma vez — mantém a UI e o clipboard utilizáveis. */
    const val MAX_PICK = 1_000

    /**
     * Acima deste tamanho de intervalo, materializar a lista inteira para
     * embaralhar custa memória demais; a engine passa a amostrar por rejeição.
     */
    const val SHUFFLE_THRESHOLD = 10_000L
}
