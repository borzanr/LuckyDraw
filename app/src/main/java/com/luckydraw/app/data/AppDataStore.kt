package com.luckydraw.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** Único DataStore do app: histórico e contador de sorteios (gate de anúncios). */
val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "luckydraw")
