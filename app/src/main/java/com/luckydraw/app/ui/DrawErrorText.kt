package com.luckydraw.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.luckydraw.app.R
import com.luckydraw.app.draw.DrawError
import com.luckydraw.app.draw.DrawLimits

/**
 * Traduz um [DrawError] para a mensagem exibida ao usuário.
 *
 * [forNames] distingue as duas mensagens de "não há candidatos suficientes",
 * que sugerem saídas diferentes em cada aba.
 */
@Composable
fun drawErrorText(error: DrawError, forNames: Boolean): String = when (error) {
    DrawError.RANGE_INVERTED -> stringResource(R.string.error_range_inverted)
    DrawError.COUNT_INVALID -> stringResource(R.string.error_count_invalid)
    DrawError.COUNT_TOO_LARGE -> stringResource(R.string.error_count_too_large, DrawLimits.MAX_PICK)
    DrawError.COUNT_EXCEEDS_POOL -> stringResource(
        if (forNames) R.string.error_count_exceeds_names else R.string.error_count_exceeds_range
    )
    DrawError.INVALID_NUMBER -> stringResource(R.string.error_invalid_number)
    DrawError.EMPTY_POOL -> stringResource(R.string.error_empty_pool)
}
