package com.digir.quizapp_mvc

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cheat.*

class CheatActivity : AppCompatActivity() {

    private var cheatPoints : Int = 0
    private var mAnswerIsTrue : Boolean = false

    companion object{
        private const val EXTRA_ANSWER_IS_TRUE : String = "com.digir.quizapp_mvc.answer_is_true"
        private const val EXTRA_ANSWER_SHOWN = "com.digir.quizapp_mvc.answer_shown"
        private const val GLOBAL_KEY = "globalKey"
        private const val KEY1 = "key1"
        private const val KEY2 = "key2"

        fun newIntent(packageContext: Context?, answerIsTrue: Boolean): Intent? {
            val intent = Intent(packageContext, CheatActivity::class.java)
            intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            return intent
        }
        fun wasAnswerShown(result: Intent) : Boolean{
            return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        checkApi()

        val appPrefs = getSharedPreferences(GLOBAL_KEY, MODE_PRIVATE)
        cheatPoints = appPrefs.getInt(KEY2, 0)

        cheat_checks_score.text = cheatPoints.toString()

        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        if(savedInstanceState != null) {
            mAnswerIsTrue = savedInstanceState.getBoolean(KEY1, false)
            useOnClick()
        }

        if(cheatPoints <= 0) {
            show_answer_button.isEnabled = false
        }
        show_answer_button.setOnClickListener {
            useOnClick()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val cx: Int = show_answer_button.width / 2
                val cy: Int = show_answer_button.height / 2
                val radius: Float = show_answer_button.width.toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(show_answer_button, cx, cy, radius, 0f)
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        show_answer_button.visibility = View.INVISIBLE
                    }
                })
                anim.start()
            } else {
                show_answer_button.visibility = View.INVISIBLE
            }
            cheatPoints--
            sharePreferences(cheatPoints)
            cheat_checks_score.text = cheatPoints.toString()
        }
    }
    private fun checkApi() {
        val version = Build.VERSION.SDK_INT
        which_api.text = ("API LVL $version")
    }
    private fun useOnClick() {
        if(mAnswerIsTrue) {
            answer_text_view.text = "Prawda"
        } else {
            answer_text_view.text = "Fałsz"
        }
        setAnswerShownResult(true)
    }
    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent()
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        setResult(RESULT_OK, data)
    }
    private fun sharePreferences(cP: Int) {
        val sharedPreferences = getSharedPreferences(GLOBAL_KEY, MODE_PRIVATE)
        val prefsEditor = sharedPreferences.edit()
        prefsEditor.putInt(KEY2, cP)
        prefsEditor.commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY1, mAnswerIsTrue)
    }

}