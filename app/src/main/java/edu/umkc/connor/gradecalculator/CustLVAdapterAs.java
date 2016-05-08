package edu.umkc.connor.gradecalculator;

//Custom Listview Adapter that implements Assignment items
//Much of this class comes from JavaPapers.com Android ListView Custom Layout Tutorial

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustLVAdapterAs extends ArrayAdapter<AssignmentItem> {

    private static final String TAG = "CustLVAdapterAs";
    private ArrayList<AssignmentItem> list = new ArrayList<AssignmentItem>();

    static class LineViewHolder {
        TextView name;
        TextView totalpoints;
        TextView score;
    }

    public CustLVAdapterAs(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public void add(AssignmentItem item) {
        list.add(item);
        super.add(item);
    }

    @Override
    public void remove(AssignmentItem item) {
        list.remove(item);
        super.remove(item);
        super.notifyDataSetChanged();
    }

    @Override
    public AssignmentItem getItem(int index) {
        return list.get(index);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LineViewHolder viewHolder;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.assn_list, parent, false);
            viewHolder = new LineViewHolder();

            //Assign Each Variable to the OBJECT in the layout
            viewHolder.name = (TextView)row.findViewById(R.id.ALtextView3);
            viewHolder.totalpoints = (TextView)row.findViewById(R.id.ALtextView7);
            viewHolder.score = (TextView)row.findViewById(R.id.ALtextView4);

            row.setTag(viewHolder);
        } else {
            viewHolder = (LineViewHolder)row.getTag();
        }
        AssignmentItem item = getItem(position);
        viewHolder.name.setText(item.getName());
        viewHolder.totalpoints.setText(String.valueOf(item.getTotalPoints()));
        viewHolder.score.setText(String.valueOf(item.getScore()));

        return row;
    }

    public double totPerc() {
        double scores = 0, totpoints = 0;
        for (AssignmentItem x: list) {
            scores+= x.getScore();
            totpoints+= x.getTotalPoints();
        }
        if(totpoints == 0 && scores == 0) {
            return 0;
        }
        return 100 * (scores/totpoints);
    }

}
