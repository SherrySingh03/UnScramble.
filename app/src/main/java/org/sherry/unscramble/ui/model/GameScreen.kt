package org.sherry.unscramble.ui.model

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sherry.unscramble.R
import org.sherry.unscramble.ui.theme.Shapes

@Composable
fun GameStatus(score: Int, modifier: Modifier = Modifier){
    Card(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.score, score),
            style = typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameLayout(
    onUserGuessChanged: (String)->Unit,
    onKeyboardDone: () -> Unit,
    userGuess: String,
    isGuessWrong: Boolean,
    wordCount: Int,
    currentScrambledWord: String,

    modifier: Modifier = Modifier){
    val mediumPadding = dimensionResource(id = R.dimen.padding_medium)

    Card(modifier = modifier,
        elevation = CardDefaults.cardElevation(5.dp)) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            Text(
                modifier = Modifier
                    .clip(Shapes.medium)
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.End),
            text = stringResource(id = R.string.word_count, wordCount),
            style = typography.titleMedium,
            color = colorScheme.onPrimary
            )
            Text(
                text = currentScrambledWord,
                style = typography.displayMedium
            )
            Text(
                text = stringResource(id = R.string.instructions),
                textAlign = TextAlign.Center,
                style = typography.titleMedium
            )
            OutlinedTextField(
                value = userGuess,
                singleLine = true,
                shape = Shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(containerColor = colorScheme.surface),
                onValueChange = onUserGuessChanged,
                label = {
                    if (isGuessWrong) {
                        Text(stringResource(id = R.string.wrong_guess))
                    } else {
                        Text(stringResource(id = R.string.enter_your_word))
                    }
                },
                isError = isGuessWrong,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {onKeyboardDone()})
            )

        }

    }
}

@Composable
fun GameScreen(
    gameViewModel1: GameViewModel = viewModel()
){
    val mediumPadding = dimensionResource(id = R.dimen.padding_medium)

    val gameUiState by gameViewModel1.uiState.collectAsState()
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = typography.titleLarge
        )
        GameLayout(
            onUserGuessChanged = {gameViewModel1.updateUserGuess(it)},
            onKeyboardDone = { gameViewModel1.checkUserGuess() },
            userGuess = gameViewModel1.userGuess,
            isGuessWrong = gameUiState.wrongGuess,
            wordCount = gameUiState.currentWordCount,
            currentScrambledWord = gameUiState.currentScrambledWord,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(mediumPadding),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(mediumPadding),
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(modifier = Modifier.fillMaxWidth(),
                    onClick = {gameViewModel1.checkUserGuess()}
            ) {
                Text(
                    text = stringResource(id = R.string.submit),
                    fontSize = 16.sp
                )
            }
            OutlinedButton(onClick = { gameViewModel1.skipWord() },
                            modifier = Modifier.fillMaxWidth()){
                Text(
                    text = stringResource(id = R.string.skip),
                    fontSize = 16.sp)
            }
        }
        GameStatus(score = gameUiState.score, modifier = Modifier.padding(20.dp))
        if(gameUiState.isGameOver){
            FinalScoreDialog(score = gameUiState.score, onPlayAgain = { gameViewModel1.resetGame() })
        }
    }
}

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
){
    val activity = (LocalContext.current as Activity)

    AlertDialog(onDismissRequest = {},
    title = { Text(text = stringResource(id = R.string.congratulations))},
    text = { Text(text = stringResource(id = R.string.you_scored, score))},
    modifier = modifier,
    dismissButton = {
        TextButton(
            onClick = {activity.finish()}
        ) {
            Text(text = stringResource(id = R.string.exit))
        }
    },
    confirmButton = {
        TextButton(onClick = onPlayAgain) {
            Text(text = stringResource(id = R.string.play_again))
            }
        }
    )
}