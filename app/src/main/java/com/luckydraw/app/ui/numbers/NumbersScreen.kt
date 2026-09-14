package com.luckydraw.app.ui.numbers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.luckydraw.app.R
import com.luckydraw.app.draw.DrawError
import com.luckydraw.app.ui.NumbersInput
import com.luckydraw.app.ui.components.DrawButton
import com.luckydraw.app.ui.components.LabeledCheckbox
import com.luckydraw.app.ui.components.NumberField
import com.luckydraw.app.ui.drawErrorText

@Composable
fun NumbersScreen(
    input: NumbersInput,
    error: DrawError?,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit,
    onCountChange: (String) -> Unit,
    onRepeatsChange: (Boolean) -> Unit,
    onDraw: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        // Só os campos rolam; o botão fica ancorado no rodapé, ao alcance do
        // polegar mesmo com o teclado aberto.
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                NumberField(
                    value = input.min,
                    onValueChange = onMinChange,
                    label = stringResource(R.string.label_min),
                    isError = error == DrawError.RANGE_INVERTED || error == DrawError.INVALID_NUMBER,
                    modifier = Modifier.weight(1f),
                )
                NumberField(
                    value = input.max,
                    onValueChange = onMaxChange,
                    label = stringResource(R.string.label_max),
                    isError = error == DrawError.RANGE_INVERTED || error == DrawError.INVALID_NUMBER,
                    modifier = Modifier.weight(1f),
                )
            }

            NumberField(
                value = input.count,
                onValueChange = onCountChange,
                label = stringResource(R.string.label_count),
                isError = error == DrawError.COUNT_INVALID ||
                    error == DrawError.COUNT_TOO_LARGE ||
                    error == DrawError.COUNT_EXCEEDS_POOL,
                modifier = Modifier.fillMaxWidth(),
            )

            LabeledCheckbox(
                checked = input.allowRepeats,
                onCheckedChange = onRepeatsChange,
                label = stringResource(R.string.allow_repeats),
            )

            if (error != null) {
                Text(
                    text = drawErrorText(error, forNames = false),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        DrawButton(
            text = stringResource(R.string.draw_button),
            onClick = onDraw,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
