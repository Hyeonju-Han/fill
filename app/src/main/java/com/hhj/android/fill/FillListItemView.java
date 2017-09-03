package com.hhj.android.fill;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

/*
    Han Hyeonju
    - 어댑터에서 받은 데이터를 세팅하는 클래스
*/

public class FillListItemView extends LinearLayout {

	private ImageView itemPhoto;
	private TextView itemYYMM;
	private TextView itemDD;
	private TextView itemHHMM;
	private TextView itemText;
	private Context mContext;
    Bitmap bitmap;

	public FillListItemView(Context context) {
		super(context);
		mContext = context;

        //LayoutInflater
        //XML에 정의된 리소스들을 View의 형태로 반환
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.fill_listitem, this, true);

		itemPhoto = (ImageView) findViewById(R.id.itemPhoto);
		itemYYMM = (TextView) findViewById(R.id.itemYYMM);
		itemDD = (TextView) findViewById(R.id.itemDD);
		itemHHMM= (TextView) findViewById(R.id.itemHHMM);
		itemText = (TextView) findViewById(R.id.itemText);
	}

	//어댑터의 getView에서 아이템에 표시할 정보를 설정
	public void setContents(int index, String data) {

        if (index == 0) {  //날짜
			itemYYMM.setText(data.substring(0,4) + "." + data.substring(5,7));
			itemDD.setText(data.substring(8,10));
			itemHHMM.setText(data.substring(11,16));
        } else if (index == 1) { //내용
            itemText.setText(data);
        } else if (index == 2) { //사진
			if (data == null || data.equals("-1") || data.equals("")) {
                itemPhoto.setVisibility(View.GONE);
                //사진정보가 없으면 ImageView사라짐
			} else {
                if (bitmap != null) {
                    bitmap.recycle(); //메모리관리
                }
                BitmapFactory.Options options = new BitmapFactory.Options();
                //비트맵의 옵션정보를 가져옴
                //options.inSampleSize = 8; //8분의 1만큼 이미지를 줄인다
                //inSampleSize옵션은 1보다 작은 값일때 무조건 1로 세팅된다
                //1보다 큰값일 때는 N분의 1만큼 이미지를 줄여서 디코딩을 한다
                bitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + data, options);
                //로컬에 존재하는 파일을 그대로 읽어올 때 쓴다 파일 경로를 파라미터로 넘겨주면
                //FileInputStream을 만들어서 decodeStream을 한다
				int imageWidth = bitmap.getWidth();
				int imageHight = bitmap.getHeight();

				Uri uri = Uri.parse(data);
				getImageRotation(mContext, uri);
				itemPhoto.setImageBitmap(bitmap);

//				//이미지뷰에 설정한 비트맵 set
//				if(imageHight < imageWidth) {
//					//itemPhoto.setImageBitmap(rotateImage(bitmap, 90));
//				}else{
//					//itemPhoto.setImageBitmap(bitmap);
//				}



				



			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	/* 이미지 회전 메소드 */
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
