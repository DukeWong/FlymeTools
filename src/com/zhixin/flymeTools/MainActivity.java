package com.zhixin.flymeTools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.base.BaseActivity;
import com.zhixin.flymeTools.controls.TableLineDrawable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static String THIS_PACKAGE_NAME = MainActivity.class.getPackage().getName();
    public  static String ROOT_DIR="data/";
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);
        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);
        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();
        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TableLayout mainTable = (TableLayout) this.findViewById(R.id.mainTable);
        TableLineDrawable drawable = new TableLineDrawable(Color.parseColor("#FFD9D9D9"), 2, 3);
        mainTable.setBackground(drawable);
        ActivityUtil.setSmartBarEnable(this);
        SharedPreferences preferences = this.getPreferences(0);
        if (preferences.getBoolean("isFirstRun", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstRun", false);
            editor.commit();
            File thisApp = new File(Environment.getExternalStorageDirectory(), ROOT_DIR+ this.getPackageName() + "/shared_prefs");
            if (thisApp.exists()) {
                RunBackupOrRestore(true);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, 0, 0, "backup");
        item.setIcon(R.drawable.ic_backup);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item = menu.add(0,1, 1, "restore");
        item.setIcon(R.drawable.ic_restore);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    protected void findFile(File folder, List<File> files) {
        File[] listFiles = folder.listFiles();
        for (int i = 0; i < listFiles.length; i++) {
            if (listFiles[i].isFile()) {
                files.add(listFiles[i]);
            }
            if (listFiles[i].isDirectory()) {
                findFile(listFiles[i], files);
            }
        }
    }

    public void backupFile(final ProgressDialog m_pDialog, boolean restore) {
        File sourceDir = restore ? Environment.getExternalStorageDirectory() : Environment.getDataDirectory();
        File targetDir = restore ? Environment.getDataDirectory() : Environment.getExternalStorageDirectory();

        File thisApp = new File(sourceDir, ROOT_DIR + this.getPackageName() + "/shared_prefs");
        File AppList = new File(sourceDir, ROOT_DIR + this.getPackageName() + "/apps_prefs");
        new File(targetDir, ROOT_DIR + this.getPackageName() + "/shared_prefs").delete();
        new File(targetDir, ROOT_DIR+ this.getPackageName() + "/apps_prefs").delete();
        m_pDialog.setProgress(0);
        List<File> listFiles = new ArrayList<File>();
        if (thisApp.exists()) {
            findFile(thisApp, listFiles);
        }
        if (AppList.exists()) {
            findFile(AppList, listFiles);
        }
        int count = listFiles.size();
        for (int i = 0; i < count; i++) {
            File sourceFile = listFiles.get(i);
            File targetFile = new File(targetDir, sourceFile.getAbsolutePath().replace(sourceDir.getAbsolutePath()+"/", ""));
            try {
                if (!targetFile.getParentFile().exists()) {
                    if (restore) {
                        FileUtil.mkdirs(targetFile.getParentFile(), true);
                    } else {
                        targetFile.getParentFile().mkdirs();
                    }
                }
                m_pDialog.setTitle(sourceFile.getAbsolutePath());
                copyFile(sourceFile, targetFile);
                if (restore) {
                    targetFile.setExecutable(true,false);
                    targetFile.setReadable(true, false);
                    targetDir.setWritable(true, true);
                }
                m_pDialog.setProgress(i / count);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        m_pDialog.cancel();
    }

    protected void RunBackupOrRestore(final boolean restore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(restore ? R.string.data_restore : R.string.data_backup);
        builder.setMessage(restore ? R.string.restore_config : R.string.backup_config);
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final ProgressDialog m_pDialog = new ProgressDialog(MainActivity.this);
                m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                m_pDialog.setTitle(restore ? R.string.are_restore_file : R.string.are_backup_file);
                m_pDialog.setMessage(getResources().getString(restore ? R.string.get_restore_list : R.string.get_backup_list));
                m_pDialog.setIcon(R.drawable.ic_backup);
                m_pDialog.setProgress(0);
                // 设置ProgressDialog 的进度条是否不明确
                m_pDialog.setIndeterminate(false);
                // 设置ProgressDialog 是否可以按退回按键取消
                m_pDialog.setCancelable(true);
                m_pDialog.show();
                m_pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Toast.makeText(MainActivity.this, restore?R.string.restore_successfully:R.string.backup_successfully, Toast.LENGTH_SHORT).show();
                    }
                });
                new Thread() {
                    public void run() {
                        backupFile(m_pDialog, restore);
                    }
                }.start();
            }
        });
        builder.setNegativeButton(R.string.No, null);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RunBackupOrRestore(item.getItemId()==1);
        return false;
    }

}
