package com.hhj.android.fill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/* Han Hyeonju */

public class FillInsertActivity extends AppCompatActivity {
    private static final String TAG = "FillInsertActivity";

    TitleBitmapButton mPhotoBtn;
    EditText mFillEdit;
    ImageView mPhoto;
    String mFillMode;
    String mFillId;
    String mFillDate;
    String mMediaPhotoId;
    String mMediaPhotoUri;
    String tempPhotoUri;
    String mDateStr;
    String mFillStr;

    Bitmap resultPhotoBitmap;
    boolean isPhotoCaptured;
    boolean isPhotoFileSaved;
    boolean isPhotoCanceled;

    Calendar mCalendar = Calendar.getInstance();
    Button insertDateButton;
    Button insertTimeButton;

    int mSelectdContentArray;
    int mChoicedArrayItem;
    int textViewMode = 0;
    EditText insert_fillEdit;


    Animation translateLeftAnim;
    Animation translateRightAnim;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fill_insert_activity);

        mPhoto = (ImageView) findViewById(R.id.insert_photo);
        mFillEdit = (EditText) findViewById(R.id.insert_fillEdit);
        insert_fillEdit = (EditText) findViewById(R.id.insert_fillEdit);
        translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animListener);
        translateRightAnim.setAnimationListener(animListener);

        mPhoto.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                /*
               //카메라 아이콘 눌렀을때, 다이얼로그에서 사진찍기와 갤러리 중 선택
                if (isPhotoCaptured || isPhotoFileSaved) {
                    showDialog(BasicInfo.CONTENT_PHOTO_EX);
                } else {
                    showDialog(BasicInfo.CONTENT_PHOTO);
                }
                */

                //카메라 아이콘 눌렀을때, 바로 갤러리로 이동
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);
            }
        });


        setMediaLayout();

        setCalendar();

        Intent intent = getIntent();
        mFillMode = intent.getStringExtra(BasicInfo.KEY_FILL_MODE);
        processIntent(intent);
        if (mFillMode.equals(BasicInfo.MODE_MODIFY)) {
            processIntent(intent);
            setTitle(R.string.modify_btn);
        } else {

            setTitle(R.string.new_title);
        }
    }

    /* 메뉴클릭 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_ok) {
            //저장 save
            boolean isParsed = parseValues();
            if (isParsed) {
                if (mFillMode.equals(BasicInfo.MODE_INSERT)) {
                    saveInput();
                } else if (mFillMode.equals(BasicInfo.MODE_MODIFY)) {
                    modifyInput();
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class SlidingPageAnimationListener implements AnimationListener {
        public void onAnimationEnd(Animation animation) {}
        public void onAnimationRepeat(Animation animation) {}
        public void onAnimationStart(Animation animation) {}
    }

    public void processIntent(Intent intent) {
        mFillId = intent.getStringExtra(BasicInfo.KEY_FILL_ID);
        mFillDate = intent.getStringExtra(BasicInfo.KEY_FILL_DATE);
        String curFillText = intent.getStringExtra(BasicInfo.KEY_FILL_TEXT);
        mFillEdit.setText(curFillText);
        mMediaPhotoId = intent.getStringExtra(BasicInfo.KEY_ID_PHOTO);
        mMediaPhotoUri = intent.getStringExtra(BasicInfo.KEY_URI_PHOTO);

        setMediaImage(mMediaPhotoId, mMediaPhotoUri);
        setFillDate(mFillDate);

        if (curFillText != null && !curFillText.equals("")) {
            textViewMode = 0;
            insert_fillEdit.setVisibility(View.VISIBLE);
        } else {
            textViewMode = 1;
        }
    }


    public void setMediaImage(String photoId, String photoUri) {
        Log.d(TAG, "photoId : " + photoId + ", photoUri : " + photoUri);

        if (photoId == null || photoId.equals("") || photoId.equals("-1")) {
            mPhoto.setImageResource(R.drawable.ic_album);
            mPhoto.setAdjustViewBounds(true);//이미지뷰와 경계를 맞춰줌
        } else {
            isPhotoFileSaved = true;
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
        }

    }

    /* 데이터베이스에 레코드 추가 */
    private void saveInput() {

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

        // Photo 데이터
        if (photoFilename != null) {
            // query picture id
            SQL = "select _ID from " + FillDatabase.TABLE_PHOTO + " where URI = '" + photoFilename + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (FillMainActivity.mDatabase != null) {
                Cursor cursor = FillMainActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();
            }
        }


        SQL = "insert into " + FillDatabase.TABLE_FILL +
                "(INPUT_DATE, CONTENT_TEXT, ID_PHOTO) values(" +
                "DATETIME('" + mDateStr + "'), " +
                "'" + mFillStr + "', " +
                "'" + photoId + "')";        // Stage3 added

        Log.d(TAG, "SQL : " + SQL);
        if (FillMainActivity.mDatabase != null) {
            FillMainActivity.mDatabase.execSQL(SQL);
        }

        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();

    }


    /* 데이터베이스 레코드 수정 */
    private void modifyInput() {

        Intent intent = getIntent();

        String photoFilename = insertPhoto();
        int photoId = -1;

        String SQL = null;

        if (photoFilename != null) {
            // query picture id
            SQL = "select _ID from " + FillDatabase.TABLE_PHOTO + " where URI = '" + photoFilename + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (FillMainActivity.mDatabase != null) {
                Cursor cursor = FillMainActivity.mDatabase.rawQuery(SQL);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();

                mMediaPhotoUri = photoFilename;

                SQL = "update " + FillDatabase.TABLE_FILL +
                        " set " +
                        " ID_PHOTO = '" + photoId + "'" +
                        " where _id = '" + mFillId + "'";

                if (FillMainActivity.mDatabase != null) {
                    FillMainActivity.mDatabase.rawQuery(SQL);
                }

                mMediaPhotoId = String.valueOf(photoId);
            }
        } else if (isPhotoCanceled && isPhotoFileSaved) {
            SQL = "delete from " + FillDatabase.TABLE_PHOTO +
                    " where _ID = '" + mMediaPhotoId + "'";
            Log.d(TAG, "SQL : " + SQL);
            if (FillMainActivity.mDatabase != null) {
                FillMainActivity.mDatabase.execSQL(SQL);
            }

            File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
            if (photoFile.exists()) {
                photoFile.delete();
            }

            SQL = "update " + FillDatabase.TABLE_FILL +
                    " set " +
                    " ID_PHOTO = '" + photoId + "'" +
                    " where _id = '" + mFillId + "'";

            if (FillMainActivity.mDatabase != null) {
                FillMainActivity.mDatabase.rawQuery(SQL);
            }

            mMediaPhotoId = String.valueOf(photoId);
        }


        // update fill info
        SQL = "update " + FillDatabase.TABLE_FILL +
                " set " +
                " INPUT_DATE = DATETIME('" + mDateStr + "'), " +
                " CONTENT_TEXT = '" + mFillStr + "'" +
                " where _id = '" + mFillId + "'";

        Log.d(TAG, "SQL : " + SQL);
        if (FillMainActivity.mDatabase != null) {
            FillMainActivity.mDatabase.execSQL(SQL);
        }

        intent.putExtra(BasicInfo.KEY_FILL_TEXT, mFillStr);
        intent.putExtra(BasicInfo.KEY_ID_PHOTO, mMediaPhotoId);
        intent.putExtra(BasicInfo.KEY_URI_PHOTO, mMediaPhotoUri);


        setResult(RESULT_OK, intent);
        finish();
    }


    /* 앨범의 사진을 사진 폴더에 복사한 후, PICTURE 테이블에 사진 정보 추가
     * 이미지의 이름은 현재 시간을 기준으로 한 getTime() 값의 문자열 사용
     */

    private String insertPhoto() {
        String photoName = null;

        if (isPhotoCaptured) { // captured Bitmap
            try {
                if (mFillMode != null && (mFillMode.equals(BasicInfo.MODE_MODIFY) || mFillMode.equals(BasicInfo.MODE_VIEW))) {
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
        insertDateButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String mDateStr = insertDateButton.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                try {
                    if (BasicInfo.language.equals("ko")) {
                        date = BasicInfo.dateDayNameFormat.parse(mDateStr);
                    } else {
                        date = BasicInfo.dateDayFormat.parse(mDateStr);
                    }
                } catch (Exception ex) {
                    Log.d(TAG, "Exception in parsing date : " + date);
                }

                calendar.setTime(date);

                new DatePickerDialog(
                        FillInsertActivity.this,
                        dateSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();

            }
        });

        insertTimeButton = (Button) findViewById(R.id.insert_timeBtn);
        insertTimeButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String mTimeStr = insertTimeButton.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                try {
                    if (BasicInfo.language.equals("ko")) {
                        date = BasicInfo.dateTimeNameFormat.parse(mTimeStr);
                    } else {
                        date = BasicInfo.dateTimeFormat.parse(mTimeStr);
                    }
                } catch (Exception ex) {
                    Log.d(TAG, "Exception in parsing date : " + date);
                }

                calendar.setTime(date);

                new TimePickerDialog(
                        FillInsertActivity.this,
                        timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();

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


    /* 일자와 메모 확인 */
    private boolean parseValues() {
        String insertDateStr = insertDateButton.getText().toString();
        String insertTimeStr = insertTimeButton.getText().toString();

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

        mFillStr = mFillEdit.getText().toString();

        // if handwriting is available
        if ((mFillMode != null && (mFillMode.equals(BasicInfo.MODE_MODIFY) || mFillMode.equals(BasicInfo.MODE_VIEW)))) {

        } else {
            // check text fill
            if (mFillStr.trim().length() < 1) {
                showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
                return false;
            }
        }

        return true;
    }


    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = null;

        switch (id) {
            case BasicInfo.CONFIRM_TEXT_INPUT:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.memo_title);
                builder.setMessage(R.string.text_input_message);
                builder.setPositiveButton(R.string.confirm_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case BasicInfo.CONTENT_PHOTO://새일기 입력 화면
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo;
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else if (mChoicedArrayItem == 1) {
                            showPhotoLoadingActivity();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case BasicInfo.CONTENT_PHOTO_EX: // 수정화면
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo_ex;
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else if (mChoicedArrayItem == 1) {
                            showPhotoLoadingActivity();
                        } else if (mChoicedArrayItem == 2) {
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            if (BasicInfo.language.equals("ko")) {
                                mPhoto.setImageResource(R.drawable.ic_add_photo);
                            } else {
                                mPhoto.setImageResource(R.drawable.ic_add_photo);
                            }
                        }
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });

                break;

            case BasicInfo.CONFIRM_DELETE:
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
                    }
                });

                break;


            default:
                break;
        }

        return builder.create();
    }


    /*메모 삭제*/
    private void deleteFill() {

        // delete photo record
        Log.d(TAG, "deleting previous photo record and file : " + mMediaPhotoId);
        String SQL = "delete from " + FillDatabase.TABLE_PHOTO +
                " where _ID = '" + mMediaPhotoId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (FillMainActivity.mDatabase != null) {
            FillMainActivity.mDatabase.execSQL(SQL);
        }

        File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
        if (photoFile.exists()) {
            photoFile.delete();
        }


        // delete fill record
        Log.d(TAG, "deleting previous fill record : " + mFillId);
        SQL = "delete from " + FillDatabase.TABLE_FILL +
                " where _id = '" + mFillId + "'";
        Log.d(TAG, "SQL : " + SQL);
        if (FillMainActivity.mDatabase != null) {
            FillMainActivity.mDatabase.execSQL(SQL);
        }

        setResult(RESULT_OK);

        finish();
    }


    /* 사진찍기 화면 */
    public void showPhotoCaptureActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);//사진찍는 액티비로 이동
    }

    /* 갤러리에서 선택 화면 */
    public void showPhotoLoadingActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);
    }

    /* 다른 액티비티로부터의 응답 처리 */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY:  // 사진 찍는 경우
                Log.d(TAG, "onActivityResult() for REQ_PHOTO_CAPTURE_ACTIVITY.");

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "resultCode : " + resultCode);

                    boolean isPhotoExists = checkCapturedPhotoFile();
                    if (isPhotoExists) {
                        Log.d(TAG, "image file exists : " + BasicInfo.FOLDER_PHOTO + "captured");

                        resultPhotoBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + "captured");

                        tempPhotoUri = "captured";

                        mPhoto.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;

                        mPhoto.invalidate();
                    } else {
                        Log.d(TAG, "image file doesn't exists : " + BasicInfo.FOLDER_PHOTO + "captured");
                    }
                }

                break;

            case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY:  // 사진을 갤러리에서 선택하는 경우
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        //이미지 데이터를 비트맵으로 받아온다.
                        resultPhotoBitmap = Images.Media.getBitmap(getContentResolver(), intent.getData());
                        //배치해놓은 ImageView에 set
                        mPhoto.setImageBitmap(resultPhotoBitmap);
                        mPhoto.setRotation(90);
                        isPhotoCaptured = true;
                        mPhoto.invalidate();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        Log.e("G", e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        Log.e("G", e.getMessage());
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e("G", e.getMessage());
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /* 저장된 사진 파일 확인 */
    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");
        if (file.exists()) {
            return true;
        }

        return false;
    }


}
