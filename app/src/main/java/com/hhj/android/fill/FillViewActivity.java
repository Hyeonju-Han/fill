package com.hhj.android.fill;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/* Han Hyeonju */

public class FillViewActivity extends AppCompatActivity {
    private static final String TAG = "FillViewActivity";

    /* 변수선언 */
    Calendar mCalendar = Calendar.getInstance(); //달력인스턴스
    Button insertDateButton; //날짜세팅버튼
    Button insertTimeButton; //시간세팅버튼
    ImageView mPhoto; //첨부한 이미지
    TextView view_text; //작성한일기

    /* itemList에서 넘겨받은 파라미터 변수 */
    String mFillMode; // 리스트에서 넘겨받은 현재상태 -> 읽기모드
    String mFillId;
    String mFillDate;
    String mFillStr;
    String mMediaPhotoId;
    String mMediaPhotoUri;
    String tempPhotoUri;
    String mDateStr; //날짜 포맷정보

    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;
    int mSelectdContentArray; //array.XML에서 다이얼로그 목록 텍스트 가져옴
    int mChoicedArrayItem; //다이얼로그에서 선택한 목록
    int textViewMode = 0;

    Bitmap resultPhotoBitmap;
    Animation translateLeftAnim;
    Animation translateRightAnim;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fill_view_activity);

        mPhoto = (ImageView) findViewById(R.id.insert_photo);
        view_text = (TextView) findViewById(R.id.view_text);
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        //translateLeftAnim.setAnimationListener(animListener);
        //translateRightAnim.setAnimationListener(animListener);

        /*
        //이미지 클릭할 경우 다이얼로그 띄움
        mPhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (isPhotoCaptured || isPhotoFileSaved) {
                    //showDialog(BasicInfo.CONTENT_PHOTO_EX);
                } else {
                    //showDialog(BasicInfo.CONTENT_PHOTO);
                }
            }
        });
        */

        setMediaLayout();
        setCalendar(); //달력

        Intent intent = getIntent();
        mFillMode = intent.getStringExtra(BasicInfo.KEY_FILL_MODE);
        if (mFillMode.equals(BasicInfo.MODE_VIEW)) {
            processIntent(intent);
            setTitle(R.string.view_title); //화면타이틀 적용
        }
    }

