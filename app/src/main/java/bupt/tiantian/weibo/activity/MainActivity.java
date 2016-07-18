package bupt.tiantian.weibo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import bupt.tiantian.weibo.R;
import bupt.tiantian.weibo.util.NetChecker;

public class MainActivity extends AppCompatActivity
        implements MainActivityFragment.OnFragmentInteractionListener,
        ShowPictureFragment.OnFragmentInteractionListener {


    private AppBarLayout mLayoutToolbar;
    private int mPicFragPagerIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetChecker.setNetWorkFlag(MainActivity.this);
        NetChecker.setLargePicFlag(MainActivity.this);


        mLayoutToolbar = (AppBarLayout) findViewById(R.id.layoutToolbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_normal);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(i);
//            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return super.getSupportFragmentManager();
    }

    @Override
    public void onStatusPicClicked(Bundle bundle) {
        ShowPictureFragment showPictureFragment = (ShowPictureFragment) getSupportFragmentManager().findFragmentById(R.id.fragShowPic);
        if (showPictureFragment != null) {
            // If article frag is available, we're in two-pane layout...
            // Call a method in the ShowPictureFragment to update its content
            System.out.println("we're in two-pane layout");
        } else {
            // Otherwise, we're in the one-pane layout and must swap frags...
            // Create fragment and give it an argument for the selected article
            ShowPictureFragment newShowPicFrag = new ShowPictureFragment();
            newShowPicFrag.setArguments(bundle);
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
//            transaction.replace(R.id.layoutCoordinator, newShowPicFrag);
            transaction.add(R.id.layoutCoordinator, newShowPicFrag);
            // and add the transaction to the back stack so the user can navigate back
            transaction.addToBackStack(null);
            // Commit the transaction
            transaction.commit();

        }
    }

    @Override
    public void onPageSelectedChange(int position) {
        mPicFragPagerIdx = position;
    }

    public int getPicFragPagerIdx() {
        return mPicFragPagerIdx;
    }
}
