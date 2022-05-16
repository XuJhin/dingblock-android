package cool.mobile.account.country;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import cool.dingstock.appbase.entity.bean.account.CountryBean;
import cool.mobile.account.R;


public class CountryListAdapter extends ArrayAdapter<CountryBean> {

    private int resource;
    private boolean inSearchMode = false;
    private ContactsSectionIndexer indexer = null;

    public CountryListAdapter(Context context, int resource, List<CountryBean> items) {
        super(context, resource,items);
        this.resource = resource;
        Collections.sort(items, new ContactItemComparator());
        setIndexer(new ContactsSectionIndexer(items));
    }

    public TextView getSectionTextView(View rowView) {
        return rowView.findViewById(R.id.sectionTextView);
    }

    public void showSectionViewIfFirstItem(View rowView, CountryBean item, int position) {
        TextView sectionTextView = getSectionTextView(rowView);
        if (inSearchMode) {
            sectionTextView.setVisibility(View.GONE);
        } else {
            if (indexer.isFirstItemInSection(position)) {
                String sectionTitle = indexer.getSectionTitle(item.getSpell());
                sectionTextView.setText(sectionTitle);
                sectionTextView.setVisibility(View.VISIBLE);
            } else
                sectionTextView.setVisibility(View.GONE);
        }
    }

    public void populateDataForRow(View parentView, CountryBean item, int position) {
        TextView nameTxt = parentView.findViewById(R.id.account_item_country_name_txt);
        TextView codeTxt = parentView.findViewById(R.id.account_item_country_code_txt);
        nameTxt.setText(item.getName());
        codeTxt.setText("+" + item.getCode());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup parentView;

        CountryBean item = getItem(position);

        if (convertView == null) {
            parentView = new LinearLayout(getContext()); // Assumption: the resource parent id is a linear layout
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, parentView, true);
        } else {
            parentView = (LinearLayout) convertView;
        }

        // for the very first section item, we will draw a section on top
        showSectionViewIfFirstItem(parentView, item, position);

        // set row mItems here
        populateDataForRow(parentView, item, position);

        return parentView;

    }

    public boolean isInSearchMode() {
        return inSearchMode;
    }

    public void setInSearchMode(boolean inSearchMode) {
        this.inSearchMode = inSearchMode;
    }

    public ContactsSectionIndexer getIndexer() {
        return indexer;
    }

    public void setIndexer(ContactsSectionIndexer indexer) {
        this.indexer = indexer;
    }


    public void updateData(List<CountryBean> data) {
        Collections.sort(data, new ContactItemComparator());
        this.setIndexer(new ContactsSectionIndexer(data));
        notifyDataSetChanged();
    }

}
