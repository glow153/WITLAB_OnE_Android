package kr.ac.kongju.witlab.uvit.activity.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kr.ac.kongju.witlab.uvit.R;
import kr.ac.kongju.witlab.uvit.fragment.FragmentSetting_Alarm;
import kr.ac.kongju.witlab.uvit.service.GeneralBluetoothControllerJake;
import kr.ac.kongju.witlab.uvit.service.MySQLiteOpenHelper;

/**
 * First developed by KM (km93@kongju.ac.kr) for project WISET in 2016
 * Refactored and modified by Jake Park in Oct. 2018
 *
 * NOTICE
 * ver. 1.0은 "Galaxy Note 4" 및  "Galaxy S5" 디바이스에 맞게 제작됨
 * ver. 2.0.1 : "Galaxy S6 edge" 디바이스에 맞게 제작됨
 * ver. 2.0.2 : "Galaxy S6 edge" 에 맞게, "Galaxy Note 4" 및  "Galaxy S5" 호환 가능 확인
 * ver. 2.1.0 : DB 오류 수정됨
 * (다른 디바이스에서는 뷰가 정상적으로 나타나지 않을 수 있음)
 *
 * UVI 표시 : 정상, 값 부정확
 * 비타민 D 합성량 누적 표시 : 정상, 값 부정확
 * RSS : 동작 x
 * GPS : 동작 x
 * 온습도 : 동작 x
 * ENTEC LED 제어 : 개발완료
 *
 * Trouble Issues
 * 밴드 연동 - 프로차일드 white 밴드에 맞게 제작됨 (2018-11-06 값 보정 안 되어있음)
 *             밴드 디바이스 문제로 인한 값의 오류인지 보정식의 오류인지 확인 안됨
 * 간헐적 종료 현상 - DB 읽고 쓰는 과정에서 exception 발생 -> 해결 완료 (181109)
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 7;
    private static final String PREF_NAME = "com.pref";

    // core controllers
    private GeneralBluetoothControllerJake btMgr;
    private MySQLiteOpenHelper dbHelper;
    private FragmentSetting_Alarm alarm_fragment;

    // vars
    private long backKeyPressedTime = 0;
    private double lat, lon;

    // view components
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // km
        getWindow().setWindowAnimations(0); //화면전환 효과 제거
        initDatabase();

        // set thread policy - km
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // get permission - jake
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_COARSE_LOCATION);
        }

        initViews();
        setListener();
        initBle();
    }

    private void initViews() {
        // initView - km
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        View tabView_monitoring = getLayoutInflater().inflate(R.layout.tab_model, null);
//        View tabView_trends = getLayoutInflater().inflate(R.layout.tab_model, null);
        View tabView_led = getLayoutInflater().inflate(R.layout.tab_model, null);
        View tabView_setting = getLayoutInflater().inflate(R.layout.tab_model, null);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_monitoring).setCustomView(tabView_monitoring));
        tabLayout.getTabAt(0).getCustomView().setSelected(true);
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_trends).setCustomView(tabView_trends));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_led).setCustomView(tabView_led));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_tab_setting).setCustomView(tabView_setting));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.container);
        final PagerAdapter adapter = new UvitPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
    }

    // set listeners of all views - jake
    private void setListener() {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private void initBle() {
        if (btMgr == null) {
            Log.d(TAG, "GeneralBluetoothControllerJake 최초 생성 - jake");
            btMgr = GeneralBluetoothControllerJake.getInstance();
            btMgr.initManager(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, yay! Start the Bluetooth device scan.
                } else {
                    // Alert the user that this application requires the location permission
                    // to perform the scan.
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rss_gps_location:
//                Dialog_GPS(gpsService.getLatitude(), gpsService.getLongitude());
                break;
            case R.id.rss_kma_location:
//                Dialog_KMA();
                break;
            case R.id.action_rss:
                break;
            case R.id.action_gps:
                Toast.makeText(getApplicationContext(), "현재위치 업데이트 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("SEND_GPS");
//                intent.putExtra("LAT", vo.getLat());
//                intent.putExtra("LON", vo.getLon());
//                intent.putExtra("ADDRESS", vo.getAddress());
                this.sendBroadcast(intent);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this,
                    "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.",
                    Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    private void Dialog_GPS(double lat, double lon) {
//        final String address = gpsService.getAddress(lat, lon);
//        final String xml = rssService.getGrid(lat, lon);
        final String[] selectedText = {null};
        final List<String> listItem = new ArrayList<>();

        String[][] rssArr = new String[18][11];
//        rssArr = rssService.parseRss(xml);

        for (int i = 0; i < rssArr.length; i++) {
            if ("모레".equals(rssArr[i][0])) break;
            listItem.add(rssArr[i][0] + " " + rssArr[i][1]);
        }
        final CharSequence[] rssItems = listItem.toArray(new CharSequence[listItem.size()]);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this); //큰 다이얼로그
        final AlertDialog.Builder inBuilder = new AlertDialog.Builder(this); // 작은 다이얼로그
        inBuilder.setCancelable(true);
        // dialogBuilder.setIcon(R.drawable.i) 아이콘 넣을때.....
//        dialogBuilder.setTitle(address + "의 날씨");
        dialogBuilder.setNegativeButton(R.string.str_ok,(DialogInterface dialoginterface, int i) -> dialoginterface.dismiss());
        dialogBuilder.setItems(rssItems, (final DialogInterface dialog, int item) -> {
            selectedText[0] = rssItems[item].toString();
            String text = selectedText[0];
            Log.d(TAG, item + ", " + String.valueOf(text));

//            inBuilder.setTitle(address + "\n" + text + "의 날씨");
//            StringBuilder sb = rssService.getRssData(item, xml); // 리스트에서 선택한 날씨 갖고 오기~

//            inBuilder.setMessage(sb);
            inBuilder.setPositiveButton(R.string.str_ok, (DialogInterface dialoginterface, int i) -> dialog.dismiss());
            inBuilder.setNegativeButton(R.string.str_ok, (DialogInterface dialoginterface, int i) -> dialogBuilder.show());
            inBuilder.show();
        });
        dialogBuilder.show();
    }

    private void Dialog_KMA() {
        final ListView listView;
        final String[] dialogTitle = {null};
        final List<String> listItem = new ArrayList<>();
        final List<String> listItem2 = new ArrayList<>();
        final List<String> listItem3 = new ArrayList<>();
//        final String[][] topArr = kmaArr(rssService.json("top"));

//        LayoutInflater inflater=getLayoutInflater();
//        final View dialogView= inflater.inflate(R.layout.dialog_address, null);
        final AlertDialog.Builder Builder = new AlertDialog.Builder(this); //큰 다이얼로그
        final AlertDialog.Builder Builder2 = new AlertDialog.Builder(this); // 작은 다이얼로그
        final AlertDialog.Builder Builder3 = new AlertDialog.Builder(this); // 작은 작은 다이얼로그
        final AlertDialog.Builder Builder4 = new AlertDialog.Builder(this); // 작은 작은 다이얼로그
//        Builder.setView(dialogView);  Builder2.setView(dialogView);  Builder3.setView(dialogView);  Builder4.setView(dialogView);
//        TextView title = (TextView) dialogView.findViewById(R.id.dialog_title);
//        ListView list = (ListView) dialogView.findViewById(R.id.dialog_list);
        Builder4.setCancelable(true);

//        for (int i = 0; i < topArr.length; i++) {
//            if (topArr[i][1] == null) break;
//            listItem.add(topArr[i][1]);
//        }
        final CharSequence[] rssItems = listItem.toArray(new CharSequence[listItem.size()]);

        // outBuilder.setIcon(R.drawable.i) 아이콘 넣을때.....
        Builder.setTitle("지역을 선택하세요.");
//        title.setText("지역을 선택하세요.");
        Builder.setNegativeButton(R.string.str_back, (DialogInterface dialoginterface, int i) -> dialoginterface.dismiss());
        Builder.setItems(rssItems, (final DialogInterface dialog, final int item) -> {
//                dialogTitle[0] = topArr[item][1] + " ";
            Builder2.setTitle(dialogTitle[0]);

//                final String[][] mdlArr = kmaArr(rssService.json(topArr[item][0]));
            listItem2.clear();
//                for (int i = 0; i < mdlArr.length; i++) listItem2.add(mdlArr[i][1]);
            final CharSequence[] rssItems2 = listItem2.toArray(new CharSequence[listItem2.size()]);

            Builder2.setNegativeButton(R.string.str_back, (DialogInterface dialoginterface, int i) -> Builder.show());

            Builder2.setItems(rssItems2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item2) {
//                        final String[][] leafArr = kmaArr_leaf(rssService.json_leaf(mdlArr[item2][0]));
//                        dialogTitle[0] += mdlArr[item2][1] + " ";
                    Builder3.setTitle(dialogTitle[0]);

                    listItem3.clear();
//                        for (int i = 0; i < leafArr.length; i++) listItem3.add(leafArr[i][1]);
                    final CharSequence[] rssItems3 = listItem3.toArray(new CharSequence[listItem3.size()]);

                    Builder3.setNegativeButton(R.string.str_back,
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialoginterface, int i) {
                                    Builder2.show();
                                }
                            });
                    Builder3.setItems(rssItems3, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int item3) {
//                                String xml = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=" + leafArr[item3][2] + "&gridy=" + leafArr[item3][3];
//                                dialogTitle[0] += leafArr[item3][1] + " ";
                            Builder4.setTitle(dialogTitle[0]);

//                                StringBuilder sb = rssService.getRssData(0, xml); // 리스트에서 선택한 날씨 갖고 오기~

//                                Builder4.setMessage(sb);
                            Builder4.setPositiveButton(R.string.str_ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialoginterface, int i) {
                                            dialog.dismiss();
                                        }
                                    });
                            Builder4.setNegativeButton(R.string.str_back,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialoginterface, int i) {
                                            Builder3.show();
                                        }
                                    });
                            Builder4.show();
                        }
                    });
                    Builder3.show();
                }
            });
            Builder2.show();
        });
        Builder.show();
    }

    public String[][] kmaArr(StringBuffer sb) {
        String buffer = sb.toString();
        String[] arr = buffer.split(",");
        Log.d(TAG, String.valueOf(arr.length));

        String[][] Arr = new String[arr.length / 2][2];
        int count = 0;
        for (int i = 0; i < arr.length; i += 2) {
            Arr[count][0] = arr[i];
            Arr[count][1] = arr[i + 1];
//            Log.d(TAG, count + " : " + Arr[count][0] + ", " + Arr[count][1]);
            count++;
        }
        return Arr;
    }

    public String[][] kmaArr_leaf(StringBuffer sb) {
        String buffer = sb.toString();
        String[] arr = buffer.split(",");
        Log.d(TAG, String.valueOf(arr.length));

        String[][] Arr = new String[arr.length / 4][4];
        int count = 0;
        for (int i = 0; i < arr.length; i += 4) {
            Arr[count][0] = arr[i];
            Arr[count][1] = arr[i + 1];
            Arr[count][2] = arr[i + 2];
            Arr[count][3] = arr[i + 3];
            Log.d(TAG, count + " : " + Arr[count][0] + ", " + Arr[count][1] + ", " + Arr[count][2] + ", " + Arr[count][3]);
            count++;
        }
        return Arr;
    }


    // check first - km , modified by jake
    private void initDatabase() {
        // 첫 실행인지 확인하고 첫 실행이면 DB 생성, 아니면 기존 DB 사용
        dbHelper = new MySQLiteOpenHelper(this);
        SharedPreferences pref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String start = pref.getString("firstTimeRun", "");
        if (start.equals("")) {
            Log.d(TAG, "처음처음");

            dbHelper.basicTable();
            dbHelper.inputIndex(); // 수집된 데이터가 없어 임의의 값을 넣어주는 메소드

            alarm_fragment = new FragmentSetting_Alarm();
            alarm_fragment.setAlarm(true);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("firstTimeRun", "no");
            editor.commit();
        } else {
            Log.d(TAG, "첫실행ㄴㄴ");
            dbHelper.getDUVTest(); //DB 데이터 확인용
        }
    }
}