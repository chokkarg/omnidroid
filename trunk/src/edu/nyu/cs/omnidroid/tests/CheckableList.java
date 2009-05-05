package edu.nyu.cs.omnidroid.tests;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import edu.nyu.cs.omnidroid.R;
import edu.nyu.cs.omnidroid.ui.Constants;
import edu.nyu.cs.omnidroid.util.UGParser;

/**
 * Overview is the main UI Launcher for the OmniDroid Application. It presents all the current
 * OmniHandlers as well as a way to add/delete/edit them.
 * 
 * @author - acase
 * 
 */
public class CheckableList extends ListActivity {
  // Menu Options of the Context variety(Android menus require int)
  private static final int MENU_EDIT = 0;
  private static final int MENU_DELETE = 1;
  // Menu Options of the Standard variety (Android menus require int)
  private static final int MENU_ADD = 2;
  private static final int MENU_SETTINGS = 3;
  private static final int MENU_HELP = 4;
  private static final int MENU_TESTS = 5;
  private static final int MENU_ABOUT = 6;

  // Pull data from the UGParser
  UGParser ug;

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    // Debug.startMethodTracing("OmniDroid");
    // Create our Activity
    super.onCreate(savedInstanceState);

    update();
  }

  /**
   * Update the list of OmniHandlers as well as any other things that may need to get updated after
   * changes occur.
   */
  private void update() {
    // Get a list of our current OmniHandlers
    ug = new UGParser(getApplicationContext());
    ArrayList<HashMap<String, String>> userConfigRecords = ug.readRecords();
    ArrayList<String> items = new ArrayList<String>();

    // Add current OmniHandlers to our list
    for (HashMap<String, String> hm : userConfigRecords) {
      items.add(hm.get((String) UGParser.KEY_INSTANCE_NAME));
    }

    //ListView clv = new CheckedListView();
    //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
    //    android.R.layout.simple_list_item_1, items);
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_multiple_choice, items);

    final ListView lv = getListView();
    
    //ListView lv = (ListView) findViewById(R.id.checkable_list);
    //lv.setAdapter(arrayAdapter);
    setListAdapter(arrayAdapter);
    registerForContextMenu(lv);
    lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    lv.setItemsCanFocus(false);
    //setContentView(R.layout.test_checkablelist);

    // If we don't have any OmniHandlers, throw up our Help Dialog
    if (userConfigRecords.size() == 0) {
      welcome();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  public void onDestroy() {
    super.onDestroy();
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View,
   * android.view.ContextMenu.ContextMenuInfo)
   */
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, MENU_EDIT, 0, R.string.edit);
    menu.add(0, MENU_DELETE, 0, R.string.del);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
   */
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

    switch (item.getItemId()) {
    case MENU_EDIT:
      editHandler(info.id);
      return true;
    case MENU_DELETE:
      deleteHandler(info.id);
      return true;
    default:
      return super.onContextItemSelected(item);
    }
  }

  /**
   * @param id
   *          of the menu item
   */
  private void deleteHandler(long id) {
    // FIXME (acase): Delete from CP
    // Delete from User Config
    String instance = (String) this.getListAdapter().getItem((int) id);
    ug.deleteRecord(instance);
    update();
    // Object data = getIntent().getData();
    // Button selected = (Button) view;
    // ug.deleteRecord(selected.getText());
    Toast.makeText(this.getBaseContext(), "Delete OmniHandler Selected", 5).show();
  }

  /**
   * @param l
   *          of the menu item
   */
  private void editHandler(long l) {
    // TODO (acase): Call next activity
    Toast.makeText(this.getBaseContext(), "Edit OmniHandler Selected", 5).show();
  }

  /**
   * Creates the options menu items
   * 
   * @param menu
   *          - the options menu to create
   */
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_ADD, 0, R.string.add_omnihandler).setIcon(android.R.drawable.ic_menu_add);
    menu.add(0, MENU_SETTINGS, 0, R.string.settings)
        .setIcon(android.R.drawable.ic_menu_preferences);
    menu.add(0, MENU_HELP, 0, R.string.help).setIcon(android.R.drawable.ic_menu_help);
    menu.add(0, MENU_ABOUT, 0, R.string.about).setIcon(android.R.drawable.ic_menu_info_details);
    menu.add(0, MENU_TESTS, 0, R.string.tests).setIcon(android.R.drawable.ic_menu_manage);
    return true;
  }

  /**
   * Handles menu item selections
   */
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case MENU_ADD:
      addOmniHandler();
      return true;
    case MENU_SETTINGS:
      settings();
      return true;
    case MENU_HELP:
      help();
      return true;
    case MENU_ABOUT:
      about();
      return true;
    case MENU_TESTS:
      runTests();
      return true;
    }
    return false;
  }

  /**
   * Presents the TestApp activity to run any available tests.
   */
  private void runTests() {
    Intent i = new Intent();
    i.setClass(this.getApplicationContext(), edu.nyu.cs.omnidroid.tests.TestApp.class);
    startActivity(i);
  }

  /**
   * Add a new OmniHandler to OmniDroid
   */
  private void addOmniHandler() {
    Intent i = new Intent();
    i.setClass(this.getApplicationContext(), edu.nyu.cs.omnidroid.ui.Event.class);
    startActivityForResult(i, Constants.RESULT_ADD_OMNIHANDER);
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
    case Constants.RESULT_ADD_OMNIHANDER:
      switch (resultCode) {
      case Constants.RESULT_SUCCESS:
        update();
        break;
      }
      break;
    }
  }

  /**
   * Call our Settings Activity
   */
  private void settings() {
    Intent i = new Intent();
    i.setClass(this.getApplicationContext(), edu.nyu.cs.omnidroid.ui.Settings.class);
    startActivityForResult(i, Constants.RESULT_EDIT_SETTINGS);
  }

  /**
   * Call our Welcome dialog
   */
  private void welcome() {
    Builder welcome = new AlertDialog.Builder(this);
    // TODO(acase): Move to some kind of resource
    // String welcome_msg = this.getResources().getString(R.string.welcome_overview);
    String welcome_msg = "OmniDroid provides a way to automate tasks on your device by "
        + "giving you an interface to customize how applications interact "
        + "with eachother.\n<br/>" + "To get started, add a new OmniHandler from the Menu.\n<br/>"
        + "For more information select \"Help\" from the Menu\n<br/>";
    welcome.setTitle(R.string.welcome);
    welcome.setIcon(R.drawable.icon);
    welcome.setMessage(Html.fromHtml(welcome_msg));
    welcome.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
      }
    });
    welcome.show();
  }

  /**
   * Call our Help dialog
   */
  private void help() {
    Builder help = new AlertDialog.Builder(this);
    // TODO(acase): Move to some kind of resource
    // String help_msg = this.getResources().getString(R.string.help_overview);
    String help_msg = "OmniDroid provides a set of OmniHandlers that "
        + "allow you to customize how applications interact " + "with eachother.\n<br/>"
        + "This page provides a list of OmniHandlers that are "
        + "saved and a checkbox to enable/disable them.\n<br/>" + "Possible Actions:\n<br/>"
        + "&nbsp;&nbsp;&nbsp;Add an OmniHandler by selecting the Add option from the Menu.\n<br/>"
        + "&nbsp;&nbsp;&nbsp;Delete an OmniHandler by long-clicking it and selecting "
        + "the Delete option.\n<br/>" + "For more help, see our webpage: "
        + "<a href=\"http://omni-droid.com/help\">http://omni-droid.com/help</a>\n<br/>";
    help.setTitle(R.string.help);
    help.setIcon(android.R.drawable.ic_menu_help);
    // TODO(acase): Link this to the webbrowser
    // Spanned spanned = Html.fromHtml(help_msg);
    // spanned.toString()
    // linked = Linkify.addLinks(spanned, Linkify.WEB_URLS);
    // help.setMessage(Linkify.addLinks(Spannable(Html.fromHtml(help_msg)),Linkify.WEB_URLS));
    // help.setMessage(Linkify.addLinks(help_msg, Linkify.WEB_URLS));
    help.setMessage(Html.fromHtml(help_msg));
    help.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
      }
    });
    help.show();
  }

  /**
   * Call our About dialog
   */
  private void about() {
    Builder about = new AlertDialog.Builder(this);
    // TODO(acase): Move to some kind of resource
    // String about_msg = this.getResources().getString(R.string.help_overview);
    // TODO(acase): Display version information
    String about_msg = "OmniDroid is brought to you in part by the letter G and the number 1.\n<br/>"
        + "License:\n<br/>"
        + "Hacking Contributions:<br/>"
        + "&nbsp;&nbsp;&nbsp;Andrew Case<br/>"
        + "&nbsp;&nbsp;&nbsp;Sucharita Gaat<br/>"
        + "&nbsp;&nbsp;&nbsp;Rajiv Sharma<br/>"
        + "&nbsp;&nbsp;&nbsp;Pradeep Harish Varma<br/>"
        + "\n"
        + "Donations from:<br/>"
        + "&nbsp;&nbsp;&nbsp;Google Inc.<br/>"
        + "&nbsp;&nbsp;&nbsp;New York University\n<br/>"
        + "Website: <a href=\"http://omni-droid.com\">http://omni-droid.com</a><br/>";
    about.setTitle(R.string.about);
    about.setIcon(R.drawable.icon);
    about.setMessage(Html.fromHtml(about_msg));
    about.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int whichButton) {
      }
    });
    about.show();
  }

}