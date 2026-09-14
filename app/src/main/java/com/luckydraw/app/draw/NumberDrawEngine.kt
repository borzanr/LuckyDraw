package com.luckydraw.app.draw

import kotlin.random.Random

/**
 * Sorteio de números num intervalo fechado [min, max].
 *
 * Lógica pura, sem dependência de Android, para poder ser testada em JVM.
 */
object NumberDrawEngine {

    fun draw(
        min: Int,
        max: Int,
        count: Int,
        allowRepeats: Boolean,
        random: Random = Random.Default,
    ): DrawResult<Int> {
        if (min > max) return DrawResult.Failure(DrawError.RANGE_INVERTED)
        if (count <= 0) return DrawResult.Failure(DrawError.COUNT_INVALID)
        if (count > DrawLimits.MAX_PICK) return DrawResult.Failure(DrawError.COUNT_TOO_LARGE)

        // Long evita overflow quando o intervalo se aproxima dos limites de Int.
        val poolSize = max.toLong() - min.toLong() + 1L

        if (allowRepeats) {
            val items = List(count) { (min + random.nextLong(poolSize)).toInt() }
            return DrawResult.Success(items)
        }

        if (count > poolSize) return DrawResult.Failure(DrawError.COUNT_EXCEEDS_POOL)

        // Intervalo pequeno: embaralhar é simples e dá distribuição perfeita.
        if (poolSize <= DrawLimits.SHUFFLE_THRESHOLD) {
            return DrawResult.Success((min..max).shuffled(random).take(count))
        }

        // Intervalo grande: amostragem por rejeição. Como count <= MAX_PICK e o
        // pool tem mais de 10.000 itens, a chance de colisão é baixa e não há
        // risco de materializar bilhões de números na memória.
        val picked = LinkedHashSet<Int>(count)
        while (picked.size < count) {
            picked.add((min + random.nextLong(poolSize)).toInt())
        }
        return DrawResult.Success(picked.toList())
    }
}
