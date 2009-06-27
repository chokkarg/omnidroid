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
package edu.nyu.cs.omnidroid.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

/**
 * Database helper class for the RegisteredEventAttributes table. Defines basic CRUD methods.
 * TODO(ehotou) document about this table.
 */
public class RegisteredEventAttributeDbAdapter extends DbAdapter {
  
  /* Column names */
  public static final String KEY_EVENTATTRIBUTEID = "EventAttributeID";
  public static final String KEY_EVENTATTRIBUTENAME = "EventAttributeName";
  public static final String KEY_EVENTID = "FK_EventID";
  public static final String KEY_DATATYPEID = "FK_DataTypeID";
  
  /* An array of all column names */
  public static final String[] KEYS = 
      {KEY_EVENTATTRIBUTEID, KEY_EVENTATTRIBUTENAME, KEY_EVENTID, KEY_DATATYPEID};
  
  /* Table name */
  private static final String DATABASE_TABLE = "RegisteredEventAttributes";
  
  /* Create and drop statement. */
  protected static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" +
      KEY_EVENTATTRIBUTEID + " integer primary key autoincrement, " + 
      KEY_EVENTATTRIBUTENAME + " text not null, " +
      KEY_EVENTID + " integer, " +
      KEY_DATATYPEID + " integer);";
  protected static final String DATABASE_DROP = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
  
  /**
   * Constructor.
   * @param database is the database object to work within.
   */
  public RegisteredEventAttributeDbAdapter(SQLiteDatabase database) {
    super(database);
  }
  
  /**
   * Insert a new RegisteredEventAttribute record.
   * @param attributeName is the name of the event attribute.
   * @param eventID is the id of the event it belongs to.
   * @param dataTypeID is the id of data type it has.
   * @return attribute id or -1 if creation failed.
   * @throw IllegalArgumentException if there is null within parameters
   */
  public long insert(String attributeName, Long eventID, Long dataTypeID) {
    if (attributeName == null || eventID == null || dataTypeID == null) {
      throw new IllegalArgumentException("insert parameter null.");
    }
    ContentValues initialValues = new ContentValues();
    initialValues.put(KEY_EVENTATTRIBUTENAME, attributeName);
    initialValues.put(KEY_EVENTID, eventID);
    initialValues.put(KEY_DATATYPEID, dataTypeID);
    // Set null because don't use 'null column hack'.
    return database.insert(DATABASE_TABLE, null, initialValues);
  }
  
  /**
   * Delete a RegisteredEventAttribute record
   * @param attributeID is the id of the record.
   * @return true if success, or false otherwise.
   * @throw IllegalArgumentException if attributeID is null
   */
  public boolean delete(Long attributeID) {
    if (attributeID == null) {
      throw new IllegalArgumentException("primary key null.");
    }
    // Set the whereArgs to null here.
    return database.delete(DATABASE_TABLE, KEY_EVENTATTRIBUTEID + "=" + attributeID, null) > 0;
  }
  
  /**
   * Delete all RegisteredEventAttribute records.
   * @return true if success, or false if failed or nothing to be deleted.
   */
  public boolean deleteAll() {
    // Set where and whereArgs to null here.
    return database.delete(DATABASE_TABLE, null, null) > 0;
  }
  
  /**
   * Return a Cursor pointing to the record matches the attributeID.
   * @param attributeID is the attribute id
   * @return a Cursor pointing to the found record.
   * @throw IllegalArgumentException if attributeID is null
   */
  public Cursor fetch(Long attributeID) {
    if (attributeID == null) {
      throw new IllegalArgumentException("primary key null.");
    }
    // Set selectionArgs, groupBy, having, orderBy and limit to be null.
    Cursor mCursor = database.query(true, DATABASE_TABLE, KEYS, 
        KEY_EVENTATTRIBUTEID + "=" + attributeID, null, null, null, null, null);
    if (mCursor != null) {
      mCursor.moveToFirst();
    }
    return mCursor;
  }
  
  /**
   * @return a Cursor that contains all RegisteredEventAttribute records.
   */
  public Cursor fetchAll() {
    // Set selections, selectionArgs, groupBy, having, orderBy to null to fetch all rows.
    return database.query(DATABASE_TABLE, KEYS, null, null, null, null, null);
  }

  /**
   * Return a Cursor contains all RegisteredEventAttribute records which matches the parameters.
   * @param attributeName is the attribute name, or null to fetch any attributeName
   * @param eventID is the event id, or null to fetch any eventID
   * @param dataTypeID is the dataType id, or null to fetch any dataTypeID
   * @return a Cursor contains all RegisteredEventAttribute records which matches the parameters.
   */
  public Cursor fetchAll(String attributeName, Long eventID, Long dataTypeID) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    qb.setTables(DATABASE_TABLE);
    qb.appendWhere("1=1");
    if(attributeName != null) {
      qb.appendWhere(" AND " + KEY_EVENTATTRIBUTENAME + " = ");
      qb.appendWhereEscapeString(attributeName);
    }
    if(eventID != null) {
      qb.appendWhere(" AND " + KEY_EVENTID + " = " + eventID);
    }
    if(dataTypeID != null) {
      qb.appendWhere(" AND " + KEY_DATATYPEID + " = " + dataTypeID);
    }
    // Not using additional selections, selectionArgs, groupBy, having, orderBy, set them to null.
    return qb.query(database, KEYS, null, null, null, null, null);
  }
  
  /**
   * Update a RegisteredEventAttribute record with specific parameters.
   * @param attributeID is the id of the record to be updated.
   * @param attributeName is the attribute name, or null if not updating it.
   * @param eventID is the event id, or null if not updating it.
   * @param dataTypeID is the dataType id, or null if not updating it.
   * @return true if success, or false otherwise.
   * @throw IllegalArgumentException if attributeID is null
   */
  public boolean update(Long attributeID, String attributeName, Long eventID, Long dataTypeID) {
    if (attributeID == null) {
      throw new IllegalArgumentException("primary key null.");
    }
    ContentValues args = new ContentValues();
    if (attributeName != null) {
      args.put(KEY_EVENTATTRIBUTENAME, attributeName);
    }
    if (eventID != null) {
      args.put(KEY_EVENTID, eventID);
    }
    if (dataTypeID != null) {
      args.put(KEY_DATATYPEID, dataTypeID);
    }
    
    if (args.size() > 0) {
      // Set whereArg to null here
      return database.update(
          DATABASE_TABLE, args, KEY_EVENTATTRIBUTEID + "=" + attributeID, null) > 0;
    }
    return false; 
  }
  
}
