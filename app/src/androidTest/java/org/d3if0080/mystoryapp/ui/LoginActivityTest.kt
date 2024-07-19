package org.d3if0080.mystoryapp.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.d3if0080.mystoryapp.R
import org.d3if0080.mystoryapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {
    @get:Rule
    var activityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginSuccess() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText("wandyaksatriya@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.ed_login_password)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoginFailed() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText("wandy@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.ed_login_password)).perform(typeText("11111111"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
    }

    @Test
    fun testLogout() {
        onView(withId(R.id.ed_login_email)).perform(
            typeText("wandyaksatriya@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.ed_login_password)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())

        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))

        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)

        onView(withText(R.string.log_out)).perform(click())

        onView(withText(R.string.are_you_sure)).check(matches(isDisplayed()))
        onView(withText(R.string.yes)).perform(click())

        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }
}
