package com.hhj.android.fill;

import java.text.SimpleDateFormat;

/* Han Hyeonju */

public class BasicInfo {

	public static String language = "";

	//외장 메모리 경로
	public static String ExternalPath = "/mnt/sdcard/";

	//외장 메모리 경로 체크 여부
	public static boolean ExternalChecked = false;

	//사진 저장 위치
	public static String FOLDER_PHOTO = "fill/photo/";

	//데이터베이스 이름
	public static String DATABASE_NAME = "fill/fill.db";

	/* 인텐트 부가정보 전달을 위한 키값 */
	public static final String KEY_FILL_MODE = "FILL_MODE";
	public static final String KEY_FILL_TEXT = "FILL_TEXT";
	public static final String KEY_FILL_ID = "FILL_ID";
	public static final String KEY_FILL_DATE = "FILL_DATE";
	public static final String KEY_ID_PHOTO = "ID_PHOTO";
	public static final String KEY_URI_PHOTO = "URI_PHOTO";

	/* 메모 모드 상수 */
	public static final String MODE_INSERT = "MODE_INSERT";
	public static final String MODE_MODIFY = "MODE_MODIFY";
	public static final String MODE_VIEW = "MODE_VIEW";

	/* 액티비티 요청 코드  */
	public static final int REQ_VIEW_ACTIVITY = 1001;
	public static final int REQ_INSERT_ACTIVITY = 1002;
	public static final int REQ_PHOTO_CAPTURE_ACTIVITY = 1501;//사진찍을때 보낼 코드
	public static final int REQ_PHOTO_SELECTION_ACTIVITY = 1502;//갤러리 불러올때 보낼 코드

	/* 날짜 포맷  */
	public static SimpleDateFormat dateDayNameFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
	public static SimpleDateFormat dateDayFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat dateNameFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
	public static SimpleDateFormat dateNameFormat2 = new SimpleDateFormat("yyyy-MM-dd HH시 mm분");
	public static SimpleDateFormat dateNameFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat dateTimeNameFormat = new SimpleDateFormat("HH시 mm분");
	public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm");

	/* 대화상자 키값  */
	public static final int IMAGE_CANNOT_BE_STORED = 1002;
	public static final int CONTENT_PHOTO = 2001;
	public static final int CONTENT_PHOTO_EX = 2005;
	public static final int CONFIRM_DELETE = 3001;
	public static final int CONFIRM_TEXT_INPUT = 3002;




}
