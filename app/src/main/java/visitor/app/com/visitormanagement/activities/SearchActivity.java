package visitor.app.com.visitormanagement.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.interfaces.OnSearchEventListener;
import visitor.app.com.visitormanagement.utils.Constants;
import visitor.app.com.visitormanagement.utils.UIUtil;
import visitor.app.com.visitormanagement.view.SearchableToolbarView;

/**
 * Created by jugal on 9/8/16.
 */
public class SearchActivity extends BaseActivity{ //implements SearchView.OnQueryTextListener{

    @Nullable
    protected SearchableToolbarView mSearchableToolbarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);
        mSearchableToolbarView = (SearchableToolbarView) findViewById(R.id.mainSearchView);
        if (mSearchableToolbarView != null)
            mSearchableToolbarView.attachActivity(getCurrentActivity());
        if (mSearchableToolbarView != null) {
            mSearchableToolbarView.setOnSearchEventListener(new OnSearchEventListener() {

                @Override
                public void onSearchRequested(@NonNull String query, @NonNull String wbId) {
                    doSearch(query, wbId);
                }
            });
        }
        showSearchUI();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return super.onCreateOptionsMenu(menu);
//        /*
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        //searchView.setOnQueryTextListener(this);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
//        searchView.setIconifiedByDefault(false);
//        return true;
//        */
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
//                showSearchUI();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    protected void showSearchUI() {
        if (mSearchableToolbarView != null) {
            mSearchableToolbarView.show();
        }
    }

    protected void doSearch(String query, String wbId) {
        if(UIUtil.isEmpty(query) || UIUtil.isEmpty(wbId)) return;
        Intent visitorListingIntent = new Intent(this, VisitorListingActivity.class);
        visitorListingIntent.putExtra(Constants.WB_ID, wbId);
        visitorListingIntent.putExtra(Constants.NAME, query);
        startActivity(visitorListingIntent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (mSearchableToolbarView != null && mSearchableToolbarView.onBackPressed()) return;
        super.onBackPressed();
    }

    @Override
    public String getScreenTag() {
        return this.getClass().getSimpleName();
    }








    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //searchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setIconifiedByDefault(false);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_result);
        handleIntent(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = getIntent().getExtras().get(SearchManager.USER_QUERY).toString();
            //use the query to search
            launchVisitorListingPage(query, null);
        }
    }

    private void launchVisitorListingPage(String query, String wbId){
        if(UIUtil.isEmpty(query) || UIUtil.isEmpty(wbId)) return;
        Intent visitorListingIntent = new Intent(this, VisitorListingActivity.class);
        visitorListingIntent.putExtra(Constants.WB_ID, wbId);
        visitorListingIntent.putExtra(Constants.NAME, query);
        startActivity(visitorListingIntent);
        this.finish();
    }

    /*
    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        Log.e("******************* , ", query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        return false;
    }
    */
}
