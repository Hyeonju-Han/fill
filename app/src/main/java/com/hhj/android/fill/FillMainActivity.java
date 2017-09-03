package com.hhj.android.fill;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.Locale;

/*
    Han Hyeonju
    - 메인 화면
*/

public class FillMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "FillMainActivity";//로그에 사용 할 태그

    ListView mFillListView;
    TextView itemCount;


    FillListAdapter mFillListAdapter;
    public static FillDatabase mDatabase = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {//onCreate : 액티비티가 처음 만들어 졌을때 호출됨
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

		/* 플러스 버튼 클릭시 */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG", "newFillBtn clicked.");

                Intent intent = new Intent(getApplicationContext(), FillInsertActivity.class);
                intent.putExtra(BasicInfo.KEY_FILL_MODE, BasicInfo.MODE_INSERT);
                startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);
            }
        });

        /* 네비게이션 토글 */
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        */

        /* 안드로이드의 설정된 정보 가져오기 */
        Locale curLocale = getResources().getConfiguration().locale;//Configuration:구성
        BasicInfo.language = curLocale.getLanguage();//설정 언어가져오기
        Log.d(TAG, "현재 언어 : " + BasicInfo.language);

        /* SD카드 사용여부 체크 */
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //MEDIA_MOUNTED : SD카드가 존재하고 연결되었으며 읽고 쓰기가 가능한 상태
            //Environment:환경
            Toast.makeText(this, "SD카드가 없습니다. 먼저 SD를 넣어주세요", Toast.LENGTH_LONG).show();
            return;
        } else {
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            //SD카드가 있을경우 외장메모리(SD카드)의 절대경로
            if (!BasicInfo.ExternalChecked && externalPath != null) {//절대경로가 null이 아니면
                BasicInfo.ExternalPath = externalPath + File.separator; //File.separator : 경로를 분리해주는 메소드
                Log.d(TAG, "ExternalPath:" + externalPath + ", File.separator:" + File.separator + " > 결과:" + BasicInfo.ExternalPath);
                BasicInfo.FOLDER_PHOTO = BasicInfo.ExternalPath + BasicInfo.FOLDER_PHOTO;//사진이 저장될 경로 저장
                BasicInfo.DATABASE_NAME = BasicInfo.ExternalPath + BasicInfo.DATABASE_NAME;//데이터베이스가 저장될 경로 저장

                BasicInfo.ExternalChecked = true;
            }
        }


        /* 일기 리스트 */
        mFillListView = (ListView) findViewById(R.id.fillList);
        mFillListAdapter = new FillListAdapter(this);//어댑터 객체생성
        mFillListView.setAdapter(mFillListAdapter);
        //setAdapter : 리스트뷰에 사용할 데이터 객체를 넘겨주는 메소드
        //ArrayAdapter객체를 생성하여 사용할 데이터를 저장하고 데이터가 저장된 ArrayAdapter객체를 setAdapter메소드에 전달
        mFillListView.setOnItemClickListener(new OnItemClickListener() {
            //리스트의 아이템를 선택했을 때 발생하는 리스너
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                viewFill(position);
            }
        });

        //아이템 개수
        /*
        itemCount = (TextView) findViewById(R.id.itemCount);
        checkDangerousPermissions();
        */



    }

    @Override
    public void onBackPressed() { //뒤로가기 버튼
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
         //더모어 버튼
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 메뉴의 목록을 클릭 했을때 발생
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkDangerousPermissions() { //위험권한부가
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                //애플리케이션이 외부저장 장치에서 읽을 수 있도록한다
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //애플리케이션이 외부저장소를 쓸 수있게 한다
                android.Manifest.permission.CAMERA
                //카메라 저장 장치에 엑섹스 할 수있어야한다
        };


        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        //PERMISSION_GRANTED : 지정된 패키지에 권한이 허가되고있는 경우

        for (int i = 0; i < permissions.length; i++) {
            //요청한 권한의 개수만큼 for문
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            //특정 권한이 부여 되었는지 확인
            //파라미터 : (Context context, String permission)
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                //PERMISSION_DENIED : 권한이 부여되지 않은 경우
                break;
            }
        }

        //권한여부 로그
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "권한있음");
        } else {
            Log.e(TAG, "권한없음");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //권한 요청을위한 이유가있는 UI를 표시해야하는지 여부를 가져옵니다.
                Log.e(TAG, "권한 설명 필요함");
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
                //이 응용 프로그램에 권한을 요청합니다.
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //사용 권한 요청의 결과에 대한 콜백입니다.
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "권한이 승인됨");
                } else {
                    Log.e(TAG, "권한이 승인되지 않음");
                }
            }
        }
    }


    protected void onStart() {
        //데이터베이스 열기
        openDatabase();

        //데이터로딩
        loadFillListData();

        super.onStart();
    }


    /* 데이터베이스 열기 (데이터베이스가 없을 때는 만들기) */
    public void openDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mDatabase = FillDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Fill database is open.");
        } else {
            Log.d(TAG, "Fill database is not open.");
        }
    }

    /* 일기의 리스트 데이터 로딩 */
    public int loadFillListData() {
        String SQL = "select _id, INPUT_DATE, CONTENT_TEXT, ID_PHOTO from FILL order by INPUT_DATE desc";

        int recordCount = -1;
        if (FillMainActivity.mDatabase != null) {
            Cursor outCursor = FillMainActivity.mDatabase.rawQuery(SQL);
            //Cursor: 커서는 실제 레코드의 데이터를 가져다 사용하는 인터페이스
            //rawQuery : 파라미터로 넘긴 SQL을 실행시키고 데이터 반환
            recordCount = outCursor.getCount();
            //데이터 행의 개수를 가져온다.
            Log.d(TAG, "cursor count : " + recordCount + "\n");

            mFillListAdapter.clear();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext(); //다음행으로 이동

                String fillId = outCursor.getString(0);
                String dateStr = outCursor.getString(1);
                //파라미터로 컬럼의 인덱스를 넘김, 필드값을 String으로 돌려준다

                if (dateStr != null && dateStr.length() > 10) {
                    //dateStr = dateStr.substring(0, 10); //시간생략
                    try {
                        Date inDate = BasicInfo.dateFormat.parse(dateStr);
                        //날짜형식으로 포맷
                        BasicInfo.dateNameFormat2.format(inDate);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    dateStr = "";
                }
                String fillStr = outCursor.getString(2);
                String photoId = outCursor.getString(3);
                String photoUriStr = getPhotoUriStr(photoId);

                mFillListAdapter.addItem(new FillListItem(fillId, dateStr, fillStr, photoId, photoUriStr));
            }
            outCursor.close();

            mFillListAdapter.notifyDataSetChanged(); //새로고침

            /*
            //아이템 개수 표시
            itemCount.setText(recordCount + " " + getResources().getString(R.string.item_count));
            //홈화면 상단에 표시되는 item의 개수
            itemCount.invalidate();//무효화...?
            */

        }

        return recordCount;
    }

    /* 사진 데이터 URI 가져오기 */
    public String getPhotoUriStr(String id_photo) {
        String photoUriStr = null;
        if (id_photo != null && !id_photo.equals("-1")) {
            String SQL = "select URI from " + FillDatabase.TABLE_PHOTO + " where _ID = " + id_photo + "";
            Cursor photoCursor = FillMainActivity.mDatabase.rawQuery(SQL);
            if (photoCursor.moveToNext()) {
                photoUriStr = photoCursor.getString(0);
            }
            photoCursor.close();
        } else if (id_photo == null || id_photo.equals("-1")) {
            photoUriStr = "";
        }
        return photoUriStr;
    }


    private void viewFill(int position) {
        FillListItem item = (FillListItem) mFillListAdapter.getItem(position);

        // item클릭했을때 데이터 넘기기
        Intent intent = new Intent(getApplicationContext(), FillViewActivity.class);
        intent.putExtra(BasicInfo.KEY_FILL_MODE, BasicInfo.MODE_VIEW);
        intent.putExtra(BasicInfo.KEY_FILL_ID, item.getId());
        intent.putExtra(BasicInfo.KEY_FILL_DATE, item.getData(0));
        intent.putExtra(BasicInfo.KEY_FILL_TEXT, item.getData(1));
        intent.putExtra(BasicInfo.KEY_ID_PHOTO, item.getData(2));
        intent.putExtra(BasicInfo.KEY_URI_PHOTO, item.getData(3));

        startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);
    }

    /* 다른 액티비티의 응답 처리 */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BasicInfo.REQ_INSERT_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    loadFillListData();
                }
                break;
            case BasicInfo.REQ_VIEW_ACTIVITY:
                loadFillListData();
                break;
        }
    }



}