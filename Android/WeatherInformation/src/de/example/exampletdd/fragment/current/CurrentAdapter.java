package de.example.exampletdd.fragment.current;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.example.exampletdd.R;

public class CurrentAdapter extends ArrayAdapter<Object> {
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private final int[] resources;

    public CurrentAdapter(final Context context, final int[] resources) {
        super(context, 0);

        this.resources = resources;
    }


    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        final View view = this.getWorkingView(position, convertView);
        final int viewType = this.getItemViewType(position);

        if (viewType == FIRST) {

            final ViewFirstHolder viewHolder = this.getViewFirstHolder(view);
            final CurrentDataEntryFirst entry = (CurrentDataEntryFirst) this
                    .getItem(position);
            viewHolder.picture.setImageBitmap(entry.getPicture());
            viewHolder.tempMax.setText(entry.getTempMax());
            viewHolder.tempMin.setText(entry.getTempMin());
        } else if (viewType == SECOND) {
            final ViewSecondHolder viewHolder = this.getViewSecondHolder(view);
            final CurrentDataEntrySecond entry = (CurrentDataEntrySecond) this
                    .getItem(position);
            viewHolder.weatherDescription.setText(entry.getWeatherDescription());
        } else if (viewType == THIRD) {
            final ViewThirdHolder viewHolder = this.getViewThirdHolder(view);
            final CurrentDataEntryFifth entry = (CurrentDataEntryFifth) this
                    .getItem(position);
            viewHolder.humidityValue.setText(entry.getHumidityValue());
            viewHolder.pressureValue.setText(entry.getPressureValue());
            viewHolder.rainValue.setText(entry.getRainValue());
            viewHolder.cloudsValue.setText(entry.getCloudsValue());
            viewHolder.windValue.setText(entry.getWindValue());
            viewHolder.sunRiseTime.setText(entry.getSunRiseTime());
            viewHolder.sunSetTime.setText(entry.getSunSetTime());
            viewHolder.feelsLike.setText(entry.getFeelsLike());
            viewHolder.snowValue.setText(entry.getSnowValue());
            viewHolder.feelsLikeUnits.setText(entry.getFeelsLikeUnits());
        }

        return view;
    }

    @Override
    public int getItemViewType(final int position) {
        int type = 0;

        if (position == 0) {
            type = FIRST;
        } else if (position == 1) {
            type = SECOND;
        } else if (position == 2) {
            type = THIRD;
        }

        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    private View getWorkingView(final int position, final View convertView) {
        View workingView = null;

        if (convertView == null) {
            final int viewType = this.getItemViewType(position);
            final Context context = this.getContext();
            final LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            workingView = inflater.inflate(this.resources[viewType], null);
        } else {
            workingView = convertView;
        }

        return workingView;
    }

    private ViewFirstHolder getViewFirstHolder(final View workingView) {
        final Object tag = workingView.getTag();
        ViewFirstHolder viewHolder = null;

        if ((null == tag) || !(tag instanceof ViewFirstHolder)) {
            viewHolder = new ViewFirstHolder();

            viewHolder.picture = (ImageView) workingView
                    .findViewById(R.id.weather_current_data_picture);
            viewHolder.tempMax = (TextView) workingView
                    .findViewById(R.id.weather_current_data_temp_max);
            viewHolder.tempMin = (TextView) workingView
                    .findViewById(R.id.weather_current_data_temp_min);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewFirstHolder) tag;
        }

        return viewHolder;
    }

    private ViewSecondHolder getViewSecondHolder(final View workingView) {
        final Object tag = workingView.getTag();
        ViewSecondHolder viewHolder = null;

        if ((null == tag) || !(tag instanceof ViewSecondHolder)) {
            viewHolder = new ViewSecondHolder();

            viewHolder.weatherDescription = (TextView) workingView
                    .findViewById(R.id.weather_current_data_description);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewSecondHolder) tag;
        }

        return viewHolder;
    }

    private ViewThirdHolder getViewThirdHolder(final View workingView) {
        final Object tag = workingView.getTag();
        ViewThirdHolder viewHolder = null;

        if ((null == tag) || !(tag instanceof ViewThirdHolder)) {
            viewHolder = new ViewThirdHolder();

            viewHolder.humidityValue = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_humidity_value);
            viewHolder.pressureValue = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_pressure_value);
            viewHolder.rainValue = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_rain_value);
            viewHolder.cloudsValue = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_clouds_value);
            viewHolder.windValue = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_wind_value);
            viewHolder.cloudsValue = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_clouds_value);
            viewHolder.snowValue = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_snow_value);
            viewHolder.sunRiseTime = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_sunrise_value);
            viewHolder.sunSetTime = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_sunset_value);
            viewHolder.feelsLike = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_feelslike_value);
            viewHolder.feelsLikeUnits = (TextView) workingView
                    .findViewById(R.id.weather_current_now_data_feelslike_units);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewThirdHolder) tag;
        }

        return viewHolder;
    }


    private static class ViewFirstHolder {
        public ImageView picture;
        public TextView tempMax;
        public TextView tempMin;
    }

    private static class ViewSecondHolder {
        public TextView weatherDescription;
    }

    private static class ViewThirdHolder {
        public TextView humidityValue;
        public TextView pressureValue;
        public TextView windValue;
        public TextView rainValue;
        public TextView cloudsValue;
        public TextView snowValue;
        public TextView sunRiseTime;
        public TextView sunSetTime;
        public TextView feelsLike;
        public TextView feelsLikeUnits;
    }
}
