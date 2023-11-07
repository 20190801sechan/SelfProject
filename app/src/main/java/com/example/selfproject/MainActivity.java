package com.example.selfproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    myDBHelper myHelper;
    EditText edtName, edtNumber, edtNameResult, edtNumberResult;
    Button btnInit, btnInsert, btnUpdate, btnDelete, btnSelect;
    SQLiteDatabase sqlDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.firefox);
        setTitle("가수 그룹 관리 DB (수정)");

        // EditText 및 Button 뷰 초기화
        edtName = (EditText) findViewById(R.id.edtName);
        edtNumber = (EditText) findViewById(R.id.edtNumber);
        edtNameResult = (EditText) findViewById(R.id.edtNameResult);
        edtNumberResult = (EditText) findViewById(R.id.edtNumberResult);

        btnInit = (Button) findViewById(R.id.btnInit);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnSelect = (Button) findViewById(R.id.btnSelect);

        // myDBHelper 클래스의 인스턴스를 생성
        myHelper = new myDBHelper(this);
        // "데이터베이스 초기화" 버튼에 대한 OnClickListener 설정
        btnInit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                // 데이터베이스를 버전 2로 업그레이드
                myHelper.onUpgrade(sqlDB, 1, 2); // 인수는 아무거나 입력하면 됨.
                sqlDB.close();
            }
        });
        // "삽입" 버튼에 대한 OnClickListener 설정
        btnInsert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                // groupTBL 테이블에 새 레코드를 삽입
                sqlDB.execSQL("INSERT INTO groupTBL VALUES ( '"
                        + edtName.getText().toString() + "' , "
                        + edtNumber.getText().toString() + ");");
                sqlDB.close();
                Toast.makeText(getApplicationContext(), "입력됨",
                        Toast.LENGTH_SHORT).show();
                // 데이터를 새로고침하기 위해 "선택" 버튼을 호출
                btnSelect.callOnClick();
            }
        });
        // "수정" 버튼에 대한 OnClickListener 설정
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                if (edtName.getText().toString() != "") {
                    // gName이 일치하는 레코드의 gNumber를 업데이트
                    sqlDB.execSQL("UPDATE groupTBL SET gNumber ="
                            + edtNumber.getText() + " WHERE gName = '"
                            + edtName.getText().toString() + "';");
                }
                sqlDB.close();

                Toast.makeText(getApplicationContext(), "수정됨",
                        Toast.LENGTH_SHORT).show();
                // 데이터를 새로고침하기 위해 "선택" 버튼을 호출
                btnSelect.callOnClick();
            }
        });
        // "삭제" 버튼에 대한 OnClickListener 설정
        btnDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getWritableDatabase();
                if (edtName.getText().toString() != "") {
                    // gName이 일치하는 레코드를 삭제
                    sqlDB.execSQL("DELETE FROM groupTBL WHERE gName = '"
                            + edtName.getText().toString() + "';");

                }
                sqlDB.close();

                Toast.makeText(getApplicationContext(), "삭제됨",
                        Toast.LENGTH_SHORT).show();
                // 데이터를 새로고침하기 위해 "선택" 버튼을 호출
                btnSelect.callOnClick();
            }
        });
        // "선택" 버튼에 대한 OnClickListener 설정
        btnSelect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;", null);

                String strNames = "그룹이름" + "\r\n" + "--------" + "\r\n";
                String strNumbers = "인원" + "\r\n" + "--------" + "\r\n";

                while (cursor.moveToNext()) {
                    strNames += cursor.getString(0) + "\r\n";
                    strNumbers += cursor.getString(1) + "\r\n";
                }

                edtNameResult.setText(strNames);
                edtNumberResult.setText(strNumbers);

                cursor.close();
                sqlDB.close();
            }
        });

    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "groupDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // groupTBL 테이블을 생성
            db.execSQL("CREATE TABLE  groupTBL ( gName CHAR(20) PRIMARY KEY, gNumber INTEGER);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // groupTBL 테이블을 삭제하고 다시 생성
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            onCreate(db);
        }
    }

}