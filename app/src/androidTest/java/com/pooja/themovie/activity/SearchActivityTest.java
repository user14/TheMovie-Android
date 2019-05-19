package com.pooja.themovie.activity;

import android.support.test.rule.ActivityTestRule;
import com.pooja.themovie.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SearchActivityTest {
    @Rule
    public ActivityTestRule<SearchActivity> searchActivityActivityTestRule = new ActivityTestRule<SearchActivity>(SearchActivity.class);

    private SearchActivity mSearchActivity = null;

    @Before
    public void setUp() throws Exception {
        mSearchActivity = searchActivityActivityTestRule.getActivity();
    }

    @After
    public void tearDown() throws Exception {
        mSearchActivity = null;
    }

    @Test
    public void testSearchButtonAction()
    {
        onView(withId(R.id.search_AutoCompleteTextView))
                .perform(typeText("Search"));

        onView(withId(R.id.search_Button)).perform(click());
    }

}