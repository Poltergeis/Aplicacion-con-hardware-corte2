package com.softcode.mymagicapp.exchangefeature.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeEffect
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeFilterTab
import com.softcode.mymagicapp.exchangefeature.presentation.viewmodel.ExchangeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExchangeScreen(
    viewModel: ExchangeViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExchangeEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ExchangeEffect.ExchangeCreated -> Unit
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Filter tabs ──────────────────────────────────────────────
            ScrollableTabRow(
                selectedTabIndex = ExchangeFilterTab.entries.indexOf(state.filterTab),
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 8.dp
            ) {
                ExchangeFilterTab.entries.forEach { tab ->
                    Tab(
                        selected = state.filterTab == tab,
                        onClick = { viewModel.onFilterTabChanged(tab) },
                        text = { Text(tab.label) }
                    )
                }
            }

            // ── Refresh button ───────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { viewModel.onRefresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                }
            }

            // ── Content ──────────────────────────────────────────────────
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.filteredExchanges.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay intercambios",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.filteredExchanges,
                            key = { it.id }
                        ) { exchange ->
                            ExchangeItem(
                                exchange = exchange,
                                currentUserId = state.currentUserId,
                                onAccept = { viewModel.onAcceptExchange(exchange.id) },
                                onReject = { viewModel.onRejectExchange(exchange.id) }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }

        // ── FAB ──────────────────────────────────────────────────────────
        FloatingActionButton(
            onClick = { viewModel.onShowCreateDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Proponer intercambio")
        }
    }

    // ── Create Exchange Dialog ────────────────────────────────────────────
    if (state.showCreateDialog) {
        CreateExchangeDialog(
            state = state,
            onReceiverIdChanged = viewModel::onReceiverIdChanged,
            onReceiverCardIdChanged = viewModel::onReceiverCardIdChanged,
            onMyCardSelected = viewModel::onMyCardSelected,
            onConfirm = viewModel::onConfirmCreate,
            onDismiss = viewModel::onDismissCreateDialog
        )
    }
}

@Composable
private fun ExchangeItem(
    exchange: ExchangeEntity,
    currentUserId: Long,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val isSent = exchange.proposerId == currentUserId
    val isReceived = exchange.receiverId == currentUserId
    val isPending = exchange.status == ExchangeEntity.STATUS_PENDING

    val statusColor = when (exchange.status) {
        ExchangeEntity.STATUS_ACCEPTED -> MaterialTheme.colorScheme.primary
        ExchangeEntity.STATUS_REJECTED -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.tertiary
    }

    val statusText = when (exchange.status) {
        ExchangeEntity.STATUS_ACCEPTED -> "Aceptado"
        ExchangeEntity.STATUS_REJECTED -> "Rechazado"
        else -> "Pendiente"
    }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = statusColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelLarge,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = dateFormat.format(Date(exchange.createdAt)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Proposer info
            Text(
                text = if (isSent) "Tu ofreces:" else "De: ${exchange.proposerUsername} ofrece:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Carta: ${exchange.proposerCardTitle} (ID: ${exchange.proposerCardId})",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Receiver info
            Text(
                text = if (isReceived) "A cambio de tu:" else "A cambio de (${exchange.receiverUsername}):",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Carta: ${exchange.receiverCardTitle} (ID: ${exchange.receiverCardId})",
                style = MaterialTheme.typography.bodyMedium
            )

            // Action buttons for received pending exchanges
            if (isReceived && isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onReject) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Rechazar",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onAccept) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Aceptar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateExchangeDialog(
    state: com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeUIState,
    onReceiverIdChanged: (String) -> Unit,
    onReceiverCardIdChanged: (String) -> Unit,
    onMyCardSelected: (com.softcode.mymagicapp.core.domain.entities.CardEntity) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Proponer Intercambio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Receiver user ID
                OutlinedTextField(
                    value = state.receiverIdText,
                    onValueChange = onReceiverIdChanged,
                    label = { Text("ID del usuario receptor") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // My card selector (dropdown)
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = state.selectedMyCard?.title ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Mi carta a ofrecer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        state.myCards.forEach { card ->
                            DropdownMenuItem(
                                text = { Text("${card.title} (ID: ${card.id})") },
                                onClick = {
                                    onMyCardSelected(card)
                                    dropdownExpanded = false
                                }
                            )
                        }
                        if (state.myCards.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No tienes cartas") },
                                onClick = { dropdownExpanded = false },
                                enabled = false
                            )
                        }
                    }
                }

                // Receiver card ID
                OutlinedTextField(
                    value = state.receiverCardIdText,
                    onValueChange = onReceiverCardIdChanged,
                    label = { Text("ID de la carta del receptor") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Proponer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