/*
    //애니메이션
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        public void onAnimationEnd(Animation animation) {}
        public void onAnimationRepeat(Animation animation) {}
        public void onAnimationStart(Animation animation) {}
    }
*/

    public void processIntent(Intent intent) {
        mFillId = intent.getStringExtra(BasicInfo.KEY_FILL_ID);
        mFillDate = intent.getStringExtra(BasicInfo.KEY_FILL_DATE);
        String curFillText = intent.getStringExtra(BasicInfo.KEY_FILL_TEXT);
        view_text.setText(curFillText);
        mMediaPhotoId = intent.getStringExtra(BasicInfo.KEY_ID_PHOTO);
        mMediaPhotoUri = intent.getStringExtra(BasicInfo.KEY_URI_PHOTO);
        setMediaImage(mMediaPhotoId, mMediaPhotoUri);
        setFillDate(mFillDate);

/*
        if (curFillText != null && !curFillText.equals("")) {
            textViewMode = 0;
        } else {
            textViewMode = 1;
        }
*/

    }

    /* 메뉴클릭 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    /* 클릭했을때 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.modify) {//수정

            Intent intent = new Intent(getApplicationContext(), FillInsertActivity.class);
            intent.putExtra(BasicInfo.KEY_FILL_MODE, BasicInfo.MODE_MODIFY);
            intent.putExtra(BasicInfo.KEY_FILL_ID, mFillId);
            intent.putExtra(BasicInfo.KEY_FILL_DATE, mFillDate);
            intent.putExtra(BasicInfo.KEY_FILL_TEXT, view_text.getText().toString());
            intent.putExtra(BasicInfo.KEY_ID_PHOTO, mMediaPhotoId);
            intent.putExtra(BasicInfo.KEY_URI_PHOTO, mMediaPhotoUri);

            startActivityForResult(intent, BasicInfo.REQ_INSERT_ACTIVITY);

            return true;
        } else if (id == R.id.delete) {
            //삭제
            showDialog(BasicInfo.CONFIRM_DELETE);
            return true;
        } else if (id == R.id.share) {
            Toast.makeText(getApplicationContext(), "개발중입니다", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* 화면에 이미지 세팅 */
    public void setMediaImage(String photoId, String photoUri) {
        Log.d(TAG, "photoId : " + photoId + ", photoUri : " + photoUri);

        if (photoId == null || photoId.equals("") || photoId.equals("-1")) {
            //첨부한 사진이 없을 경우 : 사라지게함
            //mPhoto.setImageResource(R.drawable.ic_add_photo);
            mPhoto.setVisibility(View.GONE);
        } else {
            //isPhotoFileSaved = true;
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
        }

    }

    public FillViewActivity(Context context){
        mContext = context;
    }


    /**
     * 앨범의 사진을 사진 폴더에 복사한 후, PICTURE 테이블에 사진 정보 추가
     * 이미지의 이름은 현재 시간을 기준으로 한 getTime() 값의 문자열 사용
     *
     * @return 새로 추가된 이미지의 이름
     */

    private String insertPhoto() {
        String photoName = null;

        if (isPhotoCaptured) { // captured Bitmap
            try {
                if (mFillMode != null && mFillMode.equals(BasicInfo.MODE_VIEW)) {
                    Log.d(TAG, "previous photo is newly created for modify mode.");

                    String SQL = "delete from " + FillDatabase.TABLE_PHOTO +
                            " where _ID = '" + mMediaPhotoId + "'";
                    Log.d(TAG, "SQL : " + SQL);
                    if (FillMainActivity.mDatabase != null) {
                        FillMainActivity.mDatabase.execSQL(SQL);
                    }

                    File previousFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }


                File photoFolder = new File(BasicInfo.FOLDER_PHOTO);

                //폴더가 없다면 폴더를 생성한다.
                if (!photoFolder.isDirectory()) {
                    Log.d(TAG, "creating photo folder : " + photoFolder);
                    photoFolder.mkdirs();
                }

                // Temporary Hash for photo file name
                photoName = createFilename();

                FileOutputStream outstream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultPhotoBitmap.compress(CompressFormat.PNG, 100, outstream);
                outstream.close();


                if (photoName != null) {
                    Log.d(TAG, "isCaptured            : " + isPhotoCaptured);

                    // INSERT PICTURE INFO
                    String SQL = "insert into " + FillDatabase.TABLE_PHOTO + "(URI) values(" + "'" + photoName + "')";
                    if (FillMainActivity.mDatabase != null) {
                        FillMainActivity.mDatabase.execSQL(SQL);
                    }
                }

            } catch (IOException ex) {
                Log.d(TAG, "Exception in copying photo : " + ex.toString());
            }


        }
        return photoName;
    }


    private String createFilename() {
        Date curDate = new Date();
        String curDateStr = String.valueOf(curDate.getTime());

        return curDateStr;
    }


    public void setMediaLayout() {
        isPhotoCaptured = false;
    }


    private void setCalendar() {
        insertDateButton = (Button) findViewById(R.id.insert_dateBtn);
        insertDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                String mDateStr = insertDateButton.getText().toString();
//                Calendar calendar = Calendar.getInstance();
//                Date date = new Date();
//                try {
//                    if (BasicInfo.language.equals("ko")) {
//                        date = BasicInfo.dateDayNameFormat.parse(mDateStr);
//                    } else {
//                        date = BasicInfo.dateDayFormat.parse(mDateStr);
//                    }
//                } catch (Exception ex) {
//                    Log.d(TAG, "Exception in parsing date : " + date);
//                }
//
//                calendar.setTime(date);
//
//                new DatePickerDialog(
//                        FillViewActivity.this,
//                        dateSetListener,
//                        calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH)
//                ).show();

            }
        });

        insertTimeButton = (Button) findViewById(R.id.insert_timeBtn);
        insertTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                String mTimeStr = insertTimeButton.getText().toString();
