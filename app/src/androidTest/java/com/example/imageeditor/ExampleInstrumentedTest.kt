package com.example.imageeditor

import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.imageeditor.fragment.ShapeFragment
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    lateinit var activity: MainActivity
    val timeSleep: Long = 500

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

    @Test
    fun checkIfEmojiIsDisplayedWhenEmojiIsSelected() {

        rule.scenario.onActivity {
            activity = it
        }
        rule.scenario.moveToState(Lifecycle.State.CREATED)
        rule.scenario.moveToState(Lifecycle.State.STARTED)
        rule.scenario.moveToState(Lifecycle.State.RESUMED)
        val emojis = ShapeFragment.getEmojis(activity)
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

        testClearAndRestore()
    }

    private fun testClearAndRestore() {
        var firstEmojiStickerFrameBorder = activity.findViewById<FrameLayout>(R.id.frmBorder)
        firstEmojiStickerFrameBorder.x = 0f
        sleep(timeSleep)

        Espresso.onView(ViewMatchers.withId(R.id.image_save_status)).perform(ViewActions.click())
        sleep(timeSleep)

        Espresso.onView(ViewMatchers.withId(R.id.image_clear)).perform(ViewActions.click())
        sleep(timeSleep)
        Espresso.onView(ViewMatchers.withId(R.id.image_restore)).perform(ViewActions.click())
        firstEmojiStickerFrameBorder = activity.findViewById<FrameLayout>(R.id.frmBorder)
        assertEquals(0f, firstEmojiStickerFrameBorder.x)
        sleep(timeSleep)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.imageeditor", appContext.packageName)
    }
}