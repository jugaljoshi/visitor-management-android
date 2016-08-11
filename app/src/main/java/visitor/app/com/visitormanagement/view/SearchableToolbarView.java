package visitor.app.com.visitormanagement.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.activities.BaseActivity;
import visitor.app.com.visitormanagement.adapters.SearchViewAdapter;
import visitor.app.com.visitormanagement.interfaces.OnSearchEventListener;

/**
 * Created by jugal on 9/8/16.
 */
public class SearchableToolbarView extends LinearLayout{ //implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView mSearchList;
    private EditText mSearchView;
    private SearchViewAdapter mSearchListAdapter;
    private OnSearchEventListenerProxy mOnSearchEventListenerProxy;
    private View mImgClear;
    private Context context;
    private Activity mAttachedActivity;

    public SearchableToolbarView(Context context) {
        super(context);
        init();
    }

    public SearchableToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void attachActivity(Activity activity) {
        mAttachedActivity = activity;
    }

    public void show() {
        setListAdapter();
        this.setVisibility(VISIBLE);
        mSearchView.requestFocus();
        //UIUtil.changeStatusBarColor(getContext(), R.color.primary_dark_material_light);
        BaseActivity.showKeyboard(mSearchView);
        //restartSearchHistoryLoader();
    }

    public void hide() {
        setVisibility(View.GONE);
        if (mSearchView != null) {
            mSearchView.setText("");
            mSearchView.clearFocus();
            BaseActivity.hideKeyboard(getContext(), mSearchView);
        }
        //UIUtil.changeStatusBarColor(getContext(), R.color.uiv3_status_bar_background);
        if (mAttachedActivity != null && mAttachedActivity instanceof AppCompatActivity) {
            mAttachedActivity.finish();
//            ((AppCompatActivity) mAttachedActivity).getSupportLoaderManager().destroyLoader(
//                    LoaderIds.SEARCH_HISTORY_LOADER_ID);
        }

    }

    private void init() {
        setOrientation(VERTICAL);
        //setBackgroundColor(0x88000000);
        setClickable(true);
        OnClickListener hideViewOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        };
        setOnClickListener(hideViewOnClickListener);

        View.inflate(getContext(), R.layout.searchable_toolbar_layout, this);
        mSearchView = (EditText) findViewById(R.id.searchView);
        mSearchList = (ListView) findViewById(R.id.searchList);
        mImgClear = findViewById(R.id.imgClear);

        Toolbar toolbarSearch = (Toolbar) findViewById(R.id.toolbarSearch);
        toolbarSearch.setNavigationIcon(R.drawable.ic_arrow_back_grey600_24dp);
        toolbarSearch.setNavigationOnClickListener(hideViewOnClickListener);

        mSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mOnSearchEventListenerProxy == null) return;
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null && cursor.getString(1) != null && cursor.getString(2) !=null) {
                    mOnSearchEventListenerProxy.onSearchRequested(cursor.getString(1).trim(),
                            cursor.getString(2).trim());
                }
            }
        });

        setupSearchView();
        mImgClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setText("");
            }
        });
        mOnSearchEventListenerProxy = new OnSearchEventListenerProxy(this);
    }

    private void setListAdapter() {
        if (mSearchListAdapter == null) {
            mSearchListAdapter = new SearchViewAdapter<>(getContext(), null);
            mSearchListAdapter.setFilterQueryProvider(
                    new SearchViewAdapter.SearchFilterQueryProvider(getContext()));
            mSearchList.setAdapter(mSearchListAdapter);
        }
    }
    /*

    private void restartSearchHistoryLoader() {
        if (getVisibility() == View.VISIBLE
                && mAttachedActivity != null && mAttachedActivity instanceof AppCompatActivity) {
            ((AppCompatActivity) mAttachedActivity).getSupportLoaderManager().restartLoader(
                    LoaderIds.SEARCH_HISTORY_LOADER_ID, null, this);
        }
    }

    */

    private void setupSearchView() {
        mSearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (((keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        actionId == EditorInfo.IME_ACTION_DONE) {
                    onQueryTextSubmit(mSearchView.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onQueryTextChange(s != null ? s.toString() : null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setOnSearchEventListener(OnSearchEventListener searchEventListener) {
        this.mOnSearchEventListenerProxy.setOnSearchEventListener(searchEventListener);
    }

    private void onQueryTextSubmit(String query) {
        if (mOnSearchEventListenerProxy != null && !TextUtils.isEmpty(query)) {
            mOnSearchEventListenerProxy.onSearchRequested(query.trim(), "typed");
        }
    }

    private void onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mSearchList.clearTextFilter();
            if (mImgClear != null && mImgClear.getVisibility() != View.GONE) {
                mImgClear.setVisibility(View.GONE);
            }
            //restartSearchHistoryLoader();
        } else {
            if (mImgClear != null && mImgClear.getVisibility() != View.VISIBLE) {
                mImgClear.setVisibility(View.VISIBLE);
            }
            if (mSearchList != null && mSearchListAdapter != null &&
                    mSearchListAdapter.getFilter() != null) {
                mSearchList.setFilterText(newText);
                mSearchListAdapter.getFilter().filter(newText);
            }
        }
    }

    public boolean onBackPressed() {
        if (getVisibility() == View.VISIBLE) {
            hide();
            return true;
        }
        return false;
    }

    /*
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SearchHistoryLoader(getContext());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mSearchListAdapter != null) {
            mSearchListAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mSearchListAdapter != null) {
            mSearchListAdapter.swapCursor(null);
        }
    }
    */

    private static class OnSearchEventListenerProxy implements OnSearchEventListener {
        private OnSearchEventListener mOnSearchEventListener;
        private SearchableToolbarView searchableToolbarView;

        public OnSearchEventListenerProxy(SearchableToolbarView searchableToolbarView) {
            this.searchableToolbarView = searchableToolbarView;
        }

        private void setOnSearchEventListener(OnSearchEventListener onSearchEventListener) {
            this.mOnSearchEventListener = onSearchEventListener;
        }

        private void reset() {
            if (searchableToolbarView != null) {
                searchableToolbarView.hide();
            }
        }

        @Override
        public void onSearchRequested(@NonNull String query, @NonNull String wbId) {
            reset();
            if (mOnSearchEventListener != null) {
                mOnSearchEventListener.onSearchRequested(query, wbId);
            }
        }
    }
}

