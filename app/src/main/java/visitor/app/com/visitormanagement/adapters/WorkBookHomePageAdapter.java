package visitor.app.com.visitormanagement.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import visitor.app.com.visitormanagement.R;
import visitor.app.com.visitormanagement.interfaces.AppOperationAware;
import visitor.app.com.visitormanagement.interfaces.OnWorkBookClickAware;
import visitor.app.com.visitormanagement.models.WorkBookModel;
import visitor.app.com.visitormanagement.utils.UIUtil;

/**
 * Created by jugal on 21/7/16.
 */
public class WorkBookHomePageAdapter<T extends OnWorkBookClickAware & AppOperationAware> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<WorkBookModel> workBookModelArrayList;
    private T ctx;

    public WorkBookHomePageAdapter(T ctx, ArrayList<WorkBookModel> workBookModelArrayList) {
        this.ctx = ctx;
        this.workBookModelArrayList = workBookModelArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_page_adapter_layout, parent, false);

        return new HomePageWorkBookHolder(itemView);
    }


    public int getRandomColor() {
        if (ctx.getCurrentActivity() == null) return 0x0099cc; // todo check this
        Random random = new Random();
        String[] colorsArr = ctx.getCurrentActivity().getResources().getStringArray(R.array.randomColors);
        return Color.parseColor(colorsArr[random.nextInt(colorsArr.length)]);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        WorkBookModel workBookModel = workBookModelArrayList.get(position);
        HomePageWorkBookHolder homePageWorkBookHolder = (HomePageWorkBookHolder) holder;

        ImageView imgWorkBook = homePageWorkBookHolder.getImgWorkBook();
        if (!UIUtil.isEmpty(workBookModel.getWbImgUrl())) {
            UIUtil.displayAsyncImage(imgWorkBook, workBookModel.getWbImgUrl());
        }else {
            imgWorkBook.setBackgroundColor(getRandomColor()); //todo get color
        }

        TextView txtWorkBookName = homePageWorkBookHolder.getWorkBookName();
        if (!UIUtil.isEmpty(workBookModel.getWbName())) {
            txtWorkBookName.setVisibility(View.VISIBLE);
            txtWorkBookName.setText(workBookModel.getWbName().toUpperCase());
        } else {
            txtWorkBookName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return workBookModelArrayList.size();
    }

    public class HomePageWorkBookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtWorkBookName;
        private ImageView imgWorkBook;

        public HomePageWorkBookHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public TextView getWorkBookName() {
            if (txtWorkBookName == null) {
                txtWorkBookName = (TextView) itemView.findViewById(R.id.txtWorkBookName);
            }
            return txtWorkBookName;
        }

        public ImageView getImgWorkBook() {
            if (imgWorkBook == null) {
                imgWorkBook = (ImageView) itemView.findViewById(R.id.imgWorkBook);
            }
            return imgWorkBook;
        }

        @Override
        public void onClick(View v) {
            ctx.onWorkBookItemClicked(workBookModelArrayList.get(getPosition()));
        }
    }

}
