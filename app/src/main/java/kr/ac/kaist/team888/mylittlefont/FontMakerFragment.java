package kr.ac.kaist.team888.mylittlefont;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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

import kr.ac.kaist.team888.hangulcharacter.CharacterLoader;
import kr.ac.kaist.team888.locator.Locator;
import kr.ac.kaist.team888.util.FeatureController;
import kr.ac.kaist.team888.util.ViewContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FontMakerFragment extends Fragment {
  private static final String DEFAULT_FILE_PATH = "/MyLittleFont/";
  private static final int DEFAULT_CONTROL_SIZE = 25;

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
          if (CharacterLoader.getInstance().isDrawable(key)) {
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
      public void onClick(View buttonView) {
        FeatureController.getInstance().setDefault();
        sizeControl.setProgress(DEFAULT_CONTROL_SIZE);
      }
    });

    Button exportPngBtn = (Button) view.getRootView().findViewById(R.id.export_png);
    exportPngBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Calendar cacalendar = Calendar.getInstance();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + DEFAULT_FILE_PATH + String.format("%d%d%d_%d%d%d.png",
            cacalendar.get(Calendar.YEAR),
            cacalendar.get(Calendar.MONTH),
            cacalendar.get(Calendar.DATE),
            cacalendar.get(Calendar.HOUR),
            cacalendar.get(Calendar.MINUTE),
            cacalendar.get(Calendar.SECOND));

        try {
          Bitmap bitmap = fontCanvasView.getBitmap();

          AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

          File pngFile = new File(path);
          FileOutputStream fileOutputStream = new FileOutputStream(pngFile);
          bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
          fileOutputStream.close();
          builder.setMessage(String.format("Save file: %s", pngFile.getName()));

          AlertDialog dialog = builder.create();
          dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
              fontCanvasView.invalidate();
            }
          });

          dialog.show();
        } catch (IOException e) {
          e.printStackTrace();
        }
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

    // Feature - contrast
    SeekBar contrastControl = (SeekBar) view.findViewById(R.id.contrastControl);
    new SeekBarContainer(contrastControl) {
      @Override
      public double getFeatureValue() {
        return FeatureController.getInstance().getContrast();
      }

      @Override
      public void setFeatureValue(double value) {
        FeatureController.getInstance().setContrast(value);
      }
    };

    // Feature - flattening
    SeekBar flatteningControl = (SeekBar) view.findViewById(R.id.flatteningControl);
    new SeekBarContainer(flatteningControl) {
      @Override
      public double getFeatureValue() {
        return FeatureController.getInstance().getFlattening();
      }

      @Override
      public void setFeatureValue(double value) {
        FeatureController.getInstance().setFlattening(value);
      }
    };

    // Feature - arise
    SeekBar ariseControl = (SeekBar) view.findViewById(R.id.ariseControl);
    new SeekBarContainer(ariseControl) {
      @Override
      public double getFeatureValue() {
        return FeatureController.getInstance().getArise();
      }

      @Override
      public void setFeatureValue(double value) {
        FeatureController.getInstance().setArise(value);
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
