package edu.umkc.connor.gradecalculator;

//Custom Listview Adapter that implements grade scale items
//Much of this class comes from JavaPapers.com Android ListView Custom Layout Tutorial

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustLVAdapterGS extends ArrayAdapter<GradeScaleItem> {

    private static final String TAG = "CustLVAdapterGS";
    private ArrayList<GradeScaleItem> list = new ArrayList<GradeScaleItem>();

    static class LineViewHolder {
        TextView name;
        TextView percentage;
    }

    public CustLVAdapterGS(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void add(GradeScaleItem item) {
        list.add(item);
        super.add(item);
    }

    @Override
    public void remove(GradeScaleItem item) {
        list.remove(item);
        super.remove(item);
    }

    @Override
    public GradeScaleItem getItem(int index) {
        return list.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LineViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_items_saved, parent, false);
            viewHolder = new LineViewHolder();

            //Assign Each Variable to the OBJECT in the layout
            viewHolder.name = (TextView)row.findViewById(R.id.GSTextView1);
            viewHolder.percentage = (TextView)row.findViewById(R.id.GSTextView2);

            row.setTag(viewHolder);
        } else {
            viewHolder = (LineViewHolder)row.getTag();
        }
        GradeScaleItem item = getItem(position);
        viewHolder.name.setText(item.getName());
        viewHolder.percentage.setText(String.valueOf(item.getPercentage()));

        return row;
    }

    public int totPerc() {
        int sum = 0;
        for (GradeScaleItem x : list) {
            sum += x.getPercentage();
        }
        return sum;
    }

}
