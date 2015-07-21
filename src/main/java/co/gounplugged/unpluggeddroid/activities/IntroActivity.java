package co.gounplugged.unpluggeddroid.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.fragments.BaseIntroFragment;
import co.gounplugged.unpluggeddroid.fragments.ProfileIntroFragment;
import co.gounplugged.unpluggeddroid.fragments.ProvisionIntroFragment;

public class IntroActivity extends BaseActivity {

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        setupToolbar();
        getSupportActionBar().setTitle(getString(R.string.app_name));

        setupViewPager();

        CirclePageIndicator circlePageIndicator = (CirclePageIndicator)findViewById(R.id.titles);
        circlePageIndicator.setViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_intro_action_next) {
            final int currentPage = viewPager.getCurrentItem();
            final BaseIntroFragment fragment = (BaseIntroFragment) mFragments.get(currentPage);

            if (!fragment.isInputValid())
                return super.onOptionsItemSelected(item);

            fragment.saveInfo();

            if(currentPage == 0) {
                viewPager.setCurrentItem(1, true);
            } else if (currentPage == 1) {
                BaseApplication.getInstance(this).refreshKnownMasks();
                startMain();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        mFragments = new ArrayList<>(2);
        mFragments.add(ProvisionIntroFragment.newInstance());
        mFragments.add(ProfileIntroFragment.newInstance());

        final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getFragmentManager(), mFragments);
        viewPager.setAdapter(adapter);
    }

    private void startMain() {
        Intent mainIntent = new Intent(this, ChatActivity.class);
        startActivity(mainIntent);
        finish();
    }

    static class FragmentPagerAdapter extends android.support.v13.app.FragmentPagerAdapter {

        private final List<Fragment> mViewPagerFragments;

        public FragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mViewPagerFragments = fragments;
        }

        @Override
        public Fragment getItem(int index) {
            return mViewPagerFragments.get(index);
        }

        @Override
        public int getCount() {
            return mViewPagerFragments.size();
        }
    }


}
