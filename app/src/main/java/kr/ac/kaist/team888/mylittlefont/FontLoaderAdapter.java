package kr.ac.kaist.team888.mylittlefont;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import kr.ac.kaist.team888.locator.Locator;
import kr.ac.kaist.team888.util.DatabaseOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FontLoaderAdapter extends BaseAdapter {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd. HH:mm");
  private static final char PREVIEW_LETTER = '폰';

  private Context context;
  private ArrayList<FontItem> listViewItems = new ArrayList<>();

  public FontLoaderAdapter(Context context) {
    this.context = context;
  }

  @Override
  public int getCount() {
    return listViewItems.size();
  }

  @Override
  public Object getItem(int position) {
    return listViewItems.get(position);
  }

  @Override
  public long getItemId(int position) {
    return listViewItems.get(position).getId();
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater) parent.getContext()
          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.font_loader_item, parent, false);
    }

    // Get font item.
    FontItem item = listViewItems.get(position);

    // Set text views.
    TextView fontNameView = (TextView) convertView.findViewById(R.id.fontName);
    TextView fontDateView = (TextView) convertView.findViewById(R.id.fontDate);
    fontNameView.setText(item.getName());
    fontDateView.setText(DATE_FORMAT.format(item.getDatetime()));

    // Make a locator for preview.
    Locator locator = new Locator(PREVIEW_LETTER);
    locator.applyCurve(item.getCurve());
    locator.applyWidth(item.getWidth());
    locator.applyContour(item.getWeight(), item.getRoundness());

    // Store a locator in a list.
    ArrayList<Locator> locators = new ArrayList<>(1);
    locators.add(locator);

    // Draw the preview letter on the canvas.
    FontCanvasView fontCanvasView = (FontCanvasView) convertView.findViewById(R.id.fontCanvasItem);
    fontCanvasView.setFontSize(1);
    fontCanvasView.drawLocators(locators);

    // Delete button.
    Button fontDeleteButton = (Button) convertView.findViewById(R.id.fontDeleteBtn);
    fontDeleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        DatabaseOpenHelper db = new DatabaseOpenHelper(context);
        db.deleteItem((int) getItemId(position));
        listViewItems.remove(position);
        notifyDataSetChanged();
      }
    });

    return convertView;
  }

  public void addItem(FontItem item) {
    listViewItems.add(item);
  }
}
