/*******************************************************************************
 * Copyright 2009, 2010 Omnidroid - http://code.google.com/p/omnidroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.nyu.cs.omnidroid.app.view.simple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import edu.nyu.cs.omnidroid.app.R;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelAction;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelApplication;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelAttribute;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelEvent;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelItem;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelRuleAction;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelRuleFilter;
import java.util.List;

/**
 * This activity is used to add multiple filters or actions to a new rule.
 */
public class ActivityChooseFiltersAndActions extends Activity {
  // Log tag
  private static final String TAG = ActivityChooseFiltersAndActions.class.getSimpleName();

  // State storage
  private static final String KEY_STATE = "StateActivityChooseFilters";
  private static final String KEY_PREF = "selectedRuleItem";
  private SharedPreferences state;

  /** Request codes for Activity creation */
  /** Return code for filter building activities. */
  public static final int REQUEST_ADD_FILTER = 0;
  /** Return code for action building activities. */
  public static final int REQUEST_ADD_ACTION = 1;
  /** Return code for filter editing activities. */
  public static final int REQUEST_EDIT_FILTER = 2;
  /** Return code for action editing activities. */
  public static final int REQUEST_EDIT_ACTION = 3;
  /** Return code for rule name editing activity. */
  public static final int REQUEST_SET_RULE_NAME = 4;
  /** Return code for rule building activity. */
  public static final int REQUEST_ADD_FILTERS_AND_ACTIONS = 5;
  
  /** Result codes for this activity */
  /** Result code for saving a rule successfully. */
  public static final int RESULT_RULE_SAVED = 1;

  // Context Menu Options
  private static final int MENU_EDIT = 0;
  private static final int MENU_DELETE = 1;

