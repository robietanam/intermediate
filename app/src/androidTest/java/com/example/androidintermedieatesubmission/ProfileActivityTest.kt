package com.example.androidintermedieatesubmission

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.androidintermedieatesubmission.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ProfileActivityTest{

    @get:Rule
    private val activity = ActivityScenarioRule(ProfileActivity::class.java)

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

        onView(ViewMatchers.withId(R.id.btn_logout))
            .check(matches(ViewMatchers.isDisplayed()))

        onView(ViewMatchers.withId(R.id.btn_logout)).perform(ViewActions.click())
    }

    @Test
    fun isToHomepage(){
        Intents.init()
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
        onView(ViewMatchers.withId(R.id.editTextCustom))
            .check(matches(ViewMatchers.isDisplayed()))
    }
}