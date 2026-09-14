package com.luckydraw.app.ads

import com.google.android.gms.ads.MobileAds
import com.luckydraw.app.BuildConfig

/**
 * IDs de anúncio, centralizados para a troca de teste → produção ser de uma linha.
 *
 * Builds debug SEMPRE usam os IDs de teste oficiais do Google. Isso é proposital:
 * clicar nos próprios anúncios reais durante o desenvolvimento é a forma mais
 * comum de ter a conta AdMob banida.
 *
 * O APPLICATION_ID (com "~") fica no AndroidManifest.xml e é o mesmo em todos os
 * builds — o que separa teste de produção são os unit IDs (com "/") daqui.
 */
object AdIds {

    private const val TEST_BANNER = "ca-app-pub-3940256099942544/6300978111"
    private const val TEST_INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"

    // Blocos reais da conta AdMob. O prefixo bate com o APPLICATION_ID do manifest.
    private const val PROD_BANNER = "ca-app-pub-5043299631567488/2515734651"
    private const val PROD_INTERSTITIAL = "ca-app-pub-5043299631567488/9220743802"

    val banner: String get() = if (BuildConfig.DEBUG) TEST_BANNER else PROD_BANNER
    val interstitial: String get() = if (BuildConfig.DEBUG) TEST_INTERSTITIAL else PROD_INTERSTITIAL

    /** True enquanto algum ID de produção ainda apontar para o de teste. */
    val usingTestIds: Boolean
        get() = PROD_BANNER == TEST_BANNER || PROD_INTERSTITIAL == TEST_INTERSTITIAL

    /**
     * Aparelhos que sempre recebem anúncios de teste, mesmo num build de release
     * com os IDs reais.
     *
     * É o que torna seguro instalar o release no próprio celular e tocar nos
     * anúncios: o AdMob contabiliza como impressão de teste. Clicar no próprio
     * anúncio real é a causa mais comum de banimento de conta.
     *
     * O hash sai no logcat na primeira execução, numa linha como:
     * `Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("33BE..."))`
     */
    /**
     * ⚠️ Este hash deriva do identificador de publicidade, que MUDA sempre que o
     * usuário o redefine (ou quando o app é reinstalado limpo). Quando isso
     * acontece, o aparelho deixa de ser reconhecido aqui e passa a receber
     * anúncios reais — com risco de clique inválido.
     *
     * Por isso a fonte de verdade é o painel: **AdMob → Configurações →
     * Dispositivos de teste**, que vale na hora e não exige nova versão. Esta
     * lista é só uma segunda camada.
     */
    val TEST_DEVICE_IDS: List<String> = listOf(
        "8B3FEDD52B52AD340B8E016780B25AD0", // Redmi Note 9S — após reset do AAID
        "E3153D8858021F4D58DC5E9F4F11BDD9", // Redmi Note 9S — hash anterior
    )

    /** Aplica os aparelhos de teste ao SDK. Chamar antes de carregar anúncios. */
    fun applyTestDevices() {
        if (TEST_DEVICE_IDS.isEmpty()) return
        MobileAds.setRequestConfiguration(
            MobileAds.getRequestConfiguration().toBuilder()
                .setTestDeviceIds(TEST_DEVICE_IDS)
                .build()
        )
    }
}
