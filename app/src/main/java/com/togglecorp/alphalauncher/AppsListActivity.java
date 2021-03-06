package com.togglecorp.alphalauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsListActivity extends Activity {

    private List<AppInfo> mApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);

        loadApps();
        loadListView();

        // We need width and height of layout for the wallpaper
        // which is only available after the layout is shown.
        // So, show the wallpaper on the layout's post runnable.
        final View layout = findViewById(R.id.layout_apps_list);
        layout.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    // Get the wallpaper drawable.
                    WallpaperManager wallpaperManager =
                            WallpaperManager.getInstance(AppsListActivity.this);
                    Drawable drawable = wallpaperManager.getFastDrawable();

                    // Convert to bitmap and blur it.
                    Bitmap bitmap = Utilities.convertToBitmap(drawable,
                            layout.getWidth(), layout.getHeight());
                    bitmap = Utilities.blur(AppsListActivity.this, bitmap);

                    // Set as wallpaper.
                        layout.setBackground(new BitmapDrawable(getResources(), bitmap));
                }
            }
        });
    }

    private void loadApps() {
        PackageManager manager = getPackageManager();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri: availableActivities) {
            mApps.add(new AppInfo(
                    ri.loadLabel(manager),
                    ri.activityInfo.packageName,
                    ri.activityInfo.loadIcon(manager)
            ));
        }

        Collections.sort(mApps, new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo app1, AppInfo app2) {
                return app1.label.toString().compareTo(app2.label.toString());
            }
        });
    }

    private void loadListView() {
        ListView listView = (ListView) findViewById(R.id.list_apps);

        ArrayAdapter<AppInfo> adapter = new ArrayAdapter<AppInfo>(this,
                R.layout.list_item_app, mApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.list_item_app, null);
                }

                AppInfo app = mApps.get(position);

                ImageView iconView = (ImageView) convertView.findViewById(R.id.item_app_icon);
                iconView.setImageDrawable(app.icon);
                TextView nameView = (TextView)convertView.findViewById(R.id.item_app_label);
                nameView.setText(app.label);

                return convertView;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppInfo app = mApps.get(i);
                PackageManager manager = getPackageManager();
                Intent intent = manager.getLaunchIntentForPackage(app.name.toString());
                startActivity(intent);
            }
        });
    }
}
