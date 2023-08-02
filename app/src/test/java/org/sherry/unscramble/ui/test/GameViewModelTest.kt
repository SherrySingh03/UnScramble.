package org.sherry.unscramble.ui.test

import org.junit.Test
import org.sherry.unscramble.ui.model.GameViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.sherry.unscramble.data.MAX_NO_OF_WORDS
import org.sherry.unscramble.data.SCORE_INCREASE
import org.sherry.unscramble.data.allWords
import org.sherry.unscramble.data.getUnscrambledWord


class GameViewModelTest {
    private val viewModel = GameViewModel(Words = allWords)

    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)

        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        assertFalse(null, currentGameUiState.wrongGuess)
        assertEquals(score_after_first_correct, currentGameUiState.score)
    }
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        val incorrectPlayerWord = "and"

        viewModel.updateUserGuess(incorrectPlayerWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value

        assertEquals(0, currentGameUiState.score)
        assertTrue(currentGameUiState.wrongGuess)
    }

    @Test
    fun gameViewModel_Initialization_FirstWordLoaded(){
        val gameUiState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(gameUiState.currentScrambledWord)

        assertNotEquals(unscrambledWord, gameUiState.currentScrambledWord)
        assertTrue(gameUiState.currentWordCount==1)
        assertTrue(gameUiState.score==0)
        assertFalse(gameUiState.wrongGuess)
        assertFalse(gameUiState.isGameOver)
    }
    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        var currentGameUiState = viewModel.uiState.value
        val correctPlayerWord = getUnscrambledWord(currentGameUiState.currentScrambledWord)
        viewModel.updateUserGuess(correctPlayerWord)
        viewModel.checkUserGuess()

        currentGameUiState = viewModel.uiState.value
        val lastWordCount = currentGameUiState.currentWordCount
        viewModel.skipWord()
        currentGameUiState = viewModel.uiState.value
        assertEquals(score_after_first_correct, currentGameUiState.score)
        assertEquals(lastWordCount + 1, currentGameUiState.currentWordCount)
    }

    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly(){
        var expectedScore = 0
        var gameUiState = viewModel.uiState.value
        var correctPlayerWord = getUnscrambledWord(gameUiState.currentScrambledWord)
        repeat(MAX_NO_OF_WORDS){
            expectedScore+= SCORE_INCREASE
            viewModel.updateUserGuess(correctPlayerWord)
            viewModel.checkUserGuess()
            gameUiState = viewModel.uiState.value
            correctPlayerWord = getUnscrambledWord(gameUiState.currentScrambledWord)
            assertEquals(expectedScore, gameUiState.score)
        }
        assertEquals(MAX_NO_OF_WORDS, gameUiState.currentWordCount)
        assertTrue(gameUiState.isGameOver)


    }

    companion object{
        private const val score_after_first_correct = SCORE_INCREASE
    }
}