package de.fuchsi.basal_rate_db.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;

import de.fuchsi.basal_rate_db.R;
import de.fuchsi.basal_rate_db.activity.MainActivity;

public class SortListViewDialog {

    public SortListViewDialog(){

    }
    public void showDialog(Activity activity, int sortOrder){
        final Dialog dialog = new Dialog(activity);

        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_sort);


        RadioButton buttonAsc = (RadioButton) dialog.findViewById(R.id.rb_ascending);
        buttonAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.sortList(-1);
                dialog.dismiss();
            }
        });
        RadioButton buttonDsc = (RadioButton) dialog.findViewById(R.id.rb_descending);
        buttonDsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.sortList(1);
                dialog.dismiss();
            }
        });
        if (sortOrder == -1) {
            buttonAsc.setChecked(true);
            buttonDsc.setChecked(false);
        }
        else {
            buttonAsc.setChecked(false);
            buttonDsc.setChecked(true);
        }

        dialog.show();
    }
}
