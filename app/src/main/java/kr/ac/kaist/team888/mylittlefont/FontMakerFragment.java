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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import kr.ac.kaist.team888.hangulcharacter.Hangul;
import kr.ac.kaist.team888.locator.Locator;
import kr.ac.kaist.team888.util.FeatureController;
import kr.ac.kaist.team888.util.ViewContainer;

import java.util.ArrayList;
import java.util.HashMap;

public class FontMakerFragment extends Fragment {
  private static final int DEFAULT_CONTROL_SIZE = 25;
  private static final int DEFAULT_CONTROL_WIDTH = 50;
  private static final int DEFAULT_CONTROL_CURVE = 0;
  private static final int DEFAULT_CONTROL_ROUNDNESS = 0;
  private static final int DEFAULT_CONTROL_WEIGHT = 50;

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

    final SeekBar sizeControl = (SeekBar) view.getRootView().findViewById(R.id.fontSizeControl);
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
    sizeControl.setProgress(DEFAULT_CONTROL_SIZE);

    // Reset feature values
    Button resetBtn = (Button) view.getRootView().findViewById(R.id.resetBtn);
    resetBtn.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        FeatureController controller = FeatureController.getInstance();
        controller.setWeight(DEFAULT_CONTROL_WEIGHT / 100.0);
        controller.setRoundness(DEFAULT_CONTROL_ROUNDNESS / 100.0);
        controller.setCurve(DEFAULT_CONTROL_CURVE / 100.0);
        controller.setWidth(DEFAULT_CONTROL_WIDTH / 100.0);
        sizeControl.setProgress(DEFAULT_CONTROL_SIZE);
      }
    });
  }

  @Override
  public void onStart() {
    super.onStart();
    View view = getView();

    // Feature - width
    SeekBar widthControl = (SeekBar) view.findViewById(R.id.widthControl);
    new SeekBarContainer(widthControl) {
      @Override
      public double getFeatureValue() {
        return FeatureController.getInstance().getWidth();
      }

      @Override
      public void setFeatureValue(double value) {
        FeatureController.getInstance().setWidth(value);
      }
    };

    // Feature - curve
    SeekBar curveControl = (SeekBar) view.findViewById(R.id.curveControl);
    new SeekBarContainer(curveControl) {
      @Override
      public double getFeatureValue() {
        return FeatureController.getInstance().getCurve();
      }

      @Override
      public void setFeatureValue(double value) {
        FeatureController.getInstance().setCurve(value);
      }
    };

    // Feature - roundness
    SeekBar roundnessControl = (SeekBar) view.findViewById(R.id.roundnessControl);
    new SeekBarContainer(roundnessControl) {
      @Override
      public double getFeatureValue() {
        return FeatureController.getInstance().getRoundness();
      }

      @Override
      public void setFeatureValue(double value) {
        FeatureController.getInstance().setRoundness(value);
      }
    };

    // Feature - weight
    SeekBar weightControl = (SeekBar) view.findViewById(R.id.weightControl);
    new SeekBarContainer(weightControl) {
      @Override
      public double getFeatureValue() {
        return FeatureController.getInstance().getWeight();
      }

      @Override
      public void setFeatureValue(double value) {
        FeatureController.getInstance().setWeight(value);
      }
    };
  }

  private abstract class SeekBarContainer extends ViewContainer<SeekBar> {
    public SeekBarContainer(SeekBar seekBar) {
      super(seekBar);
      view.setProgress((int) (getFeatureValue() * view.getMax()));
      view.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
          if (fromUser) {
            setFeatureValue(progress / (double) seekBar.getMax());
          }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
      });
    }

    @Override
    public void onFeatureChange() {
      view.setProgress((int) (getFeatureValue() * view.getMax()));
    }

    @Override
    public int getPriority() {
      return 1;
    }

    public abstract double getFeatureValue();

    public abstract void setFeatureValue(double value);
  }
}
