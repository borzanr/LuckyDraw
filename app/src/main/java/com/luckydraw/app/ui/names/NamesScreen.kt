package com.luckydraw.app.ui.names

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.luckydraw.app.R
import com.luckydraw.app.draw.DrawError
import com.luckydraw.app.ui.NamesInput
import com.luckydraw.app.ui.components.DrawButton
import com.luckydraw.app.ui.components.LabeledCheckbox
import com.luckydraw.app.ui.components.NumberField
import com.luckydraw.app.ui.drawErrorText
import com.luckydraw.app.ui.theme.Surface2

@Composable
fun NamesScreen(
    input: NamesInput,
    nameCount: Int,
    error: DrawError?,
    onTextChange: (String) -> Unit,
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
        // O campo tem altura mínima própria e a coluna rola: com o teclado
        // aberto ele encolheria a nada se disputasse espaço por peso.
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                value = input.text,
                onValueChange = onTextChange,
                label = { Text(stringResource(R.string.names_field_label)) },
                placeholder = { Text(stringResource(R.string.names_placeholder)) },
                isError = error == DrawError.EMPTY_POOL,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Surface2,
                    unfocusedContainerColor = Surface2,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp),
            )

            Text(
                text = pluralStringResource(R.plurals.names_detected, nameCount, nameCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumberField(
                    value = input.count,
                    onValueChange = onCountChange,
                    label = stringResource(R.string.label_count),
                    isError = error == DrawError.COUNT_INVALID ||
                        error == DrawError.COUNT_TOO_LARGE ||
                        error == DrawError.COUNT_EXCEEDS_POOL,
                    modifier = Modifier.width(130.dp),
                )
                LabeledCheckbox(
                    checked = input.allowRepeats,
                    onCheckedChange = onRepeatsChange,
                    label = stringResource(R.string.allow_repeats),
                )
            }

            if (error != null) {
                Text(
                    text = drawErrorText(error, forNames = true),
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
