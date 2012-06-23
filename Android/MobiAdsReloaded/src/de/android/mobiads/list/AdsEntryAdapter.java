package de.android.mobiads.list;

import java.util.List;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.android.mobiads.R;

/**
 * Adapts AdsEntry objects onto views for lists
 */
public final class AdsEntryAdapter extends ArrayAdapter<AdsEntry> {

	private final int adsItemLayoutResource;

	public AdsEntryAdapter(final Context context, final int adsItemLayoutResource) {
		super(context, 0);
		this.adsItemLayoutResource = adsItemLayoutResource;
	}

	public void setData(List<AdsEntry> data) {
		clear();
		if (data != null) {
			addAll(data);
		}
	}

	/**
	 * Populate new items in the list.
	 */
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		// We need to get the best view (re-used if possible) and then
		// retrieve its corresponding ViewHolder, which optimizes lookup efficiency
		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final AdsEntry entry = getItem(position);

		int color;
		int colorText;
		if (!entry.isRead()) {
			color = Color.GRAY;
			colorText = Color.BLACK;
		}
		else {
			color = Color.BLACK;
			colorText = Color.GRAY;
		}
		view.setBackgroundColor(color);
		
		// Setting the text view
		viewHolder.titleView.setText(entry.getTitle());
		viewHolder.titleView.setTextColor(colorText);

		
		viewHolder.textView.setText(entry.getText());
		viewHolder.textView.setTextColor(colorText);

		// Setting image view
		viewHolder.imageView.setImageBitmap(entry.getIcon());
		viewHolder.imageView.setBackgroundColor(Color.BLACK);
		

		return view;
	}

	private View getWorkingView(final View convertView) {
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;

		if(null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater)context.getSystemService
					(Context.LAYOUT_INFLATER_SERVICE);
	
			workingView = inflater.inflate(adsItemLayoutResource, null);
		} else {
			workingView = convertView;
		}

		return workingView;
	}

	private ViewHolder getViewHolder(final View workingView) {
		// The viewHolder allows us to avoid re-looking up view references
		// Since views are recycled, these references will never change
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;


		if(null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();
	
			viewHolder.titleView = (TextView) workingView.findViewById(R.id.ads_entry_title);
			viewHolder.textView = (TextView) workingView.findViewById(R.id.ads_entry_text);
			viewHolder.imageView = (ImageView) workingView.findViewById(R.id.ads_entry_icon);
	
			workingView.setTag(viewHolder);
	
		} else {
			viewHolder = (ViewHolder) tag;
		}

		return viewHolder;
	}

	/**
	 * ViewHolder allows us to avoid re-looking up view references
	 * Since views are recycled, these references will never change
	 */
	private static class ViewHolder {
		public TextView titleView;
		public TextView textView;
		public ImageView imageView;
	}
}
