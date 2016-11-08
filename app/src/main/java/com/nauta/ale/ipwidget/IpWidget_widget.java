package com.nauta.ale.ipwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Implementation of App Widget functionality.
 */
public class IpWidget_widget extends AppWidgetProvider {

    public static String APPWIDGET_UPDATE = "com.nauta.ale.IpWidget.APPWIDGET_UPDATE";
    public static String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    public static String WIFI_STATE_CHANGED = "android.net.wifi.WIFI_STATE_CHANGED";
    public static String TAG = "IpWidget";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.putExtra("msg", "msg click");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NetInfo netInfo = new NetInfo(context);
        CharSequence widgetText = netInfo.getIPAddress();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ip_widget_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.appwidget_text, pendingIntent);
        Log.d(TAG, "Update");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Log.d(TAG, "onUpdate");
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (APPWIDGET_UPDATE.equals(intent.getAction()) || CONNECTIVITY_CHANGE.equals(intent.getAction()) || WIFI_STATE_CHANGED.equals(intent.getAction())) {

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ip_widget_widget);
            ComponentName ipWidget = new ComponentName(context, IpWidget_widget.class);

            NetInfo netInfo = new NetInfo(context);
            CharSequence widgetText = netInfo.getIPAddress();
            Log.d(TAG, netInfo.getIPAddress());
            Log.d(TAG, netInfo.getWiFiSSID());
            Log.d(TAG, netInfo.getCarrier());
            Log.d(TAG, netInfo.getWiFiMACAddress());
            Log.d(TAG, netInfo.getGateway());
            Log.d(TAG, netInfo.getNetMask());
            Log.d(TAG, netInfo.getDns().get(0));
            Log.d(TAG, netInfo.getDns().get(1));
            Log.d(TAG, netInfo.getLinkSpeed());

            if (widgetText.equals("")) {
                widgetText = "<no ip address>";
            }

            if (widgetText != "<no ip address>") {
                if (netInfo.getCurrentNetworkType() == TYPE_WIFI) {
                    String text = "Connected to: " + netInfo.getWiFiSSID() + "\n" + "Link Speed: " + netInfo.getLinkSpeed() + "\n" + "Hostname: " + netInfo.getHostname("hostname") + "\n" + "Netmask: " + netInfo.getNetMask() + "\n" + "Gateway: " + netInfo.getGateway() + "\n" + "Dns1: " + netInfo.getDns().get(0) + "\n" + "Dns2: " + netInfo.getDns().get(1);
                    if (APPWIDGET_UPDATE.equals(intent.getAction())) Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                    widgetText = netInfo.getWifiIpAddress();
                } else if (netInfo.getCurrentNetworkType() == TYPE_MOBILE){
                    String text = "Connected to: " + netInfo.getCarrier() + "\n" + "Netmask: " + netInfo.getNetMask() + "\n" + "Hostname: " + netInfo.getHostname("hostname");
                    if (APPWIDGET_UPDATE.equals(intent.getAction())) Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                }
            }


                /*if (netInfo.getCurrentNetworkType() == 1) {
                    String text = "Connected to: " + netInfo.getWiFiSSID() + "\n" + "Linkspeed: " + netInfo.getLinkSpeed() + "\n" + "Netmask: " + netInfo.getNetMask() + "\n" + "Gateway: " + netInfo.getGateway() + "\n" + "Dns1: " + netInfo.getDns().get(0) + "\n" + "Dns2: " + netInfo.getDns().get(1);
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                }*/

            /*if (!(netInfo.getGateway().equals("0.0.0.0"))) {
                String text = "Connected to: " + netInfo.getWiFiSSID() + "\n" + "Netmask: " + netInfo.getMask() + "\n" + "Gateway: " + netInfo.getGateway() + "\n" + "Dns1: " + netInfo.getDns().get(0) + "\n" + "Dns2: " + netInfo.getDns().get(1);
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }*/

            views.setTextViewText(R.id.appwidget_text, widgetText);

            appWidgetManager.updateAppWidget(ipWidget, views);

        }

    }


}

