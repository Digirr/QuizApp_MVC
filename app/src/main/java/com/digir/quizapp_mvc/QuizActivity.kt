package com.digir.quizapp_mvc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlin.math.roundToInt
import androidx.core.content.ContextCompat.startActivity

class QuizActivity : AppCompatActivity() {

    private val mQuestionBank : ArrayList<Question> = ArrayList()
    private var mCurrentIndex = 0
    private val questWasDone : ArrayList<Boolean> = ArrayList()
    private var mIsCheater : Boolean = false
    private var score: Int = 0
    private var returnValue: String = ""

    companion object{
        private const val REQUEST_CODE_CHEAT : Int = 0
        private const val KEY_INDEX = "index"
        private const val KEY_INDEX2 = "index2"
        private const val GLOBAL_KEY = "globalKey"
        const val KEY2 = "key2"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val questionArray: Array<String> = applicationContext.resources.getStringArray(R.array.questions_array)
        val questionArrayAnswers: Array<String> = applicationContext.resources.getStringArray(R.array.questions_answers)

        for (i in questionArray.indices) {

            mQuestionBank.add(Question(questionArray[i], questionArrayAnswers[i].toBoolean()))
            questWasDone.add(false)
        }


        sharePreferences(3)

        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0)
            mIsCheater = savedInstanceState.getBoolean(KEY_INDEX2, false)
        }

        true_button.setOnClickListener {
            questWasDone[mCurrentIndex] = true
            checkAnswer(true)
            lockOrUnlock(false)
        }
        false_button.setOnClickListener {
            questWasDone[mCurrentIndex] = true
            checkAnswer(false)
            lockOrUnlock(false)
        }
        prev_button.setOnClickListener {
            if(mCurrentIndex != 0) {
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.size
                updateQuestion()
                if(questWasDone[mCurrentIndex])
                    lockOrUnlock(false)
                else
                    lockOrUnlock(true)
            }
        }
        next_button.setOnClickListener {
            if(mCurrentIndex == (mQuestionBank.size-1)) {
                val percent = (score * 100 / mQuestionBank.size).toFloat().roundToInt()
                Toast.makeText(this, "Wynik: $percent%", Toast.LENGTH_SHORT).show()
                score = 0
            } else {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.size
                mIsCheater = false
                updateQuestion()
                if(questWasDone[mCurrentIndex])
                    lockOrUnlock(false)
                else
                    lockOrUnlock(true)
            }

        }
        cheat_button.setOnClickListener {
            val answerIsTrue = mQuestionBank[mCurrentIndex].mAnswerTrueHere
            val intent = CheatActivity.newIntent(this, answerIsTrue)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }

        updateQuestion()
    }

    override fun onStart() {    //Zadanie na 5
        super.onStart()
        if(returnValue != "") {
            returnValue = (3 - returnValue.toInt()).toString()
            question_return.text = returnValue
        } else {
            question_return.text = 3.toString()
        }
    }

    private fun updateQuestion() {
        val question = mQuestionBank[mCurrentIndex].mTextResIdHere
        question_text_view.text = question
    }
    private fun checkAnswer(userPressedTrue: Boolean){
        var answerIsTrue = mQuestionBank[mCurrentIndex].mAnswerTrueHere
        var messageRes : String
        if(mIsCheater) {
            messageRes = "Oszukiwanie jest złe!"
        } else {
            if(userPressedTrue == answerIsTrue) {
                messageRes = "Poprawna odpowiedź!"
                score++
            } else {
                messageRes = "Niepoprawna odpowiedź!"
            }
        }
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }
    private fun lockOrUnlock(or: Boolean) {
        true_button.isEnabled = or
        false_button.isEnabled = or
    }
    private fun sharePreferences(cP: Int) {
        val sharedPreferences = getSharedPreferences(GLOBAL_KEY, MODE_PRIVATE)
        val prefsEditor = sharedPreferences.edit()
        prefsEditor.putInt(KEY2, cP)
        prefsEditor.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK)
            return
        else {
            returnValue = data?.getStringExtra("result").toString() //Zadanie na 5
        }

        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null)
                return
            mIsCheater = CheatActivity.wasAnswerShown(data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, mCurrentIndex)
        outState.putBoolean(KEY_INDEX2, mIsCheater)
    }
}