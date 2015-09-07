package com.swivl.lyudmila.swivlloader.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.swivl.lyudmila.swivlloader.R;
import com.swivl.lyudmila.swivlloader.data.ListData;
import com.swivl.lyudmila.swivlloader.loader.AvatarLoader;
import com.swivl.lyudmila.swivlloader.pager.ImagePager;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lyudmila on 06.09.2015.
 */
public class ListAdapter extends ArrayAdapter<ListData> {

    private static final int ITEM_VIEW_RES_ID = R.layout.list_item;
    private final LayoutInflater mLayoutInflater;
    public AvatarLoader mLoader;

    public ListAdapter(@NotNull Context context, @NotNull ListData[] values){
		super(context, ITEM_VIEW_RES_ID, values);
        this.mLayoutInflater = LayoutInflater.from(context);
        mLoader = new AvatarLoader(context);
	}

    @Override
	public long getItemId(int position) {
        return position;
    }

    @Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = this.mLayoutInflater.inflate(ITEM_VIEW_RES_ID, parent, false);
            holder = new Holder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.imageViewAvatar);
            holder.login = (TextView) convertView.findViewById(R.id.txtLogin);
            holder.hrefGit = (TextView) convertView.findViewById(R.id.txtHref);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.login.setText(getItem(position).login);
        holder.hrefGit.setText(getItem(position).urlGit);
        mLoader.DisplayImage(getItem(position).urlAvatar, holder.avatar);


        final int pos = position;
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(parent.getContext(), ImagePager.class);
                intent.putExtra(ImagePager.URL_AVATAR, getItem(pos).urlAvatar);
                parent.getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    public class Holder
    {
        ImageView avatar;
        TextView login;
        TextView hrefGit;
    }
}