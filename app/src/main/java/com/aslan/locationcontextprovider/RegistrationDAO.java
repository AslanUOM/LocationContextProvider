package com.aslan.locationcontextprovider;

/**
 * Created by Vishnuvathsasarma on 03-Aug-15.
 */

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RegistrationDAO {

    private static RegistrationDAO instance;
    private final String FILENAME;
    private RegistrationData regData;
    private boolean isSaved;

    private RegistrationDAO() {
        regData = new RegistrationData();
        isSaved = false;
        FILENAME = "RegistrationData";
    }

    public static RegistrationDAO getInstance() {
        if (instance == null) {
            instance = new RegistrationDAO();
        }
        return instance;
    }

    public RegistrationData loadData(Context context) {
        // read configuration data object from file
        FileInputStream fis;
        ObjectInputStream ois;
        try {
            fis = context.openFileInput(FILENAME);
            ois = new ObjectInputStream(fis);
            regData = (RegistrationData) ois.readObject();
            ois.close();
            fis.close();

        } catch (FileNotFoundException e) {
            // user needs to provide initial settings on first run of app
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return regData;
    }

    public boolean saveData(Context context, RegistrationData data) {
        // save the configuration setting as object in a file
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();
            isSaved = true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSaved = false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSaved = false;
        }
        return isSaved;
    }
}