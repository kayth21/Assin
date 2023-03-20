package com.ceaver.assin.intentions.input

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ceaver.assin.intentions.IntentionType

class IntentionFragment : Fragment() {

    private lateinit var arguments: IntentionFragmentArgs
    private lateinit var viewModel: IntentionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments = IntentionFragmentArgs.fromBundle(requireArguments())
        viewModel = viewModels<IntentionViewModel> { IntentionViewModel.Factory(arguments.intention) }.value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView(requireContext()).apply {
            setContent {
                IntentionForm(viewModel, findNavController())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntentionForm(viewModel: IntentionViewModel, navController: NavController) {
    val uiState by viewModel.uiData.collectAsState()
    if (uiState.state == State.DONE) {
        navController.popBackStack()
    }
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(16.dp)
    ) {
        Switch(
            checked = uiState.active,
            modifier = Modifier.padding(8.dp),
            onCheckedChange = { viewModel.updateActive(it) }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = uiState.type == IntentionType.BUY,
                onClick = { viewModel.updateType(IntentionType.BUY) },
            )
            Text(
                text = "Buy",
                modifier = Modifier
                    .clickable(onClick = { viewModel.updateType(IntentionType.BUY) })
                    .padding(start = 2.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            RadioButton(
                selected = uiState.type == IntentionType.SELL,
                onClick = { viewModel.updateType(IntentionType.SELL) })
            Text(
                text = "Sell",
                modifier = Modifier
                    .clickable(onClick = { viewModel.updateType(IntentionType.SELL) })
                    .padding(start = 4.dp)
            )
        }
        OutlinedTextField(
            value = uiState.quantity,
            label = { Text("Quantity") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.updateQuantity(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = uiState.quantityErrors.isNotEmpty()
        )
        uiState.quantityErrors.forEach {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                color = Color.Red
            )
        }
        OutlinedTextField(
            value = uiState.baseString,
            label = { Text("Base Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.updateBaseTitle(it) },
            isError = uiState.baseTitleErrors.isNotEmpty()
        )
        uiState.baseTitleErrors.forEach {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                color = Color.Red
            )
        }
        OutlinedTextField(
            value = uiState.quoteString,
            label = { Text("Quote Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.updateQuoteTitle(it) },
            isError = uiState.quoteTitleErrors.isNotEmpty()
        )
        uiState.quoteTitleErrors.forEach {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                color = Color.Red
            )
        }
        OutlinedTextField(
            value = uiState.comment,
            label = { Text("Comment") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.updateComment(it) }
        )
        OutlinedTextField(
            value = uiState.target,
            label = { Text("Target") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { viewModel.updateTarget(it) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = uiState.targetErrors.isNotEmpty()
        )
        uiState.targetErrors.forEach {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = it,
                color = Color.Red
            )
        }
        Button(
            onClick = { viewModel.update() },
            enabled = uiState.state == State.READY && uiState.hasErrors().not()
        ) {
            Text(text = "Update")
        }
    }
}