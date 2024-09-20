package by.tigertosh.tictactoeandroid

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.tigertosh.tictactoeandroid.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var board = Array(3) { CharArray(3) { ' ' } }
    private var currentPlayer = 'X'
    private var gameActive = true

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttons: Array<MaterialButton>
    private var winCombination: List<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttons = arrayOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8
        )

        for (button in buttons) {
            button.setOnClickListener(this)
        }

        binding.resetButton.setOnClickListener {
            resetGame()
        }
    }

    override fun onClick(v: View?) {
        if (!gameActive) return

        val button = v as MaterialButton
        val tag = button.tag.toString().toInt()
        val row = tag / 3
        val col = tag % 3

        if (board[row][col] == ' ') {
            board[row][col] = currentPlayer
            button.text = currentPlayer.toString()
            button.isEnabled = false

            if (checkWin(currentPlayer)) {
                binding.statusTextView.text = "Игрок $currentPlayer выиграл!"
                gameActive = false
                highlightWinningCombination()
            } else if (isBoardFull()) {
                binding.statusTextView.text = "Ничья!"
                gameActive = false
            } else {
                currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
                binding.statusTextView.text = "Игрок $currentPlayer ходит"
            }
        }
    }

    private fun checkWin(player: Char): Boolean {
        for (i in 0..2) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                winCombination = listOf(i * 3, i * 3 + 1, i * 3 + 2)
                return true
            }
        }

        for (i in 0..2) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                winCombination = listOf(i, i + 3, i + 6)
                return true
            }
        }

        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            winCombination = listOf(0, 4, 8)
            return true
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            winCombination = listOf(2, 4, 6)
            return true
        }
        return false
    }

    private fun isBoardFull(): Boolean {
        for (row in board) {
            if (row.contains(' ')) return false
        }
        return true
    }

    private fun highlightWinningCombination() {
        winCombination?.let {
            for (index in it) {
                buttons[index].backgroundTintList = ColorStateList.valueOf(Color.RED)
                buttons[index].setTextColor(Color.WHITE)
            }
        }
    }

    private fun resetGame() {
        board = Array(3) { CharArray(3) { ' ' } }
        currentPlayer = 'X'
        gameActive = true
        binding.statusTextView.text = "Игрок $currentPlayer ходит"
        winCombination = null

        for (button in buttons) {
            button.text = ""
            button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
            button.setTextColor(Color.WHITE)
            button.isEnabled = true // Разблокируем кнопки
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharArray("boardRow0", board[0])
        outState.putCharArray("boardRow1", board[1])
        outState.putCharArray("boardRow2", board[2])
        outState.putChar("currentPlayer", currentPlayer)
        outState.putBoolean("gameActive", gameActive)
        outState.putIntegerArrayList("winCombination", winCombination?.let { ArrayList(it) })
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        board[0] = savedInstanceState.getCharArray("boardRow0")!!
        board[1] = savedInstanceState.getCharArray("boardRow1")!!
        board[2] = savedInstanceState.getCharArray("boardRow2")!!
        currentPlayer = savedInstanceState.getChar("currentPlayer")
        gameActive = savedInstanceState.getBoolean("gameActive")
        winCombination = savedInstanceState.getIntegerArrayList("winCombination")

        for (i in 0..8) {
            val row = i / 3
            val col = i % 3
            val button = buttons[i]
            button.text = if (board[row][col] != ' ') board[row][col].toString() else ""
            button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
            button.setTextColor(Color.WHITE)
            button.isEnabled = board[row][col] == ' ' && gameActive

            if (winCombination != null && winCombination!!.contains(i)) {
                button.backgroundTintList = ColorStateList.valueOf(Color.RED)
                button.setTextColor(Color.WHITE)
            }
        }

        binding.statusTextView.text = if (gameActive) {
            "Игрок $currentPlayer ходит"
        } else {
            if (winCombination != null) {
                "Игрок $currentPlayer выиграл!"
            } else {
                "Ничья!"
            }
        }
    }
}