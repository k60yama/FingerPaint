package sample.application.fingerpaint;


import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.Selection;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuInflater;
//import android.view.MenuItem;

public class MemopadActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    
    //�C���X�^���X���\�b�h
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this(MemopadActivity�N���X)
        this.setContentView(R.layout.main);
        
        //�C���X�^���X���\�b�h
        //this(MemopadActivity�N���X�̃C���X�^���X���\�b�h)
        //(EditText)�L���X�g���Ă���
        //��������\��
        EditText et = (EditText) this.findViewById(R.id.editText1);
        SharedPreferences pref = this.getSharedPreferences("MemoPref", MODE_PRIVATE);
        et.setText(pref.getString("memo",""));
        et.setSelection(pref.getInt("cursor", 0));
    }
    
    @Override
    protected void onStop(){
    	super.onStop();
    	EditText et = (EditText) this.findViewById(R.id.editText1);
    	SharedPreferences pref = this.getSharedPreferences("MemoPrefs",MODE_PRIVATE);
    	SharedPreferences.Editor editor = pref.edit();
    	
    	//�������ɋL�ڂ���Ă��镶������擾
    	editor.putString("memo", et.getText().toString());
    	
    	//getSelectionStart�̓N���X���\�b�h
    	editor.putInt("cursor", Selection.getSelectionStart(et.getText()));
    	editor.commit();
    }
    
	public void saveMemo(){
		EditText et = (EditText)this.findViewById(R.id.editText1);
		String title;
		String memo = et.getText().toString();
		
		if(memo.trim().length() > 0){
			if(memo.indexOf("\n") == -1){
				title = memo.substring(0, Math.min(memo.length(), 20));
			}
			else{
				title = memo.substring(0, Math.min(memo.indexOf("\n"), 20 ));
			}
			
			String ts = DateFormat.getDateTimeInstance().format(new Date());
			MemoDBHelper memos = new MemoDBHelper(this);
			SQLiteDatabase db = memos.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put("title", title + "\n" + ts );
			values.put("memo", memo);
			
			db.insertOrThrow("memoDB", null, values);
			memos.close();

		}
			
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			EditText et = (EditText)this.findViewById(R.id.editText1);
			
			switch (requestCode) {
			case 0:
				et.setText(data.getStringExtra("text"));
				break;
			}
			
		}
		
	}

	@Override
	//���j���[�{�^���̕\��(��ʕ\����)
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater mi = this.getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		
		return super.onCreateOptionsMenu(menu);
		
	}

	/*
	@Override
	//�{�^���I����A����
	public boolean onOptionsItemSelected(MenuItem item) {
		EditText et = (EditText)this.findViewById(R.id.editText1);
		
		switch (item.getItemId()) {
		case R.id.menu_save:
			this.saveMemo();
			break;
		case R.id.menu_open:
			Intent i = new Intent(this,MemoList.class);
			this.startActivityForResult(i,0);
			break;
		case R.id.menu_new:
			et.setText("");
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
    */
}