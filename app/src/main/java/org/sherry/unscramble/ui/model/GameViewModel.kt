package org.sherry.unscramble.ui.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.sherry.unscramble.data.MAX_NO_OF_WORDS
import org.sherry.unscramble.data.SCORE_INCREASE
import org.sherry.unscramble.data.allWords
import org.sherry.unscramble.ui.GameUiState

class GameViewModel(private val Words: Set<String> = allWords) : ViewModel(){
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private lateinit var currentWord: String
    private var usedWords: MutableSet<String> = mutableSetOf()
    var userGuess by mutableStateOf("")
        private set

    // Shuffle a word
    private fun shuffleCurrentWord(currentWord: String): String{
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while(String(tempWord) == currentWord){
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    // Choose word to Shuffle
    private fun pickRandomWordAndShuffle(allWords: Set<String> = Words): String{
        currentWord = allWords.random()
        return if(usedWords.contains(currentWord)){
            pickRandomWordAndShuffle()
        }else{
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
    fun updateUserGuess(guessed: String){
        userGuess = guessed
    }

    fun checkUserGuess(){
        if (userGuess.equals(currentWord, ignoreCase = true)){
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore = updatedScore)
        }else{
            _uiState.update { currentState -> currentState.copy(wrongGuess = true) }
        }
        updateUserGuess("")
    }

    private fun updateGameState(
        updatedScore: Int
    ) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update { currentState -> currentState.copy(
                wrongGuess = false,
                score = updatedScore,
                isGameOver = true
            ) }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    wrongGuess = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
    }

    fun skipWord(){
        updateGameState(_uiState.value.score)
        updateUserGuess("")
    }

    fun resetGame(){
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    init {
        resetGame()
    }

}