package castofo.com.co.nower.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by Alejandro on 06/07/2016.
 */
//TODO belongs to fragments like in the example and not to utils.
public class DialogCreatorHelper extends DialogFragment {

  /** The activity that creates an instance of this dialog fragment must implement this interface
   * in order to receive event callbacks.
   * Each method passes the DialogFragment in case the host needs to query it.
   */
  public interface DialogCreatorListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
  }

  // Instance of the interface to deliver action events.
  DialogCreatorListener mListener;

  // Overrides the Fragment.onAttach() method to instantiate the DialogCreatorHelperListener.
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    // Verifies that the host activity implements the callback interface.
    try {
      // Instantiates the DialogCreatorListener so that events can be sent to the host.
      mListener = (DialogCreatorListener) activity;
    }
    catch (ClassCastException e) {
      // The activity doesn't implement the interface and an exception is thrown.
      throw new ClassCastException(activity.toString() + " must implement DialogCreatorListener.");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage("GPS")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // Sends the positive button event back to the host activity.
            mListener.onDialogPositiveClick(DialogCreatorHelper.this);
          }
        })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            // Sends the negative button event back to the host activity.
            mListener.onDialogNegativeClick(DialogCreatorHelper.this);
          }
        });

    // Creates the AlertDialog object and returns it.
    return builder.create();
  }
}
