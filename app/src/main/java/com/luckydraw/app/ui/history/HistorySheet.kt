package com.luckydraw.app.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.luckydraw.app.R
import com.luckydraw.app.data.DrawRecord
import com.luckydraw.app.data.DrawType
import com.luckydraw.app.data.HistoryRepository
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    records: List<DrawRecord>,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 8.dp),
        ) {
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            if (records.isNotEmpty()) {
                TextButton(onClick = onClear) {
                    Icon(Icons.Filled.DeleteOutline, contentDescription = null)
                    Text(
                        text = stringResource(R.string.history_clear),
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
            }
        }

        if (records.isEmpty()) {
            Text(
                text = stringResource(R.string.history_empty, HistoryRepository.MAX_RECORDS),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(20.dp),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp,
                ),
                modifier = Modifier.heightIn(max = 460.dp),
            ) {
                items(records) { record ->
                    HistoryRow(record)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(record: DrawRecord) {
    val typeLabel = stringResource(
        if (record.type == DrawType.NUMBERS) R.string.history_type_numbers
        else R.string.history_type_names
    )

    // Formatado na hora de exibir, e não na hora de sortear: assim o histórico
    // acompanha o idioma atual do aparelho. Registros gravados antes da v1.1 só
    // têm a frase pronta, então caem no fallback.
    val inputLabel = when {
        record.min != null && record.max != null ->
            stringResource(R.string.history_numbers_input, record.min, record.max)
        record.poolSize != null ->
            pluralStringResource(R.plurals.history_names_input, record.poolSize, record.poolSize)
        else -> record.legacyInput.orEmpty()
    }

    val timestamp = remember(record.timestamp) {
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            .format(Date(record.timestamp))
    }

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = record.result.joinToString(", "),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = "$typeLabel · $inputLabel · $timestamp",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
