/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.cmu.charades.screens.game

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.getSystemService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import edu.cmu.charades.R
import edu.cmu.charades.databinding.GameFragmentBinding

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    // added GameViewModel
    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.game_fragment,
            container,
            false
        )

        //Get the viewModel
        Log.i("GameFragement", "Called ViewModel Provider")
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // Set the viewmodel for databinding - this allows the bound layout access to all of the
        // data in the VieWModel
        binding.gameViewModel = viewModel
        binding.setLifecycleOwner(this)

        /*** move to layout
        binding.correctButton.setOnClickListener {
            viewModel.onCorrect()
           //updateScoreText()
            //updateWordText()
        }
        binding.skipButton.setOnClickListener {
            viewModel.onSkip()
           // updateScoreText()
           // updateWordText()
        }
        ***/
        /** Methods for updating the UI  --> move to Layout xml
        viewModel.score.observe(this, Observer { newScore ->
            binding.scoreText.text = newScore.toString()
        })
        /** Methods for updating the UI **/
        viewModel.word.observe(this, Observer { newWord ->
            binding.wordText.text = newWord
        })
        **/

        viewModel.eventGameFinish.observe(this, Observer{ hasFinished ->
            if(hasFinished) {
                val currentScore = viewModel.score.value ?: 0
                val action = GameFragmentDirections.actionGameToScore(currentScore)

                findNavController(this).navigate(action)
                viewModel.onGameFinishComplete()
                //gameFinished()
            }
        })
        /*** move to layout XML
        viewModel.currentTime.observe(this, Observer { newTime ->
            binding.timerText.text = DateUtils.formatElapsedTime(newTime)
        })
        ***/

        // Buzzes when triggered with different buzz events
        viewModel.eventBuzz.observe(viewLifecycleOwner, Observer { buzzType ->
            if (buzzType != GameViewModel.BuzzType.NO_BUZZ) {
                buzz(buzzType.pattern)
                viewModel.onBuzzComplete()
            }
        })


       // updateScoreText()
       // updateWordText()
        return binding.root

    }


    /**
     * Called when the game is finished
     */
    private fun gameFinished() {

            // val currentScore = viewModel.score.value ?: 0
            // val action = GameFragmentDirections.actionGameToScore(currentScore)
            //action.setScore(viewModel.score)
            // findNavController(this).navigate(action)
            Toast.makeText(this.activity, "Game has finished", Toast.LENGTH_SHORT).show()
    }

    /** Methods for updating the UI **/
    /**
    private fun updateWordText() {
    binding.wordText.text = viewModel.word

    }

    private fun updateScoreText() {
    binding.scoreText.text = viewModel.score.toString()
    }
     **/

    private fun buzz(pattern: LongArray) {
        val buzzer = activity?.getSystemService<Vibrator>()

        buzzer?.let {
            //vibrate for 500ms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                //deprecated in API 26
                buzzer.vibrate(pattern, -1)
            }
        }
    }
}
