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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.softcode.mymagicapp.core.domain.entities.CardEntity
import com.softcode.mymagicapp.core.domain.entities.ExchangeEntity
import com.softcode.mymagicapp.core.network.UserModel
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeEffect
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeFilterTab
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeStep
import com.softcode.mymagicapp.exchangefeature.presentation.ui.ExchangeUIState
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

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ExchangeEffect.ShowMessage ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
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

            when {
                state.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                state.filteredExchanges.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay intercambios",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.filteredExchanges, key = { it.id }) { exchange ->
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

    if (state.showCreateDialog) {
        CreateExchangeDialog(
            state = state,
            onUserSearchChanged = viewModel::onUserSearchChanged,
            onUserSelected = viewModel::onUserSelected,
            onMyCardSearchChanged = viewModel::onMyCardSearchChanged,
            onMyCardSelected = viewModel::onMyCardSelected,
            onReceiverCardSelected = viewModel::onReceiverCardSelected,
            onConfirm = viewModel::onConfirmCreate,
            onBack = viewModel::onStepBack,
            onDismiss = viewModel::onDismissCreateDialog
        )
    }
}

// ── Create Exchange Dialog (3-step stepper) ──────────────────────────────────

@Composable
private fun CreateExchangeDialog(
    state: ExchangeUIState,
    onUserSearchChanged: (String) -> Unit,
    onUserSelected: (UserModel) -> Unit,
    onMyCardSearchChanged: (String) -> Unit,
    onMyCardSelected: (CardEntity) -> Unit,
    onReceiverCardSelected: (CardEntity) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit
) {
    val stepIndex = state.createStep.ordinal
    val stepTitle = when (state.createStep) {
        ExchangeStep.SELECT_USER -> "Paso 1 / 3 — Seleccionar usuario"
        ExchangeStep.SELECT_MY_CARD -> "Paso 2 / 3 — Tu carta a ofrecer"
        ExchangeStep.SELECT_RECEIVER_CARD -> "Paso 3 / 3 — Carta del receptor"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.createStep != ExchangeStep.SELECT_USER) {
                        IconButton(onClick = onBack, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = stepTitle, style = MaterialTheme.typography.titleSmall)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (stepIndex + 1) / 3f },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        text = {
            when (state.createStep) {
                ExchangeStep.SELECT_USER -> StepSelectUser(
                    state = state,
                    onSearchChanged = onUserSearchChanged,
                    onUserSelected = onUserSelected
                )
                ExchangeStep.SELECT_MY_CARD -> StepSelectMyCard(
                    state = state,
                    onSearchChanged = onMyCardSearchChanged,
                    onCardSelected = onMyCardSelected
                )
                ExchangeStep.SELECT_RECEIVER_CARD -> StepSelectReceiverCard(
                    state = state,
                    onCardSelected = onReceiverCardSelected
                )
            }
        },
        confirmButton = {
            if (state.createStep == ExchangeStep.SELECT_RECEIVER_CARD) {
                Button(
                    onClick = onConfirm,
                    enabled = state.selectedReceiverCard != null
                ) { Text("Proponer") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
private fun StepSelectUser(
    state: ExchangeUIState,
    onSearchChanged: (String) -> Unit,
    onUserSelected: (UserModel) -> Unit
) {
    Column {
        OutlinedTextField(
            value = state.userSearchQuery,
            onValueChange = onSearchChanged,
            label = { Text("Buscar usuario...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        when {
            state.isLoadingUsers -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.filteredUsers.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) { Text("No se encontraron usuarios", color = MaterialTheme.colorScheme.onSurfaceVariant) }

            else -> LazyColumn(modifier = Modifier.height(200.dp)) {
                items(state.filteredUsers, key = { it.id }) { user ->
                    Card(
                        onClick = { onUserSelected(user) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Text(
                            text = user.username,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun StepSelectMyCard(
    state: ExchangeUIState,
    onSearchChanged: (String) -> Unit,
    onCardSelected: (CardEntity) -> Unit
) {
    Column {
        state.selectedUser?.let {
            Text(
                text = "Ofreciendo a: ${it.username}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        OutlinedTextField(
            value = state.myCardSearchQuery,
            onValueChange = onSearchChanged,
            label = { Text("Buscar mi carta...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (state.filteredMyCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) { Text("No tienes cartas disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        } else {
            LazyColumn(modifier = Modifier.height(200.dp)) {
                items(state.filteredMyCards, key = { it.id }) { card ->
                    CardPickerItem(card = card, onClick = { onCardSelected(card) })
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun StepSelectReceiverCard(
    state: ExchangeUIState,
    onCardSelected: (CardEntity) -> Unit
) {
    Column {
        state.selectedMyCard?.let {
            Text(
                text = "Tu carta: ${it.title}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = "Cartas de ${state.selectedUser?.username ?: ""}:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        when {
            state.isLoadingReceiverCards -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            state.receiverCards.isEmpty() -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) { Text("El usuario no tiene cartas", color = MaterialTheme.colorScheme.onSurfaceVariant) }

            else -> LazyColumn(modifier = Modifier.height(200.dp)) {
                items(state.receiverCards, key = { it.id }) { card ->
                    val isSelected = state.selectedReceiverCard?.id == card.id
                    CardPickerItem(
                        card = card,
                        isSelected = isSelected,
                        onClick = { onCardSelected(card) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CardPickerItem(
    card: CardEntity,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                             else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (card.imageUrl.isNotBlank()) {
                GlideImage(
                    model = card.imageUrl,
                    contentDescription = card.title,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "POW ${card.power} · DEF ${card.defense} · ${"★".repeat(card.rarity)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Seleccionada",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ── Exchange Item ────────────────────────────────────────────────────────────

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

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = statusColor)
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

            Text(
                text = if (isSent) "Tu ofreces:" else "De: ${exchange.proposerUsername} ofrece:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = exchange.proposerCardTitle,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isReceived) "A cambio de tu:" else "Pide a ${exchange.receiverUsername}:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = exchange.receiverCardTitle,
                style = MaterialTheme.typography.bodyMedium
            )

            if (isReceived && isPending) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onReject) {
                        Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = MaterialTheme.colorScheme.error)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onAccept) {
                        Icon(Icons.Default.Check, contentDescription = "Aceptar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
