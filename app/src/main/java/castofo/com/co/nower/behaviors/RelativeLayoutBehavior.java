package castofo.com.co.nower.behaviors;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Alejandro on 21/12/2016.
 */

public class RelativeLayoutBehavior extends CoordinatorLayout.Behavior<RelativeLayout> {

  public RelativeLayoutBehavior(Context context, AttributeSet attrs) {
  }

  @Override
  public boolean onDependentViewChanged(CoordinatorLayout parent, RelativeLayout child,
                                        View dependency) {
    float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
    child.setTranslationY(translationY);
    return true;
  }

  @Override
  public boolean layoutDependsOn(CoordinatorLayout parent, RelativeLayout child, View dependency) {
    return dependency instanceof Snackbar.SnackbarLayout;
  }
}
