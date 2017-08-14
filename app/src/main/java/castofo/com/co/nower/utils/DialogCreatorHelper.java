package castofo.com.co.nower.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.io.Serializable;

/**
 * Created by Alejandro on 07/08/2017.
 */
public class DialogCreatorHelper extends DialogFragment {

  public static final String TITLE_KEY = "title";
  public static final String MESSAGE_KEY = "message";
  public static final String POSITIVE_BTN_TEXT_KEY = "positive_btn_text";
  public static final String NEGATIVE_BTN_TEXT_KEY = "negative_btn_text";
  private static final String ARGS_KEY = "args";
  private static final String CALLBACK_KEY = "callback";

  public static final String IS_CANCELABLE_KEY = "is_cancelable";

  public DialogCreatorHelper() {
    // Required for DialogFragment.
  }

  private static DialogCreatorHelper newInstance(String title, String message,
                                                 String positiveBtnText, String negativeBtnText,
                                                 Bundle params,
                                                 DialogCreatorHelper.Callback callback) {
    DialogCreatorHelper dialog = new DialogCreatorHelper();
    Bundle args = new Bundle();
    args.putString(TITLE_KEY, title);
    args.putString(MESSAGE_KEY, message);
    args.putString(POSITIVE_BTN_TEXT_KEY, positiveBtnText);
    args.putString(NEGATIVE_BTN_TEXT_KEY, negativeBtnText);
    args.putBundle(ARGS_KEY, params);
    args.putSerializable(CALLBACK_KEY, callback);
    dialog.setArguments(args);
    return dialog;
  }

  /**
   * The activity that creates an instance of this dialog fragment must implement this interface in
   * order to receive event callbacks.
   */
  public interface Callback extends Serializable {
    void onDialogPositiveBtnClick(DialogFragment dialog, Bundle args);
    void onDialogNegativeBtnClick(DialogFragment dialog, Bundle args);
  }

  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    Bundle args = getArguments();
    String title = args.getString(TITLE_KEY);
    String message = args.getString(MESSAGE_KEY);
    String positiveBtnText = args.getString(POSITIVE_BTN_TEXT_KEY);
    String negativeBtnText = args.getString(NEGATIVE_BTN_TEXT_KEY);
    final Bundle params = args.getBundle(ARGS_KEY);
    final DialogCreatorHelper.Callback callback = (DialogCreatorHelper.Callback)
        args.getSerializable(CALLBACK_KEY);
    boolean isCancelable = params.getBoolean(IS_CANCELABLE_KEY);

    // Sets all components of the dialog.
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
    alertDialogBuilder.setTitle(title);
    alertDialogBuilder.setMessage(message);
    alertDialogBuilder.setPositiveButton(positiveBtnText, (dialog, which) -> {
      if (callback != null) callback.onDialogPositiveBtnClick(DialogCreatorHelper.this, params);
    });
    alertDialogBuilder.setNegativeButton(negativeBtnText, (dialog, which) -> {
      if (callback != null) callback.onDialogNegativeBtnClick(DialogCreatorHelper.this, params);
    });

    AlertDialog alertDialog = alertDialogBuilder.create();
    // Configures the cancelable property.
    setCancelable(isCancelable);
    alertDialog.setCanceledOnTouchOutside(isCancelable);

    return alertDialog;
  }

  public static class Builder {

    private Context context;
    private String title;
    private String message;
    private String positiveBtnText;
    private String negativeBtnText;
    private Bundle args;
    private DialogCreatorHelper.Callback callback;

    public Builder(Context context) {
      this.context = context;
    }

    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withTitleRes(int titleResId, Object... args) {
      return withTitle(context.getResources().getString(titleResId, args));
    }

    public Builder withMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder withMessageRes(int messageResId, Object... args) {
      return withMessage(context.getString(messageResId, args));
    }

    public Builder withPositiveBtnText(String positiveBtnText) {
      this.positiveBtnText = positiveBtnText;
      return this;
    }

    public Builder withPositiveBtnTextRes(int positiveBtnTextRes, Object... args) {
      return withPositiveBtnText(context.getResources().getString(positiveBtnTextRes, args));
    }

    public Builder withNegativeBtnText(String negativeBtnText) {
      this.negativeBtnText = negativeBtnText;
      return this;
    }

    public Builder withNegativeBtnTextRes(int negativeBtnTextRes, Object... args) {
      return withNegativeBtnText(context.getResources().getString(negativeBtnTextRes, args));
    }

    public Builder withArgs(Bundle args) {
      if (this.args == null) this.args = new Bundle();
      this.args.putAll(args);
      return this;
    }

    public Builder withStringArg(String key, String arg) {
      if (this.args == null) this.args = new Bundle();
      this.args.putString(key, arg);
      return this;
    }

    public Builder withIntArg(String key, int arg) {
      if (this.args == null) this.args = new Bundle();
      this.args.putInt(key, arg);
      return this;
    }

    public Builder withBooleanArg(String key, boolean arg) {
      if (this.args == null) this.args = new Bundle();
      this.args.putBoolean(key, arg);
      return this;
    }

    public Builder withCallback(DialogCreatorHelper.Callback callback) {
      this.callback = callback;
      return this;
    }

    // Returns a dialog instance with the given params.
    public DialogCreatorHelper create() {
      return DialogCreatorHelper
          .newInstance(title, message, positiveBtnText, negativeBtnText, args, callback);
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
  }
}
