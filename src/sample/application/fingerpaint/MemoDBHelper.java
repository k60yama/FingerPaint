package sample.application.fingerpaint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MemoDBHelper extends SQLiteOpenHelper {

	//class変数
	public static String name = "memos.db";
	public static CursorFactory factory = null;
	public static Integer version = 1;
	
	//コンストラクタ(戻り値がない、クラス名とメソッド名が同じ)
	//コンストラクタを実行したときに、インスタンスが生成される
	//コンストラクタの呼び出し時は MemoDBHelper memo = new MemoDBHelper();
	public MemoDBHelper(Context context, String name, CursorFactory factory,
			Integer version) {
		//super は親クラスのことを指す。
		super(context, name, factory, version);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public MemoDBHelper(Context context) {
		super(context, name, factory, version);
	}
	
	@Override
	//Context cont = new Content();
	//MemoDBHelper memo = new MemoDBHelper(cont);
	//SQLiteDatabase sq = new SQLiteDatabase;
	//memo.Create(sq);
	public void onCreate(SQLiteDatabase db) {
		//local変数
		String sql = "CREATE TABLE memoDB ("
				+ android.provider.BaseColumns._ID
				+" INTEGER PRIMARY KEY AUTOINCREMENT, title Text, memo TEXT);";
				
				//SQLiteDatabaseクラスのインスタンスメソッド
				//SQLite(データベース)に"memoDB"のテーブルを作成するSQL文を実行する。
				db.execSQL(sql);
	}

	@Override
	//インスタンスメソッド
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 自動生成されたメソッド・スタブ

	}
	
}
