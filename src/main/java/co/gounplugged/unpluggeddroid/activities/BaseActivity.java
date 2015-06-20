package co.gounplugged.unpluggeddroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import co.gounplugged.unpluggeddroid.db.DatabaseAccess;
import co.gounplugged.unpluggeddroid.models.Conversation;
import co.gounplugged.unpluggeddroid.models.Profile;
import co.gounplugged.unpluggeddroid.models.predicates.ConversationIdPredicate;
import co.gounplugged.unpluggeddroid.utils.ImageUtil;
import co.gounplugged.unpluggeddroid.utils.Predicate;

public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_CONVERSATION_ID = "co.gounplugged.unpluggeddroid.EXTRA_CONVERSATION_ID";
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

    protected void setupToolbar(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(title);

        setupDrawerContent();
    }


    private void setupDrawerContent() {

        Menu menu = mNavigationView.getMenu();
        mConversationSubMenu = menu.addSubMenu("Recent conversations");

        //TODO move to BaseApp? and keep in mem
        DatabaseAccess<Conversation> conversationAccess = new DatabaseAccess<>(getApplicationContext(), Conversation.class);
        mConversations = conversationAccess.getAll();

        for (Conversation conversation : mConversations) {
            addConversationToSubMenu(conversation);
        }
        notifyNavigationMenuChanged();

        mNavigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        menuItem.setChecked(true);
                    switch (menuItem.getItemId()) {
                        case R.id.nav_settings:
                            startActivity(new Intent(getApplicationContext(), PreferencesActivity.class));
                            mDrawerLayout.closeDrawers();
                            return true;
                        case R.id.nav_profile:
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            mDrawerLayout.closeDrawers();
                            return true;
                    }

                    Intent intent = menuItem.getIntent();
                    if (intent != null) {
                        long conversationId = intent.getLongExtra(EXTRA_CONVERSATION_ID, -1);
                        Conversation mSelectedConversation = Predicate.select(mConversations, new ConversationIdPredicate(conversationId));
                        Profile.setLastConversationId(mSelectedConversation.id);
//                            updateActivityViews();
                    }
                    mDrawerLayout.closeDrawers();
                    return true;
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

    private void addConversationToSubMenu(Conversation conversation) {
        mConversationSubMenu.add(NAVIGATION_GROUP_ID_CONVERSATIONS, Menu.NONE, mConversationSubMenu.size(), conversation.getName());
        MenuItem item  = mConversationSubMenu.getItem(mConversationSubMenu.size()-1);
        item.setIcon(ImageUtil.getDrawableFromUri(getApplicationContext(), conversation.getParticipant().getImageUri()));
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
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
