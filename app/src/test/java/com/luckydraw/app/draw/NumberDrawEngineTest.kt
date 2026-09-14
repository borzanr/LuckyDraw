package com.luckydraw.app.draw

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class NumberDrawEngineTest {

    private fun success(result: DrawResult<Int>): List<Int> {
        assertTrue("esperava sucesso, veio $result", result is DrawResult.Success)
        return (result as DrawResult.Success).items
    }

    private fun failure(result: DrawResult<Int>): DrawError {
        assertTrue("esperava falha, veio $result", result is DrawResult.Failure)
        return (result as DrawResult.Failure).error
    }

    @Test
    fun `intervalo eh inclusivo nas duas pontas`() {
        val seen = mutableSetOf<Int>()
        repeat(500) {
            seen += success(NumberDrawEngine.draw(1, 3, 1, allowRepeats = true))
        }
        assertEquals(setOf(1, 2, 3), seen)
    }

    @Test
    fun `sem repeticao devolve valores distintos dentro da faixa`() {
        val items = success(NumberDrawEngine.draw(1, 100, 5, allowRepeats = false))
        assertEquals(5, items.size)
        assertEquals(5, items.toSet().size)
        assertTrue(items.all { it in 1..100 })
    }

    @Test
    fun `com repeticao pode repetir e sempre devolve a quantidade pedida`() {
        val items = success(NumberDrawEngine.draw(1, 2, 20, allowRepeats = true))
        assertEquals(20, items.size)
        assertTrue(items.all { it in 1..2 })
    }

    @Test
    fun `intervalo de um unico numero funciona`() {
        assertEquals(listOf(7), success(NumberDrawEngine.draw(7, 7, 1, allowRepeats = false)))
    }

    @Test
    fun `numeros negativos sao suportados`() {
        val items = success(NumberDrawEngine.draw(-10, -5, 6, allowRepeats = false))
        assertEquals((-10..-5).toSet(), items.toSet())
    }

    @Test
    fun `intervalo gigante nao materializa a lista inteira`() {
        // Se a engine usasse shuffled() aqui, o teste estouraria a memória.
        val items = success(NumberDrawEngine.draw(1, 1_000_000_000, 50, allowRepeats = false))
        assertEquals(50, items.size)
        assertEquals(50, items.toSet().size)
        assertTrue(items.all { it in 1..1_000_000_000 })
    }

    @Test
    fun `intervalo cobrindo todo o dominio de Int nao estoura`() {
        val items = success(
            NumberDrawEngine.draw(Int.MIN_VALUE, Int.MAX_VALUE, 10, allowRepeats = false)
        )
        assertEquals(10, items.size)
    }

    @Test
    fun `minimo maior que maximo falha`() {
        assertEquals(DrawError.RANGE_INVERTED, failure(NumberDrawEngine.draw(10, 1, 1, true)))
    }

    @Test
    fun `quantidade invalida falha`() {
        assertEquals(DrawError.COUNT_INVALID, failure(NumberDrawEngine.draw(1, 10, 0, true)))
        assertEquals(DrawError.COUNT_INVALID, failure(NumberDrawEngine.draw(1, 10, -3, true)))
    }

    @Test
    fun `quantidade acima do teto falha`() {
        val over = DrawLimits.MAX_PICK + 1
        assertEquals(DrawError.COUNT_TOO_LARGE, failure(NumberDrawEngine.draw(1, 100_000, over, true)))
    }

    @Test
    fun `quantidade maior que a faixa falha apenas sem repeticao`() {
        assertEquals(DrawError.COUNT_EXCEEDS_POOL, failure(NumberDrawEngine.draw(1, 3, 5, false)))
        assertEquals(5, success(NumberDrawEngine.draw(1, 3, 5, true)).size)
    }

    @Test
    fun `mesma semente produz o mesmo sorteio`() {
        val a = success(NumberDrawEngine.draw(1, 1000, 8, false, Random(42)))
        val b = success(NumberDrawEngine.draw(1, 1000, 8, false, Random(42)))
        assertEquals(a, b)
    }
}
