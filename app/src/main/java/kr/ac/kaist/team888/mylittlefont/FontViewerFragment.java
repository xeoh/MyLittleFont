package kr.ac.kaist.team888.mylittlefont;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import kr.ac.kaist.team888.util.FontExporter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FontViewerFragment extends Fragment {
  private static final String DEFAULT_FILE_PATH = "/MyLittleFont/";
  private static final String FONT_PREVIEW_TEXT = "한글";
  private GridTextAdapter gridTextAdapter;
  private FontListAdapter fontListAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.font_viewer_fragment, null, false);

    ExpandableHeightGridView gridView = (ExpandableHeightGridView) view.getRootView()
        .findViewById(R.id.examine_grid_view);
    gridView.setExpanded(true);
    gridTextAdapter = new GridTextAdapter();
    gridView.setAdapter(gridTextAdapter);
    gridTextAdapter.notifyDataSetChanged();

    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        + DEFAULT_FILE_PATH;

    File directory = new File(path);
    FileFilter fileFilter = new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        int index = pathname.toString().lastIndexOf('.');
          return index > 0 && pathname.toString().substring(index + 1).equals("ttf");
      }
    };

    ListView fileListView = (ListView) view.getRootView().findViewById(R.id.examine_list_view);
    fontListAdapter = new FontListAdapter();
    fileListView.setAdapter(fontListAdapter);

    File[] fileList = directory.listFiles();
    for (File file : fileList) {
      if (fileFilter.accept(file)) {
        fontListAdapter.addFile(file);
      }
    }
    fontListAdapter.notifyDataSetChanged();
    return view;
  }

  private class FileNameComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {

      return String.CASE_INSENSITIVE_ORDER.compare(file1.getName(),
          file2.getName());
    }
  }

  private class FontListAdapter extends BaseAdapter {
    private ArrayList<File> files = new ArrayList<>();
    private FileNameComparator comp = new FileNameComparator();

    public void addFile(File file) {
      files.add(file);
      Collections.sort(files, comp);
    }

    @Override
    public int getCount() {
      return files.size();
    }

    @Override
    public Object getItem(int position) {
      return files.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.font_viewer_item, parent, false);
      }

      final File file = (File)getItem(position);
      TextView fontPreview = (TextView) convertView.findViewById(R.id.font_viewer_item_preview);
      fontPreview.setTypeface(Typeface.createFromFile(file));
      fontPreview.setText(FONT_PREVIEW_TEXT);

      TextView fontName = (TextView) convertView.findViewById(R.id.font_viewer_item_font_name);
      fontName.setText(file.getName());

      convertView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          gridTextAdapter.setTypeface(Typeface.createFromFile(file));
          gridTextAdapter.notifyDataSetChanged();
        }
      });

      return convertView;
    }
  }

  private class GridTextAdapter extends BaseAdapter {
    private Typeface typeface;

    public void setTypeface(Typeface typeface) {
      this.typeface = typeface;
    }

    @Override
    public int getCount() {
      return FontExporter.KS5601.length;
    }

    @Override
    public Object getItem(int position) {
      return FontExporter.KS5601[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = new TextView(parent.getContext());
      }
      TextView textView = (TextView) convertView;
      textView.setText(new String(new char[] {(char)getItem(position)}));
      textView.setTextSize(30);
      textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
      if (typeface != null) {
        textView.setTypeface(typeface);
      }

      return convertView;
    }
  }

}
