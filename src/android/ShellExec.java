package com.milankragujevic.plugin;

import org.apache.cordova.*;
import java.io.*;
import java.net.*;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;

public class ShellExec extends CordovaPlugin {
    private static final String TAG = "CordovaShellExecute";
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        // Log.w(TAG, "working");
        if (action.equals("execute")) {

            String cmd = data.getString(0);
            callbackContext.success(sudoForResult(cmd));
            return true;

        } else {
            
            return false;

        }
    }
    public static String sudoForResult(String...strings) {
        String res = "";
        DataOutputStream outputStream = null;
        InputStream response = null;
        try{
            Process su = Runtime.getRuntime().exec("su");
            outputStream = new DataOutputStream(su.getOutputStream());
            response = su.getInputStream();

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = readFully(response);
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            closeSilently(outputStream, response);
        }
        return res;
    }
    public static String readFully(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString("UTF-8");
    }
    public static void closeSilently(Object... xs) {
    // Note: on Android API levels prior to 19 Socket does not implement Closeable
    for (Object x : xs) {
        if (x != null) {
            try {
                Log.d(TAG,"closing: "+x);
                if (x instanceof Closeable) {
                    ((Closeable)x).close();
                } else if (x instanceof Socket) {
                    ((Socket)x).close();
                } else if (x instanceof DatagramSocket) {
                    ((DatagramSocket)x).close();
                } else {
                    Log.d(TAG,"cannot close: "+x);
                    throw new RuntimeException("cannot close "+x);
                }
            } catch (Throwable e) {
                Log.e(TAG,"Closer failed");
            }
        }
    }
}
}