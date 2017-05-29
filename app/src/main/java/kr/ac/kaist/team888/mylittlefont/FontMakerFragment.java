package kr.ac.kaist.team888.mylittlefont;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import kr.ac.kaist.team888.hangulcharacter.Hangul;
import kr.ac.kaist.team888.locator.Locator;
import kr.ac.kaist.team888.util.FeatureController;

import java.util.ArrayList;
import java.util.HashMap;

public class FontMakerFragment extends Fragment {
  private FontCanvasView fontCanvasView;
  private EditText sampleTextInput;
  private String drawingText;
  private ArrayList<Locator> locators;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.font_maker_fragment, null, false);

    return view;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    drawingText = "";
    locators = new ArrayList<>();

    fontCanvasView = (FontCanvasView) view.getRootView().findViewById(R.id.fontCanvas);
    fontCanvasView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        InputMethodManager inputManager =
            (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(sampleTextInput.getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);
      }
    });

    sampleTextInput = (EditText) view.getRootView().findViewById(R.id.sample_text);
    sampleTextInput = (EditText) view.getRootView().findViewById(R.id.sample_text);
    sampleTextInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence sequence, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable sequence) {
        String editText = "";
        HashMap<Character, Locator> charLocatorMap = new HashMap<>();
        for (int i = 0; i < drawingText.length(); i++) {
          charLocatorMap.put(drawingText.charAt(i), locators.get(i));
        }

        locators = new ArrayList<>();
        for (int i = 0; i < sequence.length(); i++) {
          char key = sequence.charAt(i);
          if (Hangul.isHangul(key)) {
            if (charLocatorMap.containsKey(key)) {
              locators.add(charLocatorMap.get(key));
            } else {
              locators.add(new Locator(key));
            }
            editText += key;
          }
        }

        fontCanvasView.drawLocators(locators);
        drawingText = editText;
      }
    });

    Switch viewSkeletonBtn = (Switch) view.getRootView().findViewById(R.id.viewSkeletonBtn);
    viewSkeletonBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        fontCanvasView.viewSkeleton(isChecked);
      }
    });

    final TextView fontSizeText = (TextView) view.getRootView().findViewById(R.id.fontSizeText);

    SeekBar sizeControl = (SeekBar) view.getRootView().findViewById(R.id.fontSizeControl);
    sizeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int fontSize = fontCanvasView.setFontSize(progress / 100.0);
        fontSizeText.setText(String.format("Font Size: %dpt", fontSize));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
    sizeControl.setProgress(25);


    // Feature - curve
    SeekBar curveControl = (SeekBar) view.getRootView().findViewById(R.id.curveControl);
    curveControl.setProgress(0);
    curveControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        FeatureController.getInstance().setCurve(progress / 100.0);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) { }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) { }
    });

    // Feature - roundness
    SeekBar roundnessControl = (SeekBar) view.getRootView().findViewById(R.id.roundnessControl);
    roundnessControl.setProgress(0);
    roundnessControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        FeatureController.getInstance().setRoundness(progress / 100.0);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) { }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) { }
    });

    // Feature - weight
    SeekBar weightControl = (SeekBar) view.getRootView().findViewById(R.id.weightControl);
    weightControl.setProgress(50);
    weightControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        FeatureController.getInstance().setWeight(progress / 100.0);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) { }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) { }
    });

    // Feature - width
    SeekBar widthControl = (SeekBar) view.getRootView().findViewById(R.id.widthControl);
    widthControl.setProgress(50);
    widthControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        FeatureController.getInstance().setWidth(progress / 100.0);
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) { }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) { }
    });
  }
}
