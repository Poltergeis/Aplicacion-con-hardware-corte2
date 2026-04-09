package com.softcode.mymagicapp.comparefeature.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.softcode.mymagicapp.comparefeature.presentation.ui.CompareEffect
import com.softcode.mymagicapp.comparefeature.presentation.viewmodel.CompareViewModel
import com.softcode.mymagicapp.core.domain.entities.CardEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompareScreen(
    viewModel: CompareViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CompareEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.selectedCards.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Comparador de Cartas",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Agrega cartas para comparar sus atributos",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            !state.canCompare -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Selecciona al menos 2 cartas para comparar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SelectedCardsRow(
                        cards = state.selectedCards,
                        onRemove = viewModel::onRemoveCardFromCompare
                    )
                }
            }
            else -> {
                CompareContent(
                    cards = state.selectedCards,
                    onRemove = viewModel::onRemoveCardFromCompare,
                    onClearAll = viewModel::onClearAll
                )
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { viewModel.onShowSelectDialog() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar carta")
        }
    }

    // Select card dialog
    if (state.showSelectDialog) {
        SelectCardDialog(
            cards = state.availableCards,
            searchQuery = state.searchQuery,
            onSearchChanged = viewModel::onSearchQueryChanged,
            onSelect = viewModel::onAddCardToCompare,
            onDismiss = viewModel::onDismissSelectDialog
        )
    }
}

@Composable
private fun SelectedCardsRow(
    cards: List<CardEntity>,
    onRemove: (CardEntity) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = cards, key = { it.id }) { card ->
            SelectedCardChip(card = card, onRemove = { onRemove(card) })
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SelectedCardChip(
    card: CardEntity,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
        ) {
            if (card.imageUrl.isNotBlank()) {
                GlideImage(
                    model = card.imageUrl,
                    contentDescription = card.title,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = card.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Quitar",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CompareContent(
    cards: List<CardEntity>,
    onRemove: (CardEntity) -> Unit,
    onClearAll: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        // Header row: clear all button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Comparando ${cards.size} cartas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClearAll) {
                Icon(Icons.Default.Delete, contentDescription = "Limpiar todo")
            }
        }

        // Comparison table
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Card headers with images
            item {
                CompareRow(label = "Carta") {
                    cards.forEach { card ->
                        CompareCardHeader(card = card, onRemove = { onRemove(card) })
                    }
                }
                HorizontalDivider()
            }

            // Title
            item {
                CompareRow(label = "Titulo") {
                    cards.forEach { card ->
                        CompareCell(value = card.title)
                    }
                }
                HorizontalDivider()
            }

            // Description
            item {
                CompareRow(label = "Descripcion") {
                    cards.forEach { card ->
                        CompareCell(value = card.description)
                    }
                }
                HorizontalDivider()
            }

            // Created at
            item {
                CompareRow(label = "Creada") {
                    cards.forEach { card ->
                        CompareCell(value = dateFormat.format(Date(card.createdAt)))
                    }
                }
                HorizontalDivider()
            }

            // ID
            item {
                CompareRow(label = "ID") {
                    cards.forEach { card ->
                        CompareCell(value = card.id.toString())
                    }
                }
                HorizontalDivider()
            }

            // Description length
            item {
                CompareRow(label = "Largo desc.") {
                    val maxLen = cards.maxOfOrNull { it.description.length } ?: 0
                    cards.forEach { card ->
                        val len = card.description.length
                        val isBest = len == maxLen && maxLen > 0
                        CompareCell(
                            value = "$len chars",
                            highlight = isBest
                        )
                    }
                }
                HorizontalDivider()
            }

            // Has image
            item {
                CompareRow(label = "Imagen") {
                    cards.forEach { card ->
                        val hasImage = card.imageUrl.isNotBlank()
                        CompareCell(
                            value = if (hasImage) "Si" else "No",
                            highlight = hasImage
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CompareRow(
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun CompareCardHeader(
    card: CardEntity,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier.width(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (card.imageUrl.isNotBlank()) {
            GlideImage(
                model = card.imageUrl,
                contentDescription = card.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(8.dp)
                    ),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin imagen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = card.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Quitar",
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun CompareCell(
    value: String,
    highlight: Boolean = false
) {
    Text(
        text = value,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.width(120.dp),
        maxLines = 4,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
        color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SelectCardDialog(
    cards: List<CardEntity>,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onSelect: (CardEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar carta") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChanged,
                    label = { Text("Buscar carta...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (cards.isEmpty()) {
                    Text(
                        text = "No hay cartas disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = cards, key = { it.id }) { card ->
                            Card(
                                onClick = { onSelect(card) },
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (card.imageUrl.isNotBlank()) {
                                        GlideImage(
                                            model = card.imageUrl,
                                            contentDescription = card.title,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(6.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = card.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = card.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
