package com.pooja.themovie.activity;


import android.support.test.rule.ActivityTestRule;
import com.pooja.themovie.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

public class MainActivityTest {

  @Rule
  public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mMainActivity = null;

    @Before
    public void setUp() throws Exception {
        mMainActivity = mainActivityActivityTestRule.getActivity();
    }

    @Test
    public void testOnCreate() {

        assertNotNull(mMainActivity.findViewById(R.id.movie_recyclerView));
        assertNotNull(mMainActivity.findViewById(R.id.search_floating_button));


    }


    @After
    public void tearDown() throws Exception {
        mMainActivity = null;
    }


}