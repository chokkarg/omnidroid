/**
 * TODO (pradeep): Use OmniHandler as the public interface instead of Hashmap.  You
 * can do this by moving the HashMap structure inside the OmniHandler class
 * 
 * @author: Pradeep Varma
 */
package edu.nyu.cs.omnidroid.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;

/**
 * Used to Specify the User Config schema
 * 
 * @param ParserType
 *          Specify UC for UserConfig and AC for Application Config
 */
public class UGParser {
  // Public class vars
  // TODO(Pradeep): public static final String KEY_ID = "ID";
  public static final String KEY_INSTANCE_NAME = "InstanceName";
  public static final String KEY_EVENT_APP = "EventApp";
  public static final String KEY_EVENT_TYPE = "EventName";
  public static final String KEY_FILTER_TYPE = "FilterType";
  public static final String KEY_FILTER_DATA = "FilterData";
  public static final String KEY_ACTION_APP = "ActionApp";
  public static final String KEY_ACTION_TYPE = "ActionName";
  public static final String KEY_ACTION_DATA1 = "ActionData";
  public static final String KEY_ACTION_DATA2 = "ActionData2";
  public static final String KEY_ENABLE_INSTANCE = "EnableInstance";
  public static final String KEY_ENABLE_OMNIDROID = "EnableOmniDroid";

  // String matching constants
  public static final String TRUE = "True";
  public static final String FALSE = "False";

  // Private class vars
  private ArrayList<String> Schema;
  private FileOutputStream fout;
  private OutputStreamWriter osw;
  private FileInputStream FIn;
  private BufferedInputStream bis;
  private DataInputStream dis;
  private Context context;
  private static final int MODE_WRITE = android.content.Context.MODE_WORLD_WRITEABLE;
  private static final int MODE_APPEND = android.content.Context.MODE_APPEND;
  
  // TODO (acase): We should really ditch this ConfigFile idea and switch to using
  // internal CPs for all this data

  /**
   * Initializes the parser to be a User Config or App Config based on the parameter
   * 
   * @param context
   *          Specify the context of the Application
   */
  public UGParser(Context context) {

    // Defining the User Config Schema in ArrayList
    Schema = new ArrayList<String>();
    // TODO(Pradeep): Schema.add("ID");
    Schema.add("InstanceName");
    Schema.add("EventName");
    Schema.add("EventApp");
    Schema.add("FilterType");
    Schema.add("FilterData");
    Schema.add("ActionApp");
    Schema.add("ActionName");
    Schema.add("ActionData");
    Schema.add("ActionData2");
    Schema.add("EnableInstance");
    this.context = context;
  }

  /**
   * Opens User Config for writing
   * 
   */
  private void OpenFileWrite(int mode) {
    try {
      fout = context.openFileOutput("UserConfig.txt", mode);
      osw = new OutputStreamWriter(fout);
    } catch (FileNotFoundException e) {
      OmLogger.write(context, "Unable to Open User Config to write");
    }
  }

  /**
   * Opens User Config for reading
   * 
   */
  private void OpenFileRead() {
    try {
      FIn = context.openFileInput("UserConfig.txt");
      bis = new BufferedInputStream(FIn);
      dis = new DataInputStream(bis);
    } catch (FileNotFoundException e) {
      OmLogger.write(context, "Unable to Open User Config to write");
    }
  }

  /**
   * deletes the entire userConfig except the Enabled Field.
   * 
   */
  public void delete_all() {
    try {
      // FIXME: Allow enabled for global
      // String Enabled = readLine("Enabled");
      // if (Enabled.equals(null))
      // Enabled = "True";
      // String LineString = new String("Enabled" + ":" + Enabled + "\n");
      OpenFileWrite(MODE_WRITE);
      osw.write("");
      // osw.write(LineString);
      osw.flush();
      osw.close();
    } catch (Exception e) {
      OmLogger.write(context, "Could not delete Instances");
    }
  }

  public void setEnabled(boolean enabled) {
    // Store our entries
    ArrayList<HashMap<String,String>> records = readRecords();

    // Erase all data
    delete_all();

    try {
      // FIXME: Setup enable/disable
      String strEnabled;
      if (enabled == true) {
        strEnabled = TRUE;
      } else { 
        strEnabled = FALSE;
      }
      OpenFileWrite(MODE_WRITE);
      //Deleting all lines
      osw.write(KEY_ENABLE_OMNIDROID + ":" + enabled+"\n");
      osw.flush();
      osw.close();
    } catch (Exception e) {
      OmLogger.write(context, "Could not delete Instances");
    }

    // Write entries back
//    HashMap<String,String> record;
    for (HashMap<String,String> record : records) {
      writeRecord(record);
    }
    
  }
  
