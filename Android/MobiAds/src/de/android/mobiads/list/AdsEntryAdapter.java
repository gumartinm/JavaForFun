package de.android.mobiads.list;

import android.widget.ArrayAdapter;
import de.android.mobiads.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapts AdsEntry objects onto views for lists
 */
public final class AdsEntryAdapter extends ArrayAdapter<AdsEntry> {

	private final int adsItemLayoutResource;

	public AdsEntryAdapter(final Context context, final int adsItemLayoutResource) {
		super(context, 0);
		this.adsItemLayoutResource = adsItemLayoutResource;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		
		// We need to get the best view (re-used if possible) and then
		// retrieve its corresponding ViewHolder, which optimizes lookup efficiency
		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final AdsEntry entry = getItem(position);
		
		// Setting the text view
		viewHolder.titleView.setText(entry.getTitle());
		
		viewHolder.textView.setText(entry.getText());
		
		// Setting image view
		viewHolder.imageView.setImageBitmap(entry.getIcon());
		
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
