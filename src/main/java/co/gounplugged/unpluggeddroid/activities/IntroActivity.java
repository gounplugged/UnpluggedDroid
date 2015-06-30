package co.gounplugged.unpluggeddroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.fragments.ProfileIntroFragment;
import co.gounplugged.unpluggeddroid.fragments.ProvisionIntroFragment;

public class IntroActivity extends BaseActivity {

    @Bind(R.id.viewpager) ViewPager viewPager;

    List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);

        setupToolbar();
        getSupportActionBar().setTitle(getString(R.string.app_name));

        setupViewPager();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_intro_action_next) {
            if(viewPager.getCurrentItem() == 0) {
                final ProvisionIntroFragment fragment = (ProvisionIntroFragment) mFragments.get(0);
                if (fragment.isInputValid()) {
                    viewPager.setCurrentItem(1, true);
                }
            } else if (viewPager.getCurrentItem() == 1) {
                BaseApplication.getInstance(this).seedKnownMasks();
                startMain();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        mFragments = new ArrayList<>(2);
        mFragments.add(ProvisionIntroFragment.newInstance());
        mFragments.add(ProfileIntroFragment.newInstance());

        final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(adapter);
    }

    private void startMain() {
        Intent mainIntent = new Intent(this, ChatActivity.class);
        startActivity(mainIntent);
        finish();
    }

    static class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

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
