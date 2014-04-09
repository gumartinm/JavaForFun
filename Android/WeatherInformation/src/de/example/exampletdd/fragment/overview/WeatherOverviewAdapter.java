package de.example.exampletdd.fragment.overview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.example.exampletdd.R;

public class WeatherOverviewAdapter extends ArrayAdapter<WeatherOverviewEntry> {
    private final int resource;

    public WeatherOverviewAdapter(final Context context, final int resource) {
        super(context, 0);

        this.resource = resource;
    }

    @Override
    public View getView(final int position, final View convertView,
            final ViewGroup parent) {

        // We need to get the best view (re-used if possible) and then
        // retrieve its corresponding ViewHolder, which optimizes lookup
        // efficiency
        final View view = this.getWorkingView(convertView);
        final ViewHolder viewHolder = this.getViewHolder(view);
        final WeatherOverviewEntry entry = this.getItem(position);


        // Setting the text view
        viewHolder.dateView.setText(entry.getDate());
        viewHolder.temperatureView.setText(entry.getTemperature());
        // Set image view
        viewHolder.pictureView.setImageBitmap(entry.getPicture());


        return view;
    }

    private View getWorkingView(final View convertView) {
        // The workingView is basically just the convertView re-used if possible
        // or inflated new if not possible
        View workingView = null;

        if(null == convertView) {
            final Context context = this.getContext();
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            workingView = inflater.inflate(this.resource, null);
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


        if((null == tag) || !(tag instanceof ViewHolder)) {
            viewHolder = new ViewHolder();

            viewHolder.dateView = (TextView) workingView
                    .findViewById(R.id.weather_main_entry_date);
            viewHolder.temperatureView = (TextView) workingView
                    .findViewById(R.id.weather_main_entry_temperature);
            viewHolder.pictureView = (ImageView) workingView
                    .findViewById(R.id.weather_main_entry_image);

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
        public TextView dateView;
        public TextView temperatureView;
        public ImageView pictureView;
    }

}