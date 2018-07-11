package com.example.jonii.pika;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class CustomAdapter extends BaseAdapter {

    List<ListaObjektet> arrayList;
    Context context;
    AdapterListener adapterListener;

    public CustomAdapter(Context context, List<ListaObjektet> arrayList, AdapterListener adapterListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.adapterListener = adapterListener;
    }

    Holder holder;

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.indexOf(getItem(position));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
        }

        holder.txtCell = (TextView) convertView.findViewById(R.id.list_row);
        holder.delete = (ImageView) convertView.findViewById(R.id.delete);
        holder.imgCode = (ImageView) convertView.findViewById(R.id.imgCode);
        holder.editname = (ImageView) convertView.findViewById(R.id.editname);
        holder.txtDescription = (TextView) convertView.findViewById(R.id.txtDescription);
        holder.txtCell.setText(arrayList.get(position).emri);


        holder.txtDescription.setVisibility(arrayList.get(position).lloji.equals("") ? View.GONE : View.VISIBLE);
        holder.txtDescription.setText((arrayList.get(position).lloji + ": " + arrayList.get(position).kodi));

        final int finalPosition = position;

        // delete imagebutton is used for deleting the items from the list
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterListener.onDeleteClicked(position);
            }
        });


        // click on image in left of the row ( barcode icon ) opens the barcode and add the kodi that you are scunning to the item
        holder.imgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterListener.onImgCodeSelected(position);
            }
        });

        // editname is frome where you can change the emri to the item
        holder.editname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterListener.onEditClicked(position);
            }
        });


        return convertView;
    }

    public interface AdapterListener {
        void onDeleteClicked(int position);

        void onImgCodeSelected(int position);

        void onEditClicked(int position);

    }

    public class Holder {
        TextView txtCell, txtDescription;
        ImageView delete, imgCode, editname;
    }

}
