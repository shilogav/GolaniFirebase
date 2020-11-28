package com.shilo.golanimanage.mainactivity.fragments;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import com.shilo.golanimanage.mainactivity.data.Repository;
import com.shilo.golanimanage.mainactivity.model.Soldier;
import com.shilo.golanimanage.model.LoggedInUser;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * used for SoldierListFragment and SoldierDetailsFragment fragments
 */
public class SoldierListViewModel extends ViewModel {
    private Repository repository;
    private Activity activity;

    public SoldierListViewModel() {
        this.repository = Repository.getInstance();
        //toCloud();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public MutableLiveData<LoggedInUser> getUser(Activity activity) {
        return repository.getUser(activity);
    }

    public void getCloud() {
        Log.i("SoldierListViewModel -> getCloud", "executed");
        //createSoldiers();
        repository.getCloud();
    }

    public MutableLiveData<List<Soldier>> getSoldiersLiveData() {
        return repository.getSoldiersLiveData();
    }

    /**
     * create soldiers by reading excel file
     */
    public void createSoldiers() {
        ArrayList<Soldier> soldiers = getDataFromExcel();
        repository.createSoldiers(soldiers);
    }

    public ArrayList<Soldier> getDataFromExcel() {
        ArrayList<Soldier> soldiers = new ArrayList<>();
    InputStream myInput;
    // initialize asset manager
    AssetManager assetManager = activity.getAssets();
        try {
            //  open excel file name as myexcelsheet.xls
            myInput = assetManager.open("data.xlsx");
            // Create a POI File System object
            POIFSFileSystem myFileSystem = null;

            //myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            XSSFWorkbook myWorkBook = new XSSFWorkbook(myInput);
            // Get the first sheet from workbook
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIt = mySheet.iterator();
            boolean startRead = false;
            while (rowIt.hasNext()) {
                Row row = rowIt.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    DataFormatter formatter = new DataFormatter();
                    String cellString = formatter.formatCellValue(cell);
                    Log.i("SoldierListViewModel", "cell: " + cellString);
                    if (cellString.equals("1")) {
                        startRead = true;
                    }
                    if (startRead) {
                        int num = Integer.parseInt(cellString);
                        if (num > 100) {
                            Soldier soldier = new Soldier();
                            soldier.setId(cellString);
                            soldier.setName(cellString);
                            soldiers.add(soldier);
                        }
                    }
                }
            }
            myWorkBook.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return soldiers;
    }



    public void toCloud(){
        //TODO: write data to cloud
        //repository.toCloud();
    }

    public void deleteSoldier(Soldier soldier, String reason) {
        repository.deleteSoldier(soldier,reason);
    }
}
