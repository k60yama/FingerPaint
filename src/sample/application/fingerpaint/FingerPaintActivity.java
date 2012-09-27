package sample.application.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class FingerPaintActivity extends Activity implements OnTouchListener{
	
	public Canvas canvas;
	public Paint paint;
	public Path path;
	public Bitmap bitmap;
	public Float x1;
	public Float y1;
	public Integer w;
	public Integer h;
	public MediaScannerConnection mc;
	
	public boolean onTouch (View v, MotionEvent event){
		float x = event.getX();
		float y = event.getY();
		
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			path.reset();
			path.moveTo(x, y);
			x1 = x;
			y1 = y;
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(x1, y1, x, y);
			x1 = x;
			y1 = y;
			canvas.drawPath(path, paint);
			path.reset();
			path.moveTo(x, y);
			break;
		case MotionEvent.ACTION_UP:
			if(x == x1 && y == y1){
				y1 = y1 + 1;
			}
			path.quadTo(x1, y1, x, y);
			canvas.drawPath(path, paint);
			path.reset();
			break;
		}
		ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
		iv.setImageBitmap(bitmap);
		
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		//ActivityクラスのonCreateを実行
		super.onCreate(savedInstanceState);
		
		//レイアウト設定ファイルの指定
		setContentView(R.layout.fingerpaint);
		
		//ImageViewオブジェクトの取得
		ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
		
		//WindowManager型のキャスト(変数の型をWindowManager型として、受け取るため)
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		
		w = disp.getWidth();
		h = disp.getHeight();
		bitmap=Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		
		//インスタンス生成
		paint = new Paint();
		path = new Path();
		canvas = new Canvas(bitmap);

		paint.setStrokeWidth(5);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		canvas.drawColor(Color.WHITE);
		iv.setImageBitmap(bitmap);
		iv.setOnTouchListener(this);
	}
	
	//保存メイン処理
	public void save(){
		SharedPreferences prefs = this.getSharedPreferences(
				"FingerPaintPreferences", MODE_PRIVATE);
		Integer imageNumber = prefs.getInt("imageNumber", 1);
		File file = null;
		
		//SDカードチェック
		if(externalMediaChecker()){
			DecimalFormat form = new DecimalFormat("0000");
			String path = Environment.getExternalStorageDirectory() + "/mypaint/";
			File outDir = new File(path);
			
			//フォルダ作成
			if(!outDir.exists()){
				outDir.mkdir();
			}
			
			//ファイル作成(ファイルが存在するまで)
			do{
				file = new File(path + "img" + form.format(imageNumber) + ".png");
				imageNumber++;
			}while(file.exists());
			
			//連番保存
			if(writeImage(file)){
				//メディアスキャンの実行
				this.scanMedia(file.getPath());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("imageNumber", imageNumber);
				editor.commit();
			}
		}
	}
	
	//microSDカードの有無確認
	public boolean externalMediaChecker(){
		
		boolean result = false;
		String status = Environment.getExternalStorageState();
		
		if(status.equals(Environment.MEDIA_MOUNTED)){
			result = true;
		}
		return result;
	}
	
	//書き込み処理
	public boolean writeImage(File file){
		try{
			FileOutputStream fo = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fo);
			fo.flush();
			fo.close();
		}catch(Exception e){
			System.out.println(e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	//メニュー生成
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater mi = this.getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	//メニュー選択処理
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_save:
			//保存処理へ
			save();
			break;
		case R.id.menu_open:
			//インテント生成
			Intent intent = new Intent(this, FilePicker.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.menu_color_change:
			final String[] items = getResources().getStringArray(R.array.ColorName);
			final int[] colors = getResources().getIntArray(R.array.Color);
			
			//ダイアログ生成
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.menu_color_change);		//ダイアログの件名
			
			//ダイアログに表示するカラーバリエーション
			ab.setItems(items, new DialogInterface.OnClickListener(){
				//押下された色に変更
				public void onClick(DialogInterface dialog, int item){
					paint.setColor(colors[item]);
				}
			});
			
			//ダイアログ表示
			ab.show();
			break;
		case R.id.menu_new:
			//通常ダイアログ生成
			ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.menu_new);			//ダイアログの件名
			ab.setMessage(R.string.confirm_new);	//ダイアログの本文
			
			//OKボタンを押下した場合
			ab.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//キャンバスを初期化
					canvas.drawColor(Color.WHITE);		
					((ImageView)findViewById(R.id.imageView1)).setImageBitmap(bitmap);
				}
			});
			
			//キャンセルを押下した場合
			ab.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			
			//ダイアログ表示
			ab.show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//メディアスキャンメイン処理
	public void scanMedia(final String fp){
		mc = new MediaScannerConnection(this,
				new MediaScannerConnection.MediaScannerConnectionClient(){
			public void onScanCompleted(String path, Uri uri){
				disconnect();
			}
			public void onMediaScannerConnected(){
				scanFile(fp);
			}
		});
		mc.connect();
	}
	
	//メディアスキャン実行処理
	public void scanFile(String fp){
		mc.scanFile(fp, "image/png");
	}
	
	//メディアスキャンサービスの切断処理
	public void disconnect(){
		mc.disconnect();
	}
	
	//画像ファイルの読み込み
	public Bitmap loadImage(String path){
		boolean landscape = false;
		Bitmap bm;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int oh = options.outHeight;
		int ow = options.outWidth;
		
		if(ow>oh){
			landscape = true;
			oh = options.outWidth;
			ow = options.outHeight;
		}
		
		options.inJustDecodeBounds = false;
		options.inSampleSize = Math.max(ow/w, oh/h);
		bm = BitmapFactory.decodeFile(path, options);
		
		if(landscape){
			Matrix matrix = new Matrix();
			matrix.setRotate(90.0f);
			bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
		}
		
		bm = Bitmap.createScaledBitmap(
				bm, (int)(w), (int)(w*((double)oh)/((double)ow)), false);
		
		Bitmap offBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas offCanvas = new Canvas(offBitmap);
		offCanvas.drawBitmap(bm, 0, (h-bm.getHeight()) / 2, null);
		bm = offBitmap;
		return bm;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		bitmap = loadImage(data.getStringExtra("fn"));
		canvas = new Canvas(bitmap);
		ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
		iv.setImageBitmap(bitmap);
	}
	
	//Backキーを押下した場合
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//ダイアログ生成
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.title_exit);			//ダイアログの件名
			ab.setMessage(R.string.confirm_new);		//ダイアログの本文
			
			//OKボタンを押下した場合
			ab.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();		//アクティビティ終了
				}
			});
			
			//キャンセルボタンを押下した場合
			ab.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			
			//ダイアログ表示
			ab.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
