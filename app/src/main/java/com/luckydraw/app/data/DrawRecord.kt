package com.luckydraw.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class DrawType { NUMBERS, NAMES }

/**
 * Um sorteio concluído, como salvo no histórico local.
 *
 * Os dados de entrada são guardados **crus** ([min]/[max] para números,
 * [poolSize] para nomes) e só viram texto na hora de exibir. Guardar a frase já
 * traduzida faria o histórico congelar no idioma em que cada sorteio foi feito.
 *
 * @param legacyInput frase pronta gravada pelas versões anteriores. Só é lido
 *   como fallback para registros antigos; sorteios novos deixam este campo nulo.
 */
@Serializable
data class DrawRecord(
    val type: DrawType,
    val result: List<String>,
    val timestamp: Long,
    val min: Int? = null,
    val max: Int? = null,
    val poolSize: Int? = null,
    // O nome no JSON continua "input": é assim que os registros antigos estão
    // gravados no DataStore de quem já usa o app.
    @SerialName("input")
    val legacyInput: String? = null,
)