  /**
   * deletes the Record from userConfig.
   * 
   * @param HM
   *          HashMap of the record to be deleted.
   */
  public boolean deleteRecord(HashMap<String, String> HM) {
    try {
      ArrayList<HashMap<String, String>> UCRecords = readRecords();
      ArrayList<HashMap<String, String>> UCRecords_New = readRecords();

      Iterator<HashMap<String, String>> i = UCRecords.iterator();
      while (i.hasNext()) {
        HashMap<String, String> HM1 = i.next();
        if (HM1.equals(HM))
          continue;
        UCRecords_New.add(HM1);
      }
      delete_all();

      Iterator<HashMap<String, String>> i1 = UCRecords_New.iterator();
      while (i1.hasNext()) {
        HashMap<String, String> HM1 = i.next();
        writeRecord(HM1);
      }
      return true;
    } catch (Exception e) {
      OmLogger.write(context, "Could not delete Instance Record");
      return false;
    }
  }

  /**
   * deletes the Record from userConfig.
   * 
   * @param InstanceName
   *          InstanceName of the record to be deleted.
   */
  public boolean deleteRecordbyInstance(String InstanceName) {
    try {
      ArrayList<HashMap<String, String>> UCRecords = readRecords();
      ArrayList<HashMap<String, String>> UCRecords_New = new ArrayList<HashMap<String, String>>();

      Iterator<HashMap<String, String>> i = UCRecords.iterator();
      while (i.hasNext()) {
        HashMap<String, String> HM1 = i.next();
        if (HM1.get(KEY_INSTANCE_NAME).equals(InstanceName))
          continue;
        UCRecords_New.add(HM1);
      }
      delete_all();

      Iterator<HashMap<String, String>> i1 = UCRecords_New.iterator();

      while (i1.hasNext()) {
        HashMap<String, String> HM1 = i1.next();
        writeRecord(HM1);
      }
      return true;
    } catch (Exception e) {
      OmLogger.write(context, "Could not delete Instance Record");
      return false;
    }
  }

  /**
   * Writes a Key Value into the UserConfig as Key:Value
   * 
   * @param Key
   *          Specify the Key to be written
   * @param Value
   *          Specify the Value to be written
   * @return Returns true if successful
   */
  public boolean write(String key, String val) {
    try {
      final String LineString = new String(key + ":" + val + "\n");
      OpenFileWrite(MODE_APPEND);
      osw.write(LineString);
      osw.flush();
      osw.close();
      return true;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to write line in User Config");
      return false;
    }
  }

