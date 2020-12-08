package com.shilo.golanimanage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.model.Question;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.model.LoggedInUser;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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


    public static boolean saveExcelFile(Context context, String fileName, List<Question> questions) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.w("FileUtils", "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new XSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        //Cell style for other rows
        CellStyle csSimple = wb.createCellStyle();

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("דוח חייל");
        sheet1.setRightToLeft(true);

        // Generate column headings
        Row row = sheet1.createRow(0);
        Row row2 = sheet1.createRow(1);
        //id header
        c = row.createCell(0);
        c.setCellValue("ת.ז חייל");
        c.setCellStyle(cs);
        //id value
        c = row2.createCell(0);
        c.setCellValue("106");
        c.setCellStyle(csSimple);
        //team header
        c = row.createCell(1);
        c.setCellValue("קבוצה");
        c.setCellStyle(cs);
        //team value
        c = row2.createCell(1);
        c.setCellValue("1");
        c.setCellStyle(csSimple);
        //report header
        c = row.createCell(2);
        c.setCellValue("סוג דוח");
        c.setCellStyle(cs);
        //report value
        c = row2.createCell(2);
        c.setCellValue("דוח שטח");
        c.setCellStyle(csSimple);

        //question header
        Row row3 = sheet1.createRow(4);
        c = row3.createCell(0);
        c.setCellValue("תכונה");
        c.setCellStyle(cs);

        c = row3.createCell(1);
        c.setCellValue("ציון");
        c.setCellStyle(cs);

        //////////////////////////
        //questions data

        for (int i = 0; i < questions.size(); i++) {
            Row row1 = sheet1.createRow(i + 5);
            c = row1.createCell(0);
            c.setCellValue(questions.get(i).getTitle());//title
            c.setCellStyle(csSimple);

            c = row1.createCell(1);
            c.setCellValue(questions.get(i).getRate());//rate
        }

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 200));
        sheet1.setColumnWidth(2, (15 * 200));

        // Create a path where we will place our List of objects on external storage
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;

    }


}
