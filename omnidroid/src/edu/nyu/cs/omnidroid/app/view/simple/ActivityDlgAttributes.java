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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import edu.nyu.cs.omnidroid.app.R;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelAttribute;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelEvent;
import edu.nyu.cs.omnidroid.app.view.simple.model.ModelRuleFilter;

/**
 * This dialog shows a list of attributes linked to the selected root event. After the user selects
 * an attribute to filter on, we move them to the {@link ActivityDlgFilters} dialog.
 */
public class ActivityDlgAttributes extends Activity {
  private ListView listView;
  private AdapterAttributes adapterAttributes;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Link up controls from the xml layout resource file.
    initializeUI();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // If the user constructed a valid filter, also kill ourselves.
    ModelRuleFilter filter = RuleBuilder.instance().getChosenRuleFilter();
    if (filter != null) {
      finish();
    }
  }

  private void initializeUI() {
    setContentView(R.layout.activity_dlg_attributes);

    ModelEvent event = RuleBuilder.instance().getChosenEvent();
    adapterAttributes = new AdapterAttributes(this, event);

    setTitle(event.getTypeName() + " Attributes");

    listView = (ListView) findViewById(R.id.activity_dlg_attributes_listview);
    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    listView.setAdapter(adapterAttributes);
    
    listView.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // Store the selected attribute in the RuleBuilder so the next activity can pick it up.
        RuleBuilder.instance().setChosenAttribute(adapterAttributes.getItem(position));

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ActivityDlgFilters.class);
        startActivityForResult(intent, ActivityChooseFiltersAndActions.REQUEST_ADD_FILTER);
      }
    });

    TextView mTextViewInfo = (TextView) findViewById(R.id.activity_dlg_attributes_tv_info1);
    mTextViewInfo.setText("Select an attribute of [" + event.getTypeName() + "] to filter on:");

    UtilUI.inflateDialog((LinearLayout) findViewById(R.id.activity_dlg_attributes_ll_main));
  }

  /**
   * Here we display attributes associated with our parent root event.
   */
  private class AdapterAttributes extends BaseAdapter {
    private Context context;
    private final List<ModelAttribute> attributes;

    public AdapterAttributes(Context context, ModelEvent eventRoot) {
      this.context = context;

      // Fetch all available attributes for the root event from the
      // database.
      attributes = UIDbHelperStore.instance().db().getAttributesForEvent(eventRoot);
    }

    public int getCount() {
      return attributes.size();
    }

    public ModelAttribute getItem(int position) {
      return attributes.get(position);
    }

    public long getItemId(int position) {
      return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

      LinearLayout ll = new LinearLayout(context);
      ll.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
      ll.setMinimumHeight(50);
      ll.setOrientation(LinearLayout.HORIZONTAL);
      ll.setGravity(Gravity.CENTER_VERTICAL);

      ImageView iv = new ImageView(context);
      iv.setImageResource(attributes.get(position).getIconResId());
      iv.setAdjustViewBounds(true);
      iv.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,
          LayoutParams.WRAP_CONTENT));
      if (listView.getCheckedItemPosition() == position) {
        iv.setBackgroundResource(R.drawable.icon_hilight);
      }

      TextView tv = new TextView(context);
      tv.setText(attributes.get(position).getDescriptionShort());
      tv.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
      tv.setGravity(Gravity.CENTER_VERTICAL);
      tv.setPadding(10, 0, 0, 0);
      tv.setTextSize(14.0f);
      tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
      tv.setTextColor(context.getResources().getColor(R.color.list_element_text));
      tv.setMinHeight(46);

      ll.addView(iv);
      ll.addView(tv);

      return ll;
    }
  }
}