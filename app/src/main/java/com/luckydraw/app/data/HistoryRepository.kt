package com.luckydraw.app.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Histórico local dos sorteios e contador usado pelo gate de anúncios.
 *
 * Persistência em DataStore Preferences, com a lista serializada em JSON —
 * o volume é pequeno (no máximo [MAX_RECORDS] registros) e não justifica um banco.
 */
class HistoryRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val history: Flow<List<DrawRecord>> = context.appDataStore.data.map { prefs ->
        val raw = prefs[HISTORY_KEY] ?: return@map emptyList()
        try {
            json.decodeFromString<List<DrawRecord>>(raw)
        } catch (e: Exception) {
            // Formato antigo ou corrompido: histórico é descartável, não vale crashar.
            Log.w(TAG, "Histórico ilegível, descartando", e)
            emptyList()
        }
    }

    suspend fun add(record: DrawRecord) {
        context.appDataStore.edit { prefs ->
            val current = prefs[HISTORY_KEY]?.let {
                runCatching { json.decodeFromString<List<DrawRecord>>(it) }.getOrDefault(emptyList())
            } ?: emptyList()
            val updated = (listOf(record) + current).take(MAX_RECORDS)
            prefs[HISTORY_KEY] = json.encodeToString(updated)
        }
    }

    suspend fun clear() {
        context.appDataStore.edit { it.remove(HISTORY_KEY) }
    }

    /** Incrementa o total de sorteios já feitos e devolve o novo valor. */
    suspend fun incrementDrawCount(): Int {
        var updated = 0
        context.appDataStore.edit { prefs ->
            updated = (prefs[DRAW_COUNT_KEY] ?: 0) + 1
            prefs[DRAW_COUNT_KEY] = updated
        }
        return updated
    }

    /** Total de sorteios já feitos neste aparelho. */
    suspend fun drawCount(): Int =
        context.appDataStore.data.first()[DRAW_COUNT_KEY] ?: 0

    /** True se o convite para avaliar o app já foi mostrado alguma vez. */
    suspend fun hasAskedForReview(): Boolean =
        context.appDataStore.data.first()[REVIEW_ASKED_KEY] ?: false

    suspend fun markReviewAsked() {
        context.appDataStore.edit { it[REVIEW_ASKED_KEY] = true }
    }

    companion object {
        private const val TAG = "HistoryRepository"
        const val MAX_RECORDS = 50

        /**
         * Sorteios antes de convidar para avaliar. Cedo demais atrapalha quem só
         * queria sortear uma vez; tarde demais e a pessoa já fechou o app.
         */
        const val DRAWS_BEFORE_REVIEW = 5

        private val HISTORY_KEY = stringPreferencesKey("history")
        private val DRAW_COUNT_KEY = intPreferencesKey("draw_count")
        private val REVIEW_ASKED_KEY = booleanPreferencesKey("review_asked")
    }
}
