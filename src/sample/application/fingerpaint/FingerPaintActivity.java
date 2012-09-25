package sample.application.fingerpaint;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
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
			save();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
