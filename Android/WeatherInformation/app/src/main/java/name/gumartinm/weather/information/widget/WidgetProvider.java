package name.gumartinm.weather.information.widget;


import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import name.gumartinm.weather.information.widget.service.WidgetIntentService;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // For each widget that needs an update, get the text that we should display:
        //   - Create a RemoteViews object for it
        //   - Set the text in the RemoteViews object
        //   - Tell the AppWidgetManager to show that views object for the widget.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            // To prevent any ANR timeouts, we perform the update in a service
        	final Intent intent = new Intent(context.getApplicationContext(), WidgetIntentService.class);
        	intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("refreshAppWidget", true);
            context.startService(intent);
        }
    }
    
    @Override
    public void onDeleted(final Context context, final int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
        	WidgetConfigure.deletePreference(context, appWidgetIds[i]);
            WidgetIntentService.deleteWidgetCurrentData(context, appWidgetIds[i]);
        }
    }

    public static void updateAppWidget(final Context context, final int appWidgetId) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());

        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    public static void refreshAppWidget(final Context context, final int appWidgetId) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());

        refreshAppWidget(context.getApplicationContext(), appWidgetManager, appWidgetId);
    }

    public static void refreshAllAppWidgets(final Context context) {
        final ComponentName widgets = new ComponentName(context.getApplicationContext(), WidgetProvider.class);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());

        final int[] appWidgetIds = appWidgetManager.getAppWidgetIds(widgets);
        for (final int appWidgetId : appWidgetIds) {
            refreshAppWidget(context.getApplicationContext(), appWidgetManager, appWidgetId);
        }
    }

    private static void refreshAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

        int widgetId;
        Bundle myOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);

        // Get the value of OPTION_APPWIDGET_HOST_CATEGORY
        int category = myOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);

        // If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
        boolean isKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;

        // Once you know the widget's category, you can optionally load a different base layout, set different
        // properties, and so on. For example:
        //int baseLayout = isKeyguard ? R.layout.keyguard_widget_layout : R.layout.widget_layout;

        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        //final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        final Intent intent = new Intent(context.getApplicationContext(), WidgetIntentService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("refreshAppWidget", true);
        context.startService(intent);
    }

    private static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

        int widgetId;
        Bundle myOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);

        // Get the value of OPTION_APPWIDGET_HOST_CATEGORY
        int category = myOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);

        // If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen widget
        boolean isKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;

        // Once you know the widget's category, you can optionally load a different base layout, set different
        // properties, and so on. For example:
        //int baseLayout = isKeyguard ? R.layout.keyguard_widget_layout : R.layout.widget_layout;

        // Construct the RemoteViews object.  It takes the package name (in our case, it's our
        // package, but it needs this because on the other side it's the widget host inflating
        // the layout from our package).
        //final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);

        final Intent intent = new Intent(context.getApplicationContext(), WidgetIntentService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("refreshAppWidget", false);
        context.startService(intent);
    }
}
