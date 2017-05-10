package kr.ac.kaist.team888.mylittlefont;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import kr.ac.kaist.team888.locator.Locator;
import kr.ac.kaist.team888.util.FeatureController;

public class FontMakerFragment extends Fragment {
  private FontCanvasView fontCanvasView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.font_maker_fragment, null, false);

    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    fontCanvasView = (FontCanvasView) view.getRootView().findViewById(R.id.fontCanvas);
    Locator locator = new Locator('갈');
    fontCanvasView.drawLocators(locator);

    Switch viewSkeletonBtn = (Switch) view.getRootView().findViewById(R.id.viewSkeletonBtn);
    viewSkeletonBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        fontCanvasView.viewSkeleton(isChecked);
      }
    });

    SeekBar curveControl = (SeekBar) view.getRootView().findViewById(R.id.curveControl);
    curveControl.setProgress(0);
    curveControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        FeatureController.getInstance().setCurve(progress / 100f);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) { }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) { }
    });
  }
}
