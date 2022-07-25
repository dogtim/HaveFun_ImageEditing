package com.example.imageeditor

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.ArrayList


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun myTest() {
        Espresso.onView(ViewMatchers.withText(R.string.app_name)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

    }

    private fun convertEmoji(emoji: String): String {
        return try {
            val convertEmojiToInt = emoji.substring(2).toInt(16)
            String(Character.toChars(convertEmojiToInt))
        } catch (e: NumberFormatException) {
            ""
        }
    }

    lateinit var context: Context

    @Test
    fun checkIfEmojiIsDisplayedWhenEmojiIsSelected() {

        rule.scenario.onActivity {
            context = it
        }

        val emojiList = context.resources.getStringArray(R.array.photo_editor_emoji)
        val emojis = ArrayList<String>()
        for (emojiUnicode in emojiList) {
            emojis.add(convertEmoji(emojiUnicode))
        }
        val emojiPosition = 1
        val emojiUnicode = emojis[emojiPosition]

        Espresso.onView(ViewMatchers.withText(R.string.editor_shape)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.recycler_shape))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    emojiPosition,
                    ViewActions.click()
                )
            )
        Espresso.onView(ViewMatchers.withText(emojiUnicode)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )

    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.imageeditor", appContext.packageName)
    }
}