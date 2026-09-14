package com.luckydraw.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.luckydraw.app.ads.AdIds
import com.luckydraw.app.ads.ConsentManager
import com.luckydraw.app.ads.InterstitialManager
import com.luckydraw.app.ui.LuckyDrawApp
import com.luckydraw.app.ui.theme.LuckyDrawTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // O app é sempre escuro, independente do tema do sistema: as barras
        // precisam ser forçadas a dark, senão a de navegação fica branca.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        // Consentimento primeiro: na Europa é proibido carregar anúncio antes de
        // perguntar. Fora de lá o callback vem quase imediatamente.
        ConsentManager.gather(this) {
            lifecycleScope.launch {
                // A inicialização do SDK faz I/O de disco: sai da main thread para
                // não atrasar o primeiro frame.
                withContext(Dispatchers.IO) { MobileAds.initialize(this@MainActivity) }
                AdIds.applyTestDevices()
                // Já o load do interstitial EXIGE a main thread (checkMainThread no SDK).
                InterstitialManager.preload(applicationContext)
            }
        }

        setContent {
            LuckyDrawTheme {
                LuckyDrawApp()
            }
        }
    }
}
