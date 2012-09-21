package sample.application.fingerpaint;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.SimpleCursorAdapter;
import android.content.Intent;

public class MemoList extends ListActivity {

	//要素3つのString型配列
	public static final String[] cols = {"title","memo",android.provider.BaseColumns._ID,};
	
	//インスタンス変数
	public MemoDBHelper memos;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {	
		super.onListItemClick(l, v, position, id);
		
		//インスタンス生成(選択されたメモインスタンス)
		this.memos = new MemoDBHelper(this);
		
		SQLiteDatabase db = this.memos.getWritableDatabase();
		Cursor cursor = db.query("memoDB", MemoList.cols, "_ID="+String.valueOf(id),null, null, null,null);
		this.startManagingCursor(cursor);
		Integer idx = cursor.getColumnIndex("memo");
		cursor.moveToFirst();
		
		//インスタンス生成
		Intent i = new Intent();
		
		i.putExtra("text", cursor.getString(idx));
		this.setResult(Activity.RESULT_OK, i);
		this.memos.close();
		this.finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//インスタンスメソッド
		this.setContentView(R.layout.memolist);
		this.showMemos(getMemos());
		
		ListView lv = (ListView)this.findViewById(android.R.id.list);
		this.registerForContextMenu(lv);
	}
	
	private void showMemos(Cursor cursor) {
		if(cursor!=null){
	
			//String型配列
			String[] from = {"title"};
			
			//int型配列
			int[] to={android.R.id.text1};
			
			//インスタンス生成
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					this, android.R.layout.simple_list_item_1,
					cursor, from, to);
			
			this.setListAdapter(adapter);
		}
		this.memos.close();
	}

	private Cursor getMemos(){
		this.memos = new MemoDBHelper(this);
		SQLiteDatabase db = memos.getReadableDatabase();
		Cursor cursor = db.query("memoDB",MemoList.cols,null,null,null,null,null);
		this.startManagingCursor(cursor);
		return cursor;
	}

}
