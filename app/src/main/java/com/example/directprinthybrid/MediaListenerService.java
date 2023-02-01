package com.example.directprinthybrid;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.widget.Toast;
import java.io.File;

public class MediaListenerService extends Service {

    public static FileObserver observer;

    public MediaListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startWatching();


    }



    private void startWatching() {

        //The desired path to watch or monitor
        //E.g Camera folder
        final String pathToWatch = android.os.Environment.getExternalStorageDirectory().toString() + "/Movies/";
        Toast.makeText(this, "My Service Started and trying to watch " + pathToWatch, Toast.LENGTH_LONG).show();

        observer = new FileObserver(pathToWatch, FileObserver.ALL_EVENTS) { // set up a file observer to watch this directory
            @Override
            public void onEvent(int event, final String file) {
                if (event == FileObserver.CREATE || event == FileObserver.CLOSE_WRITE || event == FileObserver.MODIFY || event == FileObserver.MOVED_TO && !file.equals(".probe")) { // check that it's not equal to .probe because thats created every time camera is launched
                    Log.d("MediaListenerService", "File created [" + pathToWatch + file + "]");
                    printNow(file);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            //File chosenFile=getLastModified(pathToWatch);
                            //Log.i("filename", chosenFile.getName());


                        }
                    });
                }
            }
        };
        observer.startWatching();



    }

    private String prev = "";


    private void printNow(String newDoc){
        //print from SDK
        //Toast.makeText(getBaseContext(), newDoc + " was saved!", Toast.LENGTH_SHORT).show();
        if(!newDoc.equals(prev)) {
            Log.i("filename", newDoc);
            prev = newDoc;
            final String pathPdfRead = android.os.Environment.getExternalStorageDirectory().toString() + "/Movies/"+newDoc;
            //File myFile = new File(pathPdfRead);
            PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(pathPdfRead);
            String jobName = getString(R.string.app_name) + newDoc;
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            Log.i("filename", "End Print");
        }


    }


}
