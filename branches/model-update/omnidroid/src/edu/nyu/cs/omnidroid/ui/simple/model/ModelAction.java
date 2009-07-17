/*******************************************************************************
 * Copyright 2009 OmniDroid - http://code.google.com/p/omnidroid 
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
package edu.nyu.cs.omnidroid.ui.simple.model;



/**
 * UI representation of an action. Actions always have an associated
 * parent application. 
 */
public class ModelAction extends ModelItem {
	
  /** An action always has a parent application. */
  private final ModelApplication mApplication;
  
  /** This action's database ID. */
  private final int mDatabaseId;
  
  
  public ModelAction(String typeName, 
		             String description, 
		             int iconResId, 
		             int databaseId, 
		             ModelApplication application) 
  {
	  super(typeName, description, iconResId);
	  mApplication = application;
	  mDatabaseId = databaseId;
  }
  
  public int getDatabaseId() {
    return mDatabaseId;
  }
  
  public String getDescriptionShort() {
    return getTypeName();
  }
  
  public ModelApplication getApplication() {
	  return mApplication;
  }
}