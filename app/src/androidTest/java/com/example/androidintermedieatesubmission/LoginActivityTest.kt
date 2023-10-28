package com.example.androidintermedieatesubmission

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.androidintermedieatesubmission.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest{

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }
    @Test
    fun loadUI_Success(){

        onView(withId(R.id.editTextCustom)).check(matches(isDisplayed()))
        onView(withId(R.id.passwordEditTextCustom)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_submit)).check(matches(isDisplayed()))

        onView(withId(R.id.editTextCustom)).perform(typeText("robiet@mail.com"))
        onView(withId(R.id.passwordEditTextCustom)).perform(typeText("12345678"))
        onView(withId(R.id.btn_submit)).perform(click())
    }

    @Test
    fun isToHomepage(){
        Intents.init()
        intended(hasComponent(ListStoriesActivity::class.java.name))
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
    }

}