//                Calendar calendar = Calendar.getInstance();
//                Date date = new Date();
//                try {
//                    if (BasicInfo.language.equals("ko")) {
//                        date = BasicInfo.dateTimeNameFormat.parse(mTimeStr);
//                    } else {
//                        date = BasicInfo.dateTimeFormat.parse(mTimeStr);
//                    }
//                } catch (Exception ex) {
//                    Log.d(TAG, "Exception in parsing date : " + date);
//                }
//
//                calendar.setTime(date);
//
//                new TimePickerDialog(
//                        FillViewActivity.this,
//                        timeSetListener,
//                        calendar.get(Calendar.HOUR_OF_DAY),
//                        calendar.get(Calendar.MINUTE),
//                        true
//                ).show();

            }
        });

        Date curDate = new Date();
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfYear + 1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        if (BasicInfo.language.equals("ko")) {
            insertDateButton.setText(year + "년 " + monthStr + "월 " + dayStr + "일");
        } else {
            insertDateButton.setText(year + "-" + monthStr + "-" + dayStr);
        }

        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

        if (BasicInfo.language.equals("ko")) {
            insertTimeButton.setText(hourStr + "시 " + minuteStr + "분");
        } else {
            insertTimeButton.setText(hourStr + ":" + minuteStr);
        }

    }


    private void setFillDate(String dateStr) {
        Log.d(TAG, "setFillDate() called : " + dateStr);

        Date date = new Date();
        try {
            if (BasicInfo.language.equals("ko")) {
                date = BasicInfo.dateNameFormat2.parse(dateStr);
            } else {
                date = BasicInfo.dateNameFormat3.parse(dateStr);
            }
        } catch (Exception ex) {
            Log.d(TAG, "Exception in parsing date : " + dateStr);
        }

        //Calendar calendar = Calendar.getInstance();
        mCalendar.setTime(date);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfYear + 1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        if (BasicInfo.language.equals("ko")) {
            insertDateButton.setText(year + "년 " + monthStr + "월 " + dayStr + "일");
        } else {
            insertDateButton.setText(year + "-" + monthStr + "-" + dayStr);
        }

        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

        if (BasicInfo.language.equals("ko")) {
            insertTimeButton.setText(hourStr + "시 " + minuteStr + "분");
        } else {
            insertTimeButton.setText(hourStr + ":" + minuteStr);
        }

    }

    /* 날짜 설정 리스너 */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(year, monthOfYear, dayOfMonth);

            String monthStr = String.valueOf(monthOfYear + 1);
            if (monthOfYear < 9) {
                monthStr = "0" + monthStr;
            }

            String dayStr = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10) {
                dayStr = "0" + dayStr;
            }


            if (BasicInfo.language.equals("ko")) {
                insertDateButton.setText(year + "년 " + monthStr + "월 " + dayStr + "일");
            } else {
                insertDateButton.setText(year + "-" + monthStr + "-" + dayStr);
            }
        }
    };


    /* 시간 설정 리스너 */
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour_of_day, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
            mCalendar.set(Calendar.MINUTE, minute);

            String hourStr = String.valueOf(hour_of_day);
            if (hour_of_day < 10) {
                hourStr = "0" + hourStr;
            }

            String minuteStr = String.valueOf(minute);
            if (minute < 10) {
                minuteStr = "0" + minuteStr;
            }

            if (BasicInfo.language.equals("ko")) {
                insertTimeButton.setText(hourStr + "시 " + minuteStr + "분");
            } else {
                insertTimeButton.setText(hourStr + ":" + minuteStr);
            }
        }
    };


    /* 날짜와 일기 확인 */
    private boolean parseValues() {
        String insertDateStr = insertDateButton.getText().toString();
        String insertTimeStr = insertTimeButton.getText().toString();

        //Date curDate = new Date();
        //Calendar curCalendar = Calendar.getInstance();
        //curCalendar.setTime(curDate);

        //int curHour = curCalendar.get(Calendar.HOUR_OF_DAY);
        //int curMinute = curCalendar.get(Calendar.MINUTE);
        //String curHourStr = String.valueOf(curHour);
        //String curMinuteStr = String.valueOf(curMinute);
        //if (curHourStr.length() < 2) {
        //	curHourStr = "0" + curHourStr;
        //}
        //if (curMinuteStr.length() < 2) {
        //	curMinuteStr = "0" + curMinuteStr;
        //}

        String srcDateStr = insertDateStr + " " + insertTimeStr;
        Log.d(TAG, "source date string : " + srcDateStr);

        try {
            if (BasicInfo.language.equals("ko")) {
                Date insertDate = BasicInfo.dateNameFormat.parse(srcDateStr);
                mDateStr = BasicInfo.dateFormat.format(insertDate);
            } else {
                Date insertDate = BasicInfo.dateNameFormat3.parse(srcDateStr);
                mDateStr = BasicInfo.dateFormat.format(insertDate);
            }
        } catch (ParseException ex) {
            Log.e(TAG, "Exception in parsing date : " + insertDateStr);
        }

        mFillStr = view_text.getText().toString();

        // if handwriting is available
        if ((mFillMode != null && (mFillMode.equals(BasicInfo.MODE_MODIFY) || mFillMode.equals(BasicInfo.MODE_VIEW)))) {

        } else {
            // check text fill
            if (mFillStr.trim().length() < 1) {
                //showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
                return false;
            }
        }

        return true;
    }

    /* 다이얼로그 - showDialog()를 사용 했을 떄 연결되는 메소드 */
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;

        switch (id) {
/*
            case BasicInfo.CONFIRM_TEXT_INPUT: //일기에 내용이 없을 경우
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.memo_title); //제목설정
                builder.setMessage(R.string.text_input_message); //메시지를 띄움
                builder.setPositiveButton(R.string.confirm_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {}
                });

                break;
*/
            case BasicInfo.CONTENT_PHOTO: //입력화면에 사진을 터치했을때
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo; //array.xml에서 가져온 목록
                builder.setTitle(R.string.selection_title);//타이틀 설정
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                //선택버튼
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();//사진찍는 화면으로
                        } else if (mChoicedArrayItem == 1) {
                            //showPhotoLoadingActivity();//앨범으로
                        }
                    }
                });
                //취소버튼
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                break;

            case BasicInfo.CONTENT_PHOTO_EX: //수정할때 사진 누르면 발생
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo_ex;//array.xml에서 가져온 목록
                builder.setTitle(R.string.selection_title);//타이틀 설정
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                //선택버튼
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mChoicedArrayItem == 0) {//사진찍는 화면으로
                            showPhotoCaptureActivity();
                        } else if (mChoicedArrayItem == 1) {//앨범에서 선택
                            //showPhotoLoadingActivity();
                        } else if (mChoicedArrayItem == 2) {//사진삭제
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;
                            mPhoto.setImageResource(R.drawable.ic_album);

                        }
                    }
                });
                //취소버튼
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                break;

            case BasicInfo.CONFIRM_DELETE://상세보기화면(View)에서 더모어 버튼의 삭제 클릭했을떄
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.memo_title);
                builder.setMessage(R.string.memo_delete_question);
                builder.setPositiveButton(R.string.yes_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteFill();
                    }
                });
                builder.setNegativeButton(R.string.no_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dismissDialog(BasicInfo.CONFIRM_DELETE);
                        //dismissDialog : 화면을 안전하게 종료
                    }
                });

                break;
            default:
                break;
        }
        return builder.create();
    }


    /* 일기 삭제 */
    private void deleteFill() {

        //일기 테이블의 데이터 삭제
        Log.d(TAG, "이전 내용 레코드 삭제 : " + mFillId);
        String SQL = "delete from " + FillDatabase.TABLE_FILL + " where _id = '" + mFillId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (FillMainActivity.mDatabase != null) {
            FillMainActivity.mDatabase.execSQL(SQL);
        }

        //사진테이블에서 데이터 삭제
        Log.d(TAG, "이전 사진 기록 및 파일 삭제 : " + mMediaPhotoId);
        SQL = "delete from " + FillDatabase.TABLE_PHOTO + " where _ID = '" + mMediaPhotoId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (FillMainActivity.mDatabase != null) {
            FillMainActivity.mDatabase.execSQL(SQL);
        }

        File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
        if (photoFile.exists()) {
            photoFile.delete();
        }

        setResult(RESULT_OK);
        finish();
    }

    /* 사진찍기 화면 */
    public void showPhotoCaptureActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoCaptureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);//사진찍는 액티비로 이동
    }

    /* 앨범에서 선택 화면 */
    /*
    public void showPhotoLoadingActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoSelectionActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);//앨범선택 액티비티로 이동
    }
    */

    /* 사진 선택 액티비티 */
    //startActivityForResult를 이용해서 인텐트 된 액티비티에서 결과값이 RESULT_OK일경우 실행
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY:  // 사진 찍는 경우
                Log.d(TAG, "onActivityResult() 사진 찍기 ");

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode : " + resultCode);

                    boolean isPhotoExists = checkCapturedPhotoFile();//추상파일의 존재여부확인
                    if (isPhotoExists) {
                        Log.d(TAG, "이미지파일 있음 : " + BasicInfo.FOLDER_PHOTO + "captured");

                        resultPhotoBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + "captured");
                        tempPhotoUri = "captured";
                        mPhoto.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;
                        mPhoto.invalidate();//무효화
                    } else {
                        Log.d(TAG, "이미지파일 없음 : " + BasicInfo.FOLDER_PHOTO + "captured");
                    }
                }

                break;

            case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY:  // 사진을 앨범에서 선택하는 경우
                Log.d(TAG, "onActivityResult() 앨범에서 선택 ");

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode : " + resultCode);

                    Uri getPhotoUri = intent.getParcelableExtra(BasicInfo.KEY_URI_PHOTO);
                    try {
                        resultPhotoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoUri), null, null);
                        Uri uri = intent.getData(); //uri 만들기
                        getImageRotation(mContext, uri);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


