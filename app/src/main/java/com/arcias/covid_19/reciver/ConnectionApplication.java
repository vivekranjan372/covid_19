package com.arcias.covid_19.reciver;

import android.app.Application;

public class ConnectionApplication extends Application {
 private static ConnectionApplication instance;

 @Override
 public void onCreate() {
  super.onCreate();
  instance=this;
 }
 public static synchronized ConnectionApplication getInstance()
 {
  return instance;
 }
 public void setConnectivityListener(CheckInternetConnection.ConnectionListener listener)
 {
  CheckInternetConnection.listener=listener;
 }
}