  private ListView listview;
  private AdapterRule adapterRule;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_choose_filters_and_actions);

    // Link our listview with the AdapterRule instance.
    initializeListView();

    // Link up bottom button panel areas.
    initializeButtonPanel();

    // Restore UI state if possible.
    state = getSharedPreferences(ActivityChooseFiltersAndActions.KEY_STATE,
        Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
    listview.setItemChecked(state.getInt(KEY_PREF, 0), true);
  }

  protected void onPause() {
    super.onPause();

    // Save UI state.
    SharedPreferences.Editor prefsEditor = state.edit();
    prefsEditor.putInt(KEY_PREF, listview.getCheckedItemPosition());
    prefsEditor.commit();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
    case REQUEST_ADD_FILTER:
      // Did the user construct a valid filter? If so add it to the rule.
      if (RuleBuilder.instance().getChosenRuleFilter() != null) {
        // Add the filter to the rule builder and the UI tree.
        adapterRule.addItemToParentPosition(listview.getCheckedItemPosition(), RuleBuilder
            .instance().getChosenRuleFilter());
      }
      RuleBuilder.instance().resetFilterPath();
      break;

    case REQUEST_EDIT_FILTER:
      // Did the user modify a valid filter? If so replace it in the rule.
      if (RuleBuilder.instance().getChosenRuleFilter() != null) {
        // Add the filter to the rule builder and the UI tree.
        adapterRule.replaceItem(listview.getCheckedItemPosition(), RuleBuilder.instance()
            .getChosenRuleFilter());
      }
      RuleBuilder.instance().resetFilterPath();
      break;

    case REQUEST_ADD_ACTION:
      // Did the user construct a valid action? If so add it to the rule.
      if (RuleBuilder.instance().getChosenRuleAction() != null) {
        // Add the action to the rule builder and the UI tree.
        adapterRule.addItemToParentPosition(listview.getCheckedItemPosition(), RuleBuilder
            .instance().getChosenRuleAction());
      }
      RuleBuilder.instance().resetActionPath();
      break;

    case REQUEST_EDIT_ACTION:
      // Did the user modify a valid action? If so replace it in the rule.
      if (RuleBuilder.instance().getChosenRuleAction() != null) {
        // Add the action to the rule builder and the UI tree.
        adapterRule.replaceItem(listview.getCheckedItemPosition(), RuleBuilder.instance()
            .getChosenRuleAction());
      }
      RuleBuilder.instance().resetActionPath();
      break;

    case REQUEST_SET_RULE_NAME:
      if (resultCode != Activity.RESULT_CANCELED) {
        // Try to save the rule for the user now.
        // We only get this request code if the user picked
        // a valid rule name.
        saveRule();
      }
      break;
    }
  }

  private void initializeListView() {
    listview = (ListView) findViewById(R.id.activity_choosefiltersandactions_listview);

    adapterRule = new AdapterRule(this, listview);

    listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    listview.setAdapter(adapterRule);

    // Set the adapter to render the rule stored in RuleBuilder.
    // It may be a brand new rule or a saved rule being edited.
    adapterRule.setRule(RuleBuilder.instance().getRule());
    registerForContextMenu(listview);
  }

  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(ContextMenu.NONE, MENU_EDIT, ContextMenu.NONE, R.string.edit);
    menu.add(ContextMenu.NONE, MENU_DELETE, ContextMenu.NONE, R.string.delete);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    switch (item.getItemId()) {
    case MENU_EDIT:
      editItem(info.position);
      return true;
    case MENU_DELETE:
      deleteItem(info.position);
      return true;
    default:
      return super.onContextItemSelected(item);
    }
  }

  /**
   * Checks whether the currently chosen event has attributes
   * 
   * @return true if it has at least one attribute
   */
  private boolean hasAttributes() {
    ModelEvent event = RuleBuilder.instance().getChosenEvent();
    List<ModelAttribute> attributes = UIDbHelperStore.instance().db().getAttributesForEvent(event);

    return !attributes.isEmpty();
  }

  private void initializeButtonPanel() {
    Button btnAddFilter = (Button) findViewById(R.id.activity_choosefiltersandactions_btnAddFilter);
    btnAddFilter.setOnClickListener(listenerBtnClickAddFilter);
    btnAddFilter.setEnabled(hasAttributes());

    Button btnAddAction = (Button) findViewById(R.id.activity_choosefiltersandactions_btnAddAction);
    btnAddAction.setOnClickListener(listenerBtnClickAddAction);

    Button btnSaveRule = (Button) findViewById(R.id.activity_choosefiltersandactions_btnSaveRule);
    btnSaveRule.setOnClickListener(listenerBtnClickSaveRule);

    LinearLayout llBottomButtons = (LinearLayout) findViewById(R.id.activity_choosefiltersandactions_llBottomButtons);
    llBottomButtons.setBackgroundColor(getResources().getColor(R.color.layout_button_panel));
  }

  /**
   * Wipes any UI state saves in {@link:state}. Activities which create this activity should call
   * this before launching so we appear as a brand new instance.
   * 
   * @param context
   *          Context of caller.
   */
  public static void resetUI(Context context) {
    UtilUI.resetSharedPreferences(context, KEY_STATE);
  }

  private OnClickListener listenerBtnClickAddFilter = new OnClickListener() {
    public void onClick(View v) {
      ModelItem selectedItem = adapterRule.getItem(listview.getCheckedItemPosition());
      if ((selectedItem instanceof ModelEvent) || (selectedItem instanceof ModelRuleFilter)) {
        // Now we present the user with a list of attributes they can
        // filter on for their chosen root event.
        showDlgAttributes();
      } else {
        UtilUI.showAlert(v.getContext(), getString(R.string.sorry),
            getString(R.string.add_filter_alert));
        return;
      }
    }
  };

  private OnClickListener listenerBtnClickAddAction = new OnClickListener() {
    public void onClick(View v) {
      // For actions, we don't check what node the user has selected since they must always go to
      // the end of the rule tree.
      showDlgApplications();
    }
  };

  private void deleteItem(int position) {
    // TODO: (markww) Prompt user 'are you sure you want to delete this item?'.
    ModelItem item = adapterRule.getItem(position);
    if ((item instanceof ModelRuleFilter) || (item instanceof ModelRuleAction)) {
      adapterRule.removeItem(position);
    } else {
      UtilUI.showAlert(this, getString(R.string.sorry), getString(R.string.select_filter_delete));
    }
  }

  private void editItem(int position) {
    ModelItem item = adapterRule.getItem(position);
    if (item instanceof ModelRuleFilter) {
      editFilter(position, (ModelRuleFilter) item);
    } else if (item instanceof ModelRuleAction) {
      editAction(position, (ModelRuleAction) item);
    } else {
      UtilUI.showAlert(this, getString(R.string.sorry), getString(R.string.select_filter_edit));
    }
  }

  private OnClickListener listenerBtnClickSaveRule = new OnClickListener() {
    public void onClick(View v) {

      // After press save, the rule is set to 'Enabled' as default
      RuleBuilder.instance().getRule().setIsEnabled(true);
      // Before launching the new activity, wipe its UI state.
      ActivityDlgRuleName.resetUI(v.getContext());

      // Ask the user for a new rule name.
      // The result will be stored inside RuleBuilder.
      // When the activity returns, we can continue trying to save the rule.
      Intent intent = new Intent();
      intent.setClass(getApplicationContext(), ActivityDlgRuleName.class);
      startActivityForResult(intent, REQUEST_SET_RULE_NAME);
    }
  };

  /**
   * Starts up the initial activity for adding a new filter.
   */
  private void showDlgAttributes() {
    // Reset the chosen filter data if left over from a previous run.
    RuleBuilder.instance().resetFilterPath();

    ActivityDlgAttributes.resetUI(this);

    // Launch the activity chain for adding a filter.
    // We check if the user completed a
    // filter in onActivityResult.
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), ActivityDlgAttributes.class);
    startActivityForResult(intent, REQUEST_ADD_FILTER);
  }

  /**
   * Starts up the initial activity for adding a new action.
   */
  private void showDlgApplications() {
    // Reset the chosen action data if left over from a previous run.
    RuleBuilder.instance().resetActionPath();

    ActivityDlgApplications.resetUI(this);

    // Launch the activity chain for adding an action.
    // We check if the user completed an action in onActivityResult.
    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), ActivityDlgApplications.class);
    startActivityForResult(intent, REQUEST_ADD_ACTION);
  }

  /**
   * Shortcuts directly to {@link ActivityDlgFilterInput} for editing an existing filter.
   */
  private void editFilter(int position, ModelRuleFilter filter) {
    // Set the filter data from the existing rule filter instance.
    RuleBuilder.instance().resetFilterPath();
    RuleBuilder.instance().setChosenModelFilter(filter.getModelFilter());
    RuleBuilder.instance().setChosenRuleFilterDataOld(filter.getData());

    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), ActivityDlgFilterInput.class);
    startActivityForResult(intent, REQUEST_EDIT_FILTER);
  }

  /**
   * Shortcuts directly to {@link ActivityDlgActionInput} for editing an existing action.
   */
  private void editAction(int position, ModelRuleAction ruleAction) {
    // Set the action data from the existing rule action instance.
    ModelAction action = ruleAction.getModelAction();
    RuleBuilder ruleBuilder = RuleBuilder.instance();
    ruleBuilder.resetFilterPath();
    ruleBuilder.setChosenModelAction(ruleAction.getModelAction());
    ruleBuilder.setChosenRuleActionDataOld(ruleAction.getDatas());

    // Set the application in preparation for activities using user accounts
    ModelApplication app = UIDbHelperStore.instance().db().getApplicationFromAction(action);
    ruleBuilder.setChosenApplication(app);

    Intent intent = new Intent();
    intent.setClass(getApplicationContext(), ActivityDlgActionInput.class);
    startActivityForResult(intent, REQUEST_EDIT_ACTION);
  }

  /**
   * Saves the built-up rule inside {@link RuleBuilder} to the database.
   * 
   */
  private void saveRule() {
    Log.d(TAG, "entered saveRule()");
    long newRuleId = 0;
    try {
      newRuleId = UIDbHelperStore.instance().db().saveRule(RuleBuilder.instance().getRule());
    } catch (IllegalStateException e) {
      UtilUI.showAlert(this, getString(R.string.sorry),
          getString(R.string.illegal_state_exception_catch));
      Log.e(TAG, "Save Rule Error: Caught Illegal State Exception when saving", e);
      return;
    } catch (Exception e) {
      // TODO Auto-generated catch block
      UtilUI.showAlert(this, getString(R.string.sorry), getString(R.string.error_saving_rule));
      Log.e(TAG, "Save Rule Error: Caught an error when saving", e);
      return;
    }
    Log.d(TAG, "New rule saved.");
    UtilUI.showAlert(this, "Save Rule", "Rule saved ok!");

    // We have to reload the rule from the database now so that the UI representation
    // of it is in-sync with the new element IDs assigned during the save operation.
    RuleBuilder.instance().resetForEditing(UIDbHelperStore.instance().db().loadRule(newRuleId));
    Log.d(TAG, "Save Rule: new rule reloaded from db");
    adapterRule.setRule(RuleBuilder.instance().getRule());
    Log.d(TAG, "Save Rule: new rule set");
    setResult(RESULT_RULE_SAVED);
    finish();
  }
}