//                    Uri uri = intent.getData();
//                    try {
//                        ExifInterface exifInterface = new ExifInterface(uri.getPath());
//                    } catch (IOException e) {
//                        return;
//                    }

                    mPhoto.setImageBitmap(resultPhotoBitmap);

                    isPhotoCaptured = true;

                    mPhoto.invalidate();
                }
                break;
        }
    }

    /* 저장된 사진 파일 확인*/
    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");//추상파일생성
        if (file.exists()) { //이 추상 경로가 나타나는 파일 또는 디렉토리가 존재하는 경우에
            return true;
        }
        return false;
    }

//    //이미지 회전 함수
//    public Bitmap rotateImage(Bitmap src, float degree) {
//        // Matrix 객체 생성
//        Matrix matrix = new Matrix();
//        // 회전 각도 셋팅
//        matrix.postRotate(degree);
//        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
//        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
//    }

    public static int getImageRotation(Context context, Uri imageUri) {
        try {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            if (rotation == ExifInterface.ORIENTATION_UNDEFINED)
                return getRotationFromMediaStore(context, imageUri);
            else return exifToDegrees(rotation);
        } catch (IOException e) {
            return 0;
        }
    }



    public static int getRotationFromMediaStore(Context context, Uri imageUri) {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
        if (cursor == null) return 0;

        cursor.moveToFirst();

        int orientationColumnIndex = cursor.getColumnIndex(columns[1]);
        return cursor.getInt(orientationColumnIndex);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        } else {
            return 0;
        }
    }


}
