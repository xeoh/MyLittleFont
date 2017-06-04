package kr.ac.kaist.team888.mylittlefont;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import kr.ac.kaist.team888.util.DatabaseOpenHelper;
import kr.ac.kaist.team888.util.FeatureController;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final int PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 0;
  private static final String PERMISSION_DENIED_TITLE = "Permission Denied";
  private static final String PERMISSION_DENIED_MSG = "Please allow external storage permission.";
  private static final String GITHUB_URL = "https://github.com/MyLittleFont/MyLittleFont";

  private FontMakerFragment fontMakerFragment;
  private FontViewerFragment fontViewerFragment;
  private DatabaseOpenHelper db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    db = new DatabaseOpenHelper(getApplicationContext());

    requestPermission();
  }

  private void onPermissionGranted() {
    fontMakerFragment = new FontMakerFragment();
    fontViewerFragment = new FontViewerFragment();
    changeFragment(fontMakerFragment);
  }

  @Override
  public void onBackPressed() {
    return;
//    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//    if (drawer.isDrawerOpen(GravityCompat.START)) {
//      drawer.closeDrawer(GravityCompat.START);
//    } else {
//      super.onBackPressed();
//    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_save:
        saveFeatureValues();
        return true;
      case R.id.action_load:
        loadFeatureValues();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nav_font_maker:
        changeFragment(fontMakerFragment);
        break;
      case R.id.nav_font_viewer:
        changeFragment(fontViewerFragment);
        break;
      case R.id.nav_about:
        aboutus();
        break;
      case R.id.nav_github:
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_URL));
        startActivity(browserIntent);
        break;
      default:
        break;
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void changeFragment(Fragment fragment) {
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.replace(R.id.fragment_holder, fragment);
    transaction.addToBackStack(null);
    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    transaction.commit();
  }

  private void requestPermission() {
    if ((ActivityCompat.checkSelfPermission(this,
        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        || ContextCompat.checkSelfPermission(this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this,
          new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE },
          PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
    } else {
      onPermissionGranted();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case PERMISSIONS_REQUEST_EXTERNAL_STORAGE:
        if (grantResults.length == 2
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
          onPermissionGranted();
        } else {
          onPermissionDenied();
        }
        break;
      default:
        break;
    }
  }

  private void onPermissionDenied() {
    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();

    dialog.setTitle(PERMISSION_DENIED_TITLE);
    dialog.setMessage(PERMISSION_DENIED_MSG);
    dialog.setCancelable(false);

    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();

            // openning setting
            final Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            MainActivity.this.startActivity(i);

            finishAndRemoveTask();
          }
        });

    dialog.show();
  }

  private void saveFeatureValues() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    dialogBuilder.setTitle(R.string.feature_save);
    dialogBuilder.setView(R.layout.save_dialog);
    dialogBuilder.setPositiveButton(R.string.feature_save, null);
    dialogBuilder.setNegativeButton(R.string.feature_save_cancel,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) { }
        });

    AlertDialog dialog = dialogBuilder.create();

    // When the save button is clicked.
    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
      @Override
      public void onShow(final DialogInterface dialog) {
        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
        button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            EditText nameInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.save_name);
            String name = nameInput.getText().toString();
            // Name should not be empty.
            if (name.isEmpty()) {
              Toast.makeText(getApplicationContext(), R.string.feature_save_empty_name,
                  Toast.LENGTH_SHORT).show();
              return;
            }
            new DatabaseOpenHelper(getApplicationContext())
                .saveItem(nameInput.getText().toString());
            Toast.makeText(getApplicationContext(), R.string.feature_save_success,
                Toast.LENGTH_SHORT).show();
            dialog.dismiss();
          }
        });
      }
    });

    dialog.show();
  }

  private void loadFeatureValues() {
    FontLoaderAdapter listAdapter = new FontLoaderAdapter(this);
    ListView listView = new ListView(this);
    listView.setAdapter(listAdapter);

    for (FontItem item : db.loadItems()) {
      listAdapter.addItem(item);
    }

    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    dialogBuilder.setTitle(R.string.feature_load_title);
    dialogBuilder.setView(listView);
    dialogBuilder.setNegativeButton(R.string.feature_load_cancel,
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) { }
        });

    final AlertDialog dialog = dialogBuilder.create();

    // When an item clicked.
    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FontItem item = (FontItem) parent.getAdapter().getItem(position);
        changeFragment(fontMakerFragment);
        FeatureController.getInstance().setFeatures(item);
        dialog.dismiss();
      }
    });

    dialog.show();
  }

  private void aboutus() {
    ImageView imageView = new ImageView(this);
    imageView.setImageDrawable(getDrawable(R.drawable.about_us));
    imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    imageView.setAdjustViewBounds(true);

    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    dialogBuilder.setView(imageView);
    dialogBuilder.show();
  }
}
