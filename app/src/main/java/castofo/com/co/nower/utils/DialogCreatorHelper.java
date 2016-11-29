package castofo.com.co.nower.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Alejandro on 06/07/2016.
 */
public class DialogCreatorHelper extends DialogFragment {

  public static final String TITLE_ID = "TITLE_ID";
  public static final String MESSAGE_ID = "MESSAGE_ID";
  public static final String POSITIVE_BTN_TEXT_ID = "POSITIVE_BTN_TEXT_ID";
  public static final String NEGATIVE_BTN_TEXT_ID = "NEGATIVE_BTN_TEXT_ID";
  public static final String IS_CANCELABLE = "IS_CANCELABLE";

  private DialogCreatorListener mListener;
  private int mTitleId;
  private int mMessageId;
  private int mPositiveBtnTextId;
  private int mNegativeBtnTextId;
  private static View mCustomView;
  private boolean mIsCancelable;

  /** The activity that creates an instance of this dialog fragment must implement this interface
   * in order to receive event callbacks.
   */
  public interface DialogCreatorListener {
    void onDialogPositiveClick(DialogFragment dialog);
    void onDialogNegativeClick(DialogFragment dialog);
  }

  public static DialogCreatorHelper newInstance(int titleId, int messageId, int positiveBtnTextId,
                                                int negativeBtnTextId, View customView,
                                                boolean isCancelable) {
    DialogCreatorHelper dialogCreatorHelper = new DialogCreatorHelper();

    Bundle args = new Bundle();
    args.putInt(TITLE_ID, titleId);
    args.putInt(MESSAGE_ID, messageId);
    args.putInt(POSITIVE_BTN_TEXT_ID, positiveBtnTextId);
    args.putInt(NEGATIVE_BTN_TEXT_ID, negativeBtnTextId);
    mCustomView = customView;
    args.putBoolean(IS_CANCELABLE, isCancelable);
    dialogCreatorHelper.setArguments(args);

    return dialogCreatorHelper;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mTitleId = getArguments().getInt(TITLE_ID);
    mMessageId = getArguments().getInt(MESSAGE_ID);
    mPositiveBtnTextId = getArguments().getInt(POSITIVE_BTN_TEXT_ID);
    mNegativeBtnTextId = getArguments().getInt(NEGATIVE_BTN_TEXT_ID);
    mIsCancelable = getArguments().getBoolean(IS_CANCELABLE);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
    setCancelable(mIsCancelable);
    getDialog().setCanceledOnTouchOutside(mIsCancelable);
    return super.onCreateView(inflater, root, savedInstanceState);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    // Verifies that the host activity implements the callback interface.
    try {
      // Instantiates the DialogCreatorListener so that events can be sent to the host.
      mListener = (DialogCreatorListener) context;
    }
    catch (ClassCastException e) {
      // The activity doesn't implement the interface and an exception is thrown.
      throw new ClassCastException(context.toString() + " must implement DialogCreatorListener.");
    }
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    // Sets all components to the dialog.
    builder = setView(builder);
    builder = setTitle(builder);
    builder = setMessage(builder);
    builder = setPositiveButton(builder);
    builder = setNegativeButton(builder);

    return builder.create();
  }

  /**
   * Sets a view to the given Builder, if necessary.
   *
   * @param builder The Builder for the DialogFragment
   * @return The modified Builder
   */
  public AlertDialog.Builder setView(AlertDialog.Builder builder) {
    if (mCustomView != null) {
      builder.setView(mCustomView);
    }

    return builder;
  }

  /**
   * Sets a title to the given Builder, if necessary.
   *
   * @param builder The Builder for the DialogFragment
   * @return The modified Builder
   */
  public AlertDialog.Builder setTitle(AlertDialog.Builder builder) {
    return  mTitleId != 0 ? builder.setTitle(mTitleId) : builder;
  }

  /**
   * Sets a message to the given Builder, if necessary.
   *
   * @param builder The Builder for the DialogFragment
   * @return The modified Builder
   */
  public AlertDialog.Builder setMessage(AlertDialog.Builder builder) {
    return  mMessageId != 0 ? builder.setMessage(mMessageId) : builder;
  }

  /**
   * Sets a positive button to the given Builder, if necessary.
   *
   * @param builder The Builder for the DialogFragment
   * @return The modified Builder
   */
  public AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder) {
    if (mPositiveBtnTextId != 0) {
      builder.setPositiveButton(mPositiveBtnTextId, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dialog.dismiss();
          mListener.onDialogPositiveClick(DialogCreatorHelper.this);
        }
      });
    }

    return builder;
  }

  /**
   * Sets a negative button to the given Builder, if necessary.
   *
   * @param builder The Builder for the DialogFragment
   * @return The modified Builder
   */
  public AlertDialog.Builder setNegativeButton(AlertDialog.Builder builder) {
    if (mNegativeBtnTextId != 0) {
      builder.setNegativeButton(mNegativeBtnTextId, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          mListener.onDialogNegativeClick(DialogCreatorHelper.this);
        }
      });
    }

    return builder;
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }
}
