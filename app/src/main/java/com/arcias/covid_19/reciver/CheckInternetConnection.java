package com.arcias.covid_19.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckInternetConnection extends BroadcastReceiver {
 public static ConnectionListener listener;
 public CheckInternetConnection()
 {
  super();
 }
 @Override
 public void onReceive(Context context, Intent intent) {
  ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  NetworkInfo activeNetwork=manager.getActiveNetworkInfo();
  boolean isConnected=activeNetwork!=null && activeNetwork.isConnectedOrConnecting();
  if (listener!=null)
  {
   listener.onConnectionChange(isConnected);
  }
 }
 public interface ConnectionListener
 {
  void onConnectionChange(boolean isConnected);
 }
}
