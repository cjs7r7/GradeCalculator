package edu.umkc.connor.gradecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;

public class Database {

    static String TAG = "Grade";

    //Only 1 open at a time
    private static Database db;

    private static SQLiteDatabase database;
    private static DBHelper dbHelper;

    private static final String[] classColumns = {DBHelper.keyCol, DBHelper.nameCol};
    private static final String[] gsColumns = {DBHelper.keyCol, DBHelper.classidCol, DBHelper.nameCol, DBHelper.weightCol};
    private static final String[] assnColumns = {DBHelper.keyCol, DBHelper.gsIdCol, DBHelper.nameCol, DBHelper.scoreCol, DBHelper.ptsPosCol};

    //This should only be called once, otherwise call Get Instance
    private Database(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.i(TAG, "Closing DB");
        database.close();
        dbHelper.close();
        db = null;
    }

    //Add a new Class item to the table classes
    public int addClass(String name) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.nameCol, name);
        database.insert(DBHelper.classTable, null, values);
        return getKey(name, 'c', 0);
    }

    //Add a new GS item to the GS table
    public int addGS(int classid, GradeScaleItem gs) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.classidCol, classid);
        values.put(DBHelper.nameCol, gs.getName());
        values.put(DBHelper.weightCol, gs.getPercentage());
        database.insert(DBHelper.gsTable, null, values);
        return getKey(gs.getName(), 'g', classid);
    }

    //Add a new Assn item to the Assn table
    public int addAssn(int gsitemid, AssignmentItem assn) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.gsIdCol, gsitemid);
        values.put(DBHelper.nameCol, assn.getName());
        values.put(DBHelper.scoreCol, assn.getScore());
        values.put(DBHelper.ptsPosCol, assn.getTotalPoints());
        database.insert(DBHelper.assnTable, null, values);
        return getKey(assn.getName(), 'a', gsitemid);
    }

    //Get all the classes in the class table
    public ArrayList<String> getAllClasses() {
        ArrayList<String> arrlist = new ArrayList<String>();
        try {
            Cursor cursor = database.query(DBHelper.classTable, classColumns, null, null, null, null, null);
            cursor.moveToFirst();

            //Get Data
            do {
                arrlist.add(cursor.getString(1));
            } while (cursor.moveToNext());
            return arrlist;
            //Nothing in the table returns error
        } catch (IllegalStateException e1) {
            return arrlist;
        } catch(CursorIndexOutOfBoundsException e2) {
            return arrlist;
        }
    }

    //Get Specific GSITEM
    public GradeScaleItem getGS(int key) {
        try {
            Cursor cursor = database.query(DBHelper.gsTable, gsColumns, DBHelper.keyCol + " = ?", new String[]{"" + key}, null, null, null);
            cursor.moveToFirst();
            Log.i(TAG, cursor.getString(2));
            return new GradeScaleItem(cursor.getString(2), cursor.getDouble(3));
        } catch(CursorIndexOutOfBoundsException e) {
            return null;
        }
    }

    //Get All GS Items in gs Table
    public ArrayList<GradeScaleItem> getAllGS(int class_id) {
        ArrayList<GradeScaleItem> arrlist = new ArrayList<GradeScaleItem>();
        try {
            Cursor cursor = database.query(DBHelper.gsTable, gsColumns, DBHelper.classidCol + " = ?", new String[] {"" + class_id}, null, null, null);
            cursor.moveToFirst();

            //Get Data
            do {
                arrlist.add(new GradeScaleItem(cursor.getString(2), cursor.getDouble(3)));
            } while (cursor.moveToNext());
            return arrlist;
            //Nothing in the table returns error
        } catch (IllegalStateException e) {
            return arrlist;
        } catch(CursorIndexOutOfBoundsException e2) {
        return arrlist;
        }
    }

    //Get an AssnItem
    public AssignmentItem getAssn(int key) {
        try {
            Cursor cursor = database.query(DBHelper.assnTable, assnColumns, DBHelper.keyCol + " = ?", new String[] {"" + key}, null, null, null);
            cursor.moveToFirst();
            return new AssignmentItem(cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4));
        } catch (CursorIndexOutOfBoundsException e) {
            return null;
        }
    }

    //Get All Assignment Items in Assn Table
    public ArrayList<AssignmentItem> getAllAssn(int gsID) {
        ArrayList<AssignmentItem> arrlist = new ArrayList<AssignmentItem>();
        try {
            Cursor cursor = database.query(DBHelper.assnTable, assnColumns, DBHelper.gsIdCol + " = ?",
                    new String[] {"" + gsID}, null, null, null);
            cursor.moveToFirst();

            //Get Data
            do {
                arrlist.add(new AssignmentItem(cursor.getString(2), cursor.getDouble(3), cursor.getDouble(4)));
            } while (cursor.moveToNext());
            return arrlist;
            //Nothing in the table returns error
        } catch (IllegalStateException e) {
            return arrlist;
        } catch(CursorIndexOutOfBoundsException e2) {
            return arrlist;
        }
    }

    /* Locate the index of a certain name, even tell if it is in the table already
    * ParentID is the id of the parent object, Class has no parent
    * Class is the parent of GS, and GS is the parent of Assn*/
    public int getKey(String name, char table, int parentid) {
        //Set up variables
        Cursor cursor;
        try {
            switch (table) {
                case 'a':
                    Log.i(TAG, "Looking through assn for " + name + "in gs id: " + parentid);
                    cursor = database.query(DBHelper.assnTable, assnColumns, DBHelper.nameCol + " = ? AND " + DBHelper.gsIdCol + " = ?",
                            new String[] {name, "" + parentid}, null, null, null);
                    break;
                case 'c':
                    cursor = database.query(DBHelper.classTable, classColumns, DBHelper.nameCol + " = ?",
                            new String[] {name}, null, null, null);
                    break;
                case 'g':
                    Log.i(TAG, "Looking through gs for " + name + "in class id: " + parentid);
                    cursor = database.query(DBHelper.gsTable, gsColumns, DBHelper.nameCol + " = ? AND "+ DBHelper.classidCol + " = ?",
                            new String[] {name, "" + parentid}, null, null, null);
                    break;
                default:
                    Log.i(TAG, "Default in GetKey");
                    return -1;
            }

            cursor.moveToFirst();
            Log.i(TAG,"Num: " + cursor.getInt(0));
            return cursor.getInt(0);
        } catch (CursorIndexOutOfBoundsException e) {
            Log.i(TAG, "Item not found in Database");
            return -1;
        }
    }

    public double getGrade(int classid) {
        double score = 0;
        Cursor cursor = database.query(DBHelper.gsTable, gsColumns, DBHelper.classidCol + " = ?",
                new String[] {"" + classid}, null, null, null);
        cursor.moveToFirst();
        do {
            try {
                int gskey = cursor.getInt(0);
                double weight = cursor.getDouble(3);

                //Get info from the Assns
                Cursor cursor2 = database.query(DBHelper.assnTable, assnColumns, DBHelper.gsIdCol + " = ?",
                        new String[]{"" + gskey}, null, null, null);

                double sumscores = 0, totpoints = 0;
                cursor2.moveToFirst();
                do {
                    sumscores += cursor2.getDouble(3);
                    totpoints += cursor2.getDouble(4);
                } while (cursor2.moveToNext());
                score += weight * (sumscores / totpoints);
            } catch(CursorIndexOutOfBoundsException e) {

            }

        } while (cursor.moveToNext());


        return score;
    }

    //delete a line from a table
    public void deleteClass(String name) {
        int key = getKey(name, 'c', 0);
        try {
            database.delete(DBHelper.classTable, dbHelper.nameCol + " = ?", new String[] {name});
            Cursor cursor = database.query(DBHelper.gsTable, gsColumns, DBHelper.classidCol + " = ?", new String[] {"" + key}, null, null, null);
            cursor.moveToFirst();
            //Iterate through all GS items that are under the Class
            do {
                deleteGS(cursor.getString(2), key);
                Log.i(TAG, "Deleting GS item: " + cursor.getString(2));
            } while (cursor.moveToNext());

        } catch (CursorIndexOutOfBoundsException e) {
            //No Grade Scale
            Log.i(TAG, "Deleted " + name + " No GS Items");
        }
    }

    //Delete GS and All subsequent Assn
    public void deleteGS(String name, int classid) {
        int key = getKey(name, 'g', classid);
        try {
            database.delete(DBHelper.gsTable, DBHelper.nameCol + " = ?", new String[]{name});
            database.delete(DBHelper.assnTable, DBHelper.gsIdCol + "= ?", new String[] {"" + key});
        } catch (CursorIndexOutOfBoundsException e) {
            //No Assignments
            Log.i(TAG, "Deleted " + name + " No assignments");
        }
    }

    //Delete Assignment
    public void deleteAssn(String name, int gsid) {
        database.delete(DBHelper.assnTable, dbHelper.nameCol + " = ? AND " + dbHelper.gsIdCol + " = ?",
                new String[] {name, "" + gsid});
    }

    //Get the instance if there is one, otherwise create one
    //Must be used like this Database db = Database.getInstance(this);
    static public Database getInstance(Context context) {
        if (db == null) {
            db = new Database(context);
            Log.i("Grade", "Database Created");
        } else {
            Log.i("Grade", "I have instance");
        }
        return db;
    }

}
