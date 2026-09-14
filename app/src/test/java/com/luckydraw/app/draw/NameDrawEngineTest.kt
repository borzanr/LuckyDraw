package com.luckydraw.app.draw

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NameDrawEngineTest {

    private fun success(result: DrawResult<String>): List<String> {
        assertTrue("esperava sucesso, veio $result", result is DrawResult.Success)
        return (result as DrawResult.Success).items
    }

    private fun failure(result: DrawResult<String>): DrawError {
        assertTrue("esperava falha, veio $result", result is DrawResult.Failure)
        return (result as DrawResult.Failure).error
    }

    @Test
    fun `quebra por linha`() {
        assertEquals(listOf("Ana", "Bruno", "Carla"), NameDrawEngine.parse("Ana\nBruno\nCarla"))
    }

    @Test
    fun `quebra por virgula`() {
        assertEquals(listOf("Ana", "Bruno", "Carla"), NameDrawEngine.parse("Ana, Bruno , Carla"))
    }

    @Test
    fun `mistura de separadores linhas em branco e virgula sobrando`() {
        val raw = "Ana, Bruno\n\n  Carla  ,\nDaniel;Eva,\n"
        assertEquals(listOf("Ana", "Bruno", "Carla", "Daniel", "Eva"), NameDrawEngine.parse(raw))
    }

    @Test
    fun `quebra de linha windows nao gera entradas vazias`() {
        assertEquals(listOf("Ana", "Bruno"), NameDrawEngine.parse("Ana\r\nBruno"))
    }

    @Test
    fun `texto vazio ou so separadores nao gera nomes`() {
        assertTrue(NameDrawEngine.parse("").isEmpty())
        assertTrue(NameDrawEngine.parse("  \n , ; \n ").isEmpty())
    }

    @Test
    fun `duplicados sao mantidos por padrao e removidos sob demanda`() {
        val raw = "Ana, Ana, Bruno"
        assertEquals(3, NameDrawEngine.parse(raw).size)
        assertEquals(listOf("Ana", "Bruno"), NameDrawEngine.parse(raw, dropDuplicates = true))
    }

    @Test
    fun `sorteio devolve nomes da lista sem repetir`() {
        val names = listOf("Ana", "Bruno", "Carla", "Daniel")
        val items = success(NameDrawEngine.draw(names, 3, allowRepeats = false))
        assertEquals(3, items.size)
        assertEquals(3, items.toSet().size)
        assertTrue(items.all { it in names })
    }

    @Test
    fun `com repeticao pode exceder o tamanho da lista`() {
        val items = success(NameDrawEngine.draw(listOf("Ana", "Bruno"), 5, allowRepeats = true))
        assertEquals(5, items.size)
    }

    @Test
    fun `lista vazia falha`() {
        assertEquals(DrawError.EMPTY_POOL, failure(NameDrawEngine.draw(emptyList(), 1, true)))
    }

    @Test
    fun `pedir mais nomes do que existem falha sem repeticao`() {
        assertEquals(
            DrawError.COUNT_EXCEEDS_POOL,
            failure(NameDrawEngine.draw(listOf("Ana", "Bruno"), 3, allowRepeats = false)),
        )
    }

    @Test
    fun `quantidade invalida falha`() {
        assertEquals(DrawError.COUNT_INVALID, failure(NameDrawEngine.draw(listOf("Ana"), 0, true)))
    }
}
