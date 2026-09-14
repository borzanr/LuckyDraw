package com.luckydraw.app.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * O histórico é gravado como JSON no DataStore. Se o formato mudar sem cuidado,
 * o histórico de quem já usa o app vira lixo — daí o round-trip explícito.
 */
class DrawRecordSerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `round-trip preserva todos os campos`() {
        val original = listOf(
            DrawRecord(DrawType.NUMBERS, listOf("42"), 1_700_000_000_000L, min = 1, max = 100),
            DrawRecord(DrawType.NAMES, listOf("Ana", "Bruno"), 1_700_000_001_000L, poolSize = 4),
        )

        val decoded = json.decodeFromString<List<DrawRecord>>(json.encodeToString(original))

        assertEquals(original, decoded)
    }

    @Test
    fun `campos desconhecidos de uma versao futura nao quebram a leitura`() {
        val raw = """[{"type":"NAMES","result":["Ana"],"timestamp":123,"poolSize":2,""" +
            """"futureField":"algo novo"}]"""

        val decoded = json.decodeFromString<List<DrawRecord>>(raw)

        assertEquals(1, decoded.size)
        assertEquals(listOf("Ana"), decoded.first().result)
    }

    @Test
    fun `registros da v1 continuam legiveis e caem no fallback de texto`() {
        // Formato gravado antes da v1.1: a frase já vinha traduzida em "input"
        // e não havia min/max/poolSize.
        val raw = """[{"type":"NAMES","input":"4 nomes","result":["Bruno"],"timestamp":123},""" +
            """{"type":"NUMBERS","input":"1 – 100","result":["66"],"timestamp":456}]"""

        val decoded = json.decodeFromString<List<DrawRecord>>(raw)

        assertEquals(2, decoded.size)
        assertEquals("4 nomes", decoded[0].legacyInput)
        assertNull(decoded[0].poolSize)
        assertEquals("1 – 100", decoded[1].legacyInput)
        assertNull(decoded[1].min)
        // O que importa para o usuário — o resultado — sobrevive intacto.
        assertEquals(listOf("Bruno"), decoded[0].result)
        assertEquals(listOf("66"), decoded[1].result)
    }

    @Test
    fun `sorteios novos nao gravam mais o texto pronto`() {
        val record = DrawRecord(DrawType.NUMBERS, listOf("7"), 1L, min = 1, max = 10)

        val encoded = json.encodeToString(listOf(record))

        assertTrue("não deveria gravar \"input\": $encoded", !encoded.contains("\"input\""))
        assertNull(record.legacyInput)
    }

    @Test
    fun `json corrompido lanca e o repositorio trata como historico vazio`() {
        val result = runCatching { json.decodeFromString<List<DrawRecord>>("{lixo}") }
        assertTrue(result.isFailure)
    }
}
