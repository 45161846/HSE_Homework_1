package com.example.hse_homework_1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.hse_homework_1.wrappers.DecimalFieldState
import com.example.hse_homework_1.wrappers.ScreenState
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewmodel by viewModels<MainViewModel>()

    private var medianInput: EditText? = null
    private var varianceInput: EditText? = null
    private var resultOutput: TextView? = null
    private var computeButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        medianInput = findViewById<EditText>(R.id.mean_val)
        varianceInput = findViewById<EditText>(R.id.variance_value)
        resultOutput = findViewById<TextView>(R.id.random_number_result)
        computeButton = findViewById<Button>(R.id.get_random_num)

        medianInput?.doAfterTextChanged { text ->
            viewmodel.updateMedian(text.toString())
        }

        varianceInput?.doAfterTextChanged { text ->
            viewmodel.updateVariance(text.toString())
        }

        computeButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                viewmodel.compute()
            }
        })

        lifecycleScope.launch {
            viewmodel.screen.collect { screenState ->

                when (screenState) {
                    is ScreenState.Default -> {
                        resultOutput?.setText(screenState.result)
                    }

                    is ScreenState.OnInput -> {
                        resultOutput?.setText(screenState.result)
                    }

                    is ScreenState.ComputedResult -> {
                        resultOutput?.setText(screenState.result)
                    }

                    is ScreenState.ErrorInput -> {
                        var messages = String()

                        screenState.median.let {
                            if (it is DecimalFieldState.ErrorType) {
                                messages += it.message + "\n"
                            }
                        }

                        screenState.variance.let {
                            if (it is DecimalFieldState.ErrorType) {
                                messages += it.message
                            }
                        }

                        resultOutput?.setText(messages)
                    }
                }

            }
        }

    }
}