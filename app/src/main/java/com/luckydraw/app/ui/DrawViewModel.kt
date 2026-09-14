package com.luckydraw.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luckydraw.app.data.DrawRecord
import com.luckydraw.app.data.DrawType
import com.luckydraw.app.data.HistoryRepository
import com.luckydraw.app.draw.DrawError
import com.luckydraw.app.draw.DrawResult
import com.luckydraw.app.draw.NameDrawEngine
import com.luckydraw.app.draw.NumberDrawEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class NumbersInput(
    val min: String = "1",
    val max: String = "100",
    val count: String = "1",
    val allowRepeats: Boolean = false,
)

data class NamesInput(
    val text: String = "",
    val count: String = "1",
    val allowRepeats: Boolean = false,
)

/** Etapas do fluxo "clicou em sortear" → "viu o resultado". */
sealed interface DrawPhase {
    data object Idle : DrawPhase

    /** Animação de expectativa. [samples] alimenta o ticker que pisca na tela. */
    data class Suspense(val samples: List<String>) : DrawPhase

    /** Momento do interstitial. A UI decide exibir ou seguir direto. */
    data class AwaitingAd(val showAd: Boolean) : DrawPhase

    data class Revealed(val record: DrawRecord) : DrawPhase
}

data class DrawUiState(
    val numbers: NumbersInput = NumbersInput(),
    val names: NamesInput = NamesInput(),
    val numbersError: DrawError? = null,
    val namesError: DrawError? = null,
    val phase: DrawPhase = DrawPhase.Idle,
    val history: List<DrawRecord> = emptyList(),
    /** A UI deve disparar o convite de avaliação neste momento. */
    val shouldAskReview: Boolean = false,
)

class DrawViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = HistoryRepository(app)
    private val random = Random.Default

    private val _state = MutableStateFlow(DrawUiState())
    val state: StateFlow<DrawUiState> = _state.asStateFlow()

    /** Resultado já sorteado, aguardando o fim do suspense/anúncio para ser revelado. */
    private var pending: DrawRecord? = null

    /** Sorteios desta sessão — o primeiro nunca leva interstitial. */
    private var drawsThisSession = 0

    init {
        viewModelScope.launch {
            repository.history.collect { records ->
                _state.update { it.copy(history = records) }
            }
        }
    }

    // ---------------------------------------------------------------- entradas

    fun onMinChange(value: String) = updateNumbers { it.copy(min = value.filterNumeric()) }
    fun onMaxChange(value: String) = updateNumbers { it.copy(max = value.filterNumeric()) }
    fun onNumbersCountChange(value: String) = updateNumbers { it.copy(count = value.filterDigits()) }
    fun onNumbersRepeatsChange(allow: Boolean) = updateNumbers { it.copy(allowRepeats = allow) }

    fun onNamesTextChange(value: String) = updateNames { it.copy(text = value) }
    fun onNamesCountChange(value: String) = updateNames { it.copy(count = value.filterDigits()) }
    fun onNamesRepeatsChange(allow: Boolean) = updateNames { it.copy(allowRepeats = allow) }

    private fun updateNumbers(transform: (NumbersInput) -> NumbersInput) =
        _state.update { it.copy(numbers = transform(it.numbers), numbersError = null) }

    private fun updateNames(transform: (NamesInput) -> NamesInput) =
        _state.update { it.copy(names = transform(it.names), namesError = null) }

    /** Aceita dígitos e um sinal de menos inicial (intervalos podem ser negativos). */
    private fun String.filterNumeric(): String {
        val negative = startsWith("-")
        val digits = filter { it.isDigit() }.take(10)
        return if (negative) "-$digits" else digits
    }

    private fun String.filterDigits(): String = filter { it.isDigit() }.take(4)

    /** Quantos nomes o texto atual contém — exibido ao vivo na aba Nomes. */
    fun parsedNames(): List<String> = NameDrawEngine.parse(_state.value.names.text)

    // ---------------------------------------------------------------- sorteios

    fun drawNumbers() {
        if (_state.value.phase != DrawPhase.Idle) return
        val input = _state.value.numbers

        val min = input.min.toIntOrNull()
        val max = input.max.toIntOrNull()
        val count = input.count.toIntOrNull()
        if (min == null || max == null) {
            _state.update { it.copy(numbersError = DrawError.INVALID_NUMBER) }
            return
        }
        if (count == null) {
            _state.update { it.copy(numbersError = DrawError.COUNT_INVALID) }
            return
        }

        when (val result = NumberDrawEngine.draw(min, max, count, input.allowRepeats, random)) {
            is DrawResult.Failure -> _state.update { it.copy(numbersError = result.error) }
            is DrawResult.Success -> {
                val samples = List(SAMPLE_COUNT) {
                    (min.toLong() + random.nextLong(max.toLong() - min.toLong() + 1L)).toString()
                }
                start(
                    record = DrawRecord(
                        type = DrawType.NUMBERS,
                        result = result.items.map(Int::toString),
                        timestamp = System.currentTimeMillis(),
                        min = min,
                        max = max,
                    ),
                    samples = samples,
                )
            }
        }
    }

    fun drawNames() {
        if (_state.value.phase != DrawPhase.Idle) return
        val input = _state.value.names

        val names = NameDrawEngine.parse(input.text)
        val count = input.count.toIntOrNull()
        if (count == null) {
            _state.update { it.copy(namesError = DrawError.COUNT_INVALID) }
            return
        }

        when (val result = NameDrawEngine.draw(names, count, input.allowRepeats, random)) {
            is DrawResult.Failure -> _state.update { it.copy(namesError = result.error) }
            is DrawResult.Success -> {
                val samples = if (names.size >= SAMPLE_COUNT) {
                    names.shuffled(random).take(SAMPLE_COUNT)
                } else {
                    List(SAMPLE_COUNT) { names[random.nextInt(names.size)] }
                }
                start(
                    record = DrawRecord(
                        type = DrawType.NAMES,
                        result = result.items,
                        timestamp = System.currentTimeMillis(),
                        poolSize = names.size,
                    ),
                    samples = samples,
                )
            }
        }
    }

    /**
     * Repete o sorteio da mesma aba que produziu o resultado na tela — é o que o
     * botão "Sortear de novo" promete.
     */
    fun drawAgain() {
        val type = (_state.value.phase as? DrawPhase.Revealed)?.record?.type ?: return
        dismissResult()
        when (type) {
            DrawType.NUMBERS -> drawNumbers()
            DrawType.NAMES -> drawNames()
        }
    }

    private fun start(record: DrawRecord, samples: List<String>) {
        // O resultado é decidido AGORA: a animação e o anúncio não influenciam nada.
        pending = record
        _state.update { it.copy(phase = DrawPhase.Suspense(samples)) }

        viewModelScope.launch {
            delay(SUSPENSE_MILLIS)
            drawsThisSession++
            val total = repository.incrementDrawCount()
            val showAd = drawsThisSession > 1 && total % AD_EVERY_N_DRAWS == 0
            _state.update { it.copy(phase = DrawPhase.AwaitingAd(showAd)) }
        }
    }

    /** Chamado pela UI quando o anúncio foi fechado (ou dispensado). */
    fun onAdFlowFinished() {
        val record = pending ?: return
        pending = null
        _state.update { it.copy(phase = DrawPhase.Revealed(record)) }
        viewModelScope.launch {
            repository.add(record)
            maybeOfferReview()
        }
    }

    /**
     * Convida a avaliar depois de alguns sorteios — nunca logo de cara, e uma vez
     * só. Quem abriu o app para resolver uma coisa e foi interrompido por um pedido
     * de nota tende a dar nota baixa, não alta.
     */
    private suspend fun maybeOfferReview() {
        if (repository.hasAskedForReview()) return
        if (repository.drawCount() < HistoryRepository.DRAWS_BEFORE_REVIEW) return
        _state.update { it.copy(shouldAskReview = true) }
    }

    /** A UI avisa que já exibiu (ou tentou exibir) o convite. */
    fun onReviewAsked() {
        _state.update { it.copy(shouldAskReview = false) }
        viewModelScope.launch { repository.markReviewAsked() }
    }

    fun dismissResult() {
        pending = null
        _state.update { it.copy(phase = DrawPhase.Idle) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clear() }
    }

    companion object {
        const val SUSPENSE_MILLIS = 1_500L
        private const val AD_EVERY_N_DRAWS = 3
        private const val SAMPLE_COUNT = 24
    }
}
