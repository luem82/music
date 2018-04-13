package com.example.mypc.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MyPC on 17/12/2017.
 */

public class SongAdaptet extends ArrayAdapter<Song> {
    private Context context;
    private int resource;
    private ArrayList<Song> items;
    private ArrayList<Song> arrayListBackup;
    private String strKey = "";


    public ArrayList<Song> getArrayListBackup() {
        return arrayListBackup;
    }


    class ViewHolder {
        TextView ten;
        TextView casi;
        TextView tg;
        TextView od;

        public ViewHolder(View view) {
            this.ten = (TextView) view.findViewById(R.id.tv_name);
            this.casi = (TextView) view.findViewById(R.id.tv_artist);
            this.tg = (TextView) view.findViewById(R.id.tv_duration);
            this.od = (TextView) view.findViewById(R.id.tv_order);
        }
    }

    public SongAdaptet(Context context, int resource, ArrayList<Song> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.items = objects;
        this.arrayListBackup = new ArrayList<Song>();
        this.arrayListBackup.addAll(objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) view.getTag();


        Song itemCurrent = items.get(position);
        if (itemCurrent != null) {
            viewHolder.od.setText((position + 1) + " - ");
            viewHolder.ten.setText(itemCurrent.getName().toString());
            viewHolder.casi.setText(itemCurrent.getArtist().toString());
            String tg = Ultils.getStringTimeFromDuration(itemCurrent.getDuration());
            viewHolder.tg.setText("Time: " + tg);
        }

        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void tiemKiem_CV(String strSearch) {
        items.clear();
        strKey = strSearch;
        if (strKey.length() == 0) {
            items.addAll(arrayListBackup);
        } else {
            for (Song cv : arrayListBackup) {
                String key = Ultils.removeAccent(strKey);
                String name = Ultils.removeAccent(cv.getName());
                if (name.toLowerCase().contains(key.toLowerCase())) items.add(cv);
            }
        }
        notifyDataSetChanged();
    }

    public void capNhatDanhSach() {
        arrayListBackup.clear();
        arrayListBackup.addAll(items);
        notifyDataSetChanged();
    }

}