  /**
   * Updates a Key Value with the val passed
   * 
   * @param Key
   *          Specify the Key to be updated
   * @param Value
   *          Specify the Value to be written
   * @return Returns true if successful
   */
  public boolean update(String key, String val) {
    try {
      OpenFileRead();
      String line;
      ArrayList<String> lines = new ArrayList<String>();
      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (!parts[0].toString().equalsIgnoreCase(key)) {
          lines.add(line);
        }

      }
      OpenFileWrite(MODE_WRITE);
      String LineString = new String(key + ":" + val + "\n");
      osw.write(LineString);
      Iterator<String> i = lines.iterator();
      while (i.hasNext()) {
        osw.write(i.next());
      }
      osw.flush();
      osw.close();
      return true;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to write line in User Config");
      return false;
    }
  }

  /**
   * Reads the value of the key
   * 
   * @param Key
   *          Specify the key
   * @return Returns value of the key
   */
  public String readLine(String key) {
    try {
      dis.close();
      bis.close();
    } catch (Exception e) {
    }
    String col2 = "";
    try {
      OpenFileRead();
      String line;

      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase(key)) {
          col2 = parts[1].toString();
          break;
        }
      }
      dis.close();
      bis.close();
      return col2;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Line from User Config");
      e.printStackTrace();
      return col2;
    }
  }

  /**
   * Reads values from the UserConfig based on the Key
   * 
   * @param Key
   *          Specify the Key to be read. example ActionName, EventName
   * @return Returns values as ArrayList of Strings
   */
  public ArrayList<String> readLines(String key) {
    ArrayList<String> cols2 = new ArrayList<String>();
    try {
      dis.close();
      bis.close();
    } catch (Exception e) {
    }
    try {
      dis.close();
      bis.close();
    } catch (Exception e) {
    }
    String val;
    try {
      OpenFileRead();
      String line;

      while ((line = dis.readLine()) != null) {
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase(key)) {
          val = parts[1].toString();
          cols2.add(val);
        }
      }
      return cols2;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Line from User Config");
      return cols2;
    }
  }

  /**
   * Reads Instance Records from the UserConfig
   * 
   * @return Returns Array List of HashMaps. HashMaps have the keys as EventName, EventApp,
   *         FilterType, FilterData, ActionName, ActionApp, AppData,ActionData2,EnableInstance
   */
  public ArrayList<HashMap<String, String>> readRecords() {
    ArrayList<HashMap<String, String>> UCRecords = new ArrayList<HashMap<String, String>>();
    try {
      dis.close();
      bis.close();
    } catch (Exception e) {
    }
    try {
      OpenFileRead();
      String line = "";

      while ((line = dis.readLine()) != null) {
        try
        {
        HashMap<String, String> HM = new HashMap<String, String>();
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase("InstanceName")) {

          Iterator<String> i = Schema.iterator();
          String key;
          while (i.hasNext()) {
            key = i.next();
            HM.put(key, line.split(":", 2)[1].toString());
            if (!line.split(":", 2)[0].toString().equalsIgnoreCase("EnableInstance"))
              line = dis.readLine();
          }
          UCRecords.add(HM);
        }
        }catch(Exception e){}

      }
      return UCRecords;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Line from User Config");
      return UCRecords;
    }
  }

  /**
   * Reads Instance Records from the UserConfig based on the InstanceName passed
   * 
   * @param Key
   *          InstanceName to be passed.
   * @return Returns HashMap.
   * */
  public HashMap<String, String> readRecord(String Key) {
    HashMap<String, String> HM = new HashMap<String, String>();
    try {
      OpenFileRead();
      String line = "";

      while ((line = dis.readLine()) != null) {

        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase("InstanceName")
            && parts[1].toString().equalsIgnoreCase(Key)) {
          Iterator<String> i = Schema.iterator();
          String key;
          while (i.hasNext()) {
            key = i.next();
            HM.put(key, line.split(":", 2)[1].toString());
            if (!line.split(":", 2)[0].toString().equalsIgnoreCase("EnableInstance"))
              line = dis.readLine();
          }
        }

      }
      return HM;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read record from User Config");
      return HM;
    }
  }

  /**
   * Writes an Instance in the UserConfig File
   * 
   * @param HM
   *          HashMap containing EventName, EventApp, FilterType, FilterData, ActionName, ActionApp,
   *          AppData,Actiondata2,EnableInstance
   * @return Returns 1 if successful else 0.
   */
  public int writeRecord(HashMap<String, String> HM) {
    try {
      Iterator<String> i = Schema.iterator();
      String key;
      while (i.hasNext()) {
        key = i.next();
        if(HM.containsKey(key))
        write(key, HM.get(key).toString());
      
      }
      return 1;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to write record t Usoer Config");
      return 0;
    }
  }

  /**
   * Updates the Instance Record
   * 
   * @param HM
   *          HashMap containing EventName, EventApp, FilterType, FilterData, ActionName, ActionApp,
   *          AppData,Actiondata2,EnableInstance
   * @return Returns true if successful else false.
   */
  public boolean updateRecord(HashMap<String, String> HM) {
    try {
      deleteRecordbyInstance(HM.get(KEY_INSTANCE_NAME));
      writeRecord(HM);
      return true;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to write record from User Config");
      return false;
    }
  }

  /**
   * Reads Instance Records from the UserConfig based on the EventName passed
   * 
   * @param Key
   *          EventName to be passed.
   * @return Returns ArrayList.
   * */
  public ArrayList<HashMap<String, String>> readbyEventName(String Key) {
    ArrayList<HashMap<String, String>> UCRecords = new ArrayList<HashMap<String, String>>();
    try {
      dis.close();
      bis.close();
    } catch (Exception e) {
    }
    try {
      OpenFileRead();
      String line = "";

      while ((line = dis.readLine()) != null) {
        HashMap<String, String> HM = new HashMap<String, String>();
        String[] parts = line.split(":", 2);
        if (parts[0].toString().equalsIgnoreCase("EventName")
            && parts[1].toString().equalsIgnoreCase(Key)) {
          Iterator<String> i = Schema.iterator();
          i.next();// Ignoring Instance Name
          String key;
          while (i.hasNext()) {
            key = i.next();
            HM.put(key, line.split(":", 2)[1].toString());
            if (!line.split(":", 2)[0].toString().equalsIgnoreCase("EnableInstance"))
              line = dis.readLine();
          }
          UCRecords.add(HM);
        }

      }

      return UCRecords;
    } catch (Exception e) {
      OmLogger.write(context, "Unable to read Line from User Config");
      return UCRecords;
    }
  }

  /**
   * @return
   */
  public UGParser getInstance() {
    // TODO Auto-generated method stub
    return null;
  }

}