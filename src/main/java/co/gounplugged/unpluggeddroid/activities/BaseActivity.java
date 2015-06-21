package co.gounplugged.unpluggeddroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;

import java.util.List;

import co.gounplugged.unpluggeddroid.R;
import co.gounplugged.unpluggeddroid.application.BaseApplication;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.predicates.ConversationIdPredicate;
import co.gounplugged.unpluggeddroid.utils.ImageUtil;
import co.gounplugged.unpluggeddroid.utils.Predicate;

public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_CONVERSATION_ID = "co.gounplugged.unpluggeddroid.EXTRA_CONVERSATION_ID";

    public static final int NAVIGATION_MAIN_HOME = 0;
    public static final int NAVIGATION_MAIN_SETTINGS = 1;
    public static final int NAVIGATION_MAIN_PROFILE = 2;

    public static final int NAVIGATION_GROUP_ID_CONVERSATIONS = 1;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private SubMenu mConversationSubMenu;

    private List<Conversation> mConversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setupToolbar(int mainNavigationId) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // main navigation
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mainNavigationId).setChecked(true);
        switch (mainNavigationId) {
            case NAVIGATION_MAIN_HOME:
                ab.setTitle("Home");
                break;
            case NAVIGATION_MAIN_SETTINGS:
                ab.setTitle("Settings");
                break;
            case NAVIGATION_MAIN_PROFILE:
                ab.setTitle("Profile");
                break;
        }

        setupDrawerConversationContent();
    }


    private void setupDrawerConversationContent() {

        mConversations = BaseApplication.getInstance(getApplicationContext()).getRecentConversations();

        if (mConversations != null && mConversations.size() > 0) {
            Menu menu = mNavigationView.getMenu();
            mConversationSubMenu = menu.addSubMenu("Recent conversations");
            for (Conversation conversation : mConversations) {
                addConversationToSubMenu(conversation);
            }
            notifyNavigationMenuChanged();
        }

        mNavigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    menuItem.setChecked(true);
                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            closeAndFinish();
                            return true;
                        case R.id.nav_settings:
                            Intent intent1 = new Intent(getApplicationContext(), PreferencesActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent1);
                            closeAndFinish();
                            return true;
                        case R.id.nav_profile:
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            closeAndFinish();
                            return true;
                    }

                    Intent intent = menuItem.getIntent();
                    if (intent != null) {
                        long conversationId = intent.getLongExtra(EXTRA_CONVERSATION_ID, -1);
                        Conversation mSelectedConversation = Predicate.select(mConversations, new ConversationIdPredicate(conversationId));
                        Profile.setLastConversationId(mSelectedConversation.id);
                        startActivity(intent);
                        closeAndFinish();
                    }

                    return true;
                }

                private void closeAndFinish() {
                    mDrawerLayout.closeDrawers();
//                    finish();
                }
            }
        );

    }

//    private void rebuildSubMenu() {
//        Menu menu = mNavigationView.getMenu();
//        menu.removeGroup(NAVIGATION_GROUP_ID_CONVERSATIONS);
//
//        for (Conversation conversation : mConversations) {
//            addConversationToSubMenu(conversation);
//        }
//        notifyNavigationMenuChanged();
//    }

    protected void addConversationToSubMenu(Conversation conversation) {
        mConversationSubMenu.add(NAVIGATION_GROUP_ID_CONVERSATIONS, Menu.NONE, mConversationSubMenu.size(), conversation.getName());
        MenuItem item  = mConversationSubMenu.getItem(mConversationSubMenu.size() - 1);
        item.setIcon(ImageUtil.getDrawableFromUri(getApplicationContext(), conversation.getParticipant().getImageUri()));
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        // instead of launching a new instance all other activities on top of it will be closed
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_CONVERSATION_ID, conversation.id);
        item.setIntent(intent);
    }

    //http://stackoverflow.com/questions/30609408/how-to-add-submenu-items-to-navigationview-programmatically-instead-of-menu-xml
    private void notifyNavigationMenuChanged() {
        for (int i = 0, count = mNavigationView.getChildCount(); i < count; i++) {
            final View child = mNavigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }

}
