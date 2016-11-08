package com.nauta.ale.ipwidget;

/**
 * Created by ale on 23/11/15.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class NetInfo
{
    private ConnectivityManager connManager = null;
    private WifiManager wifiManager = null;
    private TelephonyManager telManager = null;
    private WifiInfo wifiInfo = null;
    private DhcpInfo dhcpInfo = null;
    private NetworkInterface netInt = null;


    public NetInfo(Context context)
    {
        connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        dhcpInfo = wifiManager.getDhcpInfo();

    }

    public String getHostname(String defValue) {
        try {
            Method getString = Build.class.getDeclaredMethod("getString", String.class);
            getString.setAccessible(true);
            return getString.invoke(null, "net.hostname").toString();
        } catch (Exception ex) {
            return defValue;
        }
    }

    public String getCarrier() {
        if (null == telManager)
            return "";

        String carrier = telManager.getNetworkOperatorName();
        return carrier;
    }

    public String getNetMask() {

        Short netMask = null;

        try {
            netInt = NetworkInterface.getByInetAddress(InetAddress.getByName(this.getIPAddress()));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        for (InterfaceAddress address : netInt.getInterfaceAddresses()) {
            netMask = address.getNetworkPrefixLength();
        }
        Integer netMaskInt = 0xffffffff << (32 - netMask);
        return String.format("%d.%d.%d.%d",
                (netMaskInt >> 24 & 0xff),
                (netMaskInt >> 16 & 0xff),
                (netMaskInt >> 8 & 0xff),
                (netMaskInt & 0xff));
    }

    public int getCurrentNetworkType()
    {
        if (null == connManager)
            return 0;

        NetworkInfo netinfo = connManager.getActiveNetworkInfo();

        if (netinfo == null) {
            return 0;
        } else {
            return netinfo.getType();
        }


    }

    public String getWifiIpAddress()
    {
        if (null == wifiManager || null == wifiInfo)
            return "";

        int ipAddress = wifiInfo.getIpAddress();

        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),
                (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff),
                (ipAddress >> 24 & 0xff));
    }

    public String getWiFiMACAddress() {
        if (null == wifiManager || null == wifiInfo)
            return "";

        return wifiInfo.getMacAddress();
    }

    public String getWiFiSSID() {
        if (null == wifiManager || null == wifiInfo)
            return "";

        return wifiInfo.getSSID();
    }

    public String getGateway() {
        if (null == wifiManager || null == wifiInfo)
            return null;

        int tempInt = dhcpInfo.gateway;

        return String.format("%d.%d.%d.%d",
                (tempInt & 0xff),
                (tempInt >> 8 & 0xff),
                (tempInt >> 16 & 0xff),
                (tempInt >> 24 & 0xff));
    }

 /*   public String getMask() {
        if (null == wifiManager || null == wifiInfo)
            return null;

        int tempInt = dhcpInfo.netmask;

        return String.format("%d.%d.%d.%d",
                (tempInt & 0xff),
                (tempInt >> 8 & 0xff),
                (tempInt >> 16 & 0xff),
                (tempInt >> 24 & 0xff));
    }
*/
    public ArrayList<String> getDns() {
        if (null == wifiManager || null == wifiInfo)
            return null;

        int tempInt1 = dhcpInfo.dns1;
        int tempInt2 = dhcpInfo.dns2;

        String dns1 = String.format("%d.%d.%d.%d",
                (tempInt1 & 0xff),
                (tempInt1 >> 8 & 0xff),
                (tempInt1 >> 16 & 0xff),
                (tempInt1 >> 24 & 0xff));

        String dns2 = String.format("%d.%d.%d.%d",
                (tempInt2 & 0xff),
                (tempInt2 >> 8 & 0xff),
                (tempInt2 >> 16 & 0xff),
                (tempInt2 >> 24 & 0xff));

        ArrayList<String> dns = new ArrayList<String>();
        dns.add(dns1);
        dns.add(dns2);

        return dns;
    }

    public String getLinkSpeed() {
        if (null == wifiManager || null == wifiInfo)
            return null;

        String speed = Integer.toString(wifiInfo.getLinkSpeed()) + "Mbit";

        return speed;
    }

    public String getIPAddress()
    {
        String ipaddress = "";

        try
        {
            Enumeration<NetworkInterface> enumnet = NetworkInterface.getNetworkInterfaces();
            NetworkInterface netinterface;

            while(enumnet.hasMoreElements())
            {
                netinterface = enumnet.nextElement();

//                Log.d("netinfo","netinterface " + netinterface.toString());

                for (Enumeration<InetAddress> enumip = netinterface.getInetAddresses();
                     enumip.hasMoreElements();)
                {
                    InetAddress inetAddress = enumip.nextElement();

                    if(!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address)
                    {
                        ipaddress = inetAddress.getHostAddress();

//                        Log.d("netinfo","ipaddress " + ipaddress);

                        break;
                    }
                }
            }
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        }

        return ipaddress;
    }
}
