package com.shilo.golanimanage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.model.LoggedInUser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;

public class Utility {


    public static Object fromSharedPreferences(SharedPreferences prefs, String keyName)
    {
        Gson gson = new Gson();
        String json = prefs.getString(keyName, "");
        if (keyName.equals("user")) {
            return gson.fromJson(json, LoggedInUser.class);
        } else if (keyName.equals("repository")) {
            return gson.fromJson(json, Repository.class);
        }
        return null;
    }

    public static void toSharedPreferences(SharedPreferences.Editor editor,Object object, String keyName)
    {
        Gson gson=new Gson();
        String json=gson.toJson(object);
        editor.putString(keyName,json);
        editor.commit();
    }

    public static void saveUserForSharedPref(Activity activity, LoggedInUser user) {
        SharedPreferences prefs = activity.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        toSharedPreferences(editor,user, "user");
        Log.i("Utility -> saveUserForSharedPref ", "executed");
    }

    public static void getDataFromExcel1() {
        ArrayList<Soldier> soldiers = new ArrayList<>();
        //File testFile = new File("I:\\Projects\\GolaniManage\\text.txt");
        //Log.i("Utility", "testFile path is: " + testFile.getAbsolutePath());
        File excelFile = new File("I:\\Projects\\GolaniManage\\data.xlsx");
        Log.i("Utility", "excelFile.getAbsolutePath is: " + excelFile.getAbsolutePath());

        try {
            FileInputStream fis = new FileInputStream(excelFile);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIt = sheet.iterator();

            while(rowIt.hasNext()) {
                Row row = rowIt.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    Soldier soldier = new Soldier();
                    Log.i("Utility", "cell: " + cell.getStringCellValue());
                }

            }

            workbook.close();
            fis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
