package kr.ac.kongju.witlab.uvit.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import kr.ac.kongju.witlab.uvit.model.ValueObject;

/**
 * Created by km on 2016-08-17.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
//    private static final String DATABASE_NAME = "UVit_DB";
    private static final String DATABASE_NAME = "WISET_DB";
    private static final String TABLE_NAME = "records";
    private static final String RSS_TABLE_NAME = "rss_records";
    private static final String PROFILE_TABLE_NAME = "profile";
    private static final String VITAMIND_TABLE_NAME = "BASE_VITAMIND";
    private static final String SKINTYPE_TABLE_NAME = "BASE_SKINTYPE";
    private static final String TREND_TABLE_NAME = "trend_table"; //trend 그래프 관련 table

    int[][] skinTomed = {{1, 200}, {2, 250}, {3, 300}, {4, 450}, {5, 600}, {6, 1000}};
    int[][] ageTovitamind = {{1, 2, 200, 1200}, {3, 5, 200, 1500}, //>=,<
            {6, 8, 200, 1600}, {9, 11, 200, 2400},
            {12, 64, 400, 4000}, {65, 100, 600, 4000}};

    String TAG = "SQLite";

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "DATE BLOB PRIMARY KEY, " +
                "TEMP REAL, " +
                "HUM REAL," +
                "UVI REAL," +
                "UVB REAL, " +
                "ACTION TEXT," +
                "VITAMIND REAL);";
        db.execSQL(sql);
//        1. NULL
//        2. INTEGER : 1, 2, 3, 4, 6, 8 bytes의 정수 값
//        3. REAL : 8bytes의 부동 소수점 값
//        4. TEXT : UTF-8, UTF-16BE, UTF-16LE 인코딩의 문자열
//        5. BLOB : 입력 된 그대로 저장

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //밴드 데이터 INSERT
    public synchronized void addData(ValueObject vo) {
        ContentValues values = new ContentValues();
        values.put("DATE", vo.getTime());
        values.put("TEMP", vo.getTemp());
        values.put("HUM", vo.getHum());
        values.put("UVI", vo.getUvi());
        values.put("UVB", vo.getUvb());
        values.put("ACTION", vo.getAction());
        values.put("VITAMIND", vo.getVitamind());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }
    //기상 RSS 데이터 INSERT
    public void addRSSData(ValueObject vo) {
        ContentValues values = new ContentValues();

        values.put("DATE", vo.getDate());
        values.put("DAY", vo.getDay());
        values.put("HOUR", vo.getHour());
        values.put("RSSTEMP", vo.getRsstemp());
        values.put("SKY", vo.getSky());
        values.put("PTY", vo.getPty());
        values.put("POP", vo.getPop());
        values.put("R12", vo.getR12());
        values.put("S12", vo.getS12());
        values.put("WS", vo.getWs());
        values.put("WDKOR", vo.getWdKor());
        values.put("REH", vo.getReh());

        SQLiteDatabase db = getWritableDatabase();

        db.insert(RSS_TABLE_NAME, null, values);
        Log.i(TAG, "rss insert 성공");
        db.close();
    }

    //개인 프로파일 INSERT
    public void addProfileData(ValueObject vo) {
        ContentValues values = new ContentValues();
        values.put("NAME", vo.getName());
        values.put("AGE", vo.getAge());
        values.put("GENDER", vo.getGender());
        values.put("SKINTYPE", vo.getSkintype());
        values.put("EXPOSURE_UPPER", vo.getExposure_upper());
        values.put("EXPOSURE_LOWER", vo.getExposure_lower());
        values.put("TARGETS_VITAMIND_SUFFICIENT", vo.getTargets_vitamind_sufficient());
        values.put("TARGETS_VITAMIND_UPPERLIMIT", vo.getTargets_vitamind_upperlimit());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(PROFILE_TABLE_NAME, null, values);
        Log.i(TAG, "profile insert 성공");
    }

    //기상청 온습도 SELECT
    public String[] getRSS() {
        String sql = "SELECT * FROM " + RSS_TABLE_NAME + " LIMIT 1 OFFSET (SELECT COUNT(*) FROM " + RSS_TABLE_NAME + ")-1";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //7
        arr[0] = cursor.getString(0);
        arr[1] =  cursor.getString(1);
        arr[2] =  cursor.getString(2);
        arr[3] = String.valueOf(cursor.getFloat(3));
        arr[4] =  cursor.getString(4);
        arr[5] = cursor.getString(5);
        arr[6] = String.valueOf(cursor.getFloat(6));
        arr[8] = String.valueOf(cursor.getFloat(8));
        arr[9] = String.valueOf(cursor.getFloat(9));
        arr[10] =  cursor.getString(10);
        arr[11] = String.valueOf(cursor.getFloat(11));
        for (int i = 0; i < arr.length; i++) {          Log.d(TAG, cursor.getColumnName(i)+" : "+arr[i]);        }

        String[] TnH = new String[2];
        TnH [0]=arr[3];
        TnH [1]=arr[11];

        db.close();
        return TnH;
    }

    public String[] getDUVTest() {
//        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE DATE=(SELECT MAX(DATE) FROM " + TABLE_NAME + ")";
//        String sql = "SELECT * FROM " + RSS_TABLE_NAME ;
        String sql = "SELECT * FROM " + TABLE_NAME ;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        Log.d(TAG, "커서"+String.valueOf(cursor.getCount()));
        Log.d(TAG, "컬럼"+String.valueOf(cursor.getColumnCount()));
        cursor.moveToFirst();
        String[] arrDATE = new String[cursor.getCount()]; //7
        String[] arrTEMP = new String[cursor.getCount()]; //7
        String[] arrHUM = new String[cursor.getCount()]; //7
        String[] arrUVI = new String[cursor.getCount()]; //7
        String[] arrUVB = new String[cursor.getCount()]; //7
        String[] arrACTION = new String[cursor.getCount()]; //7
        String[] arrVITAMIND = new String[cursor.getCount()]; //7

        for(int i=0; i<cursor.getCount()-1; i++){
            arrDATE[i] = cursor.getString(0);
            arrTEMP[i]  =String.valueOf(cursor.getFloat(1));
            arrHUM[i]  =String.valueOf(cursor.getFloat(2));
            arrUVI[i]  = String.valueOf(cursor.getFloat(3));
            arrUVB[i]  = String.valueOf(cursor.getFloat(4));
            arrACTION[i]  =cursor.getString(5);
            arrVITAMIND[i]  = String.valueOf(cursor.getFloat(6));
            cursor.moveToNext();
        }
        for (int i = 0; i < cursor.getCount(); i++) {
//            Log.d(TAG, arrDATE[i] + "\t" +
//                    arrDATE[i] + "\t" +
//                    arrTEMP[i] + "\t" +
//                    arrHUM[i] + "\t" +
//                    arrUVI[i] + "\t" +
//                    arrUVB[i] + "\t" +
//                    arrACTION[i] + "\t" +
//                    arrVITAMIND[i] + "\t"
//            );
        }
        db.close();
        return arrDATE;
    }
    public synchronized String[] getData() {
//        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE DATE=(SELECT MAX(DATE) FROM " + TABLE_NAME + ")";
        String sql = "SELECT * FROM " + TABLE_NAME + " LIMIT 1 OFFSET (SELECT COUNT(*) FROM " + TABLE_NAME + ")-1";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //7
        arr[0] = cursor.getString(0);
        arr[1] = String.valueOf(cursor.getFloat(1));
        arr[2] = String.valueOf(cursor.getFloat(2));
        arr[3] = String.valueOf(cursor.getFloat(3));
        arr[4] = String.valueOf(cursor.getFloat(4));
        arr[5] = cursor.getString(5);
        arr[6] = String.valueOf(cursor.getFloat(6));
//        for (int i = 0; i < arr.length; i++) {          Log.d(TAG, i+" : "+arr[i]);        }

        //added by jake for fixing CursorWindow exception
        cursor.close();

        db.close();
        return arr;
    }

    public String[] getProfileData() {
        String sql = "SELECT * FROM " + PROFILE_TABLE_NAME + " LIMIT 1 OFFSET (SELECT COUNT(*) FROM " + PROFILE_TABLE_NAME + ")-1";
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG,sql);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //8
        arr[0] = cursor.getString(0);
        arr[1] = String.valueOf(cursor.getInt(1));
        arr[2] = cursor.getString(2);
        arr[3] = String.valueOf(cursor.getInt(3));
        arr[4] = String.valueOf(cursor.getInt(4));
        arr[5] = String.valueOf(cursor.getInt(5));
        arr[6] = String.valueOf(cursor.getInt(6));
        arr[7] = String.valueOf(cursor.getInt(7));
//        for (int i = 0; i < arr.length; i++) {          Log.d(TAG,i+": :"+ arr[i]);        }
        db.close();
        return arr;
    }

    public String[] getVitamindData(String ageText) {
        String sql = "select * from " + VITAMIND_TABLE_NAME + " WHERE " + ageText + ">=start_age and " + ageText + "<=end_age;";
        Log.d(TAG,sql);
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //4
        arr[0] = String.valueOf(cursor.getInt(0));
        arr[1] = String.valueOf(cursor.getInt(1));
        arr[2] = String.valueOf(cursor.getInt(2));
        arr[3] = String.valueOf(cursor.getInt(3));
//        for (int i = 0; i < arr.length; i++) {          Log.d(TAG, arr[i]);        }
        db.close();
        return arr;
    }

    public String getSkintypeData() {
        String[] arrProfile = getProfileData();
        String sql = "select * from " + SKINTYPE_TABLE_NAME + " where skin=" + arrProfile[3] + ";";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //2
        arr[0] = String.valueOf(cursor.getInt(0));
        arr[1] = String.valueOf(cursor.getInt(1));
//        for (int i = 0; i < arr.length; i++) {            Log.d(TAG, arr[i]);        }
        db.close();
        return arr[1];
    }

    public String[] getRssData(){
        String sql = "SELECT * FROM  " + RSS_TABLE_NAME + " LIMIT 1 OFFSET (SELECT COUNT(*) FROM " + RSS_TABLE_NAME + ")-1";
        SQLiteDatabase db = getWritableDatabase();
        Log.d(TAG,sql);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        String[] arr = new String[cursor.getColumnCount()]; //11
        arr[0] = String.valueOf(cursor.getInt(3));
        arr[1] = String.valueOf(cursor.getString(5));
        arr[2] = String.valueOf(cursor.getInt(11));
        db.close();
        return arr;
    }


    public void basicTable() {
        SQLiteDatabase db = getWritableDatabase();

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "DATE BLOB PRIMARY KEY, " +
                "TEMP REAL, " +
                "HUM REAL," +
                "UVI REAL," +
                "UVB REAL, " +
                "ACTION TEXT," +
                "VITAMIND REAL);";
        db.execSQL(sql);

        String rss_sql = "CREATE TABLE IF NOT EXISTS " + RSS_TABLE_NAME + "(" +
                "DATE BLOB PRIMARY KEY, " +
                "DAY TEXT, " +
                "HOUR TEXT, " +
                "RSSTEMP REAL, " +
                "SKY TEXT, " +
                "PTY TEXT, " +
                "POP REAL, " +
                "R12 REAL, " +
                "S12 REAL, " +
                "WS REAL, " +
                "WDKOR TEXT, " +
                "REH REAL);";
        db.execSQL(rss_sql);

        String profile_sql = "CREATE TABLE IF NOT EXISTS " + PROFILE_TABLE_NAME + "(" +
                "NAME TEXT, " +
                "AGE INTEGER, " +
                "GENDER TEXT, " +
                "SKINTYPE INTEGER, " +
                "EXPOSURE_UPPER INTEGER," +
                "EXPOSURE_LOWER INTEGER," +
                "TARGETS_VITAMIND_SUFFICIENT INTEGER,"+
                "TARGETS_VITAMIND_UPPERLIMIT INTEGER);";
        db.execSQL(profile_sql);

        String basic_sql = "CREATE TABLE IF NOT EXISTS " + VITAMIND_TABLE_NAME + "(" +
                "START_AGE INTEGER, " +
                "END_AGE INTEGER," +
                "VITAMIND_SUFFICIENT INTEGER, " + // 비타민 D 충분섭춰량
                "VITAMIND_UPPERLIMIT INTEGER);"; // 비타민 D 상한섭취량
        db.execSQL(basic_sql);

        String basic_sql2 = "CREATE TABLE IF NOT EXISTS " + SKINTYPE_TABLE_NAME + "(" +
                "SKIN INTEGER, " +
                "MED INTEGER);";
        db.execSQL(basic_sql2);

        String trend_sql = "CREATE TABLE IF NOT EXISTS " + TREND_TABLE_NAME + "(" +
                "YEAR TEXT NOT NULL, " + //YEAR,MONTH,DAY 문자타입
                "MONTH TEXT NOT NULL," +
                "DAY TEXT NOT NULL," +
                "HOUR TEXT NOT NULL," +
                "UVI REAL NOT NULL," + //UVI, VITAMIN float형
                "VITAMIN REAL NOT NULL);";
        db.execSQL(trend_sql);
        trendUnit(); //trend_table 초기화

        addBasicData();
    }

    public void addBasicData() {
        addRecods(TABLE_NAME);
        addProfile(PROFILE_TABLE_NAME);
        addVitamind(VITAMIND_TABLE_NAME);
        addSkintype(SKINTYPE_TABLE_NAME);
        addRss(RSS_TABLE_NAME);
    }

    private void addRecods(String tableName) {
        ContentValues values = new ContentValues();
        values.put("DATE", "동기화 버튼을 누르세요");
        values.put("TEMP", 0);
        values.put("HUM", 0);
        values.put("UVI", 0);
        values.put("UVB", 0);
        values.put("ACTION", "낮음");
        values.put("VITAMIND", 0);
        SQLiteDatabase db = getWritableDatabase();

        db.insert(tableName, null, values);
        Log.i(TAG, tableName + " 성공");
        db.close();

    }

    private void addProfile(String profileTableName) {
        ContentValues values = new ContentValues();
        values.put("NAME", "이름을 입력하세요.");
        values.put("AGE", 20);
        values.put("GENDER", "남");
        values.put("SKINTYPE", 3);
        values.put("EXPOSURE_UPPER", 25);
        values.put("EXPOSURE_LOWER", 25);
        values.put("TARGETS_VITAMIND_SUFFICIENT", 400);
        values.put("TARGETS_VITAMIND_UPPERLIMIT", 4000);
        SQLiteDatabase db = getWritableDatabase();

        db.insert(profileTableName, null, values);
        Log.i(TAG, profileTableName + " 성공");
        db.close();
    }

    private void addVitamind(String vitamindTableName) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        for (int i = 0; i < ageTovitamind.length; i++) {
            values.put("START_AGE", ageTovitamind[i][0]);
            values.put("END_AGE", ageTovitamind[i][1]);
            values.put("VITAMIND_SUFFICIENT", ageTovitamind[i][2]);
            values.put("VITAMIND_UPPERLIMIT", ageTovitamind[i][3]);
            db.insert(vitamindTableName, null, values);
        }
        Log.i(TAG, vitamindTableName + " 성공");
        db.close();
    }

    private void addSkintype(String skintypeTableName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < skinTomed.length; i++) {
            values.put("SKIN", skinTomed[i][0]);
            values.put("MED", skinTomed[i][1]);
            db.insert(skintypeTableName, null, values);
        }
        Log.i(TAG, skintypeTableName + " 성공");
        db.close();
    }

    private void addRss(String kmaRssTableName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
            values.put("DATE", "1999-01-01");
            values.put("DAY", "오늘");
            values.put("HOUR", "00:00");
            values.put("RSSTEMP", 1.0);
            values.put("SKY", "SKY");
            values.put("PTY", "PTY");
            values.put("POP", 0.0);
            values.put("R12", 0.0);
            values.put("S12", 0.0);
            values.put("WS", 0.0);
            values.put("WDKOR", "WDKOR");
            values.put("REH", 40);
            db.insert(kmaRssTableName, null, values);
        Log.i(TAG, kmaRssTableName + " 성공");
        db.close();
    }


    public void addTrendData(ValueObject vo) {
        ContentValues values = new ContentValues();
        values.put("YEAR", vo.getTrendYear());
        values.put("MONTH", vo.getTrendMonth());
        values.put("DAY", vo.getTrendDay());
        values.put("HOUR", vo.getTrendHour());
        values.put("UVI", vo.getUvi());
        values.put("VITAMIND", vo.getVitamind());


        SQLiteDatabase db = getWritableDatabase();
        db.insert(TREND_TABLE_NAME, null, values);


        db.close();
    }

//
//    public String getDay() {
//
//        String selectQuery = "SELECT  * FROM " + TREND_TABLE_NAME;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//
//        cursor.moveToLast();
//        String day = cursor.getString(2);
//
//    //    db.close();
//        return day;
//    }
//
//    public String getMonth() {
//
//        String selectQuery = "SELECT  * FROM " + TREND_TABLE_NAME;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//
//        cursor.moveToLast();
//        String month = cursor.getString(1);
//
//   //     db.close();
//
//        return month;
//
//    }
//
//    public String getYear() {
//
//        String selectQuery = "SELECT  * FROM " + TREND_TABLE_NAME;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(selectQuery, null);
//
//
//        cursor.moveToLast();
//        String year = cursor.getString(0);
//
//   //    db.close();
//        return year;
//    }



    public float[] getTrendData(String chartType, String type, String year, String month, String day ) {
        float max = 0.0f, maxTmp = 0.0f, tmp = 0.0f;
        String sql = ""; //selectQuery문

        //무조건 TEXT 비교해 주려면 'ABC' 이런형태여야 함
        String Nyear = "'"+year+"'";
        String Nmonth = "'"+month+"'";
        String Nday =  "'"+day+"'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;

        float[] arr = new float[getDayNum(type)];
        for(int i=0; i<getDayNum(type); i++){ //초기화
            arr[i] = 0.0f;
        }


        for (int i = 0; i < getDayNum(type); i++) {
            sql = ""; //sql 초기화



            if(type.equals("DAY")) { //default값 day
                //나중에 달력 옮길려면 getDay()이부분 바꾸면 됨
                String sql2 = "(SELECT * FROM  " + TREND_TABLE_NAME + " where (YEAR = " + Nyear + ") AND (MONTH = " + Nmonth + "))" ;
                sql =  "SELECT * FROM  " + sql2 + " where (DAY = " + Nday  + ") AND (HOUR = " + "'" + i + "'" + ");";
            }
            else if(type.equals("WEEK")){
                int num = Integer.parseInt(day) - 6 + i;
                String sql2 = "(SELECT * FROM  " + TREND_TABLE_NAME + " where (DAY = " + "'"+ num + "'" + ") AND (MONTH = " + Nmonth + "))" ;
                sql =  "SELECT * FROM  " + sql2 + " where (YEAR = " + Nyear +");";
            }
            else if(type.equals("MONTH")){
                String sql2 = "(SELECT * FROM  " + TREND_TABLE_NAME + " where (DAY = " +  "'" + i + "'"+ ") AND (MONTH = " + Nmonth + " ))";
                sql =  "SELECT * FROM  " + sql2 + " where (YEAR = " + Nyear +");" ;
            }

            else if (type.equals("YEAR")){ //year
                int num = Integer.parseInt(month);
                sql =  "SELECT * FROM  " + TREND_TABLE_NAME + " where MONTH = " +  "'" + i + "'"+ " AND YEAR = " + Nyear +";" ;

                if(num == 2 ) { if(i==28) break; } //2월
                if( num == 4 || num == 6 ||  num == 9 ||  num == 11 ) { //4,6,9,11월
                    if(i==30) break;
                }
            }


            cursor = db.rawQuery(sql, null);


            if (cursor != null && !cursor.isClosed()) {
                if (cursor.moveToFirst()) {
                    do {

                        if(chartType.equals("UVI"))          maxTmp = cursor.getFloat(4); //uvi은 인덱스 4
                        else if(chartType.equals("VITAMIN"))     maxTmp = cursor.getFloat(5); //vitamin은 인덱스 4

                        if (maxTmp > max) {
                            tmp = max;
                            max = maxTmp;
                            maxTmp = tmp;

                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

            arr[i] = max;
        }

        return arr;
    }

    private void trendUnit() { //trendTable 초기화

        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("INSERT INTO trend_table VALUES ('0','0', '0', '0', 0.0, 0.0 );");
    }

    //현재까지 수집된 데이터가 없어 insert 해주는 메소드
    public void inputIndex(){
        ContentValues values = new ContentValues();
        SQLiteDatabase db = getWritableDatabase();


        //  트랜잭션을 이용해 빠르게 삽입하는 방법
        db.beginTransaction();
        try{
            Log.e("inputIndex실행","inputIndex실행");

            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'9'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'10'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'11'	,	4.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18'	,	'12'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'13'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'14'	,	5.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'15'	,	4.7	,	90.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'18	'   ,	'23'	,	0.0	,	0.0	)	;");
//
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19	'   ,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'9'	,	2.0	,	15.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'10'	,	2.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'11'	,	2.5	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'12'	,	4.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'13'	,	5.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'14'	,	6.0	,	150.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'15'	,	8.0	,	250.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'16'	,	5.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19	  ',	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'19'	,	'23'	,	0.0	,	0.0	)	;");
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20	'  ,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'9'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'10'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'11'	,	4.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'12'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'13'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'14'	,	5.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	    ,	'20'	,	'15'	,	4.7	,	90.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20	'  ,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'20'	,	'23'	,	0.0	,	0.0	)	;");
//
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21	'   ,  '2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'9'	,	2.0	,	15.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'10'	,	2.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'11'	,	2.5	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'12'	,	3.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'13'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'14'	,	3.5	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09	'   ,	'21'	,	'15'	,	4.0	,	80.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'21'	,	'23'	,	0.0	,	0.0	)	;");
//
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'9'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	('	2016'	,	'09'	,	'22'	,	'10'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'11'	,	4.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'12'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'13'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'14'	,	5.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	   ,	 '22'	,	'15'	,	4.7	,	90.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'22'	,	'23'	,	0.0	,	0.0	)	;");
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'9'	,	2.0	,	15.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'10'	,	2.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'11'	,	2.5	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'12'	,	4.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'13'	,	4.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'14'	,	5.0	,	70.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	    ,	'23'	,	'15'	,	4.0	,	80.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'23'	,	'23'	,	0.0	,	0.0	)	;");
//
//            /**/
//            db.execSQL("INSERT INTO trend_table VALUES  	(	'2016'	,	'09'	,	'24'	,	'0'	,	0.7	,	10.0	)	;");
//            db.execSQL("INSERT INTO trend_table VALUES  	(	'2016'	,	'09'	,	'24'	,	'0'	,	0.5	,	5.0	)	;");
//            db.execSQL("INSERT INTO trend_table VALUES  	(	'2016'	,	'09'	,	'24'	,	'0'	,	0.3	,	3.0	)	;");
//            /**/
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'9'	,	2.0	,	15.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'10'	,	2.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'11'	,	2.5	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'12'	,	3.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'13'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'14'	,	3.5	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'  	,	'24'	,	'15'	,	4.0	,	80.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'24'	,	'23'	,	0.0	,	0.0	)	;");
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25	'   ,  '2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'9'	,	2.0	,	15.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'10'	,	2.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'11'	,	2.5	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'12'	,	3.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'13'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'14'	,	3.5	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09	'   ,	'25'	,	'15'	,	4.0	,	80.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'25'	,	'23'	,	0.0	,	0.0	)	;");
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'9'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	('	2016'	,	'09'	,	'26'	,	'10'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'11'	,	4.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'12'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'13'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'14'	,	5.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	   ,	 '26'	,	'15'	,	4.7	,	90.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'26'	,	'23'	,	0.0	,	0.0	)	;");
//
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'0'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'1'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'2'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'3'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'4'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'5'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'6'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'7'	,	1.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'8'	,	1.5	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'9'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'10'	,	3.0	,	20.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'11'	,	4.0	,	40.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27'	,	'12'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'13'	,	6.0	,	200.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'14'	,	5.0	,	100.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'15'	,	4.7	,	90.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'16'	,	3.0	,	50.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'17'	,	2.2	,	30.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'18'	,	3.6	,	60.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'19'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'20'	,	1.0	,	10.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'21'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'22'	,	0.0	,	0.0	)	;");
//            db.execSQL("INSERT INTO  trend_table VALUES 	(	'2016'	,	'09'	,	'27	'   ,	'23'	,	0.0	,	0.0	)	;");

            db.setTransactionSuccessful();
        }
        catch(Exception e){
            Log.i("InputData:", e.toString() );
        }
        finally {
            db.endTransaction();
        }

    }

    public int getDayNum(String type) {
        String dayType = "";
        if (type.equals("DAY")) return 24;  //day는 시간별로 max값 보여주어야함
        else if (type.equals("WEEK")) return 7; //week은 일별로 즉 일주일에는 7일이니까 값이 7
        else if (type.equals("MONTH"))
            return 31;// month는 일별로 즉 한달에 30일 또는 31일이니 31. (최대 31일이니까 31로 지정함)
        else return 12; //1년에 12달이 있어서 12
    }

//    private String getSql(int i, String type){
//        String sql;
//
//        if(type.equals("DAY")) {
//            sql = "SELECT * FROM  " + TREND_TABLE_NAME + " where HOUR = " + i + " AND DAY = " + g() + " AND MONTH = " + getMonth() + " AND YEAR = " + getYear(); //나중에 달력 옮길려면 getDay()이부분 바꾸면 됨
//        }
//        else if(type.equals("WEEK")){
//            int num = Integer.parseInt(getDay()) - 6 + i;
//            sql =  "SELECT * FROM  " + TREND_TABLE_NAME + " where DAY = " + num + " AND MONTH = " + getMonth() + " AND YEAR = " + getYear();
//        }
//        else if(type.equals("MONTH")){
//            sql =  "SELECT * FROM  " + TREND_TABLE_NAME + " where DAY = " + i + " AND MONTH = " + getMonth() + " AND YEAR = " + getYear();
//        }
//
//        else { //year
//            int num = Integer.parseInt(getMonth());
//            sql =  "SELECT * FROM  " + TREND_TABLE_NAME + " where MONTH = " + i + " AND YEAR = " + getYear();
//
//        }
//        return sql;
//    }


}
