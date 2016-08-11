package visitor.app.com.visitormanagement.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.interfaces.AppOperationAware;
import visitor.app.com.visitormanagement.interfaces.OnSearchTermActionCallback;
import visitor.app.com.visitormanagement.utils.SearchUtil;

/**
 * Created by jugal on 9/8/16.
 */
public class SearchViewAdapter<T> extends CursorAdapter {

    private LayoutInflater inflater;

    //private SearchTermActionListener searchTermActionListener;

    public SearchViewAdapter(T context, Cursor contactCursor) {
        super(((AppOperationAware) context).getCurrentActivity(), contactCursor, false);
        this.inflater = LayoutInflater.from(((AppOperationAware) context).getCurrentActivity());
//        this.searchTermActionListener = new SearchTermActionListener(onSearchTermActionCallback,
//                ((AppOperationAware) context).getCurrentActivity());
    }

    /*
    private static class SearchTermActionListener implements View.OnClickListener {
        private OnSearchTermActionCallback onSearchTermActionCallback;
        private Context context;

        public SearchTermActionListener(OnSearchTermActionCallback onSearchTermActionCallback, Context context) {
            this.onSearchTermActionCallback = onSearchTermActionCallback;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Object tagVal = v.getTag(R.id.search_suggestion_term_tag_id);
            if (tagVal != null) {
                fillTerm(String.valueOf(tagVal));
            }
        }

        private void fillTerm(String term) {
            if (!TextUtils.isEmpty(term)) {
                onSearchTermActionCallback.setSearchText(term);
            }
        }
    }
    */

    @Override
    @SuppressWarnings("unchecked")
    public void bindView(final View view, final Context context, Cursor cursor) {
        String termString = cursor.getString(1);
        RowViewHolder rowViewHolder = (RowViewHolder) view.getTag();
        TextView txtTerm = rowViewHolder.getTxtTerm();
        String term = termString.trim();
        int termLength = term.length();
        String constraint = getFilterQuery();
        if (!TextUtils.isEmpty(constraint)) {
            int startIndx = term.indexOf(constraint);
            int endIndx = startIndx > -1 ? startIndx + constraint.length() - 1 : -1;
            if (endIndx > 0) {
                endIndx = Math.min(endIndx, termLength - 1);
            }
            if (endIndx > startIndx) {
                SpannableString spannableString = new SpannableString(term);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD),
                        startIndx, endIndx + 1,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                txtTerm.setText(spannableString);
            } else {
                txtTerm.setText(term);
            }
        } else {
            txtTerm.setText(term);
        }
    }

    @Nullable
    private String getFilterQuery() {
        if (getFilterQueryProvider() instanceof SearchFilterQueryProvider) {
            return ((SearchFilterQueryProvider) getFilterQueryProvider()).getConstraint();
        }
        return null;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.search_row, parent, false);
        RowViewHolder rowViewHolder = new RowViewHolder(view);
        view.setTag(rowViewHolder);
        return view;
    }

    private class RowViewHolder {
        private TextView txtTerm;
        private View itemRow;

        private RowViewHolder(View itemRow) {
            this.itemRow = itemRow;
        }

        public TextView getTxtTerm() {
            if (txtTerm == null) {
                txtTerm = (TextView) itemRow.findViewById(R.id.txtTerm);
            }
            return txtTerm;
        }
    }

    public static class SearchFilterQueryProvider implements FilterQueryProvider {

        private String constraint;
        private WeakReference<Context> context;

        @Nullable
        public String getConstraint() {
            return constraint;
        }

        public SearchFilterQueryProvider(Context context) {
            this.context = new WeakReference<>(context);
        }

        @Override
        public Cursor runQuery(CharSequence constraint) {
            if (context != null && context.get() != null && constraint != null) {
                this.constraint = constraint.toString();
                return SearchUtil.searchQueryCall(constraint.toString(),
                        context.get().getApplicationContext());
            }
            return null;
        }
    }


}
