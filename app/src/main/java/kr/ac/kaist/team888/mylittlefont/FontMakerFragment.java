package kr.ac.kaist.team888.mylittlefont;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import kr.ac.kaist.team888.locator.Locator;

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
    Locator locator = new Locator('맘');
    fontCanvasView.drawStrokes(locator.getOuterStrokes(), locator.getInnerStrokes());

    Switch xrayBtn = (Switch) view.getRootView().findViewById(R.id.xrayBtn);
    xrayBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        fontCanvasView.setXrayView(isChecked);
      }
    });
  }
}
