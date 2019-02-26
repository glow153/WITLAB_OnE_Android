package kr.co.glowsoft.todayshaksik;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import kr.co.glowsoft.todayshaksik.model.Meal;
import kr.co.glowsoft.todayshaksik.util.NetworkManager;

public class MainActivity extends AppCompatActivity {
    // components of this view
    private Toolbar myToolbar;
    private TextView tvStudent, tvDorm, tvEmployee;
    private FloatingActionButton fab;
    private Button btnDebug;

    private NetworkManager net;

    private String[] todaysmenu;

    private void bindView() {
        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GregorianCalendar calendar = new GregorianCalendar();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpdlg = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // date change procedure here
                                String msg = String.format("%d / %d / %d", year, monthOfYear + 1, dayOfMonth);
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        },
                        year, month, day);

                calendar.set(Calendar.DAY_OF_WEEK, 1);
                dpdlg.getDatePicker().setMinDate(calendar.getTimeInMillis());
                calendar.set(Calendar.DAY_OF_WEEK, 7);
                dpdlg.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                dpdlg.show();
            }
        });

        tvStudent = findViewById(R.id.tvHaksik_student);
        tvDorm = findViewById(R.id.tvHaksik_dorm);
        tvEmployee = findViewById(R.id.tvHaksik_employee);

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String student, dormitory, employee;
                StringBuilder content = new StringBuilder();
                try {
                    student = "\n<학생식당>\n" + todaysmenu[0].replaceAll(" ", "\n");
                } catch (NullPointerException e) {
                    student = "\n<학생식당>\n정보없음";
                }
                try {
                    dormitory = "\n<기숙사식당>\n" + todaysmenu[2].replaceAll(" ", "\n");
                } catch (NullPointerException e) {
                    dormitory = "\n<기숙사식당>\n정보없음";
                }
                try {
                    employee = "\n<직원식당>\n" + todaysmenu[1].replaceAll(" ", "\n");
                } catch (NullPointerException e) {
                    employee = "\n<직원식당>\n정보없음";
                }
                content.append(student);
                content.append(dormitory);
                content.append(employee);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "오늘의 학식 (" + net.getDate() + ")");
                intent.putExtra(Intent.EXTRA_TEXT, content.toString());

                PackageManager packManager = getPackageManager();
                List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                boolean resolved = false;
                for (ResolveInfo resolveInfo : resolvedInfoList) {
                    if (resolveInfo.activityInfo.packageName.startsWith("com.kakao.talk")) {
                        intent.setClassName(
                                resolveInfo.activityInfo.packageName,
                                resolveInfo.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }

                if (resolved) {
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "카카오톡 앱이 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnDebug = findViewById(R.id.btnDebug);
        btnDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer sb = new StringBuffer();
                ArrayList<Meal> a = net.getMenuOfWeek(0);
                for(Meal meal : a)
                    sb.append(meal.toString());
                Log.d("debug", sb.toString());
            }
        });
    }

    private void initTodaysMenu() {
        todaysmenu = net.getTodaysMenu();
        String s = "준비중입니다.";

        try {
            tvStudent.setText(todaysmenu[0].replaceAll(" ", "\n"));
        } catch(NullPointerException e) {
            tvStudent.setText(s);
        }

        try {
            tvEmployee.setText(todaysmenu[1].replaceAll(" ", "\n"));
        } catch(NullPointerException e) {
            tvEmployee.setText(s);
        }

        try {
            tvDorm.setText(todaysmenu[2].replaceAll(" ", "\n"));
        } catch(NullPointerException e) {
            tvEmployee.setText(s);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        net = NetworkManager.getInstance();

        try {
            setTitle(getTitle() + " (" + net.getDate() + ")");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        bindView();
        initTodaysMenu();
    }


    public void updateTodaysMenu(int dayOfWeek) {
        //dayOfWeek value : 0-4 (m-f)

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // 메뉴버튼이 처음 눌러졌을 때 실행되는 콜백메서드
        // 메뉴버튼을 눌렀을 때 보여줄 menu 에 대해서 정의
        getMenuInflater().inflate(R.menu.menu, menu);
        Log.d("test", "onCreateOptionsMenu - 최초 메뉴키를 눌렀을 때 호출됨");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        Log.d("test", "onPrepareOptionsMenu - 옵션메뉴가 " +
//                "화면에 보여질때 마다 호출됨");
//        if(bLog){ // 로그인 한 상태: 로그인은 안보이게, 로그아웃은 보이게
//            menu.getItem(0).setEnabled(true);
//            menu.getItem(1).setEnabled(false);
//        }else{ // 로그 아웃 한 상태 : 로그인 보이게, 로그아웃은 안보이게
//            menu.getItem(0).setEnabled(false);
//            menu.getItem(1).setEnabled(true);
//        }
//
//        bLog = !bLog;   // 값을 반대로 바꿈

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴의 항목을 선택(클릭)했을 때 호출되는 콜백메서드
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d("test", "onOptionsItemSelected - 메뉴항목을 클릭했을 때 호출됨");

        int id = item.getItemId();
        switch (id) {
            case R.id.menu_login:
                Intent intent = new Intent(MainActivity.this, AboutAcivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
