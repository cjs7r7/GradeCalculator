package edu.umkc.connor.gradecalculator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    //Database Helper that holds multiple tables
    //Tables have been separated but all tables have  keyCol and nameCol

    private static final String dbName = "Grade.db";
    private static final int dbVersion = 1;

    //Class Table
    public static final String classTable = "Classes";
    public static final String keyCol = "id";
    public static final String nameCol = "name";

    //create table "tableName"("keyCol" integer primary key autoincrement, "nameCol" text not null)
    private static final String createClassTable = "CREATE TABLE " + classTable + "("
            + keyCol + " Integer Primary Key Autoincrement, " + nameCol + " Text Not Null)";

    //GradeScale Table
    public static final String gsTable = "GradeScaleItems";;
    public static final String classidCol = "class_Id";
    public static final String weightCol = "GS_weight";

    //Create Table "tableName"("keyCol" integer primary key autoincrement, "classidCol" integer not null, "nameCol" text not null, "weight" float not null)
    private static final String createGSTable = "Create Table " + gsTable + "(" +
            keyCol + " Integer Primary Key Autoincrement, " +
            classidCol + " Integer not null, " +
            nameCol + " Text not null, " +
            weightCol + " Double Precision not null)";

    //Assn Table
    public static final String assnTable = "AssnItems";
    public static final String gsIdCol = "gs_Id";
    public static final String scoreCol = "assn_Score";
    public static final String ptsPosCol = "assn_PossiblePts";

    private static final String createAssnTable = "Create Table " + assnTable + "(" +
            keyCol + " Integer Primary key autoincrement, " +
            gsIdCol + " Integer not null, " +
            nameCol + " Text not null, " +
            scoreCol + " Double Precision, " +
            ptsPosCol + " Double Precision)";


    public DBHelper(Context context) {
        super(context, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createClassTable);
        db.execSQL(createGSTable);
        db.execSQL(createAssnTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop Table If Exists " + classTable);
        db.execSQL("Drop Table If Exists " + gsTable);
        db.execSQL("Drop Table If Exists " + assnTable);
        onCreate(db);
    }

}
